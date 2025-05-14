package com.example.localgems.ui.search;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localgems.R;
import com.example.localgems.databinding.FragmentSearchBinding;
import com.example.localgems.model.Product;
import com.example.localgems.ui.home.HomeViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private PopupWindow popupWindow;
    private List<Product> allProducts; // Lista completa dei prodotti
    private SearchProductsAdapter adapter; // Adapter per la RecyclerView
    private List<String> recentSearches; // Lista delle ricerche recenti
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // ViewModel setup
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Binding setup
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize UI elements
        MaterialToolbar toolbar = binding.toolbar;
        RecyclerView recyclerView = binding.productsRecyclerView;
        FloatingActionButton fab = binding.cartFab;

        SearchView searchView = binding.toolbar.findViewById(R.id.search_view);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        recentSearches = loadRecentSearches();

        // Search suggestions popup
        View suggestionsPopup = inflater.inflate(R.layout.suggestion_list, null);
        ListView suggestionList = suggestionsPopup.findViewById(R.id.suggestions_list);

        // Adapter per suggerimenti
        ArrayAdapter<String> suggestionAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                recentSearches
        );
        suggestionList.setAdapter(suggestionAdapter);

        searchView.post(() -> {
            int searchViewWidth = searchView.getWidth();
            popupWindow = new PopupWindow(suggestionsPopup,
                    searchViewWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    false);
            popupWindow.setOutsideTouchable(true);

            searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
                if (hasFocus && !popupWindow.isShowing()) {
                    popupWindow.showAsDropDown(searchView);
                }
            });

            // Aggiunto listener per il tasto "clear" della SearchView
            searchView.setOnCloseListener(() -> {
                adapter.updateProducts(allProducts); // Reset filtro
                popupWindow.dismiss(); // Chiudi il popup
                return false;
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (query == null || query.trim().isEmpty()) {
                        adapter.updateProducts(allProducts); // Reset filtro se query vuota
                        popupWindow.dismiss();
                        return true;
                    }
                    saveRecentSearch(query);
                    filterProductsFromFirestore(query);
                    popupWindow.dismiss();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText == null || newText.trim().isEmpty()) {
                        adapter.updateProducts(allProducts); // Reset filtro se testo vuoto
                        popupWindow.dismiss();
                        return true;
                    }
                    filterProductsFromFirestore(newText);
                    if (!popupWindow.isShowing()) {
                        popupWindow.showAsDropDown(searchView);
                    }
                    return true;
                }
            });
        });

        // Gestisci il click sui suggerimenti
        suggestionList.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSuggestion = recentSearches.get(position);
            searchView.setQuery(selectedSuggestion, true); // Imposta la query e avvia la ricerca
        });

        // Configura la RecyclerView
        allProducts = getProducts(); // Ottieni tutti i prodotti
        adapter = new SearchProductsAdapter(allProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Set up FloatingActionButton
        fab.setOnClickListener(v ->
                Toast.makeText(getContext(), "Filtri applicati!", Toast.LENGTH_SHORT).show());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void filterProductsFromFirestore(String query) {
        if (query == null || query.trim().isEmpty()) {
            adapter.updateProducts(allProducts);
            return;
        }
        
        String lowerCaseQuery = query.toLowerCase();
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        
        // Cerca nei prodotti per nome in modo case-insensitive
        db.collection("products")
          .get()
          .addOnSuccessListener(queryDocumentSnapshots -> {
              List<Product> filteredProducts = new ArrayList<>();
              for (com.google.firebase.firestore.QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                  Product product = doc.toObject(Product.class);
                  product.setId(doc.getId());
                  
                  // Verifica se il nome del prodotto contiene la query (case-insensitive)
                  if (product.getName().toLowerCase().contains(lowerCaseQuery)) {
                      filteredProducts.add(product);
                      continue;
                  }
                  
                  // Verifica nelle parole chiave se presenti
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
              adapter.updateProducts(filteredProducts);
          })
          .addOnFailureListener(e -> {
              Toast.makeText(getContext(), "Errore nel filtraggio: " + e.getMessage(), Toast.LENGTH_SHORT).show();
          });
    }

    private void saveRecentSearch(String query) {
        if (!recentSearches.contains(query)) {
            recentSearches.add(0, query); // Aggiungi in cima
            if (recentSearches.size() > 10) {
                recentSearches.remove(recentSearches.size() - 1); // Mantieni solo le ultime 10
            }
            sharedPreferences.edit()
                    .putStringSet("recent_searches", new HashSet<>(recentSearches))
                    .apply();
        }
    }

    private List<String> loadRecentSearches() {
        return new ArrayList<>(sharedPreferences.getStringSet("recent_searches", new HashSet<>()));
    }

    private List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        return products;
    }
}
