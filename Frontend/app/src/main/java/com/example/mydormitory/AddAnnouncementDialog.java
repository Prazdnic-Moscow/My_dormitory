package com.example.mydormitory;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

public class AddAnnouncementDialog extends DialogFragment {

    public interface OnAnnouncementAddedListener {
        void onAnnouncementAdded(Announcement announcement);
    }

    private OnAnnouncementAddedListener listener;
    private Spinner categorySpinner;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText contactsEditText;
    private LinearLayout photosContainer;
    private Button publishButton;
    private List<String> selectedPhotos = new ArrayList<>();

    public static AddAnnouncementDialog newInstance() {
        return new AddAnnouncementDialog();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnAnnouncementAddedListener) {
            listener = (OnAnnouncementAddedListener) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        // Создаем кастомный заголовок
        View titleView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_title, null);
        TextView titleText = titleView.findViewById(R.id.dialogTitle);
        titleText.setText(getString(R.string.give_away_item));
        
        builder.setCustomTitle(titleView);
        
        // Создаем основное содержимое
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_announcement, null);
        setupViews(contentView);
        setupListeners();
        
        builder.setView(contentView);
        
        return builder.create();
    }

    private void setupViews(View view) {
        categorySpinner = view.findViewById(R.id.categorySpinner);
        titleEditText = view.findViewById(R.id.titleEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        contactsEditText = view.findViewById(R.id.contactsEditText);
        photosContainer = view.findViewById(R.id.photosContainer);
        publishButton = view.findViewById(R.id.publishButton);

        // Настройка спиннера категорий
        setupCategorySpinner();
    }

    private void setupCategorySpinner() {
        List<String> categories = new ArrayList<>();
        categories.add(getString(R.string.category_give_away));
        categories.add(getString(R.string.category_take));
        categories.add(getString(R.string.category_lost_found));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setTextSize(16);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                ((TextView) view).setTextSize(16);
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void setupListeners() {
        // Обработчик кнопки добавления фото
        View addPhotoButton = photosContainer.findViewById(R.id.addPhotoButton);
        addPhotoButton.setOnClickListener(v -> addPhoto());

        // Обработчик кнопки публикации
        publishButton.setOnClickListener(v -> publishAnnouncement());
    }

    private void addPhoto() {
        if (selectedPhotos.size() < 3) {
            Toast.makeText(getActivity(), "Добавление фото (макс. 3)", Toast.LENGTH_SHORT).show();
            // TODO: Реализовать выбор фото из галереи
            selectedPhotos.add("photo_" + selectedPhotos.size());
            updatePhotosDisplay();
        } else {
            Toast.makeText(getActivity(), "Максимум 3 фотографии", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePhotosDisplay() {
        // Удаляем старые фото (кроме кнопки добавления)
        for (int i = photosContainer.getChildCount() - 1; i >= 0; i--) {
            View child = photosContainer.getChildAt(i);
            if (child.getId() != R.id.addPhotoButton) {
                photosContainer.removeViewAt(i);
            }
        }

        // Добавляем новые фото
        for (int i = 0; i < selectedPhotos.size(); i++) {
            View photoView = createPhotoView(i);
            photosContainer.addView(photoView, i);
        }

        // Скрываем кнопку добавления если достигнут лимит
        View addPhotoButton = photosContainer.findViewById(R.id.addPhotoButton);
        if (selectedPhotos.size() >= 3) {
            addPhotoButton.setVisibility(View.GONE);
        } else {
            addPhotoButton.setVisibility(View.VISIBLE);
        }
    }

    private View createPhotoView(int index) {
        LinearLayout photoLayout = new LinearLayout(getActivity());
        photoLayout.setOrientation(LinearLayout.VERTICAL);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
        params.rightMargin = (int) (12 * getActivity().getResources().getDisplayMetrics().density);
        photoLayout.setLayoutParams(params);
        
        photoLayout.setBackgroundResource(R.drawable.photo_preview_background);
        photoLayout.setPadding(8, 8, 8, 8);
        
        ImageView photoImageView = new ImageView(getActivity());
        photoImageView.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        ));
        photoImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        photoImageView.setImageResource(R.drawable.ic_camera); // Заглушка
        photoImageView.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        
        photoLayout.addView(photoImageView);
        
        // Кнопка удаления
        Button deleteButton = new Button(getActivity());
        deleteButton.setText("×");
        deleteButton.setTextSize(16);
        deleteButton.setTextColor(getActivity().getResources().getColor(R.color.white));
        deleteButton.setBackgroundColor(getActivity().getResources().getColor(R.color.error_color));
        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        deleteButton.setOnClickListener(v -> removePhoto(index));
        
        photoLayout.addView(deleteButton);
        
        return photoLayout;
    }

    private void removePhoto(int index) {
        selectedPhotos.remove(index);
        updatePhotosDisplay();
    }

    private void publishAnnouncement() {
        // Валидация полей
        if (titleEditText.getText().toString().trim().isEmpty()) {
            titleEditText.setError("Заголовок обязателен");
            return;
        }

        if (descriptionEditText.getText().toString().trim().isEmpty()) {
            descriptionEditText.setError("Описание обязательно");
            return;
        }

        if (contactsEditText.getText().toString().trim().isEmpty()) {
            contactsEditText.setError("Контакты обязательны");
            return;
        }

        // Создаем объявление
        Announcement.Category category = Announcement.Category.values()[categorySpinner.getSelectedItemPosition()];
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String contacts = contactsEditText.getText().toString().trim();
        
        // Определяем location или contact
        String location = null;
        String contact = null;
        if (contacts.startsWith("@")) {
            contact = contacts;
        } else {
            location = contacts;
        }

        Announcement announcement = new Announcement(
            title,
            description,
            location,
            contact,
            category,
            "Только что"
        );

        // Отправляем обратно в активность
        if (listener != null) {
            listener.onAnnouncementAdded(announcement);
        }

        dismiss();
    }
}
