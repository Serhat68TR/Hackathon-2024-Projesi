package com.example.hackhaton_ticaret_mektebi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hackhaton_ticaret_mektebi.Models.Content;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class İcerikDetayActivity extends AppCompatActivity {

    FirebaseUser user;
    String userID;
    String userName;
    TextView icerigin_adi_dt, paylasan_kisi_dt,paylasilan_tarih_dt,icerik_bolum_adi_dt,icerik_sayfa_boyut_dt;
    TextView icerik_detay_ad_soyad_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.icerik_detay);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.icerik_detay_l), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        icerigin_adi_dt = findViewById(R.id.icerigin_adi_dt);
        paylasan_kisi_dt = findViewById(R.id.paylasan_kisi_dt);
        paylasilan_tarih_dt = findViewById(R.id.paylasilan_tarih_dt);
        icerik_bolum_adi_dt = findViewById(R.id.icerik_bolum_adi_dt);
        icerik_sayfa_boyut_dt = findViewById(R.id.icerik_sayfa_boyut_dt);

        // Get data from Intent
        Intent intent = getIntent();
        String contentName_str = intent.getStringExtra("contentName");
        String contentProvider_str = intent.getStringExtra("contentProvider");
        String contentSharedDate_str = intent.getStringExtra("contentSharedDate");
        String contentDepartment_str = intent.getStringExtra("contentDepartment");
        String contentSize_str = intent.getStringExtra("contentSize");


        icerigin_adi_dt.setText(contentName_str);
        paylasan_kisi_dt.setText(contentProvider_str);
        paylasilan_tarih_dt.setText(contentSharedDate_str);
        icerik_bolum_adi_dt.setText(contentDepartment_str);
        icerik_sayfa_boyut_dt.setText(contentSize_str);

        getCurrentUser();
        getUserInfoFromDatabase();
    }

    public void getUserInfoFromDatabase() {
        user = getCurrentUser();
        if (user != null) {
            userID = user.getUid();
            Log.d("Deneme0", userID);
            // Retrieve User information from Firebase Database
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("te").child(userID);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        userName = dataSnapshot.child("nameSurname").getValue(String.class);
                        icerik_detay_ad_soyad_text.setText("Hoşgeldin" + " " + userName);
                        Log.d("Deneme1", userID);
                        Log.d("UserInfo", "User ID: " + userID + ", Name: " + userName);
                    } else {
                        Log.d("UserInfo", "User not found in the database.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
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
