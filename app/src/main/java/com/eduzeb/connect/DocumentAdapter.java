
package com.eduzeb.connect;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {
    
    private List<Document> documents;
    private OnDocumentClickListener clickListener;
    private OnDocumentActionListener actionListener;

    public interface OnDocumentClickListener {
        void onDocumentClick(Document document);
    }

    public interface OnDocumentActionListener {
        void onDocumentAction(Document document, String action);
    }

    public DocumentAdapter(List<Document> documents, OnDocumentClickListener clickListener,
                         OnDocumentActionListener actionListener) {
        this.documents = documents;
        this.clickListener = clickListener;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_document_card, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        Document document = documents.get(position);
        
        holder.tvDocumentName.setText(document.getName());
        holder.tvDocumentType.setText(document.getTypeDisplayText());
        holder.tvUploadDate.setText("Uploaded: " + formatDate(document.getUploadedAt()));
        
        // Set document type icon and color
        setDocumentTypeIcon(holder, document.getType());
        
        // Set verification status
        if (document.isVerified()) {
            holder.ivVerificationStatus.setImageResource(R.drawable.ic_verified);
            holder.ivVerificationStatus.setColorFilter(
                holder.itemView.getContext().getResources().getColor(R.color.success_green));
            holder.tvVerificationStatus.setText("Verified");
            holder.tvVerificationStatus.setTextColor(
                holder.itemView.getContext().getResources().getColor(R.color.success_green));
        } else {
            holder.ivVerificationStatus.setImageResource(R.drawable.ic_pending_verification);
            holder.ivVerificationStatus.setColorFilter(
                holder.itemView.getContext().getResources().getColor(R.color.warning_yellow_dark));
            holder.tvVerificationStatus.setText("Pending Verification");
            holder.tvVerificationStatus.setTextColor(
                holder.itemView.getContext().getResources().getColor(R.color.warning_yellow_dark));
        }
        
        // Click listeners
        holder.cardView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onDocumentClick(document);
            }
        });
        
        holder.btnMore.setOnClickListener(v -> showDocumentActions(holder.itemView.getContext(), document));
        
        // View button
        holder.btnView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onDocumentClick(document);
            }
        });
        
        // Share button
        holder.btnShare.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDocumentAction(document, "share");
            }
        });
        
        // Animation
        holder.itemView.setAlpha(0f);
        holder.itemView.animate()
                .alpha(1f)
                .setDuration(300)
                .setStartDelay(position * 50)
                .start();
    }

    private void setDocumentTypeIcon(DocumentViewHolder holder, String type) {
        int iconRes;
        int colorRes;
        
        switch (type.toLowerCase()) {
            case "transcript":
                iconRes = R.drawable.ic_transcript;
                colorRes = R.color.primary_blue;
                break;
            case "reference":
                iconRes = R.drawable.ic_reference;
                colorRes = R.color.success_green;
                break;
            case "certificate":
                iconRes = R.drawable.ic_certificate;
                colorRes = R.color.accent_orange;
                break;
            default:
                iconRes = R.drawable.ic_document;
                colorRes = R.color.text_secondary;
                break;
        }
        
        holder.ivDocumentIcon.setImageResource(iconRes);
        holder.ivDocumentIcon.setColorFilter(
            holder.itemView.getContext().getResources().getColor(colorRes));
    }

    private void showDocumentActions(android.content.Context context, Document document) {
        String[] actions = {"View", "Share", "Rename", "Delete"};
        
        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle(document.getName())
                .setItems(actions, (dialog, which) -> {
                    if (actionListener != null) {
                        switch (which) {
                            case 0:
                                clickListener.onDocumentClick(document);
                                break;
                            case 1:
                                actionListener.onDocumentAction(document, "share");
                                break;
                            case 2:
                                actionListener.onDocumentAction(document, "rename");
                                break;
                            case 3:
                                actionListener.onDocumentAction(document, "delete");
                                break;
                        }
                    }
                })
                .show();
    }

    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateString;
        }
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public void updateDocuments(List<Document> newDocuments) {
        this.documents = newDocuments;
        notifyDataSetChanged();
    }

    static class DocumentViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivDocumentIcon, ivVerificationStatus;
        TextView tvDocumentName, tvDocumentType, tvUploadDate, tvVerificationStatus;
        ImageButton btnMore;
        MaterialButton btnView, btnShare;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardDocument);
            ivDocumentIcon = itemView.findViewById(R.id.ivDocumentIcon);
            ivVerificationStatus = itemView.findViewById(R.id.ivVerificationStatus);
            tvDocumentName = itemView.findViewById(R.id.tvDocumentName);
            tvDocumentType = itemView.findViewById(R.id.tvDocumentType);
            tvUploadDate = itemView.findViewById(R.id.tvUploadDate);
            tvVerificationStatus = itemView.findViewById(R.id.tvVerificationStatus);
            btnMore = itemView.findViewById(R.id.btnMore);
            btnView = itemView.findViewById(R.id.btnView);
            btnShare = itemView.findViewById(R.id.btnShare);
        }
    }
}
