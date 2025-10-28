package com.example.mydormitory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DocumentTemplateAdapter extends RecyclerView.Adapter<DocumentTemplateAdapter.DocumentViewHolder> {
    
    private List<DocumentTemplate> documentList;

    public DocumentTemplateAdapter(List<DocumentTemplate> documentList) {
        this.documentList = documentList != null ? documentList : new ArrayList<>();
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_document_template, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        DocumentTemplate document = documentList.get(position);
        holder.bind(document);
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    public void updateDocumentList(List<DocumentTemplate> newDocumentList) {
        this.documentList = newDocumentList != null ? newDocumentList : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class DocumentViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView detailsTextView;
        private TextView downloadTextView;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            detailsTextView = itemView.findViewById(R.id.detailsTextView);
            downloadTextView = itemView.findViewById(R.id.downloadTextView);
        }

        public void bind(DocumentTemplate document) {
            titleTextView.setText(document.getTitle());
            detailsTextView.setText(document.getFileType() + " - " + document.getFileSize());
            
            downloadTextView.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), 
                    "Скачивание: " + document.getTitle(), 
                    Toast.LENGTH_SHORT).show();
                // TODO: Реализовать скачивание файла
            });
        }
    }
}
