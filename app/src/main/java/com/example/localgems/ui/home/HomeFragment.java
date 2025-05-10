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
import java.util.List;

public class HomeFragment extends Fragment {

    private List<Product> products = new ArrayList<>() ;
    private ProductsAdapter productsAdapter;
    private FragmentHomeBinding binding;
    private PopupWindow popupWindow;

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

        // Recent searches
        List<String> recentSearches = new ArrayList<>();
        recentSearches.add("Manzanas");
        recentSearches.add("Bananas");
        recentSearches.add("Piñas");

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

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    popupWindow.dismiss();
                    Bundle bundle = new Bundle();
                    bundle.putString("search_query", query);
                    // TODO: Vedere come creare una nuovo fragment "Search" e aprirlo al posto di fragment home.
                    //Navigation.findNavController(this).navigate(R.id.nav_search, bundle);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (!popupWindow.isShowing()) {
                        popupWindow.showAsDropDown(searchView);
                    }
                    return false;
                }
            });
        });

/*
        // Configure the suggestionsPopup
        PopupWindow popupWindow = new PopupWindow(suggestionsPopup,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                false);
        popupWindow.setOutsideTouchable(true);

        // Show the popup window when the search view is clicked
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && !popupWindow.isShowing()) {
                popupWindow.showAsDropDown(searchView);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search submission
                popupWindow.dismiss();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Show the popup when text changes
                if (!popupWindow.isShowing()) {
                    popupWindow.showAsDropDown(searchView);
                }
                return false;
            }
        }); */

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

    // Metodo fittizio per ottenere i prodotti
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
                        ratedProducts.add(product);
                    }

                    // Ad esempio: aggiorna l'adapter con la nuova lista

                    if (products != null)
                    {
                        products.clear();
                    }
                    products.addAll(ratedProducts);
                    if( productsAdapter != null) {
                        productsAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE", "Errore nel recupero dei prodotti per valutazione", e);
                });

        return products;
    }
}