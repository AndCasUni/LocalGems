package com.example.localgems.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localgems.R;
import com.example.localgems.model.Product;
import com.google.firebase.inappmessaging.model.Button;

import com.bumptech.glide.Glide;


import java.util.ArrayList;
import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    private final List<Product> products;
    private Context context;

    // Costruttore
        public ProductsAdapter(List<Product> products) {
        this.products = products;
    }

    public ProductsAdapter(List<Product> productList, Context context) {
        this.context = context;
        this.products = (productList != null) ? productList : new ArrayList<>();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflazione del layout dell'elemento
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }


    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        Log.d("ADAPTER", "Bind del prodotto: " + product.getName());

        holder.nameTextView.setText(product.getName());
        holder.descriptionTextView.setText(product.getDescription());
        holder.priceTextView.setText(String.format("€ %.2f", product.getPrice()));

        // Caricamento immagine da URL
        Glide.with(holder.itemView.getContext())
                .load(product.getImage_url())
                .placeholder(R.drawable.placeholder_product)
                .into(holder.imageView);
        Log.d("URL", "URL: " + product.getImage_url());


        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            Log.d("URL", "URL DOPO: " + product.getImage_url());
            bundle.putString("productId", product.getId());  // Passi solo l'ID del prodotto
            bundle.putString("imageURL", product.getImage_url());
            Navigation.findNavController(v).navigate(R.id.nav_product_details, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return (products != null) ? products.size() : 0;
    }

    // ViewHolder: definisce gli elementi visivi di ogni item
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, descriptionTextView, priceTextView;

        ImageView imageView;
        Button addToCartButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.product_name);
            descriptionTextView = itemView.findViewById(R.id.product_description);
            priceTextView = itemView.findViewById(R.id.product_price);
            imageView = itemView.findViewById(R.id.product_image);
            //addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        }


    }
}