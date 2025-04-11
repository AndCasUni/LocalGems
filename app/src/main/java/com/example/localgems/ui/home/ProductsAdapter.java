package com.example.localgems.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localgems.R;
import com.example.localgems.model.Product;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    private final List<Product> products;

    // Costruttore
    public ProductsAdapter(List<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflazione del layout dell'elemento
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Collegamento dati con la UI
        Product product = products.get(position);
        holder.nameTextView.setText(product.getName());
        holder.priceTextView.setText(String.format("€ %.2f", product.getPrice()));

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("product_name", product.getName());
            bundle.putDouble("product_price", product.getPrice());

            Navigation.findNavController(v).navigate(R.id.nav_product_details, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    // ViewHolder: definisce gli elementi visivi di ogni item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, priceTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.product_name);
            priceTextView = itemView.findViewById(R.id.product_price);
        }
    }
}