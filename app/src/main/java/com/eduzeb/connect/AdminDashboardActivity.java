
package com.eduzeb.connect;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvAdminName, tvPendingApplications, tvTotalSchools, tvTotalStudents;
    private CardView cardManageApplications, cardManageSchools, cardStudentAnalytics, 
                     cardReports, cardNotificationCenter, cardSettings;
    private FloatingActionButton fabQuickActions;
    private RecyclerView rvRecentActivities;
    private RecentActivityAdapter activityAdapter;
    private DatabaseHelper dbHelper;
    
    private int userId;
    private String userName, userEmail;
    private List<StudentDashboardActivity.ActivityItem> recentActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initializeViews();
        setupToolbar();
        getUserData();
        setupClickListeners();
        loadDashboardData();
        setupRecentActivities();
    }

    private void initializeViews() {
        tvAdminName = findViewById(R.id.tvAdminName);
        tvPendingApplications = findViewById(R.id.tvPendingApplications);
        tvTotalSchools = findViewById(R.id.tvTotalSchools);
        tvTotalStudents = findViewById(R.id.tvTotalStudents);
        
        cardManageApplications = findViewById(R.id.cardManageApplications);
        cardManageSchools = findViewById(R.id.cardManageSchools);
        cardStudentAnalytics = findViewById(R.id.cardStudentAnalytics);
        cardReports = findViewById(R.id.cardReports);
        cardNotificationCenter = findViewById(R.id.cardNotificationCenter);
        cardSettings = findViewById(R.id.cardSettings);
        
        fabQuickActions = findViewById(R.id.fabQuickActions);
        rvRecentActivities = findViewById(R.id.rvRecentActivities);
        
        dbHelper = new DatabaseHelper(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin Dashboard");
        }
    }

    private void getUserData() {
        Intent intent = getIntent();
        userId = intent.getIntExtra("user_id", -1);
        userName = intent.getStringExtra("user_name");
        userEmail = intent.getStringExtra("user_email");
        
        if (userName != null && !userName.isEmpty()) {
            tvAdminName.setText(userName);
        } else {
            tvAdminName.setText("Administrator");
        }
    }

    private void setupClickListeners() {
        cardManageApplications.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageApplicationsActivity.class);
            intent.putExtra("admin_id", userId);
            startActivity(intent);
        });

        cardManageSchools.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageSchoolsActivity.class);
            intent.putExtra("admin_id", userId);
            startActivity(intent);
        });

        cardStudentAnalytics.setOnClickListener(v -> {
            Intent intent = new Intent(this, StudentAnalyticsActivity.class);
            intent.putExtra("admin_id", userId);
            startActivity(intent);
        });

        cardReports.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReportsActivity.class);
            intent.putExtra("admin_id", userId);
            startActivity(intent);
        });

        cardNotificationCenter.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminNotificationsActivity.class);
            intent.putExtra("admin_id", userId);
            startActivity(intent);
        });

        cardSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminSettingsActivity.class);
            intent.putExtra("admin_id", userId);
            startActivity(intent);
        });

        fabQuickActions.setOnClickListener(v -> showQuickActionsMenu());
    }

    private void loadDashboardData() {
        // Load pending applications count
        int pendingApps = dbHelper.getPendingApplicationsCount();
        tvPendingApplications.setText(String.valueOf(pendingApps));
        
        // Load total schools count
        int totalSchools = dbHelper.getTotalSchoolsCount();
        tvTotalSchools.setText(String.valueOf(totalSchools));
        
        // Load total students count
        int totalStudents = dbHelper.getTotalStudentsCount();
        tvTotalStudents.setText(String.valueOf(totalStudents));
    }

    private void setupRecentActivities() {
        recentActivities = new ArrayList<>();
        loadRecentAdminActivities();
        
        activityAdapter = new RecentActivityAdapter(recentActivities);
        rvRecentActivities.setLayoutManager(new LinearLayoutManager(this));
        rvRecentActivities.setAdapter(activityAdapter);
    }

    private void loadRecentAdminActivities() {
        // Sample recent admin activities - replace with database calls
        recentActivities.add(new StudentDashboardActivity.ActivityItem(
            "Application Approved",
            "John Doe's application to Springfield High approved",
            "1 hour ago",
            R.drawable.ic_check_circle
        ));
        
        recentActivities.add(new StudentDashboardActivity.ActivityItem(
            "New School Added",
            "Riverside Academy added to the system",
            "3 hours ago",
            R.drawable.ic_school_add
        ));
        
        recentActivities.add(new StudentDashboardActivity.ActivityItem(
            "Application Rejected",
            "Jane Smith's application needs additional documents",
            "5 hours ago",
            R.drawable.ic_close_circle
        ));
        
        recentActivities.add(new StudentDashboardActivity.ActivityItem(
            "System Update",
            "ML recommendation engine updated",
            "1 day ago",
            R.drawable.ic_system_update
        ));
    }

    private void showQuickActionsMenu() {
        String[] quickActions = {
            "Add New School",
            "Bulk Approve Applications",
            "Send Notification to All Students",
            "Generate Monthly Report",
            "Update School Rankings"
        };

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Quick Actions")
                .setItems(quickActions, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            addNewSchool();
                            break;
                        case 1:
                            bulkApproveApplications();
                            break;
                        case 2:
                            sendBulkNotification();
                            break;
                        case 3:
                            generateMonthlyReport();
                            break;
                        case 4:
                            updateSchoolRankings();
                            break;
                    }
                });
        builder.show();
    }

    private void addNewSchool() {
        Intent intent = new Intent(this, AddSchoolActivity.class);
        intent.putExtra("admin_id", userId);
        startActivity(intent);
    }

    private void bulkApproveApplications() {
        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Bulk Approve Applications")
                .setMessage("This will approve all pending applications that meet the minimum requirements. Continue?")
                .setPositiveButton("Approve All", (dialog, which) -> {
                    int approved = dbHelper.bulkApproveQualifiedApplications();
                    showToast(approved + " applications approved successfully");
                    loadDashboardData();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void sendBulkNotification() {
        Intent intent = new Intent(this, BulkNotificationActivity.class);
        intent.putExtra("admin_id", userId);
        startActivity(intent);
    }

    private void generateMonthlyReport() {
        showToast("Generating monthly report...");
        // In a real app, this would generate and download/email a report
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simulate report generation
                runOnUiThread(() -> showToast("Monthly report generated and saved"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateSchoolRankings() {
        showToast("Updating school rankings based on latest data...");
        // In a real app, this would run ML algorithms to update rankings
        new Thread(() -> {
            try {
                Thread.sleep(3000); // Simulate ranking update
                runOnUiThread(() -> {
                    showToast("School rankings updated successfully");
                    loadDashboardData();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_refresh) {
            refreshDashboard();
            return true;
        } else if (id == R.id.action_backup) {
            performBackup();
            return true;
        } else if (id == R.id.action_admin_profile) {
            openAdminProfile();
            return true;
        } else if (id == R.id.action_logout) {
            performLogout();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void refreshDashboard() {
        showToast("Refreshing dashboard...");
        loadDashboardData();
        loadRecentAdminActivities();
        activityAdapter.notifyDataSetChanged();
    }

    private void performBackup() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("System Backup")
                .setMessage("This will create a backup of all application data. Continue?")
                .setPositiveButton("Create Backup", (dialog, which) -> {
                    showToast("Creating system backup...");
                    // In a real app, implement actual backup functionality
                    new Thread(() -> {
                        try {
                            Thread.sleep(5000); // Simulate backup process
                            runOnUiThread(() -> showToast("Backup completed successfully"));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openAdminProfile() {
        Intent intent = new Intent(this, AdminProfileActivity.class);
        intent.putExtra("admin_id", userId);
        intent.putExtra("admin_name", userName);
        intent.putExtra("admin_email", userEmail);
        startActivity(intent);
    }

    private void performLogout() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    // Clear saved login data
                    getSharedPreferences("EduZebPrefs", MODE_PRIVATE)
                            .edit()
                            .clear()
                            .apply();
                    
                    // Navigate back to login
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    
                    showToast("Logged out successfully");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to dashboard
        loadDashboardData();
    }
}
