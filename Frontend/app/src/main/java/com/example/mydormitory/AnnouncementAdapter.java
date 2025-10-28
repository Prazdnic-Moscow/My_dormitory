package com.example.mydormitory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder> {
    
    private List<Announcement> announcements;
    private OnAnnouncementClickListener listener;

    public interface OnAnnouncementClickListener {
        void onAnnouncementClick(Announcement announcement);
    }

    public AnnouncementAdapter(List<Announcement> announcements, OnAnnouncementClickListener listener) {
        this.announcements = announcements != null ? announcements : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_announcement, parent, false);
        return new AnnouncementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        Announcement announcement = announcements.get(position);
        holder.bind(announcement, listener);
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    public void updateAnnouncements(List<Announcement> newAnnouncements) {
        this.announcements = newAnnouncements != null ? newAnnouncements : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void filterByCategory(Announcement.Category category) {
        // В реальном приложении здесь была бы фильтрация
        // Пока просто обновляем весь список
        notifyDataSetChanged();
    }

    static class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView descriptionTextView;
        private TextView locationTextView;
        private TextView categoryTextView;
        private TextView timestampTextView;

        public AnnouncementViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
        }

        public void bind(Announcement announcement, OnAnnouncementClickListener listener) {
            titleTextView.setText(announcement.getTitle());
            descriptionTextView.setText(announcement.getDescription());
            
            // Показываем либо location, либо contact
            if (announcement.getLocation() != null && !announcement.getLocation().isEmpty()) {
                locationTextView.setText(announcement.getLocation());
            } else if (announcement.getContact() != null && !announcement.getContact().isEmpty()) {
                locationTextView.setText(announcement.getContact());
            } else {
                locationTextView.setText("");
            }
            
            categoryTextView.setText(announcement.getCategory().getDisplayName());
            timestampTextView.setText(announcement.getTimestamp());
            
            // Устанавливаем цвет категории
            int categoryColor = ContextCompat.getColor(itemView.getContext(), announcement.getCategory().getColorResource());
            categoryTextView.setBackgroundColor(categoryColor);
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAnnouncementClick(announcement);
                }
            });
        }
    }
}
