package com.example.localgems.ui.orders;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localgems.R;
import com.example.localgems.model.Purchase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class OrdersFragment extends Fragment {

    private RecyclerView recyclerView;
    private PurchaseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        recyclerView = view.findViewById(R.id.recycler_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        adapter = new PurchaseAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> purchaseIds = (List<String>) documentSnapshot.get("purchases");
                        if (purchaseIds != null && !purchaseIds.isEmpty()) {
                            // Prendi solo gli ultimi 5 ID in ordine inverso
                            List<String> latestIds = purchaseIds.subList(Math.max(purchaseIds.size() - 5, 0), purchaseIds.size());
                            Collections.reverse(latestIds); // Ordina dal più recente
                            Log.e("FIREBASE", "trovati : " + latestIds.size());

                            fetchPurchases(latestIds);
                        } else {
                            // Usa ordini finti se non ci sono ordini reali
                            useFakeOrders();
                        }
                    } else {
                        // Usa ordini finti se il documento non esiste
                        useFakeOrders();
                    }
                });

        return view;
    }

    private void fetchPurchases(List<String> ids) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Purchase> purchases = new ArrayList<>();

        for (String id : ids) {
            db.collection("purchases").document(id)
                    .get()
                    .addOnSuccessListener(doc -> {
                        Purchase p = doc.toObject(Purchase.class);
                        if (p != null) {
                            p.setId(doc.getId());  // aggiungi ID del documento
                            purchases.add(p);
                            Log.d("PurchaseAdapter", "ID: " + p.getId() + ", Date: " + p.getTimestamp() + ", Total: " + p.getTotal_price());

                        }
                        if (purchases.size() == ids.size()) {
                            // tutti caricati → aggiorna adapter
                            adapter = new PurchaseAdapter(purchases);
                            recyclerView.setAdapter(adapter);
                        }
                    });
        }
    }

    private void useFakeOrders() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            List<Purchase> fakePurchases = Arrays.asList(
                    new Purchase("fake1", dateFormat.parse("2023-10-01"), 29.99),
                    new Purchase("fake2", dateFormat.parse("2023-09-25"), 49.99),
                    new Purchase("fake3", dateFormat.parse("2023-09-15"), 19.99)
            );

            adapter = new PurchaseAdapter(fakePurchases);
            recyclerView.setAdapter(adapter);
        } catch (ParseException e) {
            Log.e("FIREBASE", "Errore nella creazione delle date fittizie", e);
        }
    }
}
