
package com.eduzeb.connect;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminApplicationAdapter extends RecyclerView.Adapter<AdminApplicationAdapter.ApplicationViewHolder> {

    private List<Application> applications;
    private OnApplicationClickListener onClickListener;
    private OnApplicationActionListener onActionListener;
    private DatabaseHelper dbHelper;

    public interface OnApplicationClickListener {
        void onApplicationClick(Application application);
    }

    public interface OnApplicationActionListener {
        void onApplicationAction(Application application, String action);
    }

    public AdminApplicationAdapter(List<Application> applications, 
                                 OnApplicationClickListener clickListener,
                                 OnApplicationActionListener actionListener) {
        this.applications = applications;
        this.onClickListener = clickListener;
        this.onActionListener = actionListener;
    }

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_application_card, parent, false);
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(parent.getContext());
        }
        return new ApplicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        Application application = applications.get(position);
        
        // Get student name
        String studentName = dbHelper.getStudentName(application.getUserId());
        holder.tvStudentName.setText(studentName);
        
        holder.tvSchoolName.setText(application.getSchoolName());
        holder.tvSchoolLocation.setText(application.getSchoolLocation());
        
        // Format submission date
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            Date date = inputFormat.parse(application.getSubmissionDate());
            holder.tvSubmissionDate.setText(outputFormat.format(date));
        } catch (Exception e) {
            holder.tvSubmissionDate.setText(application.getSubmissionDate());
        }
        
        // Set status chip
        setupStatusChip(holder.chipStatus, application.getStatus());
        
        // Set priority indicator based on application age and status
        setPriorityIndicator(holder.ivPriority, application);
        
        // Setup action buttons based on status
        setupActionButtons(holder, application);
        
        // Click listener for card
        holder.cardView.setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onApplicationClick(application);
            }
        });
        
        // Add animation
        holder.itemView.setAlpha(0f);
        holder.itemView.animate()
                .alpha(1f)
                .setDuration(300)
                .setStartDelay(position * 50)
                .start();
    }

    private void setupStatusChip(Chip chip, String status) {
        switch (status.toLowerCase()) {
            case "pending":
                chip.setText("Pending Review");
                chip.setChipBackgroundColorResource(R.color.warning_yellow_light);
                chip.setTextColor(chip.getContext().getResources().getColor(R.color.warning_yellow_dark));
                break;
            case "under_review":
                chip.setText("Under Review");
                chip.setChipBackgroundColorResource(R.color.primary_light);
                chip.setTextColor(chip.getContext().getResources().getColor(R.color.primary_blue));
                break;
            case "approved":
                chip.setText("Approved");
                chip.setChipBackgroundColorResource(R.color.success_green_light);
                chip.setTextColor(chip.getContext().getResources().getColor(R.color.success_green));
                break;
            case "rejected":
                chip.setText("Rejected");
                chip.setChipBackgroundColorResource(R.color.error_red_light);
                chip.setTextColor(chip.getContext().getResources().getColor(R.color.error_red));
                break;
            default:
                chip.setText("Unknown");
                chip.setChipBackgroundColorResource(R.color.light_gray);
                chip.setTextColor(chip.getContext().getResources().getColor(R.color.text_secondary));
        }
    }

    private void setPriorityIndicator(ImageView priorityIndicator, Application application) {
        // Calculate days since submission
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date submissionDate = sdf.parse(application.getSubmissionDate());
            long daysSince = (System.currentTimeMillis() - submissionDate.getTime()) / (1000 * 60 * 60 * 24);
            
            if (application.getStatus().equals("pending")) {
                if (daysSince > 7) {
                    // High priority - pending for more than a week
                    priorityIndicator.setImageResource(R.drawable.ic_priority_high);
                    priorityIndicator.setColorFilter(priorityIndicator.getContext()
                            .getResources().getColor(R.color.error_red));
                    priorityIndicator.setVisibility(View.VISIBLE);
                } else if (daysSince > 3) {
                    // Medium priority - pending for more than 3 days
                    priorityIndicator.setImageResource(R.drawable.ic_priority_medium);
                    priorityIndicator.setColorFilter(priorityIndicator.getContext()
                            .getResources().getColor(R.color.warning_yellow_dark));
                    priorityIndicator.setVisibility(View.VISIBLE);
                } else {
                    priorityIndicator.setVisibility(View.GONE);
                }
            } else {
                priorityIndicator.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            priorityIndicator.setVisibility(View.GONE);
        }
    }

    private void setupActionButtons(ApplicationViewHolder holder, Application application) {
        String status = application.getStatus().toLowerCase();
        
        // Reset button visibility
        holder.btnApprove.setVisibility(View.GONE);
        holder.btnReject.setVisibility(View.GONE);
        holder.btnReview.setVisibility(View.GONE);
        
        switch (status) {
            case "pending":
                holder.btnReview.setVisibility(View.VISIBLE);
                holder.btnApprove.setVisibility(View.VISIBLE);
                holder.btnReject.setVisibility(View.VISIBLE);
                break;
            case "under_review":
                holder.btnApprove.setVisibility(View.VISIBLE);
                holder.btnReject.setVisibility(View.VISIBLE);
                break;
            case "approved":
                // No action buttons for approved applications
                break;
            case "rejected":
                // Option to reconsider
                holder.btnReview.setVisibility(View.VISIBLE);
                holder.btnReview.setText("Reconsider");
                break;
        }
        
        // Set click listeners
        holder.btnApprove.setOnClickListener(v -> {
            if (onActionListener != null) {
                onActionListener.onApplicationAction(application, "approve");
            }
        });
        
        holder.btnReject.setOnClickListener(v -> {
            if (onActionListener != null) {
                onActionListener.onApplicationAction(application, "reject");
            }
        });
        
        holder.btnReview.setOnClickListener(v -> {
            if (onActionListener != null) {
                onActionListener.onApplicationAction(application, "review");
            }
        });
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    public void updateApplications(List<Application> newApplications) {
        this.applications = newApplications;
        notifyDataSetChanged();
    }

    static class ApplicationViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvStudentName, tvSchoolName, tvSchoolLocation, tvSubmissionDate;
        Chip chipStatus;
        ImageView ivPriority;
        MaterialButton btnApprove, btnReject, btnReview;

        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardApplication);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvSchoolName = itemView.findViewById(R.id.tvSchoolName);
        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardApplication);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvSchoolName = itemView.findViewById(R.id.tvSchoolName);
            tvSchoolLocation = itemView.findViewById(R.id.tvSchoolLocation);
            tvSubmissionDate = itemView.findViewById(R.id.tvSubmissionDate);
            chipStatus = itemView.findViewById(R.id.chipStatus);
            ivPriority = itemView.findViewById(R.id.ivPriority);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnReview = itemView.findViewById(R.id.btnReview);
        }
    }
}
