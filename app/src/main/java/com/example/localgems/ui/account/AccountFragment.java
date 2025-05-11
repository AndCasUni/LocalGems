package com.example.localgems.ui.account;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import com.example.localgems.LoginActivity;
import com.example.localgems.R;
import com.example.localgems.model.User;
import com.example.localgems.ui.orders.OrdersFragment;
import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {

    private TextView emailText, nameText, lastNameText, birthDateText;
    private Button seeOrdersButton, logoutButton;

    private User currentUser;

    public AccountFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Initialize views
        emailText = view.findViewById(R.id.account_email);
        nameText = view.findViewById(R.id.account_first_name);
        lastNameText = view.findViewById(R.id.account_last_name);
        birthDateText = view.findViewById(R.id.account_birth_date);
        seeOrdersButton = view.findViewById(R.id.account_see_orders_button);
        logoutButton = view.findViewById(R.id.account_logout_button);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentUser = documentSnapshot.toObject(User.class);

                        if (currentUser != null) {
                            emailText.setText(currentUser.getEmail());
                            nameText.setText(currentUser.getFirstName());
                            lastNameText.setText(currentUser.getLastName());
                            birthDateText.setText(currentUser.getBirthDate());
                        }
                    } else {
                        Toast.makeText(getContext(), "Utente non trovato", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Errore nel recupero dati utente: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

        // Handle password change button click
        seeOrdersButton.setOnClickListener(v -> showOrdersFragment());

        // Handle logout button click
        logoutButton.setOnClickListener(v -> doLogout());

        return view;
    }

    private void showOrdersFragment() {
        Navigation.findNavController(getView())
                .navigate(R.id.nav_orders);
    }

    private void doLogout() {
        // Show confirmation dialog
        new AlertDialog.Builder(getContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Perform logout action
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                    // Navigate to login screen or main screen
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    requireActivity().finish();

                })
                .setNegativeButton("No", null)
                .show();
    }
}