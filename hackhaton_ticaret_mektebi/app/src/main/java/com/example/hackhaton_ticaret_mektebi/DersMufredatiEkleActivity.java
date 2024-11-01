package com.example.hackhaton_ticaret_mektebi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.hackhaton_ticaret_mektebi.Models.Student;
import com.example.hackhaton_ticaret_mektebi.Models.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class DersMufredatiEkleActivity extends AppCompatActivity {
    FirebaseUser user;
    private DatabaseReference userDatabaseRef;
    String userID;
    String userName,courseDepartment_str,courseName_str,courseStartTime_str,courseEndTime_str;
    TextView ders_mufredati_dersin_hocasi_dt;
    Button ders_mufredati_paylas_btn;
    TextView ders_mufredati_ad_soyad_text;
    String userDepartment;
    String userPhotoUrl;
    ImageView ders_mufredati_pp;
    EditText ders_mufredati_dersin_adi_dt, ders_mufredati_ders_baslama_adi_dt, ders_mufredati_ders_bitis_dt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ders_mufredati_ekle);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ders_mufredati_ekle_l), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


      ders_mufredati_pp = findViewById(R.id.ders_mufredati_pp);
      ders_mufredati_dersin_adi_dt = findViewById(R.id.ders_mufredati_dersin_adi_dt);
      ders_mufredati_ders_baslama_adi_dt = findViewById(R.id.ders_mufredati_ders_baslama_adi_dt);
      ders_mufredati_ders_bitis_dt = findViewById(R.id.ders_mufredati_ders_bitis_dt);
      ders_mufredati_dersin_hocasi_dt = findViewById(R.id.ders_mufredati_dersin_hocasi_dt);
      ders_mufredati_ad_soyad_text=findViewById(R.id.ders_mufredati_ad_soyad_text);
      ders_mufredati_paylas_btn = findViewById(R.id.ders_mufredati_paylas_btn);
        getUserInfoFromDatabase();
        ders_mufredati_paylas_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Firebase veritabanı referansını al
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference coursesRef = database.getReference("courses");

                // Mevcut içerik ID'lerini almak için contents düğümünü dinle
                coursesRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Mevcut en yüksek contentID'yi bulmak için bir değişken tanımla
                        int maxCourseID = 0;

                        // Mevcut tüm düğümleri döngüyle kontrol et
                        for (DataSnapshot snapshot : task.getResult().getChildren()) {
                            String key = snapshot.getKey();
                            if (key != null && key.startsWith("courseID")) {
                                // contentID'den sonraki sayıyı al ve int'e çevir
                                int courseNumber = Integer.parseInt(key.replace("courseID", ""));
                                if (courseNumber > maxCourseID) {
                                    maxCourseID = courseNumber;
                                }
                            }
                        }

                        // Yeni contentID oluştur
                        String newCourseID = "courseID" + (maxCourseID + 1);

                        //bunları düzenlersin
                        //courseDepartment_str = editTextVeri.getText().toString();

                        // Yeni verileri eklemek için bir HashMap oluştur
                        Map<String, Object> newCourseData = new HashMap<>();
                        newCourseData.put("courseName", "Yeni Müfredat"); // Örnek veri
                        newCourseData.put("courseDepartment", "Serhat Mühendisliği"); // Örnek veri
                        newCourseData.put("courseStartTime", "2024-25-01"); // Örnek veri
                        newCourseData.put("teacherID", "6vAn2g4aQ4cHDjv67izOUt9sAih2"); // Örnek veri
                        // Yeni contentID ile veriyi Firebase'e yaz
                        coursesRef.child(newCourseID).setValue(newCourseData).addOnCompleteListener(writeTask -> {
                            if (writeTask.isSuccessful()) {
                                // Veri başarıyla eklendiğinde yapılacak işlem
                                System.out.println("Yeni içerik başarıyla eklendi: " + newCourseID);

                                // UserMainActivity'ye geçiş yap
                                Intent intent = new Intent(DersMufredatiEkleActivity.this, UserMainActivity.class);
                                startActivity(intent);
                                finish(); // Kullanıcı geri basarsa login ekranına dönmesin diye
                            } else {
                                // Hata durumunda yapılacak işlem
                                System.err.println("Veri ekleme hatası: " + writeTask.getException());
                            }
                        });
                    } else {
                        // Hata durumunda yapılacak işlem
                        System.err.println("Mevcut içerikler okunurken hata oluştu: " + task.getException());
                    }
                });
            }
        });


    }


    // Kullanıcı bilgilerini Firebase Realtime Database'den çeker

    public void getUserInfoFromDatabase() {
        user = getCurrentUser();
        if (user != null) {
            userID = user.getUid();

            // Önce teachers düğümünde kontrol edelim
            DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference("teachers").child(userID);
            teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Kullanıcı teacher düğümündeyse
                        Teacher teacher = dataSnapshot.getValue(Teacher.class);
                        if (teacher != null) {

                            userName = teacher.getNameSurname();
                            userPhotoUrl = teacher.getProfilePictureURL();

                            ders_mufredati_dersin_hocasi_dt.setText(userName);
                            ders_mufredati_ad_soyad_text.setText(userName);
                            if (userPhotoUrl != null) {
                                Glide.with(DersMufredatiEkleActivity.this).load(userPhotoUrl).into(ders_mufredati_pp);
                            } else {
                                Log.e("ImageLoadError", "Profil resmi URL'si null."); // URL'nin null olup olmadığını kontrol et
                            }
                            ders_mufredati_ad_soyad_text.setText("Hoşgeldin Öğretmenim"+" " + userName);
                            Log.d("UserInfo", "Teacher - User ID: " + userID + ", Name: " + userName + ", Department: " + userDepartment + ", Photo URL: " + userPhotoUrl);
                        }
                    } else {
                        // Eğer teacher düğümünde değilse, students düğümünü kontrol et
                        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference("students").child(userID);
                        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot studentSnapshot) {
                                if (studentSnapshot.exists()) {
                                    // Kullanıcı student düğümündeyse
                                    Student student = studentSnapshot.getValue(Student.class);
                                    if (student != null) {
                                        userName = student.getNameSurname();
                                        userDepartment = student.getUserDepartment();
                                        userPhotoUrl = student.getProfilePictureURL();
                                        if (userPhotoUrl != null) {
                                            Glide.with(DersMufredatiEkleActivity.this).load(userPhotoUrl).into(ders_mufredati_pp);
                                        } else {
                                            Log.e("ImageLoadError", "Profil resmi URL'si null."); // URL'nin null olup olmadığını kontrol et
                                        }
                                        ders_mufredati_ad_soyad_text.setText("Hoşgeldin Öğrencim "+ " " + userName);
                                        Log.d("UserInfo", "Student - User ID: " + userID + ", Name: " + userName + ", Class: " + ", Photo URL: " + userPhotoUrl);
                                    }
                                } else {
                                    Log.d("UserInfo", "Kullanıcı ne öğretmen ne de öğrenci.");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e("UserInfo", "Error: " + databaseError.getMessage());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("UserInfo", "Error: " + databaseError.getMessage());
                }
            });
        } else {
            Log.d("UserInfo", "No user is signed in.");
        }
    }

    // Firebase'den giriş yapmış kullanıcıyı döndürür
    public FirebaseUser getCurrentUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser();
    }

}
