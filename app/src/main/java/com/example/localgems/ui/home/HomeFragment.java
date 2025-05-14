package com.example.localgems.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.example.localgems.R;
import com.example.localgems.databinding.FragmentHomeBinding;
import com.example.localgems.ui.search.SearchProductsAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.localgems.model.*;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class HomeFragment extends Fragment {

    private List<Product> products = new ArrayList<>();
    private ProductsAdapter productsAdapter;
    private FragmentHomeBinding binding;
    private PopupWindow popupWindow;
    private SharedPreferences sharedPreferences;
    private List<String> recentSearches;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // ViewModel setup
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Binding setup
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize UI elements
        MaterialToolbar toolbar = binding.toolbar;
        RecyclerView recyclerView = binding.productsRecyclerView;
        FloatingActionButton fab = binding.cartFab;

        SearchView searchView = binding.toolbar.findViewById(R.id.search_view);

        // SharedPreferences setup
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        recentSearches = loadRecentSearches();

        // Search suggestions popup
        View suggestionsPopup = inflater.inflate(R.layout.suggestion_list, null);
        ListView suggestionList = suggestionsPopup.findViewById(R.id.suggestions_list);

        // Adapter for suggestions
        ArrayAdapter<String> suggestionAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                recentSearches
        );
        suggestionList.setAdapter(suggestionAdapter);

        searchView.post(() -> {
            int searchViewWidth = searchView.getWidth();

            // Configura il PopupWindow con la larghezza calcolata
            popupWindow = new PopupWindow(suggestionsPopup,
                    searchViewWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    false);
            popupWindow.setOutsideTouchable(true);

            // Mostra il popup quando la SearchView riceve il focus
            searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
                if (hasFocus && !popupWindow.isShowing()) {
                    popupWindow.showAsDropDown(searchView);
                }
            });

            // Aggiunto listener per il tasto "clear" della SearchView
            searchView.setOnCloseListener(() -> {
                getProducts(); // Reset filtro richiamando i prodotti iniziali
                return false;
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    popupWindow.dismiss();
                    if (query != null && !query.trim().isEmpty()) {
                        saveRecentSearch(query);
                        suggestionAdapter.notifyDataSetChanged();
                    }
                    filterProductsFromFirebase(query); // Filtra i prodotti tramite Firebase
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText == null || newText.trim().isEmpty()) {
                        getProducts(); // Reset filtraggio
                        return true;
                    }
                    if (!popupWindow.isShowing()) {
                        popupWindow.showAsDropDown(searchView);
                    }
                    return false;
                }
            });
        });

        // Handle suggestion click
        suggestionList.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSuggestion = recentSearches.get(position);
            searchView.setQuery(selectedSuggestion, false);
            popupWindow.dismiss();
            Toast.makeText(getContext(), "Selezionato: " + selectedSuggestion, Toast.LENGTH_SHORT).show();
        });

        // Set up RecyclerView
        products = getProducts();
        productsAdapter = new ProductsAdapter(products, getContext());
        recyclerView.setAdapter(productsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Set up FloatingActionButton
        fab.setOnClickListener((v) -> {
            Navigation.findNavController(v).navigate(R.id.nav_cart);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private List<Product> getProducts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("products")
                .orderBy("rating", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> ratedProducts = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Product product = doc.toObject(Product.class);
                        product.setId(doc.getId()); // Memorizza l'id del documento

                        // Aggiorna le searchKeywords in minuscolo se necessario
                        updateSearchKeywords(doc);

                        ratedProducts.add(product);
                    }

                    if (products != null) {
                        products.clear();
                    }
                    products.addAll(ratedProducts);
                    if (productsAdapter != null) {
                        productsAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE", "Errore nel recupero dei prodotti per valutazione", e);
                });

        return products;
    }

    // Nuova funzione per filtrare i prodotti tramite Firebase
    private void filterProductsFromFirebase(String query) {
        if (query == null || query.trim().isEmpty()) {
            getProducts();
            return;
        }
        String lowerCaseQuery = query.toLowerCase();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Product> filteredProducts = new ArrayList<>();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Product product = doc.toObject(Product.class);
                    product.setId(doc.getId());
                    if (product.getName().toLowerCase().contains(lowerCaseQuery)) {
                        filteredProducts.add(product);
                        continue;
                    }
                    List<String> keywords = (List<String>) doc.get("searchKeywords");
                    if (keywords != null) {
                        for (String keyword : keywords) {
                            if (keyword.toLowerCase().contains(lowerCaseQuery)) {
                                filteredProducts.add(product);
                                break;
                            }
                        }
                    }
                }
                products.clear();
                products.addAll(filteredProducts);
                productsAdapter.notifyDataSetChanged();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Errore nel filtraggio: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    // Metodo per aggiornare le parole chiave in minuscolo
    private void updateSearchKeywords(QueryDocumentSnapshot doc) {
        List<String> keywords = (List<String>) doc.get("searchKeywords");
        if (keywords != null) {
            boolean needsUpdate = false;
            List<String> lowerCaseKeywords = new ArrayList<>();
            for (String kw : keywords) {
                String lowerKw = kw.toLowerCase();
                lowerCaseKeywords.add(lowerKw);
                if (!kw.equals(lowerKw)) { // Se la keyword non è già in minuscolo
                    needsUpdate = true;
                }
            }
            if (needsUpdate) {
                doc.getReference().update("searchKeywords", lowerCaseKeywords);
            }
        }
    }

    private void saveRecentSearch(String query) {
        if (!recentSearches.contains(query)) {
            recentSearches.add(0, query);
            if (recentSearches.size() > 10) {
                recentSearches.remove(recentSearches.size() - 1);
            }
            sharedPreferences.edit()
                    .putStringSet("recent_searches_home", new HashSet<>(recentSearches))
                    .apply();
        }
    }

    private List<String> loadRecentSearches() {
        return new ArrayList<>(sharedPreferences.getStringSet("recent_searches_home", new HashSet<>()));
    }
}
