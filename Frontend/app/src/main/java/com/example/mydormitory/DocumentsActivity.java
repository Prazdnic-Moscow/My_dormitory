package com.example.mydormitory;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DocumentsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerViewDocuments;
    private DocumentTemplateAdapter documentAdapter;
    private Button addDocumentButton;
    
    // Временная проверка: true = админ, false = обычный пользователь
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        // Настройка Toolbar
        setupToolbar();

        // Настройка списка документов
        setupDocumentsList();

        // Настройка кнопки добавления
        setupAddButton();

        // Загрузка тестовых данных
        loadSampleDocuments();

        // Настройка системных окон
        setupWindowInsets();
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.documents_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupDocumentsList() {
        recyclerViewDocuments = findViewById(R.id.documents);
        if (recyclerViewDocuments != null) {
            recyclerViewDocuments.setLayoutManager(new LinearLayoutManager(this));
            documentAdapter = new DocumentTemplateAdapter(new ArrayList<>());
            recyclerViewDocuments.setAdapter(documentAdapter);
        }
    }

    private void setupAddButton() {
        addDocumentButton = findViewById(R.id.addDocumentButton);
        if (addDocumentButton != null) {
            addDocumentButton.setOnClickListener(v -> {
                if (isAdmin) {
                    Toast.makeText(this, "Добавление документа", Toast.LENGTH_SHORT).show();
                    // TODO: Реализовать добавление документа
                } else {
                    Toast.makeText(this, "Только для администраторов", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadSampleDocuments() {
        List<DocumentTemplate> documents = new ArrayList<>();
        
        documents.add(new DocumentTemplate(
            getString(R.string.guest_mode_application),
            "DOCX",
            "45 KB",
            "https://example.com/guest-mode.docx"
        ));
        
        documents.add(new DocumentTemplate(
            getString(R.string.settlement_application),
            "DOCX", 
            "52 KB",
            "https://example.com/settlement.docx"
        ));
        
        if (documentAdapter != null) {
            documentAdapter.updateDocumentList(documents);
        }
    }

    private void setupWindowInsets() {
        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
