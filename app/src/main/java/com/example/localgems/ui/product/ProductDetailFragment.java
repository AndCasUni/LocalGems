package com.example.localgems.ui.product;

import android.os.Bundle;
import android.util.Log;
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

import com.example.localgems.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProductDetailFragment extends Fragment {

    private String productId;
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

        addToCartButton.setOnClickListener(v -> {
            // Prendi l'utente loggato
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(getContext(), "Devi effettuare il login per aggiungere al carrello.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Prepara i dati da salvare
            String userId = user.getUid();
            Bundle bundle = getArguments();
            String ID = null;
            if (bundle != null) {
                ID = bundle.getString("productId");
            }
            String productId = ID ; // <-- supponendo che tu abbia il prodotto caricato
            String name = productName.getText().toString();
            String priceString = productPrice.getText().toString().replace("€", "").trim(); // Togli l'euro se presente
            double price = Double.parseDouble(priceString);

            int quantity = Integer.parseInt(quantityText.getText().toString());

            // Crea la mappa dei dati da inserire
            Map<String, Object> cartItem = new HashMap<>();
            cartItem.put("name", name);
            cartItem.put("quantity", quantity);
            cartItem.put("price", price);

            // Salva nel database
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .document(userId)
                    .collection("cart")
                    .document(productId)
                    .set(cartItem)
                    .addOnSuccessListener(documentReference ->
                            Toast.makeText(getContext(), "Articolo aggiunto al carrello!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Errore nell'aggiunta al carrello.", Toast.LENGTH_SHORT).show());
        });
        String productId = getArguments() != null ? getArguments().getString("productId") : null;

        if (productId != null) {
            FirebaseFirestore.getInstance()
                    .collection("products")
                    .document(productId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Product product = documentSnapshot.toObject(Product.class);
                            if (product != null) {
                                // Mostra i dati nella UI
                                TextView name = root.findViewById(R.id.product_name);
                                TextView description = root.findViewById(R.id.product_description);
                                TextView price = root.findViewById(R.id.product_price);

                                name.setText(product.getName());
                                description.setText(product.getDescription());
                                price.setText("€ " + product.getPrice());

                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e("DETAILS", "Errore nel recupero del prodotto", e));
        }



        return root;
    }
}