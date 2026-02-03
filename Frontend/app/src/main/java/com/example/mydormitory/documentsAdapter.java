package com.example.mydormitory;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class documentsAdapter extends RecyclerView.Adapter<documentsAdapter.DocumentsViewHolder> {

    private List<documents> documentsList;
    private Context context;

    public documentsAdapter(List<documents> documentsList, Context context) {
        this.documentsList = documentsList;
        this.context = context;
    }

    public interface OnDocumentClickListener {
        void onDeleteClick(documents document, int position);
    }

    private OnDocumentClickListener listener;

    public void setOnDocumentClickListener(OnDocumentClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public DocumentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.documents_item, parent, false);
        return new DocumentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentsViewHolder holder, int position) {
        documents document = documentsList.get(position);

        holder.documentsBody.setText(document.getBody());
        holder.documentsDate.setText(document.getDate());
        holder.filesContainerForDocuments.removeAllViews();

        if (document.getDocumentsPath() != null) {
            for (String filePath : document.getDocumentsPath()) {
                addFileToContainer(holder.filesContainerForDocuments, filePath);
            }
        }

        // 👇 КЛИК НА УДАЛЕНИЕ
        holder.btnDeleteFromDocument.setOnClickListener(v -> {
            if (listener != null) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(document, pos);
                }
            }
        });
    }


    private void addFileToContainer(LinearLayout container, String filePath) {
        ImageView imageView = new ImageView(container.getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, 300);
        params.setMargins(0, 0, 16, 15);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setBackground(null);

        // ПРОВЕРКА ТИПА ФАЙЛА
        if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg") || filePath.endsWith(".png"))
        {
            loadImage(imageView, filePath);
        }
        else
        {
            if (filePath.endsWith(".pdf"))
            {
                imageView.setImageResource(R.drawable.ic_pdf);
            }
            else if (filePath.endsWith(".doc") || filePath.endsWith(".docx"))
            {
                imageView.setImageResource(R.drawable.ic_word);
            }
            else
            {
                imageView.setImageResource(R.drawable.ic_downloads);
            }
        }

        // СКАЧИВАНИЕ ПРИ НАЖАТИИ
        imageView.setOnClickListener(v -> {
            String fileName = filePath.substring(filePath.lastIndexOf("/"));
            String fullUrl = "http://10.0.2.2:3000/file" + filePath;

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fullUrl));
            request.setTitle(fileName);
            request.setDescription("Скачивание файла");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            // MIME тип тоже можно определить прямо здесь
            if (filePath.endsWith(".pdf"))
            {
                request.setMimeType("application/pdf");
            }
            else if (filePath.endsWith(".doc") || filePath.endsWith(".docx"))
            {
                request.setMimeType("application/msword");
            }
            else if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg"))
            {
                request.setMimeType("image/jpeg");
            }
            else if (filePath.endsWith(".png"))
            {
                request.setMimeType("image/png");
            }

            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            dm.enqueue(request);

            Toast.makeText(context, "Скачивание: " + fullUrl, Toast.LENGTH_SHORT).show();
        });

        container.addView(imageView);
    }

    private void loadImage(ImageView imageView, String imagePath) {
        new Thread(() -> {
            try {
                android.graphics.Bitmap bitmap = utils.downloadImageFromServer(imagePath);
                if (bitmap != null) {
                    imageView.post(() -> imageView.setImageBitmap(bitmap));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return documentsList.size();
    }

    public static class DocumentsViewHolder extends RecyclerView.ViewHolder {
        TextView documentsBody, documentsDate;
        LinearLayout filesContainerForDocuments;
        ImageButton btnDeleteFromDocument;


        public DocumentsViewHolder(@NonNull View itemView) {
            super(itemView);
            documentsBody = itemView.findViewById(R.id.documentsBody);
            documentsDate = itemView.findViewById(R.id.documentsDate);
            filesContainerForDocuments = itemView.findViewById(R.id.filesContainerForDocuments);
            btnDeleteFromDocument = itemView.findViewById(R.id.btnDeleteFromDocument);

        }
    }
}