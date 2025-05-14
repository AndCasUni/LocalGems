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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.localgems.R;

import com.example.localgems.model.Product;
import com.example.localgems.model.Review;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private RecyclerView reviewsRecyclerView;
    private ReviewsAdapter reviewsAdapter;
    private List<Review> reviewsList = new ArrayList<>();

    private int quantity = 1;
    double rating = 0;

    String ID = null;
    String imageURL = null;

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
        addToCartButton = root.findViewById(R.id.review_button);
        quantityPlusButton = root.findViewById(R.id.quantity_plus_button);
        quantityMinusButton = root.findViewById(R.id.quantity_minus_button);
        RecyclerView recyclerView = root.findViewById(R.id.reviews_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewsAdapter = new ReviewsAdapter(reviewsList, getContext());
        recyclerView.setAdapter(reviewsAdapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userId = user.getUid();
        Bundle bundle = getArguments();
        if (bundle != null) {
            ID = bundle.getString("productId");
            imageURL = bundle.getString("imageURL");
            rating = bundle.getDouble("rating");
        }

        Glide.with(requireContext())
                .load(imageURL)
                .placeholder(R.drawable.placeholder_product) // Un'icona mentre carica
                .error(R.drawable.placeholder_product)       // Se fallisce
                .into(productImage);

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
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users")
                    .document(userId)
                    .collection("cart")
                    .document(ID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Se esiste già
                            Toast.makeText(getContext(), "Prodotto già presente nel carrello", Toast.LENGTH_SHORT).show();
                        } else {
                            // Altrimenti, aggiungi
                            String name = productName.getText().toString();
                            String priceString = productPrice.getText().toString().replace("€", "").trim();
                            double price = Double.parseDouble(priceString);
                            String description = productDescription.getText().toString();
                            String imageUrl = imageURL;
                            int quantity = Integer.parseInt(quantityText.getText().toString());

                            Map<String, Object> cartItem = new HashMap<>();
                            cartItem.put("id", ID);
                            cartItem.put("name", name);
                            cartItem.put("rating", rating);
                            cartItem.put("quantity", quantity);
                            cartItem.put("price", price);
                            cartItem.put("description", description);
                            cartItem.put("image_url", imageUrl);

                            db.collection("users")
                                    .document(userId)
                                    .collection("cart")
                                    .document(ID)
                                    .set(cartItem)
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(getContext(), "Articolo aggiunto al carrello!", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(getContext(), "Errore nell'aggiunta al carrello.", Toast.LENGTH_SHORT).show());
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Errore nel controllo del carrello.", Toast.LENGTH_SHORT).show());
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

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("products")
                    .document(productId)
                    .collection("reviews")
                    .orderBy("descrizione", Query.Direction.DESCENDING) // Ordine alfabetico inverso
                    .limit(5) // Solo le ultime 5
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        reviewsList.clear();

                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Review review = new Review();

                            review.setDescrizione(doc.getString("descrizione"));
                            review.setUtente(doc.getString("utente"));

                            Long valuationLong = doc.getLong("valutazione");
                            int valuation = (valuationLong != null) ? valuationLong.intValue() : 0;
                            review.setValutazione(valuation);

                            Timestamp timestamp = doc.getTimestamp("ora");
                            if (timestamp != null) {
                                review.setOra(timestamp.toDate());
                            }

                            reviewsList.add(review);

                            Log.d("REVIEW_DEBUG", "Recuperata: " +
                                    review.getDescrizione() + ", " +
                                    review.getUtente() + ", " +
                                    review.getValutazione() + ", " +
                                    review.getOra());
                        }

                        reviewsAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> Log.e("DETAILS", "Errore nel recupero delle recensioni", e));

        }

        return root;
    }
}
