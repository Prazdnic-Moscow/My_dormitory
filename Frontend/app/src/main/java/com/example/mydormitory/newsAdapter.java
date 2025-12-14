package com.example.mydormitory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class newsAdapter extends RecyclerView.Adapter<newsAdapter.NewsViewHolder> {

    private List<news> newsList;
    public newsAdapter(List<news> newsList) {
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        news news = newsList.get(position);

        holder.newsHeader.setText(news.getHeader());
        holder.newsBody.setText(news.getBody());
        // Форматируем даты с разделителем
        String datesText = "Начало: " + news.getDateStart() + " Конец: " + news.getDateEnd();
        holder.newsDateStartAndEnd.setText(datesText);
        holder.newsAuthor.setText(news.getAuthor());
        holder.filesContainerForNews.removeAllViews();
        holder.newsDate.setText(news.getDate());

        if (news.getNewsPath() != null && !news.getNewsPath().isEmpty()) {
            for (int i = 0; i < news.getNewsPath().size(); i++) {
                addImageToContainer(holder.filesContainerForNews, news.getNewsPath().get(i));
            }
        }
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
        TextView newsHeader, newsBody, newsDateStartAndEnd, newsAuthor, newsDate;
        LinearLayout filesContainerForNews;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsHeader = itemView.findViewById(R.id.newsHeader);
            newsBody = itemView.findViewById(R.id.newsBody);
            newsDateStartAndEnd = itemView.findViewById(R.id.newsDateStartAndEnd);
            newsAuthor = itemView.findViewById(R.id.newsAuthor);
            filesContainerForNews = itemView.findViewById(R.id.filesContainerForNews);
            newsDate = itemView.findViewById(R.id.newsDate);
        }
    }
}