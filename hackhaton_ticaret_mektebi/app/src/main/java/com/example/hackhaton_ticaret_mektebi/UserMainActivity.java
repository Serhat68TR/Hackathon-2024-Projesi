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

import com.bumptech.glide.Glide;
import com.example.hackhaton_ticaret_mektebi.Adapters.DersDetayAdapter;
import com.example.hackhaton_ticaret_mektebi.Adapters.IcerikDetayAdapter;
import com.example.hackhaton_ticaret_mektebi.Adapters.MyAdapter;
import com.example.hackhaton_ticaret_mektebi.Models.Content;
import com.example.hackhaton_ticaret_mektebi.Models.Course;
import com.example.hackhaton_ticaret_mektebi.Models.Student;
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
    String userName;
    String userDepartment;
    String userPhotoUrl;
    private RecyclerView.LayoutManager layoutManager,layoutManager2;
    TextView ad_soyad_uma,ders_mu_uma,s_p_icerikler_uma;
    String userID;
    Button btn;

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
                user = getCurrentUser();
                if (user != null) {
                    String userID = user.getUid();
                    DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference("teachers").child(userID);
                    teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Eğer kullanıcı teacher ise OgretmenArayuzActivity'i aç
                                Intent intent = new Intent(UserMainActivity.this, OgretmenArayuzActivity.class);
                                startActivity(intent);
                                finish(); // Kullanıcı geri basarsa login ekranına dönmesin diye
                            } else {
                                // Eğer kullanıcı teacher değilse, student'ı kontrol et
                                DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference("students").child(userID);
                                studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot studentSnapshot) {
                                        if (studentSnapshot.exists()) {
                                            // Eğer kullanıcı student ise OgrenciArayuzActivity'i aç
                                            Intent intent = new Intent(UserMainActivity.this, OgrenciArayuzActivity.class);
                                            startActivity(intent);
                                            finish(); // Kullanıcı geri basarsa login ekranına dönmesin diye
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

                                    if (userPhotoUrl != null) {
                                        Glide.with(UserMainActivity.this).load(userPhotoUrl).into(user_main_pp);
                                    } else {
                                        Log.e("ImageLoadError", "Profil resmi URL'si null."); // URL'nin null olup olmadığını kontrol et
                                    }
                            ad_soyad_uma.setText("Hoşgeldin Öğretmenim"+" " + userName);
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
                                            Glide.with(UserMainActivity.this).load(userPhotoUrl).into(user_main_pp);
                                        } else {
                                            Log.e("ImageLoadError", "Profil resmi URL'si null."); // URL'nin null olup olmadığını kontrol et
                                        }
                                        ad_soyad_uma.setText("Hoşgeldin Öğrencim "+ " " + userName);
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
    public void getContentFromDatabase() {
        // Önce kullanıcı tipi (öğretmen/öğrenci) kontrol edilir.
        DatabaseReference refTeachers = FirebaseDatabase.getInstance().getReference("teachers").child(userID);
        DatabaseReference refStudents = FirebaseDatabase.getInstance().getReference("students").child(userID);

        // Kullanıcının öğretmen olup olmadığını kontrol et
        refTeachers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot teacherSnapshot) {
                if (teacherSnapshot.exists()) {
                    // Kullanıcı öğretmen
                    fetchContentsByProvider(userID);
                } else {
                    // Öğretmen değilse öğrencilik kontrol ediliyor
                    refStudents.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot studentSnapshot) {
                            if (studentSnapshot.exists()) {
                                // Kullanıcı öğrenci
                                String userDepartment = studentSnapshot.child("userDepartment").getValue(String.class);
                                fetchContentsByDepartment(userDepartment);
                            } else {
                                Log.e("getContentFromDatabase", "Kullanıcı teachers veya students düğümünde bulunamadı.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("getContentFromDatabase", "Student kontrolünde hata: " + error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("getContentFromDatabase", "Teacher kontrolünde hata: " + error.getMessage());
            }
        });
    }

    // Öğretmenin içeriklerini çekme
    private void fetchContentsByProvider(String contentProviderID) {
        DatabaseReference refContents = FirebaseDatabase.getInstance().getReference("contents");

        refContents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contentItemList.clear(); // Listeyi sıfırlayın
                for (DataSnapshot contentSnapshot : snapshot.getChildren()) {
                    String contentProvider = contentSnapshot.child("contentProvider").getValue(String.class);

                    if (contentProviderID.equals(contentProvider)) {
                        Content contentItem = createContentFromSnapshot(contentSnapshot);
                        contentItemList.add(contentItem);
                    }
                }
                updateRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("fetchContentsByProvider", "Error: " + error.getMessage());
            }
        });
    }

    // Öğrencinin departmanına göre içerikleri çekme
    private void fetchContentsByDepartment(String department) {
        DatabaseReference refContents = FirebaseDatabase.getInstance().getReference("contents");

        refContents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contentItemList.clear(); // Listeyi sıfırlayın
                for (DataSnapshot contentSnapshot : snapshot.getChildren()) {
                    String contentDepartment = contentSnapshot.child("contentDepartment").getValue(String.class);

                    if (department.equals(contentDepartment)) {
                        Content contentItem = createContentFromSnapshot(contentSnapshot);
                        contentItemList.add(contentItem);
                    }
                }
                updateRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("fetchContentsByDepartment", "Error: " + error.getMessage());
            }
        });
    }

    // Content nesnesini DataSnapshot'tan oluşturma
    private Content createContentFromSnapshot(DataSnapshot contentSnapshot) {
        Content contentItem = new Content();
        contentItem.setContentProvider(contentSnapshot.child("contentProvider").getValue(String.class));
        contentItem.setContentName(contentSnapshot.child("contentName").getValue(String.class));
        contentItem.setContentSize(contentSnapshot.child("contentSize").getValue(String.class));
        contentItem.setContentSharedDate(contentSnapshot.child("contentSharedDate").getValue(String.class));
        contentItem.setContentDepartment(contentSnapshot.child("contentDepartment").getValue(String.class));
        return contentItem;
    }

    // RecyclerView'i güncelleme
    private void updateRecyclerView() {
        IcerikDetayAdapter adapter = new IcerikDetayAdapter(contentItemList);
        recyclerView.setAdapter(adapter);
    }


    // RecyclerView'i güncelleme
    private void updateCourseRecyclerView() {
        // Adapter'ı güncelle
        DersDetayAdapter adapter2 = new DersDetayAdapter(courseItemList);
        recyclerView2.setAdapter(adapter2);
    }

    // Firebase'den giriş yapmış kullanıcıyı döndürür
    public FirebaseUser getCurrentUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser();
    }


                        // Kurs içeriklerini çekme
                        private void fetchCoursesByTeacher(String teacherID) {
                            DatabaseReference refCourses = FirebaseDatabase.getInstance().getReference("courses");

                            refCourses.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    courseItemList.clear(); // Listeyi sıfırlayın
                                    for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                                        String courseTeacherID = courseSnapshot.child("teacherID").getValue(String.class);

                                        if (teacherID.equals(courseTeacherID)) {
                                            Course courseItem = createCourseFromSnapshot(courseSnapshot);
                                            courseItemList.add(courseItem);
                                        }
                                    }
                                    updateCourseRecyclerView();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("fetchCoursesByTeacher", "Error: " + error.getMessage());
                                }
                            });
                        }

                        // Kurs nesnesini DataSnapshot'tan oluşturma
                        private Course createCourseFromSnapshot(DataSnapshot courseSnapshot) {
                            Course courseItem = new Course();
                            courseItem.setCourseName(courseSnapshot.child("courseName").getValue(String.class));
                            courseItem.setCourseDate(courseSnapshot.child("courseDate").getValue(String.class));
                            courseItem.setCourseDepartment(courseSnapshot.child("courseDepartment").getValue(String.class));
                            courseItem.setCourseEndTime(courseSnapshot.child("courseEndTime").getValue(String.class));
                            courseItem.setCourseStartTime(courseSnapshot.child("courseStartTime").getValue(String.class));
                            courseItem.setTeacherID(courseSnapshot.child("teacherID").getValue(String.class));
                            return courseItem;
                        }

                        // Öğrencinin departmanına göre kurs içeriklerini çekme
                        private void fetchCoursesByDepartment(String department) {
                            DatabaseReference refCourses = FirebaseDatabase.getInstance().getReference("courses");

                            refCourses.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    courseItemList.clear();
                                    for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                                        String courseDepartment = courseSnapshot.child("courseDepartment").getValue(String.class);

                                        if (department.equals(courseDepartment)) {
                                            Course courseItem = createCourseFromSnapshot(courseSnapshot);
                                            courseItemList.add(courseItem);
                                        }
                                    }
                                    updateCourseRecyclerView();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("fetchCoursesByDepartment", "Error: " + error.getMessage());
                                }
                            });
                        }
                        // Kullanıcıya göre kursları çekme
                        public void getCourseFromDatabase() {
                            DatabaseReference refTeachers = FirebaseDatabase.getInstance().getReference("teachers").child(userID);
                            DatabaseReference refStudents = FirebaseDatabase.getInstance().getReference("students").child(userID);

                            refTeachers.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot teacherSnapshot) {
                                    if (teacherSnapshot.exists()) {
                                        // Kullanıcı öğretmense, öğretmenin kurslarını çek
                                        fetchCoursesByTeacher(userID);
                                    } else {
                                        // Öğrenci ise departmanına göre kursları çek
                                        refStudents.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot studentSnapshot) {
                                                if (studentSnapshot.exists()) {
                                                    String userDepartment = studentSnapshot.child("userDepartment").getValue(String.class);
                                                    fetchCoursesByDepartment(userDepartment);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Log.e("getCoursesFromDatabase", "Error: " + error.getMessage());
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("getCoursesFromDatabase", "Error: " + error.getMessage());
                                }
                            });
                        }


}
