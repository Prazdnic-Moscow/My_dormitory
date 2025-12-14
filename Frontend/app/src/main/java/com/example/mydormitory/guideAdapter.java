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

public class guideAdapter extends RecyclerView.Adapter<guideAdapter.GuideViewHolder> {

    private List<guide> guideList;
    public guideAdapter(List<guide> guideList) {
        this.guideList = guideList;
    }

    @NonNull
    @Override
    public GuideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.guide_item, parent, false);
        return new GuideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuideViewHolder holder, int position) {
        guide guide = guideList.get(position);

        holder.guideHeader.setText(guide.getHeader());
        holder.guideBody.setText(guide.getBody());
        holder.guideDate.setText(guide.getDate());
        holder.filesContainerForGuides.removeAllViews();

        if (guide.getTutorPath() != null && !guide.getTutorPath().isEmpty()) {
            for (int i = 0; i < guide.getTutorPath().size(); i++) {
                addImageToContainer(holder.filesContainerForGuides, guide.getTutorPath().get(i));
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
        return guideList.size();
    }

    public static class GuideViewHolder extends RecyclerView.ViewHolder {
        TextView guideHeader, guideBody, guideDate;
        LinearLayout filesContainerForGuides;

        public GuideViewHolder(@NonNull View itemView) {
            super(itemView);
            guideHeader = itemView.findViewById(R.id.guideHeader);
            guideBody = itemView.findViewById(R.id.guideBody);
            guideDate = itemView.findViewById(R.id.guideDate);
            filesContainerForGuides = itemView.findViewById(R.id.filesContainerForGuides);
        }
    }
}