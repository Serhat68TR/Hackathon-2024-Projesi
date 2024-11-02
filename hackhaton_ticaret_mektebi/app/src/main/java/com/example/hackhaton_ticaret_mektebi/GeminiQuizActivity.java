package com.example.hackhaton_ticaret_mektebi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class GeminiQuizActivity extends AppCompatActivity {

    private static final int WRITE_PERMISSION_REQUEST_CODE = 1000;
    private String apiKey = "AIzaSyAl49b9RagN8JXPPM86EcTg1tWcF37whmM";
    private String resultText;
    private String cevap = "";
    private EditText ders, konu, sayi, zorluk;
    private TextView sonucText; // Changed variable name for consistency
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gemini_quiz);

        ders = findViewById(R.id.editTextDers);
        konu = findViewById(R.id.editTextKonu);
        sayi = findViewById(R.id.editTextSayi);
        zorluk = findViewById(R.id.editTextZorluk);
        sonucText = findViewById(R.id.sonuc); // Corrected the view ID
        button = findViewById(R.id.soruOlustur);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Input validation
                if (TextUtils.isEmpty(ders.getText()) || TextUtils.isEmpty(konu.getText()) ||
                        TextUtils.isEmpty(sayi.getText()) || TextUtils.isEmpty(zorluk.getText())) {
                    Toast.makeText(GeminiQuizActivity.this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String soru = ders.getText().toString() + " dersinden " +
                        konu.getText().toString() + " konusundan " +
                        zorluk.getText().toString() + " zorluk seviyesinde " +
                        sayi.getText().toString() + " adet test sorusu hazırla";

                // API modelini oluşturma
                GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", apiKey);
                GenerativeModelFutures model = GenerativeModelFutures.from(gm);

                Content content = new Content.Builder().addText(soru).build();

                Executor executor = Executors.newSingleThreadExecutor();
                ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

                Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        try {
                            resultText = result.getText();
                            cevap = resultText;
                            runOnUiThread(() -> sonucText.setText(cevap)); // Update the TextView with the result
                            createPDF();
                        } catch (Exception e) {
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Bir hata oluştu", Toast.LENGTH_SHORT).show());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "API çağrısı başarısız oldu", Toast.LENGTH_SHORT).show());
                        t.printStackTrace();
                    }
                }, executor);
            }
        });
    }

    private void createPDF() {
        // Check for WRITE_EXTERNAL_STORAGE permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST_CODE);
        } else {
            try {
                // PDF dosyasının kaydedileceği yol
                String pdfPath = getExternalFilesDir(null).toString();
                File file = new File(pdfPath, konu.getText().toString() + ".pdf"); // Save as .txt

                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(cevap.getBytes());
                outputStream.close();

                runOnUiThread(() -> Toast.makeText(this, "Dosya Oluşturuldu: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Dosya oluşturulamadı: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createPDF(); // Re-attempt PDF creation if permission was granted
            } else {
                Toast.makeText(this, "Dosya yazma izni verilmedi", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
