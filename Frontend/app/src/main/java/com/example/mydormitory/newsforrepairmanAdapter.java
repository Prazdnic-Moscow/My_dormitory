package com.example.mydormitory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class newsforrepairmanAdapter extends RecyclerView.Adapter<newsforrepairmanAdapter.NewsViewHolder> {

    private List<newsforrepairman> newsList;
    private OnRepairButtonClickListener listener;
    public interface OnRepairButtonClickListener {
        void onRepairButtonClick(int position, newsforrepairman news);
    }
    public newsforrepairmanAdapter(List<newsforrepairman> newsList) {
        this.newsList = newsList;
    }

    public void setOnRepairButtonClickListener(OnRepairButtonClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.newsforrepairman_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        newsforrepairman news = newsList.get(position);

        holder.typeRepairman.setText(news.getType());
        holder.repairmanBody.setText(news.getBody());
        holder.repairmanDate.setText(news.getDate());
        holder.repairmanRoom.setText("Комната: " + news.getRoom());
        holder.filesContainerForRepairman.removeAllViews();
        if (news.getActivity())
        {
            holder.repairmanActivity.setText("Заказ занят");
            holder.buttonActivity.setText("Отменить заказ");
        }
        else
        {
            holder.repairmanActivity.setText("Заказ свободен");
            holder.buttonActivity.setText("Взять заказ");
        }

        if (news.getNewsPath() != null && !news.getNewsPath().isEmpty()) {
            for (int i = 0; i < news.getNewsPath().size(); i++) {
                addImageToContainer(holder.filesContainerForRepairman, news.getNewsPath().get(i));
            }
        }
        // Обработка клика по кнопке
        holder.buttonActivity.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRepairButtonClick(position, news);
            }
        });
    }

    private void addImageToContainer(LinearLayout container, String imagePath) {
        ImageView imageView = new ImageView(container.getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, 300);
        params.setMargins(0, 0, 16, 15);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setBackgroundColor(0xFFEEEEEE);

        loadImage(imageView, imagePath);

        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(container.getContext(), fullScreenImageActivity.class);
            intent.putExtra("image_path", imagePath);
            container.getContext().startActivity(intent);
        });
        container.addView(imageView);
    }

    private void loadImage(ImageView imageView, String imagePath) {
        new Thread(() -> {
            try {
                Bitmap bitmap = utils.downloadImageFromServer(imagePath);
                if (bitmap != null) {
                    imageView.post(() -> imageView.setImageBitmap(bitmap));
                }
            } catch (Exception e) {
                Log.e("IMAGE_DEBUG", "Load failed: " + imagePath);
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView typeRepairman, repairmanBody, repairmanDate, repairmanRoom, repairmanActivity;
        Button buttonActivity;
        LinearLayout filesContainerForRepairman;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            typeRepairman = itemView.findViewById(R.id.typeRepairman);
            repairmanBody = itemView.findViewById(R.id.repairmanBody);
            repairmanDate = itemView.findViewById(R.id.repairmanDate);
            repairmanRoom = itemView.findViewById(R.id.repairmanRoom);
            filesContainerForRepairman = itemView.findViewById(R.id.filesContainerForRepairman);
            repairmanActivity = itemView.findViewById(R.id.repairmanActivity);
            buttonActivity = itemView.findViewById(R.id.buttonActivity);
        }
    }
}