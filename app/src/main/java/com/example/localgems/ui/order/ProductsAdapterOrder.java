package com.example.localgems.ui.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localgems.R;
import com.example.localgems.model.Product;

import java.util.List;

public class ProductsAdapterOrder extends RecyclerView.Adapter<ProductsAdapterOrder.ProductViewHolder> {

    private final List<Product> products;

    public ProductsAdapterOrder(List<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);  // Usa layout con i pulsanti
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView productName;
        private final TextView productDescription;
        private final TextView productPrice;
        private final TextView sellerName;
        private final Button btnDecrease, btnIncrease, btnDelete;
        private final TextView quantityText;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productDescription = itemView.findViewById(R.id.product_description);
            productPrice = itemView.findViewById(R.id.product_price);
            sellerName = itemView.findViewById(R.id.item_product_seller_name);

            // Pulsanti da nascondere
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            quantityText = itemView.findViewById(R.id.quantity_text);
        }

        public void bind(Product product) {
            productName.setText(product.getName());
            productDescription.setText(product.getDescription());
            productPrice.setText(String.format("€ %.2f", product.getPrice()));
            sellerName.setText("Venditore Sconosciuto"); // oppure product.getSeller()

            // Placeholder immagine
            productImage.setImageResource(R.drawable.placeholder_product);

            // Nascondi i pulsanti
            btnDecrease.setVisibility(View.GONE);
            btnIncrease.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            quantityText.setVisibility(View.GONE);
        }
    }
}
