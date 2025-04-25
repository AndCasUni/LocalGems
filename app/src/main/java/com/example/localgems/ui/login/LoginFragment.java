/* package com.example.localgems.ui.login;

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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.localgems.MainActivity;
import com.example.localgems.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    private EditText emailInput, passwordInput;
    private Button loginBtn, registerBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        // Initialize input fields and buttons
        emailInput = view.findViewById(R.id.input_email);
        passwordInput = view.findViewById(R.id.input_password);
        loginBtn = view.findViewById(R.id.button_login);
        registerBtn = view.findViewById(R.id.button_register);

        // Handle login click
        loginBtn.setOnClickListener(v -> loginUser());

        // Handle registration click
        registerBtn.setOnClickListener(v -> registerUser());
    }

    private void loginUser() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        // Basic input check
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Try to log the user in
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(requireContext(), "Logged in successfully", Toast.LENGTH_SHORT).show();

                        // Move to main screen
                        Intent intent = new Intent(requireContext(), MainActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    } else {
                        Log.w("LOGIN", "Login failed", task.getException());
                        Toast.makeText(requireContext(), "Wrong email or password", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerUser() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        // Basic input validation
        if (email.isEmpty() || password.length() < 6) {
            Toast.makeText(requireContext(), "Enter a valid email and password (6+ characters)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Try to register the user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(), "Registration successful! You can now log in", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w("REGISTER", "Registration failed", task.getException());
                        Toast.makeText(requireContext(), "Something went wrong while registering", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
*/