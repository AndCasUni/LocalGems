package com.example.localgems.ui.order;

import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

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
                            ((TextView) view.findViewById(R.id.order_id)).setText("Ordine #" + purchase.getId().substring(5));
                            ((TextView) view.findViewById(R.id.order_date)).setText("Data: " + purchase.getTimestamp());
                            ((TextView) view.findViewById(R.id.order_total)).setText(String.format("Totale: €%.2f", purchase.getTotal_price()));
                        }
                    });
        }

        // RecyclerView
        recyclerView = view.findViewById(R.id.recycler_order_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        // Esempi artificiali di prodotti
        List<Product> products = Arrays.asList(
                new Product("Prodotto 1", "Descrizione prodotto 1", 9.99),
                new Product("Prodotto 2", "Descrizione prodotto 2", 19.99),
                new Product("Prodotto 3", "Descrizione prodotto 3", 29.99)
        );

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
            String reviewText = reviewInput.getText().toString().trim();
            Long rating = (long) ratingBar.getRating();

            if (!reviewText.isEmpty() && rating > 0 && orderId != null) {
                Review review = new Review(orderId, reviewText, rating, System.currentTimeMillis());

                db.collection("reviews")
                        .add(review)
                        .addOnSuccessListener(docRef -> {
                            Toast.makeText(getContext(), "Recensione inviata!", Toast.LENGTH_SHORT).show();
                            reviewWidget.setVisibility(View.GONE);
                            reviewInput.setText("");
                            ratingBar.setRating(0);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Errore durante l'invio", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(getContext(), "Inserisci testo e una valutazione", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
