package com.example.localgems.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.localgems.R;

public class LoginFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Gonfia il layout del frammento
        return inflater.inflate(R.layout.fragment_log_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Trova il pulsante di login
        Button loginButton = view.findViewById(R.id.loginButton);

        // Imposta un listener per il pulsante di login
        loginButton.setOnClickListener(v -> {
            // Azione da eseguire al click del pulsante
            Toast.makeText(requireContext(), "Login eseguito!", Toast.LENGTH_SHORT).show();
        });

        // Trova il pulsante di registrazione
        Button registerButton = view.findViewById(R.id.registerText);

        // Imposta un listener per il pulsante di registrazione
        registerButton.setOnClickListener(v -> {
            // Azione da eseguire al click del pulsante di registrazione
            Toast.makeText(requireContext(), "Naviga alla schermata di registrazione!", Toast.LENGTH_SHORT).show();
        });
    }
}