package com.example.localgems.ui.home;

import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

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
        FloatingActionButton fab = binding.filterFab;

        // Set up toolbar
        toolbar.setTitle("Prodotti in vendita");
        toolbar.setNavigationOnClickListener(v ->
                Toast.makeText(getContext(), "Navigation click!", Toast.LENGTH_SHORT).show());

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new ProductsAdapter(getProducts())); // Assicurati di avere un Adapter configurato

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
        // Simula una lista di prodotti (aggiungi la classe Product nel tuo progetto)
        List<Product> products = new ArrayList<>();
        products.add(new Product("Prodotto 1", 9.99));
        products.add(new Product("Prodotto 2", 14.99));
        return products;
    }
}