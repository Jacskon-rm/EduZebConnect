
// Replace the simple placeholder with the full implementation
class DocumentsActivity extends AppCompatActivity {

    private CardView cardUploadPhoto, cardUploadFile, cardUploadProgress;
    private MaterialButton btnUploadTranscript, btnUploadReference, btnUploadCertificate, btnUploadOther;
    private RecyclerView rvDocuments;
    private LinearLayout layoutEmptyDocuments;
    private FloatingActionButton fabUploadDocument;
    private ProgressBar progressUpload;
    private TextView tvUploadStatus;
    
    private DocumentAdapter documentAdapter;
    private DatabaseHelper dbHelper;
    private List<Document> userDocuments;
    
    private int userId;
    private String currentDocumentType = "";
    private static final int PERMISSION_REQUEST_CODE = 100;

    // Activity result launchers
    private ActivityResultLauncher<Intent> filePickerLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        package com.eduzeb.connect;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DocumentsActivity extends AppCompatActivity {

    private CardView cardUploadPhoto, cardUploadFile, cardUploadProgress;
    private MaterialButton btnUploadTranscript, btnUploadReference, btnUploadCertificate, btnUploadOther;
    private RecyclerView rvDocuments;
    private LinearLayout layoutEmptyDocuments;
    private FloatingActionButton fabUploadDocument;
    private ProgressBar progressUpload;
    private TextView tvUploadStatus;
    
    private DocumentAdapter documentAdapter;
    private DatabaseHelper dbHelper;
    private List<Document> userDocuments;
    
    private int userId;
    private String currentDocumentType = "";
    private static final int PERMISSION_REQUEST_CODE = 100;

    // Activity result launchers
    private ActivityResultLauncher<Intent> filePickerLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        initializeViews();
        setupToolbar();
        getUserData();
        setupActivityLaunchers();
        setupRecyclerView();
        setupClickListeners();
        loadUserDocuments();
    }

    private void initializeViews() {
        cardUploadPhoto = findViewById(R.id.cardUploadPhoto);
        cardUploadFile = findViewById(R.id.cardUploadFile);
        cardUploadProgress = findViewById(R.id.cardUploadProgress);
        
        btnUploadTranscript = findViewById(R.id.btnUploadTranscript);
        btnUploadReference = findViewById(R.id.btnUploadReference);
        btnUploadCertificate = findViewById(R.id.btnUploadCertificate);
        btnUploadOther = findViewById(R.id.btnUploadOther);
        
        rvDocuments = findViewById(R.id.rvDocuments);
        layoutEmptyDocuments = findViewById(R.id.layoutEmptyDocuments);
        fabUploadDocument = findViewById(R.id.fabUploadDocument);
        progressUpload = findViewById(R.id.progressUpload);
        tvUploadStatus = findViewById(R.id.tvUploadStatus);
        
        dbHelper = new DatabaseHelper(this);
        userDocuments = new ArrayList<>();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Documents");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void getUserData() {
        Intent intent = getIntent();
        userId = intent.getIntExtra("user_id", -1);
    }

    private void setupActivityLaunchers() {
        // File picker launcher
        filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri fileUri = result.getData().getData();
                    if (fileUri != null) {
                        handleFileSelection(fileUri);
                    }
                }
            }
        );

        // Camera launcher
        cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Handle camera result
                    showToast("Photo captured successfully!");
                    // In a real app, you would process the captured image
                }
            }
        );

        // Permission launcher
        permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    showToast("Camera permission is required to take photos");
                }
            }
        );
    }

    private void setupRecyclerView() {
        documentAdapter = new DocumentAdapter(userDocuments, this::onDocumentClick, this::onDocumentAction);
        rvDocuments.setLayoutManager(new LinearLayoutManager(this));
        rvDocuments.setAdapter(documentAdapter);
    }

    private void setupClickListeners() {
        cardUploadPhoto.setOnClickListener(v -> checkCameraPermissionAndOpen());
        cardUploadFile.setOnClickListener(v -> openFilePicker("general"));
        
        btnUploadTranscript.setOnClickListener(v -> {
            currentDocumentType = "transcript";
            openFilePicker("transcript");
        });
        
        btnUploadReference.setOnClickListener(v -> {
            currentDocumentType = "reference";
            openFilePicker("reference");
        });
        
        btnUploadCertificate.setOnClickListener(v -> {
            currentDocumentType = "certificate";
            openFilePicker("certificate");
        });
        
        btnUploadOther.setOnClickListener(v -> {
            currentDocumentType = "other";
            openFilePicker("other");
        });
        
        fabUploadDocument.setOnClickListener(v -> showUploadOptions());
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(cameraIntent);
        } else {
            showToast("No camera app available");
        }
    }

    private void openFilePicker(String documentType) {
        currentDocumentType = documentType;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
            "application/pdf",
            "image/jpeg",
            "image/png",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        });
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        
        try {
            filePickerLauncher.launch(Intent.createChooser(intent, "Select Document"));
        } catch (Exception e) {
            showToast("No file manager app available");
        }
    }

    private void handleFileSelection(Uri fileUri) {
        try {
            String fileName = getFileName(fileUri);
            String fileSize = getFileSize(fileUri);
            
            // Show upload progress
            showUploadProgress(true);
            
            // Simulate file processing and upload
            new Thread(() -> {
                try {
                    // Copy file to app directory
                    File internalFile = copyFileToInternal(fileUri, fileName);
                    
                    // Simulate upload progress
                    for (int progress = 0; progress <= 100; progress += 10) {
                        final int currentProgress = progress;
                        runOnUiThread(() -> {
                            progressUpload.setProgress(currentProgress);
                            tvUploadStatus.setText("Uploading... " + currentProgress + "%");
                        });
                        Thread.sleep(200);
                    }
                    
                    // Save to database
                    boolean success = dbHelper.uploadDocument(userId, fileName, currentDocumentType, 
                            internalFile.getAbsolutePath());
                    
                    runOnUiThread(() -> {
                        showUploadProgress(false);
                        if (success) {
                            showToast("Document uploaded successfully!");
                            loadUserDocuments();
                            
                            // Create notification
                            dbHelper.createNotification(userId, "Document Uploaded", 
                                    "Your " + currentDocumentType + " has been uploaded successfully");
                        } else {
                            showToast("Failed to upload document");
                        }
                    });
                    
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        showUploadProgress(false);
                        showToast("Error uploading file: " + e.getMessage());
                    });
                }
            }).start();
            
        } catch (Exception e) {
            showToast("Error processing file: " + e.getMessage());
        }
    }

    private String getFileName(Uri uri) {
        String fileName = "document";
        try {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex);
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }

    private String getFileSize(Uri uri) {
        try {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (sizeIndex != -1) {
                    long size = cursor.getLong(sizeIndex);
                    cursor.close();
                    return formatFileSize(size);
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown size";
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        return (bytes / (1024 * 1024)) + " MB";
    }

    private File copyFileToInternal(Uri uri, String fileName) throws Exception {
        File documentsDir = new File(getFilesDir(), "documents");
        if (!documentsDir.exists()) {
            documentsDir.mkdirs();
        }
        
        File destinationFile = new File(documentsDir, System.currentTimeMillis() + "_" + fileName);
        
        InputStream inputStream = getContentResolver().openInputStream(uri);
        FileOutputStream outputStream = new FileOutputStream(destinationFile);
        
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        
        inputStream.close();
        outputStream.close();
        
        return destinationFile;
    }

    private void showUploadProgress(boolean show) {
        cardUploadProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            progressUpload.setProgress(0);
            tvUploadStatus.setText("Preparing upload...");
        }
    }

    private void loadUserDocuments() {
        userDocuments = dbHelper.getUserDocuments(userId);
        updateDocumentsUI();
    }

    private void updateDocumentsUI() {
        documentAdapter.updateDocuments(userDocuments);
        
        if (userDocuments.isEmpty()) {
            rvDocuments.setVisibility(View.GONE);
            layoutEmptyDocuments.setVisibility(View.VISIBLE);
        } else {
            rvDocuments.setVisibility(View.VISIBLE);
            layoutEmptyDocuments.setVisibility(View.GONE);
        }
    }

    private void onDocumentClick(Document document) {
        // Open document viewer
        Intent intent = new Intent(this, DocumentViewerActivity.class);
        intent.putExtra("document_id", document.getId());
        intent.putExtra("package com.eduzeb.connect;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class ManageSchoolsActivity extends AppCompatActivity {

    private TextInputEditText etSearchSchools;
    private MaterialButton btnAddSchool, btnExportSchools;
    private RecyclerView rvSchools;
    private LinearLayout layoutEmptyState;
    private FloatingActionButton fabAddSchool;
    
    private AdminSchoolAdapter schoolAdapter;
    private DatabaseHelper dbHelper;
    private List<School> allSchools;
    private List<School> filteredSchools;
    
    private int adminId;
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_schools);

        initializeViews();
        setupToolbar();
        getUserData();
        setupRecyclerView();
        setupClickListeners();
        setupSearch();
        loadSchools();
    }

    private void initializeViews() {
        etSearchSchools = findViewById(R.id.etSearchSchools);
        btnAddSchool = findViewById(R.id.btnAddSchool);
        btnExportSchools = findViewById(R.id.btnExportSchools);
        rvSchools = findViewById(R.id.rvSchools);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        fabAddSchool = findViewById(R.id.fabAddSchool);
        
        dbHelper = new DatabaseHelper(this);
        allSchools = new ArrayList<>();
        filteredSchools = new ArrayList<>();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manage Schools");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void getUserData() {
        Intent intent = getIntent();
        adminId = intent.getIntExtra("admin_id", -1);
    }

    private void setupRecyclerView() {
        schoolAdapter = new AdminSchoolAdapter(filteredSchools, 
                this::onSchoolClick, this::onSchoolAction);
        rvSchools.setLayoutManager(new LinearLayoutManager(this));
        rvSchools.setAdapter(schoolAdapter);
    }

    private void setupClickListeners() {
        btnAddSchool.setOnClickListener(v -> addNewSchool());
        fabAddSchool.setOnClickListener(v -> addNewSchool());
        btnExportSchools.setOnClickListener(v -> exportSchoolData());
    }

    private void setupSearch() {
        etSearchSchools.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().trim().toLowerCase();
                filterSchools();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadSchools() {
        allSchools = dbHelper.getAllSchools();
        filterSchools();
    }

    private void filterSchools() {
        filteredSchools.clear();
        
        for (School school : allSchools) {
            boolean matchesSearch = currentSearchQuery.isEmpty() ||
                    school.getName().toLowerCase().contains(currentSearchQuery) ||
                    school.getLocation().toLowerCase().contains(currentSearchQuery) ||
                    school.getType().toLowerCase().contains(currentSearchQuery);
            
            if (matchesSearch) {
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
        } else {
            rvSchools.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }

    private void onSchoolClick(School school) {
        Intent intent = new Intent(this, SchoolDetailActivity.class);
        intent.putExtra("school_id", school.getId());
        intent.putExtra("is_admin", true);
        intent.putExtra("admin_id", adminId);
        startActivity(intent);
    }

    private void onSchoolAction(School school, String action) {
        switch (action) {
            case "edit":
                editSchool(school);
                break;
            case "delete":
                showDeleteConfirmation(school);
                break;
            case "view_applications":
                viewSchoolApplications(school);
                break;
        }
    }

    private void addNewSchool() {
        Intent intent = new Intent(this, AddEditSchoolActivity.class);
        intent.putExtra("admin_id", adminId);
        intent.putExtra("mode", "add");
        startActivity(intent);
    }

    private void editSchool(School school) {
        Intent intent = new Intent(this, AddEditSchoolActivity.class);
        intent.putExtra("admin_id", adminId);
        intent.putExtra("mode", "edit");
        intent.putExtra("school_id", school.getId());
        startActivity(intent);
    }

    private void showDeleteConfirmation(School school) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete School")
                .setMessage("Are you sure you want to delete " + school.getName() + "? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean success = dbHelper.deleteSchool(school.getId());
                    if (success) {
                        showToast("School deleted successfully");
                        loadSchools();
                    } else {
                        showToast("Cannot delete school with existing applications");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void viewSchoolApplications(School school) {
        Intent intent = new Intent(this, SchoolApplicationsActivity.class);
        intent.putExtra("school_id", school.getId());
        intent.putExtra("school_name", school.getName());
        intent.putExtra("admin_id", adminId);
        startActivity(intent);
    }

    private void exportSchoolData() {
        showToast("Exporting school data...");
        // In a real app, this would export to CSV/Excel
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                runOnUiThread(() -> showToast("School data exported successfully"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSchools(); // Refresh data when returning
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

// Simple placeholder activities to prevent crashes
class MyApplicationsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.activity_list_item);
        setTitle("My Applications");
        Toast.makeText(this, "My Applications - Coming Soon!", Toast.LENGTH_SHORT).show();
    }
}

class RecommendationsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.activity_list_item);
        setTitle("Recommendations");
        Toast.makeText(this, "Recommendations - Coming Soon!", Toast.LENGTH_SHORT).show();
    }
}

class DocumentsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.activity_list_item);
        setTitle("My Documents");
        Toast.makeText(this, "Documents - Coming Soon!", Toast.LENGTH_SHORT).show();
    }
}

class NotificationsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.activity_list_item);
        setTitle("Notifications");
        Toast.makeText(this, "Notifications - Coming Soon!", Toast.LENGTH_SHORT).show();
    }
}

class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.activity_list_item);
        setTitle("My Profile");
        Toast.makeText(this, "Profile - Coming Soon!", Toast.LENGTH_SHORT).show();
    }
}

class NewApplicationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.activity_list_item);
        setTitle("New Application");
        Toast.makeText(this, "New Application - Coming Soon!", Toast.LENGTH_SHORT).show();
    }
}

class ForgotPasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.activity_list_item);
        setTitle("Forgot Password");
        Toast.makeText(this, "Forgot Password - Coming Soon!", Toast.LENGTH_SHORT).show();
    }
}

class SchoolDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.activity_list_item);
        setTitle("School Details");
        Toast.makeText(this, "School Details - Coming Soon!", Toast.LENGTH_SHORT).show();
    }
}

// Admin activities
class StudentAnalyticsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.activity_list_item);
        setTitle("Student Analytics");
        Toast.makeText(this, "Student Analytics - Coming Soon!", Toast.LENGTH_SHORT).show();
    }
}

class ReportsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.activity_list_item);
        setTitle("Reports");
        Toast.makeText(this, "Reports - Coming Soon!", Toast.LENGTH_SHORT).show();
    }
}

class AdminNotificationsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.activity_list_item);
        setTitle("Admin Notifications");
        Toast.makeText(this, "Admin Notifications - Coming Soon!", Toast.LENGTH_SHORT).show();
    }
}

class AdminSettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.activity_list_item);
        setTitle("Admin Settings");
        Toast.makeText(this, "Admin Settings - Coming Soon!", Toast.LENGTH_SHORT).show();
    }
}

class AddSchoolActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.activity_list_item);
        setTitle("Add School");
        Toast.makeText(this, "Add School - Coming Soon!", Toast.LENGTH_SHORT).show();
    }
}

class BulkNotificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.activity_list_item);
        setTitle("Bulk Notification");
        Toast.makeText(this, "Bulk Notification - Coming Soon!", Toast.LENGTH_SHORT).show();
    }
}

class AdminProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.activity_list_item);
        setTitle("Admin Profile");
        Toast.makeText(this, "Admin Profile - Coming Soon!", Toast.LENGTH_SHORT).show();
    }
}

class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.activity_list_item);
        setTitle("Settings");
        Toast.makeText(this, "Settings - Coming Soon!", Toast.LENGTH_SHORT).show();
    }
}

class ApplicationDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.activity_list_item);
        setTitle("Application Details");
        Toast.makeText(this, "Application Details - Coming Soon!", Toast.LENGTH_SHORT).show();
    }
}

class AddEditSchoolActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.activity_list_item);
        setTitle("Add/Edit School");
        Toast.makeText(this, "Add/Edit School - Coming Soon!", Toast.LENGTH_SHORT).show();
    }
}

class SchoolApplicationsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.activity_list_item);
        setTitle("School Applications");
        Toast.makeText(this, "School Applications - Coming Soon!", Toast.LENGTH_SHORT).show();
    }
}

class AdminSchoolAdapter extends RecyclerView.Adapter<AdminSchoolAdapter.ViewHolder> {
    private List<School> schools;
    private OnSchoolClickListener clickListener;
    private OnSchoolActionListener actionListener;

    public interface OnSchoolClickListener {
        void onSchoolClick(School school);
    }

    public interface OnSchoolActionListener {
        void onSchoolAction(School school, String action);
    }

    public AdminSchoolAdapter(List<School> schools, OnSchoolClickListener clickListener, 
                            OnSchoolActionListener actionListener) {
        this.schools = schools;
        this.clickListener = clickListener;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        School school = schools.get(position);
        ((TextView)holder.itemView.findViewById(android.R.id.text1)).setText(school.getName());
        ((TextView)holder.itemView.findViewById(android.R.id.text2)).setText(school.getLocation());
        
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onSchoolClick(school);
            }
        });
    }

    @Override
    public int getItemCount() {
        return schools.size();
    }

    public void updateSchools(List<School> newSchools) {
        this.schools = newSchools;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
