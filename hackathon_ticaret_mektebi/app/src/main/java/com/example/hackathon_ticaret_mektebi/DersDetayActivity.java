package com.example.hackathon_ticaret_mektebi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
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

public class DersDetayActivity extends AppCompatActivity {
    FirebaseUser user;
    String userID;
    private DatabaseReference databaseReference;
    String userName;
    ImageButton gemini;
    ImageView ders_detay_pp;
    String teacherName,nameID;
    TextView ders_detay_ad_soyad_text;
    Button ders_detay_tamam_btn;
    TextView ders_detay_dersin_adi_dt, ders_detay_dersin_hocasi_dt, ders_detay_ders_tarihi_dt, ders_detay_ders_baslama_saati_dt, ders_detay_ders_bitis_saati_dt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
            setContentView(R.layout.ders_detay_activity);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ders_detay_l), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        gemini=findViewById(R.id.ders_detay_yapay_zeka);
        gemini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DersDetayActivity.this, GeminiActivity.class);
                startActivity(intent);
            }
        });
        ders_detay_pp=findViewById(R.id.ders_detay_pp);

        ders_detay_tamam_btn = findViewById(R.id.ders_detay_tamam_btn);
        ders_detay_dersin_adi_dt = findViewById(R.id.ders_detay_dersin_adi_dt);
        ders_detay_dersin_hocasi_dt = findViewById(R.id.ders_detay_dersin_hocasi_dt);
        ders_detay_ders_tarihi_dt = findViewById(R.id.ders_detay_ders_tarihi_dt);
        ders_detay_ders_baslama_saati_dt = findViewById(R.id.ders_detay_ders_baslama_saati_dt);
        ders_detay_ders_bitis_saati_dt = findViewById(R.id.ders_detay_ders_bitis_saati_dt);
        ders_detay_ad_soyad_text = findViewById(R.id.ders_detay_ad_soyad_text);

        // Intent'ten gelen verileri al
        Intent intent = getIntent();
        String courseName = intent.getStringExtra("courseName");
        String courseDate = intent.getStringExtra("courseDate");
        String courseStartTime = intent.getStringExtra("courseStartTime");
        String courseEndTime = intent.getStringExtra("courseEndTime");
        String courseProviderID = intent.getStringExtra("teacherID");

        // Firebase veritabanı referansı
        databaseReference = FirebaseDatabase.getInstance().getReference("teachers");

        // Ders bilgilerini doğrudan atayın

        ders_detay_dersin_adi_dt.setText(courseName);
        ders_detay_ders_tarihi_dt.setText(courseDate);
        ders_detay_ders_baslama_saati_dt.setText(courseStartTime);
        ders_detay_ders_bitis_saati_dt.setText(courseEndTime);
        ders_detay_dersin_hocasi_dt.setText(courseProviderID);

        ders_detay_tamam_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DersDetayActivity.this, UserMainActivity.class);
                startActivity(intent);
            }
        });

        databaseReference.child(courseProviderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Öğretmenin adını aldıktan sonra TextView'e ayarla
                    String courseProvider = dataSnapshot.child("nameSurname").getValue(String.class);
                    ders_detay_dersin_hocasi_dt.setText(courseProvider);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DersDetayActivity.this, "Veritabanı hatası: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        ders_detay_pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = getCurrentUser();
                if (user != null) {
                    String userID = user.getUid();
                    DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference("teachers").child(userID);
                    teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Eğer kullanıcı teacher ise OgretmenArayuzActivity'i aç
                                Intent intent = new Intent(DersDetayActivity.this, OgretmenArayuzActivity.class);
                                startActivity(intent);
                            } else {
                                // Eğer kullanıcı teacher değilse, student'ı kontrol et
                                DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference("students").child(userID);
                                studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot studentSnapshot) {
                                        if (studentSnapshot.exists()) {
                                            // Eğer kullanıcı student ise OgrenciArayuzActivity'i aç
                                            Intent intent = new Intent(DersDetayActivity.this, OgrenciArayuzActivity.class);
                                            startActivity(intent);
                                        } else {
                                            // Kullanıcı ne öğretmen ne de öğrenci
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
        });


        getCurrentUser();
        getUserInfoFromDatabase();
        getCourseDetailFromDatabase();
    }
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
                            String userDepartment = teacher.getUserDepartment();
                            String userPhotoUrl = teacher.getProfilePictureURL();
                            if (userPhotoUrl != null) {
                                Glide.with(DersDetayActivity.this).load(userPhotoUrl).into(ders_detay_pp);
                            } else {
                                Log.e("ImageLoadError", "Profil resmi URL'si null."); // URL'nin null olup olmadığını kontrol et
                            }
                            ders_detay_ad_soyad_text.setText(userName);
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
                                        String userName = student.getNameSurname();
                                        String userClass = student.getUserDepartment();
                                        String userPhotoUrl = student.getProfilePictureURL();
                                        if (userPhotoUrl != null) {
                                            Glide.with(DersDetayActivity.this).load(userPhotoUrl).into(ders_detay_pp);
                                        } else {
                                            Log.e("ImageLoadError", "Profil resmi URL'si null."); // URL'nin null olup olmadığını kontrol et
                                        }
                                        ders_detay_ad_soyad_text.setText(userName);
                                        Log.d("UserInfo", "Student - User ID: " + userID + ", Name: " + userName + ", Class: " + userClass + ", Photo URL: " + userPhotoUrl);
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

    public void getCourseDetailFromDatabase() {
        DatabaseReference ders_detay_hoca_name = FirebaseDatabase.getInstance().getReference("teachers");


        ders_detay_hoca_name.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ders_detay_Snapshot2 : dataSnapshot.getChildren()) {
                    nameID = ders_detay_Snapshot2.child("nameSurname").getValue(String.class);
                    if (userID.equals(nameID)) {
                        teacherName = ders_detay_Snapshot2.child("nameSurname").getValue(String.class);
                        Log.d("adam", teacherName);
                        ders_detay_dersin_hocasi_dt.setText(teacherName);
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
                Log.e("Firebase", "Error retrieving data: " + error.getMessage());
            }
        });

    }


    // Firebase'den giriş yapmış kullanıcıyı döndürür
    public FirebaseUser getCurrentUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser();
    }
}