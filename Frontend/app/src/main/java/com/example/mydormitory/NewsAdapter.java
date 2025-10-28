package com.example.mydormitory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<News> newsList;

    public NewsAdapter(List<News> newsList) {
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = newsList.get(position);
        holder.bind(news);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public void updateNewsList(List<News> newNewsList) {
        this.newsList = newNewsList;
        notifyDataSetChanged();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewActionDate;
        private TextView textViewTitle;
        private TextView textViewDescription;
        private TextView textViewAuthor;
        private TextView textViewPublishDate;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewActionDate = itemView.findViewById(R.id.textViewActionDate);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            textViewPublishDate = itemView.findViewById(R.id.textViewPublishDate);
        }

        public void bind(News news) {
            textViewActionDate.setText(news.getActionDate());
            textViewTitle.setText(news.getTitle());
            textViewDescription.setText(news.getDescription());
            textViewAuthor.setText(news.getAuthor());
            textViewPublishDate.setText(news.getFormattedPublishDate());
        }
    }
}
