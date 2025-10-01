
package com.eduzeb.connect;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
                    showToast("Photo captured successfully!");
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
            addSampleDocument();
        });
        
        btnUploadReference.setOnClickListener(v -> {
            currentDocumentType = "reference";
            addSampleDocument();
        });
        
        btnUploadCertificate.setOnClickListener(v -> {
            currentDocumentType = "certificate";
            addSampleDocument();
        });
        
        btnUploadOther.setOnClickListener(v -> {
            currentDocumentType = "other";
            addSampleDocument();
        });
        
        fabUploadDocument.setOnClickListener(v -> showUploadOptions());
    }

    private void checkCameraPermissionAndOpen() {
        showToast("Camera functionality - Feature coming soon!");
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
        showToast("File picker - Feature coming soon!");
        addSampleDocument();
    }

    private void handleFileSelection(Uri fileUri) {
        showToast("File selected: " + fileUri.toString());
        addSampleDocument();
    }

    private void addSampleDocument() {
        boolean success = dbHelper.uploadDocument(userId, 
            "Sample_" + currentDocumentType + ".pdf", 
            currentDocumentType, 
            "/internal/sample_path");
        
        if (success) {
            showToast("Document uploaded successfully!");
            dbHelper.createNotification(userId, "Document Uploaded", 
                "Your " + currentDocumentType + " has been uploaded successfully");
            loadUserDocuments();
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
        showToast("Viewing: " + document.getName());
    }

    private void onDocumentAction(Document document, String action) {
        switch (action) {
            case "delete":
                showDeleteConfirmation(document);
                break;
            case "share":
                showToast("Sharing: " + document.getName());
                break;
            case "rename":
                showToast("Rename functionality - Coming soon!");
                break;
        }
    }

    private void showDeleteConfirmation(Document document) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Document")
                .setMessage("Are you sure you want to delete \"" + document.getName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean success = dbHelper.deleteDocument(document.getId());
                    if (success) {
                        showToast("Document deleted successfully");
                        loadUserDocuments();
                    } else {
                        showToast("Failed to delete document");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showUploadOptions() {
        String[] options = {"Academic Transcript", "Reference Letter", "Birth Certificate", "Other Document"};

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Upload Document Type")
                .setItems(options, (dialog, which) -> {
                    String[] types = {"transcript", "reference", "certificate", "other"};
                    currentDocumentType = types[which];
                    addSampleDocument();
                })
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserDocuments();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
