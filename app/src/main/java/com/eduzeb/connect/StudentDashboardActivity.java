
package com.eduzeb.connect;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class StudentDashboardActivity extends AppCompatActivity {

    private TextView tvStudentName, tvActiveApplications, tvRecommendations, tvNotificationBadge;
    private CardView cardSearchSchools, cardMyApplications, cardRecommendations, 
                     cardDocuments, cardNotifications, cardProfile;
    private FloatingActionButton fabNewApplication;
    private RecyclerView rvRecentActivity;
    private RecentActivityAdapter activityAdapter;
    private DatabaseHelper dbHelper;
    
    private int userId;
    private String userName, userEmail;
    private List<ActivityItem> recentActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        initializeViews();
        setupToolbar();
        getUserData();
        setupClickListeners();
        loadDashboardData();
        setupRecentActivity();
    }

    private void initializeViews() {
        tvStudentName = findViewById(R.id.tvStudentName);
        tvActiveApplications = findViewById(R.id.tvActiveApplications);
        tvRecommendations = findViewById(R.id.tvRecommendations);
        tvNotificationBadge = findViewById(R.id.tvNotificationBadge);
        
        cardSearchSchools = findViewById(R.id.cardSearchSchools);
        cardMyApplications = findViewById(R.id.cardMyApplications);
        cardRecommendations = findViewById(R.id.cardRecommendations);
        cardDocuments = findViewById(R.id.cardDocuments);
        cardNotifications = findViewById(R.id.cardNotifications);
        cardProfile = findViewById(R.id.cardProfile);
        
        fabNewApplication = findViewById(R.id.fabNewApplication);
        rvRecentActivity = findViewById(R.id.rvRecentActivity);
        
        dbHelper = new DatabaseHelper(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("EduZebConnect");
        }
    }

    private void getUserData() {
        Intent intent = getIntent();
        userId = intent.getIntExtra("user_id", -1);
        userName = intent.getStringExtra("user_name");
        userEmail = intent.getStringExtra("user_email");
        
        if (userName != null && !userName.isEmpty()) {
            tvStudentName.setText(userName);
        } else {
            tvStudentName.setText("Student");
        }
    }

    private void setupClickListeners() {
        cardSearchSchools.setOnClickListener(v -> {
            startActivity(new Intent(this, SearchSchoolsActivity.class));
        });

        cardMyApplications.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyApplicationsActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        cardRecommendations.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecommendationsActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        cardDocuments.setOnClickListener(v -> {
            Intent intent = new Intent(this, DocumentsActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        cardNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationsActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        cardProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("user_name", userName);
            intent.putExtra("user_email", userEmail);
            startActivity(intent);
        });

        fabNewApplication.setOnClickListener(v -> {
            Intent intent = new Intent(this, NewApplicationActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });
    }

    private void loadDashboardData() {
        // Load active applications count
        int activeApps = dbHelper.getActiveApplicationsCount(userId);
        tvActiveApplications.setText(String.valueOf(activeApps));
        
        // Load recommendations count
        int recommendationsCount = dbHelper.getRecommendationsCount(userId);
        tvRecommendations.setText(String.valueOf(recommendationsCount));
        
        // Load notification count
        int notificationCount = dbHelper.getUnreadNotificationsCount(userId);
        if (notificationCount > 0) {
            tvNotificationBadge.setText(String.valueOf(notificationCount));
            tvNotificationBadge.setVisibility(View.VISIBLE);
        } else {
            tvNotificationBadge.setVisibility(View.GONE);
        }
    }

    private void setupRecentActivity() {
        recentActivities = new ArrayList<>();
        loadRecentActivities();
        
        activityAdapter = new RecentActivityAdapter(recentActivities);
        rvRecentActivity.setLayoutManager(new LinearLayoutManager(this));
        rvRecentActivity.setAdapter(activityAdapter);
    }

    private void loadRecentActivities() {
        // Sample recent activities - replace with database calls
        recentActivities.add(new ActivityItem(
            "Application Submitted",
            "Springfield High School application submitted successfully",
            "2 hours ago",
            R.drawable.ic_check_circle
        ));
        
        recentActivities.add(new ActivityItem(
            "New Recommendation",
            "Oakwood Academy matches your profile",
            "1 day ago",
            R.drawable.ic_lightbulb
        ));
        
        recentActivities.add(new ActivityItem(
            "Document Uploaded",
            "Grade report uploaded to your profile",
            "3 days ago",
            R.drawable.ic_upload
        ));
        
        recentActivities.add(new ActivityItem(
            "Application Update",
            "Riverside High School reviewed your application",
            "1 week ago",
            R.drawable.ic_update
        ));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_refresh) {
            refreshDashboard();
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            performLogout();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void refreshDashboard() {
        Toast.makeText(this, "Refreshing dashboard...", Toast.LENGTH_SHORT).show();
        loadDashboardData();
        loadRecentActivities();
        activityAdapter.notifyDataSetChanged();
    }

    private void performLogout() {
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
        
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to dashboard
        loadDashboardData();
    }

    // Inner class for activity items
    public static class ActivityItem {
        private String title;
        private String description;
        private String timestamp;
        private int iconResource;

        public ActivityItem(String title, String description, String timestamp, int iconResource) {
            this.title = title;
            this.description = description;
            this.timestamp = timestamp;
            this.iconResource = iconResource;
        }

        // Getters
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getTimestamp() { return timestamp; }
        public int getIconResource() { return iconResource; }
    }
}
