package com.example.hackathon_ticaret_mektebi;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class OgretmenArayuzActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView userProfilePictureImageView, ogretmen_profil_foto;
    private DatabaseReference userDatabaseRef;
    String userID;
    String userName;
    FirebaseUser user;
    Button ogr_ders_mufredati_ekle_btn;
    Button ogr_yeni_icerik_paylas_btn;
    TextView ogretmen_arayuz_ad_soyad_text;
    TextView ogr_bolum_ad;
    String userPhotoUrl,userDepartment;
    ImageButton gemini;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ogretmen_arayuz_activity);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ogretmen_arayuz), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        gemini=findViewById(R.id.ogr_yapay_btn);
        gemini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OgretmenArayuzActivity.this, GeminiActivity.class);
                startActivity(intent);
            }
        });
        getUserInfoFromDatabase();
        ogretmen_arayuz_ad_soyad_text=findViewById(R.id.ogretmen_arayuz_ad_soyad_text);
        ogr_bolum_ad=findViewById(R.id.ogr_bolum_ad);
        ogr_ders_mufredati_ekle_btn = findViewById(R.id.ogr_ders_mufredati_ekle_btn);
        userProfilePictureImageView = findViewById(R.id.ogretmen_arayuz_profil);
        Button changeProfilePicButton = findViewById(R.id.ogr_pp_degistir_btn);
        ogretmen_profil_foto = findViewById(R.id.ogretmen_profil_foto);
        ogr_yeni_icerik_paylas_btn = findViewById(R.id.ogr_yeni_icerik_paylas_btn);
        ogr_yeni_icerik_paylas_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OgretmenArayuzActivity.this, YeniIcerikPaylasActivity.class);
                startActivity(intent);
            }
        });

        userProfilePictureImageView.setOnClickListener(new View.OnClickListener() {
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
                                Intent intent = new Intent(OgretmenArayuzActivity.this, OgretmenArayuzActivity.class);
                                startActivity(intent);
                            } else {
                                // Eğer kullanıcı teacher değilse, student'ı kontrol et
                                DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference("students").child(userID);
                                studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot studentSnapshot) {
                                        if (studentSnapshot.exists()) {
                                            // Eğer kullanıcı student ise OgrenciArayuzActivity'i aç
                                            Intent intent = new Intent(OgretmenArayuzActivity.this, OgrenciArayuzActivity.class);
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



        changeProfilePicButton.setOnClickListener(v -> openGallery());

        ogr_ders_mufredati_ekle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OgretmenArayuzActivity.this, DersMufredatiEkleActivity.class);
                startActivity(intent);
            }
        });

        Button ogretmenSifremiUnuttumBtn = findViewById(R.id.ogretmen_sifremi_unuttum_btn);

        ogretmenSifremiUnuttumBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Şifre Sıfırlama Yardımı");
            builder.setMessage("Şifrenizi değiştirmek için bir yöneticiyle iletişime geçin.");
            builder.setPositiveButton("Tamam", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
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
                            userDepartment = teacher.getUserDepartment();
                            userPhotoUrl = teacher.getProfilePictureURL();

                            ogr_bolum_ad.setText(userDepartment);
                            ogretmen_arayuz_ad_soyad_text.setText(userName);
                            if (userPhotoUrl != null) {
                                Glide.with(OgretmenArayuzActivity.this).load(userPhotoUrl).into(userProfilePictureImageView);
                                Glide.with(OgretmenArayuzActivity.this).load(userPhotoUrl).into(ogretmen_profil_foto);
                            } else {
                                Log.e("ImageLoadError", "Profil resmi URL'si null."); // URL'nin null olup olmadığını kontrol et
                            }
                            ogretmen_arayuz_ad_soyad_text.setText(userName);
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
                                            Glide.with(OgretmenArayuzActivity.this).load(userPhotoUrl).into(userProfilePictureImageView);
                                            Glide.with(OgretmenArayuzActivity.this).load(userPhotoUrl).into(ogretmen_profil_foto);
                                        } else {
                                            Log.e("ImageLoadError", "Profil resmi URL'si null."); // URL'nin null olup olmadığını kontrol et
                                        }
                                        ogretmen_arayuz_ad_soyad_text.setText("Hoşgeldin Öğrencim "+ " " + userName);
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


    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Resim Seç"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                userProfilePictureImageView.setImageBitmap(bitmap);
                ogretmen_profil_foto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            uploadImageToFirebase();
        }
    }

    private void uploadImageToFirebase() {
        if (userID != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();

            // Rastgele bir UUID oluştur
            String uniqueID = UUID.randomUUID().toString(); // Rastgele UUID
            String fileName = "profilePictures/" + uniqueID + ".jpg"; // Rastgele isim ile dosya adı

            StorageReference storageRef = storage.getReference().child(fileName);

            UploadTask uploadTask = storageRef.putFile(imageUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String profilePictureURL = uri.toString();
                    Log.d("FirebaseURL", "Profil resmi URL'si: " + profilePictureURL); // URL'yi logla
                    updateProfilePictureInDatabase(userID, profilePictureURL);
                });
            }).addOnFailureListener(e -> {
                Log.e("FirebaseError", "Resim yüklenirken hata oluştu: " + e.getMessage()); // Hata mesajını logla
                Toast.makeText(OgretmenArayuzActivity.this, "Resim yüklenirken hata oluştu.", Toast.LENGTH_SHORT).show();
            });
        } else {
            Log.e("FirebaseAuth", "Kullanıcı oturumu kapalı."); // Kullanıcı oturumunun açık olup olmadığını kontrol et
        }
    }

    private void updateProfilePictureInDatabase(String userId, String profilePictureURL) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("teachers").child(userId);
        dbRef.child("profilePictureURL").setValue(profilePictureURL)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(OgretmenArayuzActivity.this, "Profil resmi başarıyla güncellendi!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(OgretmenArayuzActivity.this, "Profil resmi güncellenirken hata oluştu.", Toast.LENGTH_SHORT).show();
                });
        Log.d("ProfilePictureURL", "URL: " + profilePictureURL);
    }

    private void loadUserProfilePicture() {
        userDatabaseRef.child("profilePictureURL").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profilePictureURL = snapshot.getValue(String.class);
                Log.d("ProfilePictureURL", "Yüklenmeye çalışılan URL: " + profilePictureURL); // URL'yi logla
                if (profilePictureURL != null) {
                    Glide.with(OgretmenArayuzActivity.this).load(profilePictureURL).into(userProfilePictureImageView);
                    Glide.with(OgretmenArayuzActivity.this).load(profilePictureURL).into(ogretmen_profil_foto);
                } else {
                    Log.e("ImageLoadError", "Profil resmi URL'si null."); // URL'nin null olup olmadığını kontrol et
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DatabaseError", "Profil resmi yüklenemedi: " + error.getMessage()); // Hata mesajını logla
            }
        });
    }

    // Firebase'den giriş yapmış kullanıcıyı döndürür
    public FirebaseUser getCurrentUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser();
    }


}

