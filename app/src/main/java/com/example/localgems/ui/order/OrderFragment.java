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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

        return view;
    }
}
