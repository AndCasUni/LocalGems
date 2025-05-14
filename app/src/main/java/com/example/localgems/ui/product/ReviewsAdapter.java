package com.example.localgems.ui.product;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localgems.R;
import com.example.localgems.model.Review;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private final List<Review> reviews;
    private final Context context;

    // Costruttore
    public ReviewsAdapter(List<Review> reviewList, Context context) {
        this.context = context;
        this.reviews = (reviewList != null) ? reviewList : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviews.get(position);

        // Recupera autore dal campo "user" (user ID)
        String authorID = review.getUtente();
        Log.e("USER_INFO", "UTENTE " + authorID);

        if (authorID != null && !authorID.isEmpty()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(authorID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("firstName");
                            String surname = documentSnapshot.getString("lastName");
                            holder.authorTextView.setText(name + " " + surname);
                        } else {
                            holder.authorTextView.setText("Utente sconosciuto");
                        }
                    })
                    .addOnFailureListener(e -> {
                        holder.authorTextView.setText("Errore caricamento autore");
                        Log.e("USER_INFO", "Errore nel recupero utente", e);
                    });
        } else {
            holder.authorTextView.setText("Utente non disponibile");
        }

        // Format data
        if (review.getOra() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            holder.date.setText(sdf.format(review.getOra()));
        } else {
            holder.date.setText("Data non disponibile");
        }

        // Stelle
        int stars = review.getValutazione();
        Log.e("USER_INFO", "STELLE " + stars);

        StringBuilder starsString = new StringBuilder();
        for (int i = 0; i < stars; i++) starsString.append("★");
        for (int i = stars; i < 5; i++) starsString.append("☆");
        holder.starsTextView.setText(starsString.toString());

        // Contenuto recensione
        holder.contentTextView.setText(review.getDescrizione());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    // ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView authorTextView, starsTextView, contentTextView, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.review_author);
            starsTextView = itemView.findViewById(R.id.review_stars);
            contentTextView = itemView.findViewById(R.id.review_content);
            date = itemView.findViewById(R.id.review_date);
        }
    }
}
