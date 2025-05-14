package com.example.localgems.ui.order;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localgems.R;
import com.example.localgems.model.Product;
import com.example.localgems.model.Purchase;
import com.example.localgems.model.Review;
import com.google.android.material.button.MaterialButton;


import java.text.SimpleDateFormat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductsAdapterOrder adapter;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        db = FirebaseFirestore.getInstance();


        // Recupera l'ID dell'ordine passato come argomento
        String orderId = getArguments() != null ? getArguments().getString("orderId") : null;

        if (orderId != null) {
            db.collection("purchases").document(orderId)
                    .get()
                    .addOnSuccessListener(doc -> {
                        Purchase purchase = doc.toObject(Purchase.class);
                        if (purchase != null) {
                            purchase.setId(doc.getId());
                            ((TextView) view.findViewById(R.id.order_id)).setText(getString(R.string.order_id, purchase.getId().substring(5)));

                            // Formatta il timestamp in formato "Weekday DD/MM/YYYY" in spagnolo
                            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd/MM/yyyy", new Locale("es", "ES"));
                            String formattedDate = dateFormat.format(purchase.getTimestamp());
                            ((TextView) view.findViewById(R.id.order_date)).setText(getString(R.string.order_date, formattedDate));

                            ((TextView) view.findViewById(R.id.order_total)).setText(getString(R.string.order_total, String.format(Locale.getDefault(), "%.2f", purchase.getTotal_price())));

                            // Recupera i prodotti dalla sottocollezione productsPurchased
                            db.collection("purchases").document(orderId).collection("productsPurchased")
                                    .get()
                                    .addOnSuccessListener(querySnapshot -> {
                                        List<Product> products = new ArrayList<>();
                                        for (DocumentSnapshot productDoc : querySnapshot) {
                                            Product product = new Product();
                                            product.setName(productDoc.getString("name"));
                                            product.setPrice(productDoc.getDouble("price"));
                                            product.setDescription(""); // Descrizione non presente
                                            product.setImage_url(productDoc.getString("image_url"));
                                            product.setQuantity(productDoc.getLong("quantity").intValue());
                                            products.add(product);
                                        }

                                        // Imposta l'adapter con i prodotti recuperati
                                        adapter = new ProductsAdapterOrder(products);
                                        recyclerView.setAdapter(adapter);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Errore nel recupero dei prodotti.", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    });
        }

        // RecyclerView
        recyclerView = view.findViewById(R.id.recycler_order_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);


        List<Product> products = new ArrayList<>();
        adapter = new ProductsAdapterOrder(products);
        recyclerView.setAdapter(adapter);

        db.collection("purchases")
                .document(orderId)
                .collection("productsPurchased")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String productId = doc.getId(); // Ogni documento rappresenta un prodotto acquistato

                        db.collection("products")
                                .document(productId)
                                .get()
                                .addOnSuccessListener(productDoc -> {
                                    Product product = productDoc.toObject(Product.class);
                                    if (product != null) {
                                        product.setId(productDoc.getId());
                                        products.add(product);
                                        adapter.notifyDataSetChanged(); // aggiorna ogni volta
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ORDER_FRAGMENT", "Errore nel recupero dei prodotti acquistati", e);
                });

        adapter = new ProductsAdapterOrder(products); // Nascondi i pulsanti
        recyclerView.setAdapter(adapter);

        // --- Gestione Widget Recensione ---
        View reviewWidget = view.findViewById(R.id.review_widget);
        TextView closeReviewButton = view.findViewById(R.id.review_close_button);
        EditText reviewInput = view.findViewById(R.id.review_description);
        RatingBar ratingBar = view.findViewById(R.id.review_rating_bar);
        MaterialButton publishReviewButton = view.findViewById(R.id.review_publish_button);
        MaterialButton openReviewButton = view.findViewById(R.id.order_review_button);

        // Mostra il widget recensione
        openReviewButton.setOnClickListener(v -> {
            reviewWidget.setVisibility(View.VISIBLE);
        });

        // Chiudi il widget recensione
        closeReviewButton.setOnClickListener(v -> {
            reviewWidget.setVisibility(View.GONE);
            reviewInput.setText("");
            ratingBar.setRating(0);
        });

        // Pubblica recensione
        publishReviewButton.setOnClickListener(v -> {

            Log.d("REVIEW", "Pulsante pubblica premuto!");

            String reviewText = reviewInput.getText().toString().trim();
            int ratingValue = (int) ratingBar.getRating();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Se usi Auth


            if (!reviewText.isEmpty() && ratingValue > 0 && orderId != null) {
                db.collection("purchases")
                        .document(orderId)
                        .collection("productsPurchased")
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            for (DocumentSnapshot doc : querySnapshot) {
                                String productId = doc.getId(); // se il documentId è l'id del prodotto
                                Review review = new Review(reviewText, userId, ratingValue,new Date());

                                db.collection("products")
                                        .document(productId)
                                        .collection("reviews")
                                        .add(review)
                                        .addOnSuccessListener(reviewDoc -> {
                                            // aggiornamento rating
                                            db.collection("products")
                                                    .document(productId)
                                                    .collection("reviews")
                                                    .get()
                                                    .addOnSuccessListener(snapshot -> {
                                                        int totalRating = 0;
                                                        int count = 0;

                                                        for (DocumentSnapshot rDoc : snapshot.getDocuments()) {
                                                            Long r = rDoc.getLong("valutazione");
                                                            if (r != null) {
                                                                totalRating += r.intValue();
                                                                count++;
                                                            }
                                                        }

                                                        if (count > 0) {
                                                            double average = Math.round(((double) totalRating / count) * 100.0) / 100.0;
                                                            db.collection("products")
                                                                    .document(productId)
                                                                    .update("rating", average);
                                                        }
                                                    });
                                        });
                            }

                            Toast.makeText(getContext(), "Recensione inviata!", Toast.LENGTH_SHORT).show();
                            reviewWidget.setVisibility(View.GONE);
                            reviewInput.setText("");
                            ratingBar.setRating(0);
                        });

            } else {
                Toast.makeText(getContext(), "Inserisci testo e una valutazione", Toast.LENGTH_SHORT).show();
            }

        });

        return view;
    }
}
