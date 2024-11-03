package com.example.hackathon_ticaret_mektebi.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hackathon_ticaret_mektebi.Models.Content;
import com.example.hackathon_ticaret_mektebi.IcerikDetayActivity; // İçerik detayını gösteren aktivite
import com.example.hackathon_ticaret_mektebi.R;

import java.util.List;

public class IcerikDetayAdapter extends RecyclerView.Adapter<IcerikDetayAdapter.IcerikDetayViewHolder> {

    private static List<Content> dataSet;

    // Constructor - Adapter'a veri setini geçiyoruz
    public IcerikDetayAdapter(List<Content> dataSet) {
        this.dataSet = dataSet;
    }

    // ViewHolder sınıfı, her bir liste öğesini temsil eder
    public static class IcerikDetayViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public IcerikDetayViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textView); // card_item.xml'deki TextView'in ID'si ile eşleşiyor
            Button btn = view.findViewById(R.id.icerik_detay_caBtn);
            // OnClickListener ekle
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    int position = getAdapterPosition(); // Tıklanan kartın pozisyonunu al
                    if (position != RecyclerView.NO_POSITION) {
                        Content clickedContent = dataSet.get(position); // Tıklanan kartın bilgilerini al

                        // Yeni bir intent oluştur
                        Intent intent = new Intent(context, IcerikDetayActivity.class);
                        // Kartın bilgilerini intent'e ekle
                        intent.putExtra("contentName", clickedContent.getContentName());
                        intent.putExtra("contentProvider", clickedContent.getContentProvider());
                        intent.putExtra("contentSharedDate", clickedContent.getContentSharedDate());
                        intent.putExtra("contentDepartment", clickedContent.getContentDepartment());
                        intent.putExtra("contentSize",clickedContent.getContentSize());

                        Log.d("obsiti",clickedContent.getContentName());

                        // Yeni aktiviteyi başlat
                        context.startActivity(intent);
                        Toast.makeText(context, "Content clicked!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public IcerikDetayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // card_item.xml layout'unu inflate ediyoruz
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        return new IcerikDetayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IcerikDetayAdapter.IcerikDetayViewHolder holder, int position) {
        // Her bir öğe için veriyi bağlıyoruz
        holder.textView.setText(dataSet.get(position).getContentName()); // İçerik ismini ayarlıyoruz
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
