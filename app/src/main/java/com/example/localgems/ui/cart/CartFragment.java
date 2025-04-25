package com.example.localgems.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localgems.R;
import com.example.localgems.model.CartItem;
import com.example.localgems.model.Product;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems = new ArrayList<>();
    private Button buyNowButton;

    public CartFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerView = view.findViewById(R.id.cart_recycler_view);
        buyNowButton = view.findViewById(R.id.buy_now_button);

        // Mock prodotti
        cartItems.add(new CartItem(new Product("Formaggio di capra", 12.50), 1));
        cartItems.add(new CartItem(new Product("Vino locale", 8.99), 2));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(cartItems, getContext(), this::updateTotal);
        recyclerView.setAdapter(cartAdapter);

        updateTotal(); // inizializza il totale

        return view;
    }

    private void updateTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        String text = String.format("Compra ora (€ %.2f)", total);
        buyNowButton.setText(text);
    }
}
