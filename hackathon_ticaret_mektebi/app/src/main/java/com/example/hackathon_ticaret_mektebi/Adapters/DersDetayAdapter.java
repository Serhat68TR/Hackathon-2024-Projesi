package com.example.hackathon_ticaret_mektebi.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hackathon_ticaret_mektebi.Models.Course;
import com.example.hackathon_ticaret_mektebi.DersDetayActivity;
import com.example.hackathon_ticaret_mektebi.R;


import java.util.List;

public class DersDetayAdapter extends RecyclerView.Adapter<DersDetayAdapter.DersDetayViewHolder> {
    private static List<Course> dataSet;

    public static class DersDetayViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public DersDetayViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.dd_card_item_text);
            Button btn = itemView.findViewById(R.id.ders_detay_btn);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    int position = getAdapterPosition(); // Tıklanan kartın pozisyonunu al
                    if (position != RecyclerView.NO_POSITION) {
                        Course clickedCourse = dataSet.get(position); // Tıklanan kartın bilgilerini al

                        // Yeni bir intent oluştur
                        Intent intent = new Intent(context, DersDetayActivity.class);
                        // Kartın bilgilerini intent'e ekle
                        intent.putExtra("courseName", clickedCourse.getCourseName());
                        intent.putExtra("courseDate", clickedCourse.getCourseDate());
                        intent.putExtra("courseDepartment", clickedCourse.getCourseDepartment());
                        intent.putExtra("courseStartTime", clickedCourse.getCourseStartTime());
                        intent.putExtra("courseEndTime", clickedCourse.getCourseEndTime());
                        intent.putExtra("teacherID", clickedCourse.getTeacherID());

                        // Yeni aktiviteyi başlat
                        context.startActivity(intent);
                        Toast.makeText(context, "Button clicked!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public DersDetayAdapter(List<Course> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public DersDetayAdapter.DersDetayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ders_detay_card_item, parent, false);
        return new DersDetayAdapter.DersDetayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DersDetayAdapter.DersDetayViewHolder holder, int position) {
        holder.textView.setText(dataSet.get(position).getCourseName());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
