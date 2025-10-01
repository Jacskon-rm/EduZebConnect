
package com.eduzeb.connect;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ManageApplicationsActivity extends AppCompatActivity {

    private TextInputEditText etSearchApplications;
    private Chip chipAll, chipPending, chipUnderReview, chipApproved;
    private MaterialButton btnBulkApprove, btnExportData;
    private RecyclerView rvApplications;
    private LinearLayout layoutEmptyState;
    private FloatingActionButton fabQuickReview;
    
    private AdminApplicationAdapter applicationAdapter;
    private DatabaseHelper dbHelper;
    private List<Application> allApplications;
    private List<Application> filteredApplications;
    
    private int adminId;
    private String currentFilter = "all";
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_applications);

        initializeViews();
        setupToolbar();
        getUserData();
        setupRecyclerView();
        setupClickListeners();
        setupFilters();
        loadApplications();
    }

    private void initializeViews() {
        etSearchApplications = findViewById(R.id.etSearchApplications);
        chipAll = findViewById(R.id.chipAll);
        chipPending = findViewById(R.id.chipPending);
        chipUnderReview = findViewById(R.id.chipUnderReview);
        chipApproved = findViewById(R.id.chipApproved);
        btnBulkApprove = findViewById(R.id.btnBulkApprove);
        btnExportData = findViewById(R.id.btnExportData);
        rvApplications = findViewById(R.id.rvApplications);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        fabQuickReview = findViewById(R.id.fabQuickReview);
        
        dbHelper = new DatabaseHelper(this);
        allApplications = new ArrayList<>();
        filteredApplications = new ArrayList<>();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manage Applications");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void getUserData() {
        Intent intent = getIntent();
        adminId = intent.getIntExtra("admin_id", -1);
    }

    private void setupRecyclerView() {
        applicationAdapter = new AdminApplicationAdapter(filteredApplications, 
                this::onApplicationClick, this::onApplicationAction);
        rvApplications.setLayoutManager(new LinearLayoutManager(this));
        rvApplications.setAdapter(applicationAdapter);
    }

    private void setupClickListeners() {
        btnBulkApprove.setOnClickListener(v -> performBulkApprove());
        btnExportData.setOnClickListener(v -> exportApplicationData());
        fabQuickReview.setOnClickListener(v -> startQuickReview());
    }

    private void setupFilters() {
        // Search functionality
        etSearchApplications.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().trim().toLowerCase();
                filterApplications();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Filter chips
        chipAll.setOnClickListener(v -> {
            currentFilter = "all";
            updateFilterChips();
            filterApplications();
        });

        chipPending.setOnClickListener(v -> {
            currentFilter = "pending";
            updateFilterChips();
            filterApplications();
        });

        chipUnderReview.setOnClickListener(v -> {
            currentFilter = "under_review";
            updateFilterChips();
            filterApplications();
        });

        chipApproved.setOnClickListener(v -> {
            currentFilter = "approved";
            updateFilterChips();
            filterApplications();
        });
    }

    private void updateFilterChips() {
        chipAll.setChecked(currentFilter.equals("all"));
        chipPending.setChecked(currentFilter.equals("pending"));
        chipUnderReview.setChecked(currentFilter.equals("under_review"));
        chipApproved.setChecked(currentFilter.equals("approved"));
    }

    private void loadApplications() {
        allApplications = dbHelper.getAllApplicationsForAdmin();
        updateFilterCounts();
        filterApplications();
    }

    private void updateFilterCounts() {
        int allCount = allApplications.size();
        int pendingCount = (int) allApplications.stream()
                .filter(app -> app.getStatus().equals("pending")).count();
        int reviewCount = (int) allApplications.stream()
                .filter(app -> app.getStatus().equals("under_review")).count();
        int approvedCount = (int) allApplications.stream()
                .filter(app -> app.getStatus().equals("approved")).count();

        chipAll.setText("All (" + allCount + ")");
        chipPending.setText("Pending (" + pendingCount + ")");
        chipUnderReview.setText("Review (" + reviewCount + ")");
        chipApproved.setText("Approved (" + approvedCount + ")");
    }

    private void filterApplications() {
        filteredApplications.clear();
        
        for (Application app : allApplications) {
            boolean matchesFilter = currentFilter.equals("all") || 
                    app.getStatus().equals(currentFilter);
            
            boolean matchesSearch = currentSearchQuery.isEmpty() ||
                    app.getSchoolName().toLowerCase().contains(currentSearchQuery) ||
                    dbHelper.getStudentName(app.getUserId()).toLowerCase().contains(currentSearchQuery);
            
            if (matchesFilter && matchesSearch) {
                filteredApplications.add(app);
            }
        }
        
        updateUI();
    }

    private void updateUI() {
        applicationAdapter.updateApplications(filteredApplications);
        
        if (filteredApplications.isEmpty()) {
            rvApplications.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvApplications.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }

    private void onApplicationClick(Application application) {
        Intent intent = new Intent(this, ApplicationDetailActivity.class);
        intent.putExtra("application_id", application.getId());
        intent.putExtra("admin_id", adminId);
        startActivity(intent);
    }

    private void onApplicationAction(Application application, String action) {
        switch (action) {
            case "approve":
                updateApplicationStatus(application, "approved");
                break;
            case "reject":
                showRejectDialog(application);
                break;
            case "review":
                updateApplicationStatus(application, "under_review");
                break;
        }
    }

    private void updateApplicationStatus(Application application, String status) {
        boolean success = dbHelper.updateApplicationStatus(application.getId(), status, "Updated by admin");
        
        if (success) {
            // Send notification to student
            String title = "Application Status Update";
            String message = "Your application to " + application.getSchoolName() + 
                    " has been " + status;
            dbHelper.createNotification(application.getUserId(), title, message);
            
            showToast("Application " + status + " successfully");
            loadApplications(); // Reload data
        } else {
            showToast("Failed to update application status");
        }
    }

    private void showRejectDialog(Application application) {
        androidx.appcompat.app.AlertDialog.Builder builder = 
                new androidx.appcompat.app.AlertDialog.Builder(this);
        
        final android.widget.EditText reasonInput = new android.widget.EditText(this);
        reasonInput.setHint("Reason for rejection (optional)");
        reasonInput.setPadding(50, 30, 50, 30);
        
        builder.setTitle("Reject Application")
                .setMessage("Reject application from " + 
                        dbHelper.getStudentName(application.getUserId()) + 
                        " to " + application.getSchoolName() + "?")
                .setView(reasonInput)
                .setPositiveButton("Reject", (dialog, which) -> {
                    String reason = reasonInput.getText().toString().trim();
                    if (reason.isEmpty()) {
                        reason = "Application requirements not met";
                    }
                    
                    boolean success = dbHelper.updateApplicationStatus(
                            application.getId(), "rejected", reason);
                    
                    if (success) {
                        // Send notification to student
                        String title = "Application Rejected";
                        String message = "Your application to " + application.getSchoolName() + 
                                " has been rejected. Reason: " + reason;
                        dbHelper.createNotification(application.getUserId(), title, message);
                        
                        showToast("Application rejected");
                        loadApplications();
                    } else {
                        showToast("Failed to reject application");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performBulkApprove() {
        List<Application> pendingApps = filteredApplications.stream()
                .filter(app -> app.getStatus().equals("pending"))
                .collect(Collectors.toList());
        
        if (pendingApps.isEmpty()) {
            showToast("No pending applications to approve");
            return;
        }
        
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Bulk Approve Applications")
                .setMessage("This will approve " + pendingApps.size() + 
                        " pending applications. Continue?")
                .setPositiveButton("Approve All", (dialog, which) -> {
                    int approved = 0;
                    for (Application app : pendingApps) {
                        if (dbHelper.updateApplicationStatus(app.getId(), "approved", 
                                "Bulk approved by admin")) {
                            // Send notification to student
                            String title = "Application Approved";
                            String message = "Congratulations! Your application to " + 
                                    app.getSchoolName() + " has been approved.";
                            dbHelper.createNotification(app.getUserId(), title, message);
                            approved++;
                        }
                    }
                    showToast(approved + " applications approved successfully");
                    loadApplications();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void exportApplicationData() {
        showToast("Exporting application data...");
        // In a real app, this would export to CSV/Excel
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simulate export process
                runOnUiThread(() -> showToast("Application data exported successfully"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startQuickReview() {
        // Find the first pending application for quick review
        Application pendingApp = filteredApplications.stream()
                .filter(app -> app.getStatus().equals("pending"))
                .findFirst()
                .orElse(null);
        
        if (pendingApp != null) {
            onApplicationClick(pendingApp);
        } else {
            showToast("No pending applications for quick review");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadApplications(); // Refresh data when returning
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
