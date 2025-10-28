package com.example.mydormitory;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

public class RepairRequestDialog extends DialogFragment {

    public interface OnRepairRequestSubmittedListener {
        void onRepairRequestSubmitted(String serviceType, String roomNumber, String description, List<String> photos);
    }

    private OnRepairRequestSubmittedListener listener;
    private String serviceType;
    private EditText roomNumberEditText;
    private EditText descriptionEditText;
    private LinearLayout photosContainer;
    private Button submitButton;
    private List<String> selectedPhotos = new ArrayList<>();

    public static RepairRequestDialog newInstance(String serviceType) {
        RepairRequestDialog dialog = new RepairRequestDialog();
        Bundle args = new Bundle();
        args.putString("serviceType", serviceType);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnRepairRequestSubmittedListener) {
            listener = (OnRepairRequestSubmittedListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            serviceType = getArguments().getString("serviceType");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        // Создаем кастомный заголовок
        View titleView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_title_repair, null);
        TextView titleText = titleView.findViewById(R.id.dialogTitle);
        titleText.setText("Заявка: " + serviceType);
        
        ImageButton closeButton = titleView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dismiss());
        
        builder.setCustomTitle(titleView);
        
        // Создаем основное содержимое
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_repair_request, null);
        setupViews(contentView);
        setupListeners();
        
        builder.setView(contentView);
        
        return builder.create();
    }

    private void setupViews(View view) {
        roomNumberEditText = view.findViewById(R.id.roomNumberEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        photosContainer = view.findViewById(R.id.photosContainer);
        submitButton = view.findViewById(R.id.submitButton);
    }

    private void setupListeners() {
        // Обработчик кнопки добавления фото
        View addPhotoButton = photosContainer.findViewById(R.id.addPhotoButton);
        if (addPhotoButton != null) {
            addPhotoButton.setOnClickListener(v -> addPhoto());
        }

        // Обработчик кнопки отправки
        if (submitButton != null) {
            submitButton.setOnClickListener(v -> submitRequest());
        }
    }

    private void addPhoto() {
        if (getActivity() == null) return;
        
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
        if (photosContainer == null) return;
        
        try {
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
                if (photoView != null) {
                    photosContainer.addView(photoView, i);
                }
            }

            // Скрываем кнопку добавления если достигнут лимит
            View addPhotoButton = photosContainer.findViewById(R.id.addPhotoButton);
            if (addPhotoButton != null) {
                if (selectedPhotos.size() >= 3) {
                    addPhotoButton.setVisibility(View.GONE);
                } else {
                    addPhotoButton.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            // Логируем ошибку, но не крашим приложение
            e.printStackTrace();
        }
    }

    private View createPhotoView(int index) {
        try {
            if (getActivity() == null) return null;
            
            LinearLayout photoLayout = new LinearLayout(getActivity());
            photoLayout.setOrientation(LinearLayout.VERTICAL);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
            params.rightMargin = (int) (12 * getActivity().getResources().getDisplayMetrics().density);
            photoLayout.setLayoutParams(params);
            
            photoLayout.setBackgroundResource(R.drawable.photo_preview_background);
            photoLayout.setPadding(8, 8, 8, 8);
            
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
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void removePhoto(int index) {
        selectedPhotos.remove(index);
        updatePhotosDisplay();
    }

    private void submitRequest() {
        if (getActivity() == null) return;
        
        // Валидация полей
        if (roomNumberEditText == null || roomNumberEditText.getText().toString().trim().isEmpty()) {
            if (roomNumberEditText != null) {
                roomNumberEditText.setError("Номер комнаты обязателен");
            }
            return;
        }

        if (descriptionEditText == null || descriptionEditText.getText().toString().trim().isEmpty()) {
            if (descriptionEditText != null) {
                descriptionEditText.setError("Описание проблемы обязательно");
            }
            return;
        }

        // Отправляем заявку
        String roomNumber = roomNumberEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        
        if (listener != null) {
            listener.onRepairRequestSubmitted(serviceType, roomNumber, description, selectedPhotos);
        }

        Toast.makeText(getActivity(), "Заявка отправлена!", Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
