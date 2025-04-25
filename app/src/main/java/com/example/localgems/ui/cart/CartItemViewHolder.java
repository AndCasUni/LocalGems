package com.example.localgems.ui.cart;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localgems.R;

public class CartItemViewHolder extends RecyclerView.ViewHolder {

    public ImageView productImage;
    public TextView productName, productPrice, quantityText;
    public Button btnDecrease, btnIncrease, btnDelete;

    public CartItemViewHolder(@NonNull View itemView) {
        super(itemView);

        productImage = itemView.findViewById(R.id.product_image);
        productName = itemView.findViewById(R.id.product_name);
        productPrice = itemView.findViewById(R.id.product_price);
        quantityText = itemView.findViewById(R.id.quantity_text);
        btnDecrease = itemView.findViewById(R.id.btn_decrease);
        btnIncrease = itemView.findViewById(R.id.btn_increase);
        btnDelete = itemView.findViewById(R.id.btn_delete);
    }
}
