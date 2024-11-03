package com.example.hackathon_ticaret_mektebi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.hackathon_ticaret_mektebi.R;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GeminiNotActivity extends AppCompatActivity {

    private EditText ozet;
    private TextView sonuc;
    private Button button;
    private static final int WRITE_PERMISSION_REQUEST_CODE = 1000;
    private String apiKey = "AIzaSyAl49b9RagN8JXPPM86EcTg1tWcF37whmM";
    private String resultText;
    String soru;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gemini_not);

        ozet = findViewById(R.id.editTextText);
        sonuc = findViewById(R.id.textView7);
        button = findViewById(R.id.button);

        button.setOnClickListener(v -> {
            Toast.makeText(GeminiNotActivity.this, "Butona tıklandı!", Toast.LENGTH_SHORT).show();
            soru = ozet.getText().toString();
            generateContentAndCreatePDF(soru);
        });


    }

    private void generateContentAndCreatePDF(String soru) {
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", apiKey);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder().addText(soru).build();
        Executor executor = Executors.newSingleThreadExecutor();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                resultText = result.getText();
                runOnUiThread(() -> sonuc.setText("PDF başarıyla oluşturuldu"));
                requestStoragePermissionAndCreatePDF();
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Bir hata oluştu: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }, executor);
    }

    private void requestStoragePermissionAndCreatePDF() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST_CODE);
        } else {
            createPDF();
        }
    }

    private void createPDF() {
        try {
            String pdfPath = getExternalFilesDir(null).toString();
            File file = new File(pdfPath, soru.substring(0,7)+".pdf");
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            String[] paragraphs = resultText.split("\n");
            for (String paragraph : paragraphs) {
                document.add(new Paragraph(paragraph));
            }

            document.close();
            runOnUiThread(() -> Toast.makeText(this, "PDF oluşturuldu: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show());
        } catch (FileNotFoundException e) {
            runOnUiThread(() -> Toast.makeText(this, "PDF oluşturulamadı: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createPDF();
            } else {
                Toast.makeText(this, "İzin verilmedi!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
