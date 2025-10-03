package com.eduzeb.connect;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class SearchSchoolsActivity extends AppCompatActivity {

    private TextInputEditText etSearchQuery;
    private TextView tvCurrentDistrict, tvResultsCount;
    private MaterialButton btnDetectLocation, btnUseAI;
    private AutoCompleteTextView spinnerSchoolType;
    private RecyclerView rvSchools;
    private LinearLayout layoutEmptyState, layoutLocationInfo;
    private FloatingActionButton fabFilter;
    
    private SchoolAdapter schoolAdapter;
    private DatabaseHelper dbHelper;
    private LocationHelper locationHelper;
    private SchoolPerformanceAI performanceAI;
    
    private List<School> allSchools;
    private List<School> filteredSchools;
    
    private int userId;
    private String currentDistrict = "";
    private String currentQuery = "";
    private String currentType = "all";
    private boolean aiEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_schools);

        initializeViews();
        setupToolbar();
        getUserData();
        setupRecyclerView();
        setupClickListeners();
        setupFilters();
        detectUserLocation();
    }

    private void initializeViews() {
        etSearchQuery = findViewById(R.id.etSearchQuery);
        tvCurrentDistrict = findViewById(R.id.tvCurrentDistrict);
        tvResultsCount = findViewById(R.id.tvResultsCount);
        btnDetectLocation = findViewById(R.id.btnDetectLocation);
        btnUseAI = findViewById(R.id.btnUseAI);
        spinnerSchoolType = findViewById(R.id.spinnerSchoolType);
        rvSchools = findViewById(R.id.rvSchools);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        layoutLocationInfo = findViewById(R.id.layoutLocationInfo);
        fabFilter = findViewById(R.id.fabFilter);
        
        dbHelper = new DatabaseHelper(this);
        locationHelper = new LocationHelper(this);
        performanceAI = new SchoolPerformanceAI(this);
        
        allSchools = new ArrayList<>();
        filteredSchools = new ArrayList<>();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Find Schools");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void getUserData() {
        Intent intent = getIntent();
        userId = intent.getIntExtra("user_id", -1);
    }

    private void setupRecyclerView() {
        schoolAdapter = new SchoolAdapter(filteredSchools, this::onSchoolClick);
        rvSchools.setLayoutManager(new LinearLayoutManager(this));
        rvSchools.setAdapter(schoolAdapter);
    }

    private void setupClickListeners() {
        btnDetectLocation.setOnClickListener(v -> detectUserLocation());
        
        btnUseAI.setOnClickListener(v -> toggleAIMode());
        
        fabFilter.setOnClickListener(v -> showFilterOptions());
    }

    private void setupFilters() {
        // Search functionality
        etSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s.toString().trim().toLowerCase();
                filterSchools();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // School type dropdown
        String[] schoolTypes = {"All Types", "Public", "Private"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, schoolTypes);
        spinnerSchoolType.setAdapter(typeAdapter);
        spinnerSchoolType.setText(schoolTypes[0], false);
        
        spinnerSchoolType.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            currentType = selected.equals("All Types") ? "all" : selected.toLowerCase();
            filterSchools();
        });
    }

    private void detectUserLocation() {
        btnDetectLocation.setEnabled(false);
        btnDetectLocation.setText("Detecting...");
        
        locationHelper.getCurrentLocation(new LocationHelper.LocationCallback() {
            @Override
            public void onLocationDetected(String district, double latitude, double longitude) {
                currentDistrict = district;
                tvCurrentDistrict.setText("📍 " + district);
                layoutLocationInfo.setVisibility(View.VISIBLE);
                
                // Save to student profile
                StudentProfile profile = dbHelper.getStudentProfile(userId);
                if (profile == null) {
                    profile = new StudentProfile(userId, 0, new String[]{}, 
                            new String[]{}, district, latitude, longitude);
                } else {
                    profile.setDistrict(district);
                    profile.setLocation(latitude, longitude);
                }
                dbHelper.saveStudentProfile(userId, profile);
                
                // Load schools in district
                loadSchoolsByDistrict(district);
                
                btnDetectLocation.setText("Location Detected ✓");
                btnDetectLocation.setEnabled(true);
                
                Toast.makeText(SearchSchoolsActivity.this, 
                        "Location detected: " + district, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLocationError(String error) {
                btnDetectLocation.setText("Detect Location");
                btnDetectLocation.setEnabled(true);
                Toast.makeText(SearchSchoolsActivity.this, 
                        "Error: " + error, Toast.LENGTH_LONG).show();
                
                // Load all schools as fallback
                loadAllSchools();
            }
        });
    }

    private void loadSchoolsByDistrict(String district) {
        allSchools = dbHelper.getSchoolsByDistrict(district);
        
        if (aiEnabled) {
            fetchAIPerformanceForSchools();
        }
        
        filterSchools();
    }

    private void loadAllSchools() {
        allSchools = dbHelper.getAllSchools();
        filterSchools();
    }

    private void toggleAIMode() {
        aiEnabled = !aiEnabled;
        
        if (aiEnabled) {
            btnUseAI.setBackgroundColor(getResources().getColor(R.color.success_green));
            btnUseAI.setText("AI Mode: ON");
            Toast.makeText(this, "AI Performance Analysis Enabled", Toast.LENGTH_SHORT).show();
            
            if (!allSchools.isEmpty()) {
                fetchAIPerformanceForSchools();
            }
        } else {
            btnUseAI.setBackgroundColor(getResources().getColor(R.color.primary_blue));
            btnUseAI.setText("Enable AI Analysis");
        }
    }

    private void fetchAIPerformanceForSchools() {
        Toast.makeText(this, "Analyzing school performance with AI...", Toast.LENGTH_SHORT).show();
        
        for (School school : allSchools) {
            // Check if we already have performance data
            SchoolPerformance existing = dbHelper.getSchoolPerformance(school.getId());
            
            if (existing == null) {
                // Fetch from AI
                performanceAI.analyzeSchoolPerformance(school.getName(), currentDistrict,
                        new SchoolPerformanceAI.PerformanceCallback() {
                            @Override
                            public void onPerformanceDataReceived(SchoolPerformance performance) {
                                dbHelper.saveSchoolPerformance(school.getId(), performance);
                                runOnUiThread(() -> {
                                    Toast.makeText(SearchSchoolsActivity.this,
                                            "Performance data updated for " + school.getName(),
                                            Toast.LENGTH_SHORT).show();
                                    filterSchools(); // Refresh display
                                });
                            }

                            @Override
                            public void onError(String error) {
                                runOnUiThread(() -> {
                                    Toast.makeText(SearchSchoolsActivity.this,
                                            "AI analysis error: " + error,
                                            Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
            }
        }
    }

    private void filterSchools() {
        filteredSchools.clear();
        
        for (School school : allSchools) {
            boolean matchesType = currentType.equals("all") || 
                    school.getType().equals(currentType);
            
            boolean matchesSearch = currentQuery.isEmpty() ||
                    school.getName().toLowerCase().contains(currentQuery) ||
                    school.getLocation().toLowerCase().contains(currentQuery);
            
            if (matchesType && matchesSearch) {
                filteredSchools.add(school);
            }
        }
        
        updateUI();
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

    private void onSchoolClick(School school) {
        // TODO: Open school detail page with performance data
        Toast.makeText(this, "Opening " + school.getName(), Toast.LENGTH_SHORT).show();
    }

    private void showFilterOptions() {
        // TODO: Show advanced filter dialog
        Toast.makeText(this, "Advanced filters - Coming soon!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationHelper.stopLocationUpdates();
    }
}
