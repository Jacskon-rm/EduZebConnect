
package com.eduzeb.connect;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SchoolAdapter extends RecyclerView.Adapter<SchoolAdapter.SchoolViewHolder> {

    private List<School> schools;
    private OnSchoolClickListener onSchoolClickListener;

    public interface OnSchoolClickListener {
        void onSchoolClick(School school);
    }

    public SchoolAdapter(List<School> schools, OnSchoolClickListener listener) {
        this.schools = schools;
        this.onSchoolClickListener = listener;
    }

    @NonNull
    @Override
    public SchoolViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_school_card, parent, false);
        return new SchoolViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SchoolViewHolder holder, int position) {
        School school = schools.get(position);
        
        holder.tvSchoolName.setText(school.getName());
        holder.tvSchoolLocation.setText(school.getLocation());
        holder.tvSchoolType.setText(school.getType().substring(0, 1).toUpperCase() + 
            school.getType().substring(1));
        
        // Set rating
        holder.ratingBar.setRating((float) school.getRating());
        holder.tvRating.setText(String.format(Locale.getDefault(), "%.1f", school.getRating()));
        
        // Set fees
        if (school.getFees() == 0) {
            holder.tvFees.setText("Free");
            holder.tvFees.setTextColor(holder.itemView.getContext()
                .getResources().getColor(R.color.success_green));
        } else {
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            holder.tvFees.setText(formatter.format(school.getFees()));
            holder.tvFees.setTextColor(holder.itemView.getContext()
                .getResources().getColor(R.color.text_primary));
        }
        
        // Set description
        holder.tvDescription.setText(school.getDescription());
        
        // Set school type badge color
        int badgeColor;
        if (school.getType().equals("public")) {
            badgeColor = holder.itemView.getContext().getResources().getColor(R.color.primary_blue);
        } else {
            badgeColor = holder.itemView.getContext().getResources().getColor(R.color.accent_orange);
        }
        holder.tvSchoolType.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(badgeColor));
        
        // Set school icon based on type
        if (school.getType().equals("public")) {
            holder.ivSchoolIcon.setImageResource(R.drawable.ic_school_public);
        } else {
            holder.ivSchoolIcon.setImageResource(R.drawable.ic_school_private);
        }
        
        // Click listener
        holder.cardView.setOnClickListener(v -> {
            if (onSchoolClickListener != null) {
                onSchoolClickListener.onSchoolClick(school);
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

    @Override
    public int getItemCount() {
        return schools.size();
    }

    public void updateSchools(List<School> newSchools) {
        this.schools = newSchools;
        notifyDataSetChanged();
    }

    static class SchoolViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivSchoolIcon;
        TextView tvSchoolName, tvSchoolLocation, tvSchoolType, tvRating, tvFees, tvDescription;
        RatingBar ratingBar;

        public SchoolViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardSchool);
            ivSchoolIcon = itemView.findViewById(R.id.ivSchoolIcon);
            tvSchoolName = itemView.findViewById(R.id.tvSchoolName);
            tvSchoolLocation = itemView.findViewById(R.id.tvSchoolLocation);
            tvSchoolType = itemView.findViewById(R.id.tvSchoolType);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvFees = itemView.findViewById(R.id.tvFees);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
