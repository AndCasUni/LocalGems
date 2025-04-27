package com.example.localgems.ui.cart;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localgems.R;
import com.example.localgems.model.CartItem;
import com.example.localgems.model.Product;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems = new ArrayList<>();
    private Button buyNowButton;

    public CartFragment() {}

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userId = auth.getCurrentUser().getUid();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerView = view.findViewById(R.id.cart_recycler_view);
        buyNowButton = view.findViewById(R.id.buy_now_button);


        db.collection("users")
                .document(userId)
                .collection("cart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        // Recupera i dati
                        String productId = documentSnapshot.getId();
                        String name = documentSnapshot.getString("name");
                        Double price = documentSnapshot.getDouble("price");
                        String description = documentSnapshot.getString("description");
                        String imageUrl = documentSnapshot.getString("imageUrl");
                        Long quantityLong = documentSnapshot.getLong("quantity");
                        int quantity = (quantityLong != null) ? quantityLong.intValue() : 0;

                        // Costruisci l'oggetto Product
                        Product product = new Product();
                        product.setId(productId);
                        product.setName(name);
                        product.setPrice(price);
                        product.setDescription(description);
                        product.setImage_url(imageUrl);

                        // Costruisci il CartItem
                        CartItem cartItem = new CartItem(product, quantity);

                        // Aggiungi all'array
                        cartItems.add(cartItem);
                    }

                    // Qui hai l'array cartItems pieno!
                    cartAdapter = new CartAdapter(cartItems, getContext(), this::updateTotal);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(cartAdapter);
                    updateTotal();
                })
                .addOnFailureListener(e -> {
                    Log.e("CartLoad", "Errore caricamento carrello: ", e);
                });


        updateTotal(); // inizializza il totale
        buyNowButton.setOnClickListener(v -> {
            db = FirebaseFirestore.getInstance();
            auth = FirebaseAuth.getInstance();
            userId = auth.getCurrentUser().getUid();

            List<Task<Boolean>> availabilityTasksList = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
                DocumentReference productRef = db.collection("products").document(cartItem.getProduct().getId());
                Task<Boolean> task = productRef.get().continueWith(t -> {
                    if (t.isSuccessful()) {
                        Long stock = t.getResult().getLong("stock");
                        if (stock == null) stock = 0L;
                        return stock >= cartItem.getQuantity();
                    } else {
                        throw t.getException();
                    }
                });
                availabilityTasksList.add(task);
            }

            Task<List<Boolean>> availabilityTasks = Tasks.whenAllSuccess(availabilityTasksList);

            availabilityTasks.addOnSuccessListener(results -> {
                boolean allAvailable = true;
                for (Object available : results) {
                    if (!(Boolean) available) {
                        allAvailable = false;
                        break;
                    }
                }

                if (!allAvailable) {
                    Toast.makeText(requireContext(), "Disponibilità insufficiente per alcuni prodotti!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 2. Registra l'acquisto
                Map<String, Object> purchaseData = new HashMap<>();
                purchaseData.put("userId", userId);
                purchaseData.put("timestamp", FieldValue.serverTimestamp());

                List<Map<String, Object>> purchasedProducts = new ArrayList<>();
                double total = 0;

                for (CartItem cartItem : cartItems) {
                    Map<String, Object> p = new HashMap<>();
                    p.put("productId", cartItem.getProduct().getId());
                    p.put("name", cartItem.getProduct().getName());
                    p.put("quantity", cartItem.getQuantity());
                    p.put("price", cartItem.getProduct().getPrice());
                    purchasedProducts.add(p);

                    total += cartItem.getQuantity() * cartItem.getProduct().getPrice();
                }

                purchaseData.put("products", purchasedProducts);
                purchaseData.put("total", total);

                db.collection("purchases")
                        .add(purchaseData)
                        .addOnSuccessListener(documentReference -> {
                            // 3. Aggiorna quantità stock dei prodotti
                            WriteBatch batch = db.batch();
                            for (CartItem cartItem : cartItems) {
                                DocumentReference productRef = db.collection("products").document(cartItem.getProduct().getId());
                                batch.update(productRef, "stock", FieldValue.increment(-cartItem.getQuantity()));
                            }

                            // 4. Svuota carrello
                            DocumentReference userCartRef = db.collection("users").document(userId);
                            batch.update(userCartRef, "cart", new HashMap<>()); // oppure elimina i singoli prodotti se necessario

                            batch.commit()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(requireContext(), "Acquisto completato!", Toast.LENGTH_SHORT).show();
                                        cartItems.clear();
                                        cartAdapter.notifyDataSetChanged();  // aggiorna RecyclerView
                                        updateTotal();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(requireContext(), "Errore nell'aggiornamento prodotti.", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(requireContext(), "Errore durante la registrazione dell'acquisto.", Toast.LENGTH_SHORT).show();
                        });

            }).addOnFailureListener(e -> {
                Toast.makeText(requireContext(), "Errore nel controllo disponibilità.", Toast.LENGTH_SHORT).show();
            });
        });

        return view;
    }

    private void updateTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        String text = String.format("Compra ora (€ %.2f)", total);
        buyNowButton.setText(text);
    }
}
