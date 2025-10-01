
package com.eduzeb.connect;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android:widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class SearchSchoolsActivity extends AppCompatActivity {

    private TextInputEditText etSearchQuery, etLocation;
    private AutoCompleteTextView spinnerSchoolType;
    private RecyclerView rvSchools;
    private TextView tvResultsCount;
    private ImageView ivFilterToggle, ivSortToggle;
    private LinearLayout layoutEmptyState;
    private MaterialButton btnShowAllSchools;
    
    private SchoolAdapter schoolAdapter;
    private DatabaseHelper dbHelper;
    private List<School> allSchools;
    private List<School> filteredSchools;
    
    private String currentQuery = "";
    private String currentType = "all";
    private String currentLocation = "";
    private String currentSortBy = "rating"; // rating, name, fees

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_schools);

        initializeViews();
        setupToolbar();
        setupFilters();
        setupRecyclerView();
        setupClickListeners();
        loadSchools();
    }

    private void initializeViews() {
        etSearchQuery = findViewById(R.id.etSearchQuery);
        etLocation = findViewById(R.id.etLocation);
        spinnerSchoolType = findViewById(R.id.spinnerSchoolType);
        rvSchools = findViewById(R.id.rvSchools);
        tvResultsCount = findViewById(R.id.tvResultsCount);
        ivFilterToggle = findViewById(R.id.ivFilterToggle);
        ivSortToggle = findViewById(R.id.ivSortToggle);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        btnShowAllSchools = findViewById(R.id.btnShowAllSchools);
        
        dbHelper = new DatabaseHelper(this);
        allSchools = new ArrayList<>();
        filteredSchools = new ArrayList<>();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Search Schools");
        }
        
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupFilters() {
        // Setup school type dropdown
        String[] schoolTypes = {"All Types", "Public", "Private"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, schoolTypes);
        spinnerSchoolType.setAdapter(typeAdapter);
        spinnerSchoolType.setText(schoolTypes[0], false);
        
        // Setup search listeners
        etSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s.toString().trim();
                performSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentLocation = s.toString().trim();
                performSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        spinnerSchoolType.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            currentType = selected.equals("All Types") ? "all" : selected.toLowerCase();
            performSearch();
        });
    }

    private void setupRecyclerView() {
        schoolAdapter = new SchoolAdapter(filteredSchools, this::onSchoolClick);
        rvSchools.setLayoutManager(new LinearLayoutManager(this));
        rvSchools.setAdapter(schoolAdapter);
    }

    private void setupClickListeners() {
        btnShowAllSchools.setOnClickListener(v -> {
            clearFilters();
            loadSchools();
        });

        ivSortToggle.setOnClickListener(v -> showSortOptions());
        
        ivFilterToggle.setOnClickListener(v -> showAdvancedFilters());
    }

    private void loadSchools() {
        allSchools = dbHelper.getAllSchools();
        performSearch();
    }

    private void performSearch() {
        if (currentQuery.isEmpty() && currentType.equals("all") && currentLocation.isEmpty()) {
            filteredSchools.clear();
            filteredSchools.addAll(allSchools);
        } else {
            filteredSchools = dbHelper.searchSchools(
                currentQuery.isEmpty() ? null : currentQuery,
                currentType.equals("all") ? null : currentType,
                currentLocation.isEmpty() ? null : currentLocation
            );
        }
        
        sortSchools();
        updateUI();
    }

    private void sortSchools() {
        switch (currentSortBy) {
            case "name":
                filteredSchools.sort((s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()));
                break;
            case "fees":
                filteredSchools.sort((s1, s2) -> Integer.compare(s1.getFees(), s2.getFees()));
                break;
            case "rating":
            default:
                filteredSchools.sort((s1, s2) -> Double.compare(s2.getRating(), s1.getRating()));
                break;
        }
    }

    private void updateUI() {
        schoolAdapter.updateSchools(filteredSchools);
        
        if (filteredSchools.isEmpty()) {
            rvSchools.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
            tvResultsCount.setText("No schools found");
        } else {
            rvSchools.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
            tvResultsCount.setText(filteredSchools.size() + " school" + 
                (filteredSchools.size() == 1 ? "" : "s") + " found");
        }
    }

    private void clearFilters() {
        etSearchQuery.setText("");
        etLocation.setText("");
        spinnerSchoolType.setText("All Types", false);
        currentQuery = "";
        currentType = "all";
        currentLocation = "";
    }

    private void showSortOptions() {
        String[] sortOptions = {"Rating (High to Low)", "Name (A-Z)", "Fees (Low to High)"};
        
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Sort by")
                .setItems(sortOptions, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            currentSortBy = "rating";
                            break;
                        case 1:
                            currentSortBy = "name";
                            break;
                        case 2:
                            currentSortBy = "fees";
                            break;
                    }
                    performSearch();
                });
        builder.show();
    }

    private void showAdvancedFilters() {
        // This could open a bottom sheet or dialog with more filter options
        // For now, show a simple toast
        android.widget.Toast.makeText(this, "Advanced filters coming soon!", 
            android.widget.Toast.LENGTH_SHORT).show();
    }

    private void onSchoolClick(School school) {
        Intent intent = new Intent(this, SchoolDetailActivity.class);
        intent.putExtra("school_id", school.getId());
        intent.putExtra("school_name", school.getName());
        intent.putExtra("school_type", school.getType());
        intent.putExtra("school_location", school.getLocation());
        intent.putExtra("school_rating", school.getRating());
        intent.putExtra("school_fees", school.getFees());
        intent.putExtra("school_description", school.getDescription());
        intent.putExtra("school_requirements", school.getRequirements());
        intent.putExtra("school_contact", school.getContact());
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
