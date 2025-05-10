package com.example.localgems.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.localgems.MainActivity;
import com.example.localgems.R;
import com.example.localgems.ui.register.SignupFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    private EditText emailInput, passwordInput;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();

        emailInput = view.findViewById(R.id.input_email);
        passwordInput = view.findViewById(R.id.input_password);
        Button loginBtn = view.findViewById(R.id.button_login);
        Button registerBtn = view.findViewById(R.id.button_register);

        loginBtn.setOnClickListener(v -> loginUser());
        registerBtn.setOnClickListener(v -> showRegistrationFragment());

        return view;
    }

    private void loginUser() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Compila tutti i campi", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(requireContext(), "Login effettuato!", Toast.LENGTH_SHORT).show();

                        // Chiudi l'activity padre e apri MainActivity
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    } else {
                        Log.w("LOGIN", "Errore login", task.getException());
                        Toast.makeText(requireContext(), "Email o password errati", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showRegistrationFragment() {
        // Sostituisci con il fragment di registrazione
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SignupFragment())
                .addToBackStack(null)
                .commit();
    }
}