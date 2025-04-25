package com.example.localgems.ui.account;

import android.app.AlertDialog;
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

import com.example.localgems.R;
import com.example.localgems.model.User;

public class AccountFragment extends Fragment {

    private TextView emailText, nameText, lastNameText, birthDateText;
    private Button changePasswordButton;

    private User currentUser;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Initialize views
        emailText = view.findViewById(R.id.account_email);
        nameText = view.findViewById(R.id.account_first_name);
        lastNameText = view.findViewById(R.id.account_last_name);
        birthDateText = view.findViewById(R.id.account_birth_date);
        changePasswordButton = view.findViewById(R.id.change_password_button);

        // Simulating a user object, normally fetched from backend or database
        currentUser = new User("user@example.com", "Mario", "Rossi", "15/03/1990", "password123");

        // Set user details on screen
        emailText.setText(currentUser.getEmail());
        nameText.setText(currentUser.getFirstName());
        lastNameText.setText(currentUser.getLastName());
        birthDateText.setText(currentUser.getBirthDate());

        // Handle password change button click
        changePasswordButton.setOnClickListener(v -> showChangePasswordDialog());

        return view;
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Change Password");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);

        EditText oldPassword = dialogView.findViewById(R.id.old_password);
        EditText newPassword = dialogView.findViewById(R.id.new_password);
        EditText confirmPassword = dialogView.findViewById(R.id.confirm_password);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String old = oldPassword.getText().toString();
            String newP = newPassword.getText().toString();
            String confirm = confirmPassword.getText().toString();

            // Check if old password matches
            if (!old.equals(currentUser.getPassword())) {
                Toast.makeText(getContext(), "Incorrect current password", Toast.LENGTH_SHORT).show();
            }
            // Check if new passwords match
            else if (!newP.equals(confirm)) {
                Toast.makeText(getContext(), "New passwords do not match", Toast.LENGTH_SHORT).show();
            }
            // All good, update password
            else {
                currentUser.setPassword(newP);
                Toast.makeText(getContext(), "Password successfully updated", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
