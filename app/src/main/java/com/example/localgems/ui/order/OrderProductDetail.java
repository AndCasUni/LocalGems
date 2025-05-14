package com.example.localgems.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.localgems.R;
import com.example.localgems.model.Review;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class OrderProductDetail extends Fragment {

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail_order, container, false);
        db = FirebaseFirestore.getInstance();

        // Recupera i dati dal bundle
        Bundle args = getArguments();
        if (args != null) {
            String productName = args.getString("productName");
            String productDescription = args.getString("productDescription");
            double productPrice = args.getDouble("productPrice");
            String productImageUrl = args.getString("productImageUrl"); // Aggiunto

            ((TextView) view.findViewById(R.id.product_name)).setText(productName);
            ((TextView) view.findViewById(R.id.product_description)).setText(productDescription);
            ((TextView) view.findViewById(R.id.product_price)).setText(String.format("€ %.2f", productPrice));

            // Carica l'immagine del prodotto
            ImageView productImage = view.findViewById(R.id.product_image);
            Glide.with(this)
                .load(productImageUrl)
                .placeholder(R.drawable.placeholder_product) // Immagine di placeholder
                .error(R.drawable.placeholder_product) // Immagine di fallback in caso di errore
                .into(productImage);
        }

        // --- Gestione Widget Recensione ---
        View reviewWidget = view.findViewById(R.id.review_widget);
        TextView closeReviewButton = view.findViewById(R.id.review_close_button);
        EditText reviewInput = view.findViewById(R.id.review_description);
        RatingBar ratingBar = view.findViewById(R.id.review_rating_bar);
        MaterialButton publishReviewButton = view.findViewById(R.id.review_publish_button);
        MaterialButton openReviewButton = view.findViewById(R.id.review_button);

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
            int rating = (int) ratingBar.getRating();

            if (!reviewText.isEmpty() && rating > 0) {
                Review review = new Review("orderId_placeholder", reviewText, rating, new Date(System.currentTimeMillis()));

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
