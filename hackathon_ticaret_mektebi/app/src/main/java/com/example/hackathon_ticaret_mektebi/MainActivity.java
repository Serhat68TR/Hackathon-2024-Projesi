package com.example.hackathon_ticaret_mektebi;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hackathon_ticaret_mektebi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    Button giris_yap_btn_login;
    EditText email_login, password_login;
    String email;
    String password;
    // FirebaseAuth nesnesi oluşturuluyor
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // EditText'leri ve butonu tanımlıyoruz
        email_login = findViewById(R.id.email_login);
        password_login = findViewById(R.id.password_login);
        giris_yap_btn_login = findViewById(R.id.giris_yap_login);

        // Login butonuna tıklanınca
        giris_yap_btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = email_login.getText().toString();
                password = password_login.getText().toString();

                // Email ve şifrenin boş olmadığından emin ol
                if (TextUtils.isEmpty(email)) {
                    email_login.setError("Email gerekli!");
                } else if (TextUtils.isEmpty(password)) {
                    password_login.setError("Şifre gerekli!");
                } else {
                    // Giriş yap fonksiyonunu çağır
                    loginUser(email, password);
                }
            }
        });

        Button ogretmenSifremiUnuttumBtn = findViewById(R.id.main_activity_sifremi_unuttum_btn);

        ogretmenSifremiUnuttumBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Şifre Sıfırlama Yardımı");
            builder.setMessage("Şifrenizi değiştirmek için bir yöneticiyle iletişime geçin.");
            builder.setPositiveButton("Tamam", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

    }

    // Kullanıcı giriş fonksiyonu
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Giriş başarılı
                        FirebaseUser user = mAuth.getCurrentUser();
                        // Ana ekrana yönlendirme ya da başka bir işlem
                        Toast.makeText(getApplicationContext(), "Giriş Başarılı", Toast.LENGTH_SHORT).show();
                        // Örneğin, ana ekrana geçiş
                        Intent intent = new Intent(this, UserMainActivity.class);
                        startActivity(intent);
                    } else {
                        // Giriş başarısız, hata mesajı göster
                        Toast.makeText(getApplicationContext(), "Şifre veya E-posta hatalı!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
