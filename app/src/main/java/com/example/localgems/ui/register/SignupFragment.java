package com.example.localgems.ui.register;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.localgems.R;
import com.example.localgems.ui.login.LoginFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SignupFragment extends Fragment {

    private EditText inputFirstName, inputLastName, inputBirthDate, inputEmail, inputPassword;
    private TextView errorFirstName, errorLastName, errorBirthDate, errorEmail, errorPassword, termsError;
    private CheckBox checkboxTerms;

    private Button buttonLogin;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        // Inizializza tutte le view qui
        inputFirstName = view.findViewById(R.id.input_first_name);
        inputLastName = view.findViewById(R.id.input_last_name);
        inputBirthDate = view.findViewById(R.id.input_birth_date);
        inputEmail = view.findViewById(R.id.input_email);
        inputPassword = view.findViewById(R.id.input_password);
        checkboxTerms = view.findViewById(R.id.checkbox_terms);
        termsError = view.findViewById(R.id.terms_error);
        buttonLogin = view.findViewById(R.id.button_login);

        // Inizializza anche i campi errori
        errorFirstName = view.findViewById(R.id.error_first_name);
        errorLastName = view.findViewById(R.id.error_last_name);
        errorBirthDate = view.findViewById(R.id.error_birth_date);
        errorEmail = view.findViewById(R.id.error_email);
        errorPassword = view.findViewById(R.id.error_password);

        setupDatePicker();

        // Setup del bottone usando la view appena inflata
        Button registerButton = view.findViewById(R.id.button_register);
        registerButton.setOnClickListener(v -> validateForm());
        buttonLogin.setOnClickListener(v -> validateForm());
        checkboxTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkboxTerms.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                termsError.setVisibility(View.GONE);
            } else {
                // Solo se vuoi forzare il rosso anche quando viene deselezionata manualmente
                checkboxTerms.setButtonTintList(ColorStateList.valueOf(Color.RED));
            }
        });

        return view;
    }

    private void setupDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        inputBirthDate.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        inputBirthDate.setText(dateFormat.format(calendar.getTime()));
                        errorBirthDate.setVisibility(View.GONE);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePicker.show();
        });
    }

    private void validateForm() {
        boolean isValid = true;
        resetAllErrors();

        // Validazione nome
        if (TextUtils.isEmpty(inputFirstName.getText())) {
            showFieldError(inputFirstName, errorFirstName);
            isValid = false;
        }

        // Validazione cognome
        if (TextUtils.isEmpty(inputLastName.getText())) {
            showFieldError(inputLastName, errorLastName);
            isValid = false;
        }

        // Validazione data di nascita
        if (TextUtils.isEmpty(inputBirthDate.getText())) {
            showFieldError(inputBirthDate, errorBirthDate);
            isValid = false;
        } else if (!isUserAtLeast16(inputBirthDate.getText().toString())) {
            errorBirthDate.setText("Devi avere almeno 16 anni");
            errorBirthDate.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Validazione email
        if (TextUtils.isEmpty(inputEmail.getText())) {
            showFieldError(inputEmail, errorEmail);
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText()).matches()) {
            errorEmail.setText("Email non valida");
            errorEmail.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Validazione password
        if (TextUtils.isEmpty(inputPassword.getText())) {
            showFieldError(inputPassword, errorPassword);
            isValid = false;
        } else if (inputPassword.getText().length() < 6) {
            errorPassword.setText("Minimo 6 caratteri");
            errorPassword.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Validazione checkbox termini
        if (!checkboxTerms.isChecked()) {
            termsError.setVisibility(View.VISIBLE);
            checkboxTerms.setButtonTintList(ColorStateList.valueOf(Color.RED));
            isValid = false;
        }

        if (isValid) {
            registerUser();
        }
    }

    private boolean isUserAtLeast16(String birthDate) {
        try {
            Date date = dateFormat.parse(birthDate);
            if (date == null) return false;

            Calendar dob = Calendar.getInstance();
            dob.setTime(date);

            Calendar today = Calendar.getInstance();

            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return age >= 16;
        } catch (ParseException e) {
            return false;
        }
    }

    private void showFieldError(EditText input, TextView error) {
        input.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
        error.setVisibility(View.VISIBLE);
    }

    private void resetAllErrors() {
        // Reset errori
        errorFirstName.setVisibility(View.GONE);
        errorLastName.setVisibility(View.GONE);
        errorBirthDate.setVisibility(View.GONE);
        errorEmail.setVisibility(View.GONE);
        errorPassword.setVisibility(View.GONE);
        termsError.setVisibility(View.GONE);

        // Reset testo errori a "Campo obbligatorio"
        errorFirstName.setText("Campo obbligatorio");
        errorLastName.setText("Campo obbligatorio");
        errorBirthDate.setText("Campo obbligatorio");
        errorEmail.setText("Campo obbligatorio");
        errorPassword.setText("Campo obbligatorio");

        // Reset colori
        resetFieldColor(inputFirstName);
        resetFieldColor(inputLastName);
        resetFieldColor(inputBirthDate);
        resetFieldColor(inputEmail);
        resetFieldColor(inputPassword);
        checkboxTerms.setButtonTintList(ColorStateList.valueOf(Color.GRAY));
    }

    private void resetFieldColor(EditText field) {
        field.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
    }

    private void registerUser() {
        String firstName = inputFirstName.getText().toString().trim();
        String lastName = inputLastName.getText().toString().trim();
        String birthDate = inputBirthDate.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        com.google.firebase.auth.FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();

                        // Costruisci l'oggetto user da salvare nel DB
                        java.util.HashMap<String, Object> userData = new java.util.HashMap<>();
                        userData.put("firstName", firstName);
                        userData.put("lastName", lastName);
                        userData.put("birthDate", birthDate);
                        userData.put("email", email);
                        userData.put("cart", new java.util.ArrayList<>()); // opzionale: inizializza carrello vuoto

                        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(userId)
                                .set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(requireContext(), "Registrazione completata!", Toast.LENGTH_SHORT).show();

                                    // Chiudi activity o passa al login
                                    if (getActivity() != null) {
                                        navToLogin(email, password);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(requireContext(), "Errore durante il salvataggio dati utente: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });

                    } else {
                        Toast.makeText(requireContext(), "Registrazione fallita: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void navToLogin(String email, String password) {
        com.google.firebase.auth.FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Login riuscito: apri la MainActivity
                        if (getActivity() != null) {
                            Intent intent = new Intent(getActivity(), com.example.localgems.MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // cancella lo stack
                            startActivity(intent);
                            getActivity().finish(); // chiude l'attuale activity (es: RegisterActivity)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Login fallito: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

}