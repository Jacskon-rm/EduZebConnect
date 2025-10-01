
package com.eduzeb.connect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etEmail, etPhone, etPassword, etConfirmPassword, etAdminCode;
    private MaterialButton btnRegisterStudent, btnRegisterAdmin, btnRegister;
    private TextView tvSignIn;
    private CheckBox cbTerms;
    private TextInputLayout layoutAdminCode;
    private DatabaseHelper dbHelper;
    
    private boolean isAdminRegistration = false;
    private static final String ADMIN_CODE = "EDUZEB2024ADMIN"; // In production, this should be more secure

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();
        setupClickListeners();
        setupUserTypeSelection();
    }

    private void initializeViews() {
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etAdminCode = findViewById(R.id.etAdminCode);
        
        btnRegisterStudent = findViewById(R.id.btnRegisterStudent);
        btnRegisterAdmin = findViewById(R.id.btnRegisterAdmin);
        btnRegister = findViewById(R.id.btnRegister);
        
        tvSignIn = findViewById(R.id.tvSignIn);
        cbTerms = findViewById(R.id.cbTerms);
        layoutAdminCode = findViewById(R.id.layoutAdminCode);
        
        dbHelper = new DatabaseHelper(this);
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> attemptRegistration());
        
        tvSignIn.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        });
    }

    private void setupUserTypeSelection() {
        btnRegisterStudent.setOnClickListener(v -> {
            isAdminRegistration = false;
            updateUserTypeButtons();
            layoutAdminCode.setVisibility(View.GONE);
        });
        
        btnRegisterAdmin.setOnClickListener(v -> {
            isAdminRegistration = true;
            updateUserTypeButtons();
            layoutAdminCode.setVisibility(View.VISIBLE);
        });
        
        updateUserTypeButtons();
    }

    private void updateUserTypeButtons() {
        if (isAdminRegistration) {
            btnRegisterAdmin.setBackgroundColor(getResources().getColor(R.color.primary_blue));
            btnRegisterAdmin.setTextColor(getResources().getColor(R.color.white));
            btnRegisterStudent.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            btnRegisterStudent.setTextColor(getResources().getColor(R.color.primary_blue));
            btnRegister.setText("Create Admin Account");
        } else {
            btnRegisterStudent.setBackgroundColor(getResources().getColor(R.color.primary_blue));
            btnRegisterStudent.setTextColor(getResources().getColor(R.color.white));
            btnRegisterAdmin.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            btnRegisterAdmin.setTextColor(getResources().getColor(R.color.primary_blue));
            btnRegister.setText("Create Student Account");
        }
    }

    private void attemptRegistration() {
        if (!validateInput()) {
            return;
        }

        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString();
        String userType = isAdminRegistration ? "admin" : "student";

        // Show loading state
        btnRegister.setText("Creating Account...");
        btnRegister.setEnabled(false);

        // Check if email already exists
        if (dbHelper.isEmailExists(email)) {
            showToast("Email already exists. Please use a different email.");
            resetRegisterButton();
            return;
        }

        // Create user account
        boolean success = dbHelper.createUser(fullName, email, phone, password, userType);
        
        if (success) {
            showToast("Account created successfully! Please sign in.");
            
            // Navigate to login screen
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.putExtra("registered_email", email);
            intent.putExtra("user_type", userType);
            startActivity(intent);
            finish();
        } else {
            showToast("Registration failed. Please try again.");
            resetRegisterButton();
        }
    }

    private boolean validateInput() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Validate full name
        if (fullName.isEmpty()) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return false;
        }

        if (fullName.length() < 2) {
            etFullName.setError("Name must be at least 2 characters");
            etFullName.requestFocus();
            return false;
        }

        // Validate email
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return false;
        }

        // Validate phone
        if (phone.isEmpty()) {
            etPhone.setError("Phone number is required");
            etPhone.requestFocus();
            return false;
        }

        if (phone.length() < 10) {
            etPhone.setError("Please enter a valid phone number");
            etPhone.requestFocus();
            return false;
        }

        // Validate password
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return false;
        }

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Please confirm your password");
            etConfirmPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return false;
        }

        // Validate admin code if admin registration
        if (isAdminRegistration) {
            String adminCode = etAdminCode.getText().toString().trim();
            if (adminCode.isEmpty()) {
                etAdminCode.setError("Admin code is required");
                etAdminCode.requestFocus();
                return false;
            }

            if (!adminCode.equals(ADMIN_CODE)) {
                etAdminCode.setError("Invalid admin authorization code");
                etAdminCode.requestFocus();
                return false;
            }
        }

        // Validate terms and conditions
        if (!cbTerms.isChecked()) {
            showToast("Please accept the Terms and Conditions");
            return false;
        }

        return true;
    }

    private void resetRegisterButton() {
        btnRegister.setText(isAdminRegistration ? "Create Admin Account" : "Create Student Account");
        btnRegister.setEnabled(true);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
