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
import com.google.firebase.firestore.CollectionReference;
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
                        String imageUrl = documentSnapshot.getString("image_url");
                        Long quantityLong = documentSnapshot.getLong("quantity");
                        double rating = documentSnapshot.getDouble("rating");
                        int quantity = (quantityLong != null) ? quantityLong.intValue() : 0;

                        // Costruisci l'oggetto Product
                        Product product = new Product();
                        product.setId(productId);
                        product.setName(name);
                        product.setPrice(price);
                        product.setDescription(description);
                        product.setImage_url(imageUrl);
                        product.setRating(rating);

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

            CollectionReference cartRef = db.collection("users").document(userId).collection("cart");
            CollectionReference productsRef = db.collection("products");
            CollectionReference purchasesRef = db.collection("purchases");
            DocumentReference userRef = db.collection("users").document(userId);


// 1. Leggi tutti gli articoli nel carrello
            cartRef.get().addOnSuccessListener(cartSnapshot -> {
                if (cartSnapshot.isEmpty()) {
                    Toast.makeText(requireContext(), "Carrello vuoto!", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Map<String, Object>> purchasedProducts = new ArrayList<>();
                WriteBatch batch = db.batch();
                boolean[] hasInsufficientStock = {false};

                double[] totalPrice = {0.0}; // per mantenere somma totale

                for (DocumentSnapshot cartItemDoc : cartSnapshot) {
                    String productId = cartItemDoc.getId();
                    int quantityInCart = cartItemDoc.getLong("quantity").intValue();

                    DocumentReference productDocRef = productsRef.document(productId);

                    // Verifica disponibilità
                    productDocRef.get().addOnSuccessListener(productSnapshot -> {
                        if (productSnapshot.exists()) {
                            int stockAvailable = productSnapshot.getLong("stock").intValue();

                            if (stockAvailable < quantityInCart) {
                                hasInsufficientStock[0] = true;
                                Toast.makeText(requireContext(), "Prodotto " + productSnapshot.getString("name") + " non disponibile a sufficienza.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // 2. Se tutto ok, prepara i dati per l'acquisto
                            Map<String, Object> purchasedProduct = new HashMap<>();
                            purchasedProduct.put("product_id", productId);
                            purchasedProduct.put("name", productSnapshot.getString("name"));
                            purchasedProduct.put("price", productSnapshot.getDouble("price"));
                            purchasedProduct.put("quantity", quantityInCart);
                            purchasedProduct.put("image_url", productSnapshot.getString("image_url"));

                            purchasedProducts.add(purchasedProduct);

                            double price = productSnapshot.getDouble("price");
                            totalPrice[0] += price * quantityInCart;

                            // 3. Scala la quantità disponibile nei prodotti
                            batch.update(productDocRef, "stock", stockAvailable - quantityInCart);

                            // 4. Cancella il prodotto dal carrello
                            batch.delete(cartRef.document(productId));

                            // --- Alla fine di tutti i prodotti ---
                            if (purchasedProducts.size() == cartSnapshot.size()) {
                                if (hasInsufficientStock[0]) {
                                    return; // Se anche solo un prodotto era insufficiente, fermati
                                }

                                // 5. Crea ID acquisto incrementale
                                db.collection("purchases").get().addOnSuccessListener(purchaseSnapshot -> {
                                    int nextPurchaseId = purchaseSnapshot.size() + 1;
                                    String purchaseId = String.format("purch%03d", nextPurchaseId);

                                    // 6. Crea l'acquisto
                                    DocumentReference purchaseDocRef = purchasesRef.document(purchaseId);

// 1. Dati generali dell'acquisto
                                    Map<String, Object> purchaseData = new HashMap<>();
                                    purchaseData.put("user_id", userId);
                                    purchaseData.put("total_price", Math.round(totalPrice[0] * 100.0) / 100.0);
                                    purchaseData.put("timestamp", FieldValue.serverTimestamp());

                                    batch.set(purchaseDocRef, purchaseData);

// 2. Aggiungi ogni prodotto alla sottocollezione productsPurchased
                                    for (Map<String, Object> purchased : purchasedProducts) {
                                        String prodId = (String) purchased.get("product_id");

                                        Map<String, Object> productData = new HashMap<>();
                                        productData.put("name", purchased.get("name"));
                                        productData.put("price", purchased.get("price"));
                                        productData.put("quantity", purchased.get("quantity"));
                                        productData.put("image_url", purchased.get("image_url"));
                                        productData.put("reviewed", false);

                                        DocumentReference productPurchasedRef = purchaseDocRef.collection("productsPurchased").document(prodId);
                                        batch.set(productPurchasedRef, productData);
                                    }
                                    batch.update(userRef, "purchases", FieldValue.arrayUnion(purchaseId));

                                    // 8. Commit finale
                                    batch.commit()
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(requireContext(), "Acquisto completato!", Toast.LENGTH_SHORT).show();
                                                cartItems.clear();
                                                cartAdapter.notifyDataSetChanged();
                                                updateTotal();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(requireContext(), "Errore nel completare l'acquisto.", Toast.LENGTH_SHORT).show();
                                            });

                                }).addOnFailureListener(e -> {
                                    Toast.makeText(requireContext(), "Errore nella creazione ID acquisto.", Toast.LENGTH_SHORT).show();
                                });
                            }

                        } else {
                            Toast.makeText(requireContext(), "Prodotto non trovato.", Toast.LENGTH_SHORT).show();
                        }

                    }).addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Errore nel recuperare prodotto.", Toast.LENGTH_SHORT).show();
                    });

                }
            }).addOnFailureListener(e -> {
                Toast.makeText(requireContext(), "Errore nel recuperare carrello.", Toast.LENGTH_SHORT).show();
            });
        });

        return view;
    }

    private void updateTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        String text = getString(R.string.buy_now_button, String.format("%.2f", total));
        buyNowButton.setText(text);
    }
}
