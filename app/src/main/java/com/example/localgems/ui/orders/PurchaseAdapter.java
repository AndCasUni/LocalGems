package com.example.localgems.ui.orders;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation;

import com.example.localgems.R;
import com.example.localgems.model.Purchase;


import java.text.SimpleDateFormat;
import java.util.Locale;

import java.util.ArrayList;

import java.util.List;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.PurchaseViewHolder> {

    private final List<Purchase> purchases;

    public PurchaseAdapter(List<Purchase> purchases) {
        this.purchases = (purchases != null) ? purchases : new ArrayList<Purchase>();
    }

    @NonNull
    @Override
    public PurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_purchase, parent, false);
        return new PurchaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseViewHolder holder, int position) {
        Purchase purchase = purchases.get(position);
        holder.bind(purchase);
    }

    @Override
    public int getItemCount() {
        return purchases.size();
    }

    static class PurchaseViewHolder extends RecyclerView.ViewHolder {
        private final TextView orderId;
        private final TextView orderDate;
        private final TextView orderTotal;

        public PurchaseViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_id);
            orderDate = itemView.findViewById(R.id.order_date);
            orderTotal = itemView.findViewById(R.id.order_total);
        }

        public void bind(Purchase purchase) {
            orderId.setText(itemView.getContext().getString(R.string.order_id, purchase.getId().substring(5)));

            // Formatta il timestamp in formato "Weekday DD/MM/YYYY" in spagnolo
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd/MM/yyyy", new Locale("es", "ES"));
            String formattedDate = dateFormat.format(purchase.getTimestamp());
            orderDate.setText(itemView.getContext().getString(R.string.order_date, formattedDate));

            orderTotal.setText(itemView.getContext().getString(R.string.order_total, String.format(Locale.getDefault(), "%.2f", purchase.getTotal_price())));

            itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("orderId", purchase.getId());
                Navigation.findNavController(v).navigate(R.id.action_nav_orders_to_nav_order, bundle);
            });
        }
    }
}
