package com.example.localgems.ui.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.localgems.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

public class ProductDetailFragment extends Fragment {

    private ImageView productImage;
    private TextView productName;
    private TextView productDescription;
    private TextView productPrice;
    private TextView quantityText;
    private Button addToCartButton;
    private Button quantityPlusButton;
    private Button quantityMinusButton;

    private int quantity = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_product_detail, container, false);

        // Inizializzazione elementi UI
        productImage = root.findViewById(R.id.product_image);
        productName = root.findViewById(R.id.product_name);
        productDescription = root.findViewById(R.id.product_description);
        productPrice = root.findViewById(R.id.product_price);
        quantityText = root.findViewById(R.id.quantity_text);
        addToCartButton = root.findViewById(R.id.add_to_cart_button);
        quantityPlusButton = root.findViewById(R.id.quantity_plus_button);
        quantityMinusButton = root.findViewById(R.id.quantity_minus_button);

        // Logica della quantità
        quantityMinusButton.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                quantityText.setText(String.valueOf(quantity));
            }
        });

        quantityPlusButton.setOnClickListener(v -> {
            quantity++;
            quantityText.setText(String.valueOf(quantity));
        });

        // Pulsante aggiungi al carrello
        addToCartButton.setOnClickListener(v ->
                Toast.makeText(getContext(), "Prodotto aggiunto al carrello!", Toast.LENGTH_SHORT).show());

        // Dummy Data: Popola la UI (puoi sostituire con dati reali)
        productName.setText("Esempio di prodotto");
        productDescription.setText("Una descrizione dettagliata del prodotto.");
        productPrice.setText("€ 9.99");

        return root;
    }
}