package com.example.localgems.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localgems.R;
import com.example.localgems.databinding.FragmentHomeBinding;
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

    private List<Product> products ;
    private ProductsAdapter productsAdapter;
    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // ViewModel setup
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Binding setup
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        products = new ArrayList<>();
        productsAdapter = new ProductsAdapter(products);

        // Initialize UI elements
        MaterialToolbar toolbar = binding.toolbar;
        RecyclerView recyclerView = binding.productsRecyclerView;
        FloatingActionButton fab = binding.filterFab;

        // Set up toolbar
        toolbar.setTitle("Prodotti in vendita");
        toolbar.setNavigationOnClickListener(v ->
                Toast.makeText(getContext(), "Navigation click!", Toast.LENGTH_SHORT).show());

        // Set up RecyclerView
        products = getProducts();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(productsAdapter); // Assicurati di avere un Adapter configurato

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

    // Metodo fittizio per ottenere i prodotti
    private List<Product> getProducts() {


       // products.add(new Product("Prodotto 1", 9.99));
        //products.add(new Product("Prodotto 2", 14.99));

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
                    products.clear();
                    products.addAll(ratedProducts);
                    productsAdapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE", "Errore nel recupero dei prodotti per valutazione", e);
                });

        return products;
    }
}