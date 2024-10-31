package com.example.hackhaton_ticaret_mektebi.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hackhaton_ticaret_mektebi.Models.Content;
import com.example.hackhaton_ticaret_mektebi.R;
import com.example.hackhaton_ticaret_mektebi.İcerikDetayActivity;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<Content> dataSet; // Burayı List<Content> olarak değiştirin

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            // Kart layout'undaki textView ile ilişkilendirme
            textView = itemView.findViewById(R.id.textView);
            // Butonu card_item.xml içinden tanımla
            Button btn = itemView.findViewById(R.id.icerik_detay_caBtn);

            // OnClickListener ekle
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    // Tıklandığında yapılacak işlemler burada
                    Intent intent = new Intent(context, İcerikDetayActivity.class);
                    context.startActivity(intent);
                    Toast.makeText(v.getContext(), "Button clicked!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Constructor: Adapter, veriyi alır
    public MyAdapter(List<Content> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Kart layout'unu bağla
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // Her öğedeki veriyi ayarla
        holder.textView.setText(dataSet.get(position).getContentName()); // İçeriğin adını al
    }

    @Override
    public int getItemCount() {
        return dataSet.size();  // Listedeki öğe sayısı
    }
}
