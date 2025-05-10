package com.example.localgems.ui.cart;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.localgems.R;
import com.example.localgems.model.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartItemViewHolder> {

    private List<CartItem> cartItemList;
    private Context context;
    private Runnable onCartChanged;

    public CartAdapter(List<CartItem> cartItemList, Context context, Runnable onCartChanged) {
        this.cartItemList = cartItemList;
        this.context = context;
        this.onCartChanged = onCartChanged;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem item = cartItemList.get(position);

        String name = item.getProduct().getName();
        double unitPrice = item.getProduct().getPrice();
        int quantity = item.getQuantity();
        double totalPrice = unitPrice * quantity;
        holder.productName.setText(name);
        holder.rating.setText("(" + item.getProduct().getRating() + "★)");
        holder.productPrice.setText(String.format("€ %.2f x%d = € %.2f", unitPrice, quantity, totalPrice));
        holder.quantityText.setText(String.valueOf(quantity));



        Glide.with(context)
                .load(item.getProduct().getImage_url())
                .placeholder(R.drawable.placeholder_product) // immagine di default mentre carica
                .error(R.drawable.placeholder_product)       // immagine se errore
                .into(holder.productImage);


        holder.btnIncrease.setOnClickListener(v -> {
            item.increaseQuantity();
            notifyItemChanged(position);
            onCartChanged.run();
        });

        holder.btnDecrease.setOnClickListener(v -> {
            item.decreaseQuantity();
            notifyItemChanged(position);
            onCartChanged.run();
        });

        holder.btnDelete.setOnClickListener(v -> {
            // Rimuovi visivamente dalla lista
            cartItemList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItemList.size());
            onCartChanged.run();

            // Rimuovi dal database
            String productId = item.getProduct().getId();
            String userId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();

            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("cart")
                    .document(productId)
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d("CartAdapter", "Prodotto rimosso dal carrello nel DB: " + productId))
                    .addOnFailureListener(e -> Log.e("CartAdapter", "Errore nella rimozione dal carrello: " + productId, e));
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }
}
