package com.example.hackhaton_ticaret_mektebi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hackhaton_ticaret_mektebi.Adapters.DersDetayAdapter;
import com.example.hackhaton_ticaret_mektebi.Adapters.IcerikDetayAdapter;
import com.example.hackhaton_ticaret_mektebi.Adapters.MyAdapter;
import com.example.hackhaton_ticaret_mektebi.Models.Content;
import com.example.hackhaton_ticaret_mektebi.Models.Course;
import com.example.hackhaton_ticaret_mektebi.Models.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserMainActivity extends AppCompatActivity {
    private RecyclerView recyclerView,recyclerView2;
    private RecyclerView.Adapter adapter,adapter2;
    FirebaseUser user;
    ImageView user_main_pp;
    TextView user_main_ad_soyad;
    ImageButton user_main_yapay_zeka;

    private RecyclerView.LayoutManager layoutManager,layoutManager2;
    TextView ad_soyad_uma,ders_mu_uma,s_p_icerikler_uma;
    String userID;
    Button btn;
    String userName;
    String userDepartment;
    String userPhotoUrl;

    List<Content> contentItemList = new ArrayList<>();
    List<Course> courseItemList=new ArrayList<>();
    String contentProvider,contentDepartment,contentName,contentSize,contentSharedDate;
    String teacherID,courseName,courseDate,courseDepartment,courseEndTime,courseStartTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_main_activity);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_main_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        user_main_pp=findViewById(R.id.user_main_pp);
        user_main_yapay_zeka=findViewById(R.id.user_main_yapay_zeka);
        ad_soyad_uma=findViewById(R.id.user_main_ad_soyad);
        // RecyclerView'a referans ver
        recyclerView = findViewById(R.id.user_main_rec);
        recyclerView2=findViewById(R.id.user_main_rec_2);

        layoutManager2 = new LinearLayoutManager(this);
        recyclerView2.setLayoutManager(layoutManager2);
        // LayoutManager ayarla (dikey liste için LinearLayoutManager kullanıyoruz)
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



        getUserInfoFromDatabase();
       // getContentDepartmentsFromDatabase();
        getCourseFromDatabase();
        getContentFromDatabase();
        user_main_pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserMainActivity.this, OgretmenArayuzActivity.class);
                startActivity(intent);
                finish(); // Kullanıcı geri basarsa login ekranına dönmesin diye
            }
        });
    }

    // Kullanıcı bilgilerini Firebase Realtime Database'den çeker

    public void getUserInfoFromDatabase() {
         user = getCurrentUser();
        if (user != null) {
             userID = user.getUid();
            Log.d("Deneme0",userID);
            // Veritabanından Teacher bilgilerini çekmek için
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("teachers").child(userID);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
                    if (teacher != null) {
                         userName = teacher.getNameSurname();
                         userDepartment = teacher.getUserDepartment();
                         userPhotoUrl = teacher.getProfilePictureURL();

                        ad_soyad_uma.setText("Hoşgeldin" + " " +userName);

                        Log.d("Deneme1",userID);
                        Log.d("UserInfo", "User ID: " + userID + ", Name: " + userName + ", Department: " + userDepartment + ", Photo URL: " + userPhotoUrl);
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
    public void getContentFromDatabase() {
        DatabaseReference refContents = FirebaseDatabase.getInstance().getReference("contents");

        refContents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot contentSnapshot : snapshot.getChildren()) {
                    // Firebase'den verileri çekme
                    String contentProvider = contentSnapshot.child("contentProvider").getValue(String.class);
                    String contentName = contentSnapshot.child("contentName").getValue(String.class);
                    String contentSize = contentSnapshot.child("contentSize").getValue(String.class);
                    String contentSharedDate = contentSnapshot.child("contentSharedDate").getValue(String.class);
                    String contentDepartment = contentSnapshot.child("contentDepartment").getValue(String.class);

                    // Sadece geçerli kullanıcıya ait içerikleri ekleyelim
                    if (userID.equals(contentProvider)) {
                        // Yeni Content nesnesi oluştur
                        Content contentItem = new Content();
                        contentItem.setContentProvider(contentProvider);
                        contentItem.setContentName(contentName);
                        contentItem.setContentSize(contentSize);
                        contentItem.setContentSharedDate(contentSharedDate);
                        contentItem.setContentDepartment(contentDepartment);

                        // Content öğesini listeye ekle
                        contentItemList.add(contentItem);
                    }
                }

                // Adapter'ı güncelle
                IcerikDetayAdapter adapter = new IcerikDetayAdapter(contentItemList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("getContentFromDatabase", "Error: " + error.getMessage());
            }
        });
    }

    public void getCourseFromDatabase()
    {
        DatabaseReference refCourses = FirebaseDatabase.getInstance().getReference("courses");

        refCourses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    teacherID = courseSnapshot.child("teacherID").getValue(String.class);
                     courseName = courseSnapshot.child("courseName").getValue(String.class);
                     courseDate = courseSnapshot.child("courseDate").getValue(String.class);
                     courseDepartment = courseSnapshot.child("courseDepartment").getValue(String.class);
                     courseEndTime = courseSnapshot.child("courseEndTime").getValue(String.class);
                     courseStartTime = courseSnapshot.child("courseStartTime").getValue(String.class);

                    Log.d("FirebaseData", "Teacher ID: " + teacherID);

                    if (userID.equals(teacherID)) {
                        Log.d("FirebaseData0", "Teacher ID: " + teacherID);

                        // Create a new Course object
                        Course courseItem = new Course();
                        courseItem.setCourseName(courseName); // Set course name
                        courseItem.setCourseDate(courseDate); // Set course date
                        courseItem.setCourseDepartment(courseDepartment); // Set course department
                        courseItem.setCourseEndTime(courseEndTime); // Set course end time
                        courseItem.setCourseStartTime(courseStartTime); // Set course start time
                        courseItem.setTeacherID(teacherID); // Set teacher ID

                        courseItemList.add(courseItem); // Add the item to the list

                        Log.d("FirebaseData99", "Course Department: " + courseName);

                        // Update the adapter
                        DersDetayAdapter adapter2 = new DersDetayAdapter(courseItemList);
                        recyclerView2.setAdapter(adapter2);
                    }
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Bilgisyar Programcılığı yazanları çeker
    //ID1'den başlar. ID1'de programcılık yazdırır. ID3'de userID, ID4'de programcılık yazdırır.
    public void getContentDepartmentsFromDatabase() {
        // Firebase veritabanı referansı
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("contents");

        // Firebase'den veri çekme
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Belirli bir contentDepartment değeri arama
                for (DataSnapshot contentSnapshot : dataSnapshot.getChildren()) {
                    // contentDepartment verisini çek
                     contentDepartment = contentSnapshot.child("contentDepartment").getValue(String.class);
                     contentProvider = contentSnapshot.child("contentProvider").getValue(String.class);
                     contentName = contentSnapshot.child("contentName").getValue(String.class);

                     //Burda name bilgilerini falan da list ile yapcan
                    if(userID.equals(contentProvider))
                    {
                        Log.d("FirebaseData1", "Content Provider: " + contentProvider);
                        Log.d("FirebaseData1", "Content Provider: " + contentName);

                        // Yeni bir ContentItem oluştur
                        Content contentItem = new Content();
                        contentItem.setContentName(contentName); // İçeriğin adını ayarla
                        contentItemList.add(contentItem);

// Adapter'ı güncelle
                        MyAdapter adapter = new MyAdapter(contentItemList);
                        recyclerView.setAdapter(adapter);


                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("UserInfo", "Error: " + databaseError.getMessage());
            }
        });
    }


    // Firebase'den giriş yapmış kullanıcıyı döndürür
    public FirebaseUser getCurrentUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser();
    }
}
