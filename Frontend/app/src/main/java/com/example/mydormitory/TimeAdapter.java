package com.example.mydormitory;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.TimeViewHolder> {

    public interface OnTimeSlotClick {
        void onSlotClick(int index);
    }

    private final List<TimeSlot> timeSlots;
    private final OnTimeSlotClick onTimeSlotClick;
    private int selectedStart = -1;
    private int selectedEnd = -1;

    public TimeAdapter(List<TimeSlot> timeSlots, OnTimeSlotClick onTimeSlotClick) {
        this.timeSlots = timeSlots;
        this.onTimeSlotClick = onTimeSlotClick;
    }

    public void setInterval(int start, int end) {
        this.selectedStart = start;
        this.selectedEnd = end;
        notifyDataSetChanged();
    }

    public void reset() {
        selectedStart = -1;
        selectedEnd = -1;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_time_slot, parent, false);
        return new TimeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeViewHolder holder, int position) {
        TimeSlot slot = timeSlots.get(position);
        holder.timeButton.setText(slot.getTime());

        if (slot.isBooked()) {
            // Занятый слот - серый и недоступный
            holder.timeButton.setBackgroundColor(Color.GRAY);
            holder.timeButton.setEnabled(false);
            holder.timeButton.setTextColor(Color.WHITE);
        } else if (position >= selectedStart && position <= selectedEnd) {
            // Выбранный интервал
            holder.timeButton.setBackgroundColor(Color.parseColor("#2196F3"));
            holder.timeButton.setTextColor(Color.WHITE);
            holder.timeButton.setEnabled(true);
        } else {
            // Свободный слот
            holder.timeButton.setBackgroundColor(Color.TRANSPARENT);
            holder.timeButton.setTextColor(Color.BLACK);
            holder.timeButton.setEnabled(true);
        }

        holder.timeButton.setOnClickListener(v -> {
            if (!slot.isBooked()) {
                onTimeSlotClick.onSlotClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    static class TimeViewHolder extends RecyclerView.ViewHolder {
        MaterialButton timeButton;

        TimeViewHolder(View v) {
            super(v);
            timeButton = v.findViewById(R.id.btnTime);
        }
    }
}