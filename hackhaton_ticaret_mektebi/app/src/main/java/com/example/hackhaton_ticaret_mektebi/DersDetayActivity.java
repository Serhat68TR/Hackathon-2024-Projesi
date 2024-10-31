package com.example.hackhaton_ticaret_mektebi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hackhaton_ticaret_mektebi.Adapters.MyAdapter;
import com.example.hackhaton_ticaret_mektebi.Models.Content;
import com.example.hackhaton_ticaret_mektebi.Models.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DersDetayActivity extends AppCompatActivity {
    /*
        private RecyclerView recyclerView3;
        private RecyclerView.Adapter adapter;
        private RecyclerView.LayoutManager layoutManager;
    */
    FirebaseUser user;
    String userID;
    String userName;
    String teacherID,teacherName,nameID;
    TextView ders_detay_ad_soyad_text;
    String ders_detay_dersin_adi_str, ders_detay_ders_baslama_saati_str, ders_detay_ders_bitis_saati_str, ders_detay_ders_tarihi_str;
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

        ders_detay_dersin_adi_dt = findViewById(R.id.ders_detay_dersin_adi_dt);
        ders_detay_dersin_hocasi_dt = findViewById(R.id.ders_detay_dersin_hocasi_dt);
        ders_detay_ders_tarihi_dt = findViewById(R.id.ders_detay_ders_tarihi_dt);
        ders_detay_ders_baslama_saati_dt = findViewById(R.id.ders_detay_ders_baslama_saati_dt);
        ders_detay_ders_bitis_saati_dt = findViewById(R.id.ders_detay_ders_bitis_saati_dt);
        ders_detay_ad_soyad_text = findViewById(R.id.ders_detay_ad_soyad_text);
        // Intent'ten verileri al

        //bilgileri yazdırmayı düzenle
        Intent intent = getIntent();
        String courseName = intent.getStringExtra("courseName");
        String courseDate = intent.getStringExtra("courseDate");
        String courseDepartment = intent.getStringExtra("courseDepartment");
        String courseStartTime = intent.getStringExtra("courseStartTime");
        String courseEndTime = intent.getStringExtra("courseEndTime");
        String teacherID = intent.getStringExtra("teacherID");



        ders_detay_dersin_adi_dt.setText(courseName);
        ders_detay_ders_tarihi_dt.setText(courseDate);
        ders_detay_ders_baslama_saati_dt.setText(courseStartTime);
        ders_detay_ders_bitis_saati_dt.setText(courseEndTime);


        getCurrentUser();
        getUserInfoFromDatabase();
        getCourseDetailFromDatabase();
    }

    public void getUserInfoFromDatabase() {
        user = getCurrentUser();
        if (user != null) {
            userID = user.getUid();
            Log.d("Deneme0",userID);
            // Veritabanından Teacher bilgilerini çekmek için
            DatabaseReference ders_detay_ref = FirebaseDatabase.getInstance().getReference("teachers").child(userID);
            ders_detay_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
                    if (teacher != null) {
                        userName = teacher.getNameSurname();
                        ders_detay_ad_soyad_text.setText("Hoşgeldin" + " " +userName);
                        Log.d("Deneme1",userID);
                        Log.d("UserInfo", "User ID: " + userID + ", Name: " + userName);
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
        // Firebase veritabanı referansı
        DatabaseReference ders_detay_ref2 = FirebaseDatabase.getInstance().getReference("courses");
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