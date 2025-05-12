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


import java.util.ArrayList;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private final List<Review> reviews;
    private Context context;

    // Costruttore
    public ReviewsAdapter(List<Review> products) {
        this.reviews = products;
    }

    public ReviewsAdapter(List<Review> reviewList, Context context) {
        this.context = context;
        this.reviews = (reviewList != null) ? reviewList : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflazione del layout dell'elemento
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviews.get(position);
        Log.d("ADAPTER", "Bind della recensione: " + review.getDescrizione());

        // Dobbiamo prendere il nome dell'autore dalla raccolta degli utenti, associando l'ID
        String authorID = review.getUtente();
        // Qui dovresti implementare la logica per ottenere il nome dell'autore dall'ID
        // usa firebase
        // chatty fai qualcosa non voglio usare un esempio voglio proprio scrivere il codice



        // Impostazione della valutazione
        String starsString = "";
        for (int i = 0; i < review.getValutazione(); i++) {
            starsString += "★";
        }
        for (int i = Math.toIntExact(review.getValutazione()); i < 5; i++) {
            starsString += "☆";
        }

        holder.starsTextView.setText(starsString);

        holder.contentTextView.setText(review.getDescrizione());
    }

    @Override
    public int getItemCount() {
        return (reviews != null) ? reviews.size() : 0;
    }

    // ViewHolder: definisce gli elementi visivi di ogni item
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView authorTextView, starsTextView, contentTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.review_author);
            starsTextView = itemView.findViewById(R.id.review_stars);
            contentTextView = itemView.findViewById(R.id.review_content);
        }
    }
}
