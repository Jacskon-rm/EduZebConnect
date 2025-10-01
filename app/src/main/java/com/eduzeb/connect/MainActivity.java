
package com.eduzeb.connect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private CheckBox cbRememberMe;
    private MaterialButton btnLogin, btnStudentLogin, btnAdminLogin;
    private TextView tvForgotPassword, tvSignUp;
    private SharedPreferences sharedPreferences;
    private boolean isStudentLogin = true;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupClickListeners();
        setupUserTypeSelection();
        checkRememberedLogin();
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        btnLogin = findViewById(R.id.btnLogin);
        btnStudentLogin = findViewById(R.id.btnStudentLogin);
        btnAdminLogin = findViewById(R.id.btnAdminLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignUp = findViewById(R.id.tvSignUp);
        
        sharedPreferences = getSharedPreferences("EduZebPrefs", MODE_PRIVATE);
        dbHelper = new DatabaseHelper(this);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
        
        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Forgot Password - Coming Soon!", Toast.LENGTH_SHORT).show();
        });
        
        tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });
    }

    private void setupUserTypeSelection() {
        btnStudentLogin.setOnClickListener(v -> {
            isStudentLogin = true;
            updateUserTypeButtons();
        });
        
        btnAdminLogin.setOnClickListener(v -> {
            isStudentLogin = false;
            updateUserTypeButtons();
        });
        
        updateUserTypeButtons();
    }

    private void updateUserTypeButtons() {
        if (isStudentLogin) {
            btnStudentLogin.setBackgroundColor(getResources().getColor(R.color.primary_blue));
            btnStudentLogin.setTextColor(getResources().getColor(R.color.white));
            btnAdminLogin.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            btnAdminLogin.setTextColor(getResources().getColor(R.color.primary_blue));
        } else {
            btnAdminLogin.setBackgroundColor(getResources().getColor(R.color.primary_blue));
            btnAdminLogin.setTextColor(getResources().getColor(R.color.white));
            btnStudentLogin.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            btnStudentLogin.setTextColor(getResources().getColor(R.color.primary_blue));
        }
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        btnLogin.setText("Signing in...");
        btnLogin.setEnabled(false);

        performLogin(email, password);
    }

    private void performLogin(String email, String password) {
        User user = dbHelper.authenticateUser(email, password, isStudentLogin ? "student" : "admin");
        
        if (user != null) {
            if (cbRememberMe.isChecked()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("saved_email", email);
                editor.putString("user_type", isStudentLogin ? "student" : "admin");
                editor.putBoolean("remember_login", true);
                editor.putInt("user_id", user.getId());
                editor.apply();
            }

            Intent intent;
            if (isStudentLogin) {
                intent = new Intent(MainActivity.this, StudentDashboardActivity.class);
            } else {
                intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
            }
            
            intent.putExtra("user_id", user.getId());
            intent.putExtra("user_name", user.getName());
            intent.putExtra("user_email", user.getEmail());
            
            startActivity(intent);
            finish();
            
            Toast.makeText(this, "Welcome back, " + user.getName() + "!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Invalid email or password. Please try again.", Toast.LENGTH_SHORT).show();
            resetLoginButton();
        }
    }

    private void checkRememberedLogin() {
        boolean rememberLogin = sharedPreferences.getBoolean("remember_login", false);
        if (rememberLogin) {
            String savedEmail = sharedPreferences.getString("saved_email", "");
            String userType = sharedPreferences.getString("user_type", "student");
            
            if (!savedEmail.isEmpty()) {
                etEmail.setText(savedEmail);
                cbRememberMe.setChecked(true);
                isStudentLogin = userType.equals("student");
                updateUserTypeButtons();
            }
        }
    }

    private void resetLoginButton() {
        btnLogin.setText("Sign In");
        btnLogin.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetLoginButton();
    }
}
