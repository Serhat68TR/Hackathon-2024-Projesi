package com.example.hackathon_ticaret_mektebi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.hackathon_ticaret_mektebi.Models.Course;
import com.example.hackathon_ticaret_mektebi.Models.Student;
import com.example.hackathon_ticaret_mektebi.Models.Teacher;
import com.example.hackathon_ticaret_mektebi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DersMufredatiEkleActivity extends AppCompatActivity {
    FirebaseUser user;

    String userID;
    String userName;
    TextView ders_mufredati_dersin_hocasi_dt;
    Button ders_mufredati_paylas_btn;
    TextView ders_mufredati_ad_soyad_text;
    String userDepartment;
    DatabaseReference courseDatabaseRef;
    String userPhotoUrl;
    ImageButton gemini;
    ImageView ders_mufredati_pp;
    EditText ders_mufredati_department_dt;
    EditText ders_mufredati_dersin_adi_dt, ders_mufredati_ders_baslama_adi_dt, ders_mufredati_ders_bitis_dt, ders_mufredati_dersin_tarihi_dt;
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
        gemini=findViewById(R.id.ders_mufredati_yapay_zeka);
        gemini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DersMufredatiEkleActivity.this, GeminiActivity.class);
                startActivity(intent);
            }
        });

      ders_mufredati_pp = findViewById(R.id.ders_mufredati_pp);
      ders_mufredati_dersin_tarihi_dt = findViewById(R.id.ders_mufredati_dersin_tarihi_dt);
      ders_mufredati_ders_baslama_adi_dt = findViewById(R.id.ders_mufredati_ders_baslama_adi_dt);
      ders_mufredati_ders_bitis_dt = findViewById(R.id.ders_mufredati_ders_bitis_dt);
      ders_mufredati_dersin_hocasi_dt = findViewById(R.id.ders_mufredati_dersin_hocasi_dt);
      ders_mufredati_dersin_adi_dt = findViewById(R.id.ders_mufredati_dersin_adi_dt);
      ders_mufredati_ad_soyad_text=findViewById(R.id.ders_mufredati_ad_soyad_text);
      ders_mufredati_department_dt=findViewById(R.id.ders_mufredati_department_dt);
      ders_mufredati_paylas_btn = findViewById(R.id.ders_mufredati_paylas_btn);

      courseDatabaseRef = FirebaseDatabase.getInstance().getReference("courses");

      //değişiklikler başlıyor


        //değişiklikler bitiyor
      getUserInfoFromDatabase();

      ders_mufredati_paylas_btn.setOnClickListener(v -> shareNewCourseFromDatabase());

    }

    public void shareNewCourseFromDatabase() {
        String courseName = ders_mufredati_dersin_adi_dt.getText().toString().trim();
        String courseProvider = ders_mufredati_dersin_hocasi_dt.getText().toString().trim();
        String courseDate = ders_mufredati_dersin_tarihi_dt.getText().toString().trim();
        String courseEndTime = ders_mufredati_ders_bitis_dt.getText().toString().trim();
        String courseDepartment = ders_mufredati_department_dt.getText().toString().trim(); // COURSEDEPARTMAN == OTURUM AÇAN HOCANIN DEPARTMANI
        String courseStartTime = ders_mufredati_ders_baslama_adi_dt.getText().toString().trim();

        if (!courseName.isEmpty() && !courseProvider.isEmpty() && !courseDate.isEmpty() && !courseDepartment.isEmpty() && !courseStartTime.isEmpty() && !courseEndTime.isEmpty()) {
            String courseId = courseDatabaseRef.push().getKey();
            Course course = new Course(courseDate, courseDepartment, courseEndTime, courseName, courseStartTime, courseProvider);
            courseDatabaseRef.child(courseId).setValue(course)
                    .addOnSuccessListener(aVoid -> Toast.makeText(DersMufredatiEkleActivity.this, "Müfredat başarıyla paylaşıldı.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(DersMufredatiEkleActivity.this, "Müfredat paylaşılırken hata oluştu.", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Tüm alanlar doldurulmalıdır.", Toast.LENGTH_SHORT).show();
        }
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
                            userDepartment = teacher.getUserDepartment();
                            ders_mufredati_dersin_hocasi_dt.setText(userName);
                            ders_mufredati_ad_soyad_text.setText(userName);
                            if (userPhotoUrl != null) {
                                Glide.with(DersMufredatiEkleActivity.this).load(userPhotoUrl).into(ders_mufredati_pp);
                            } else {
                                Log.e("ImageLoadError", "Profil resmi URL'si null."); // URL'nin null olup olmadığını kontrol et
                            }
                            ders_mufredati_ad_soyad_text.setText(userName);
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
                                        ders_mufredati_ad_soyad_text.setText(userName);
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
