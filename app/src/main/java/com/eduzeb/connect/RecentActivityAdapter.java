
package com.eduzeb.connect;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecentActivityAdapter extends RecyclerView.Adapter<RecentActivityAdapter.ActivityViewHolder> {

    private List<StudentDashboardActivity.ActivityItem> activities;

    public RecentActivityAdapter(List<StudentDashboardActivity.ActivityItem> activities) {
        this.activities = activities;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        StudentDashboardActivity.ActivityItem activity = activities.get(position);
        
        holder.tvTitle.setText(activity.getTitle());
        holder.tvDescription.setText(activity.getDescription());
        holder.tvTimestamp.setText(activity.getTimestamp());
        holder.ivIcon.setImageResource(activity.getIconResource());
        
        // Set icon tint based on activity type
        if (activity.getTitle().contains("Submitted") || activity.getTitle().contains("Uploaded")) {
            holder.ivIcon.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.success_green));
        } else if (activity.getTitle().contains("Recommendation")) {
            holder.ivIcon.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.primary_blue));
        } else if (activity.getTitle().contains("Update")) {
            holder.ivIcon.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.accent_orange));
        } else {
            holder.ivIcon.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.text_secondary));
        }
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public void updateActivities(List<StudentDashboardActivity.ActivityItem> newActivities) {
        this.activities = newActivities;
        notifyDataSetChanged();
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvTimestamp;
        ImageView ivIcon;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvActivityTitle);
            tvDescription = itemView.findViewById(R.id.tvActivityDescription);
            tvTimestamp = itemView.findViewById(R.id.tvActivityTimestamp);
            ivIcon = itemView.findViewById(R.id.ivActivityIcon);
        }
    }
}
