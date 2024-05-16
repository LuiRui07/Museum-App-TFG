package com.example.museumapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.museumapp.Models.Obra;
import com.example.museumapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ObrasAdapter extends RecyclerView.Adapter<ObrasAdapter.ObrasViewHolder> {

    private List<Obra> obras;

    public ObrasAdapter(List<Obra> obras) {
        this.obras = obras;
    }

    @NonNull
    @Override
    public ObrasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_obra, parent, false);
        return new ObrasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObrasViewHolder holder, int position) {
        Obra obra = obras.get(position);
        holder.bind(obra);
    }

    @Override
    public int getItemCount() {
        return obras.size();
    }

    public static class ObrasViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        ImageView imageView;

        public ObrasViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);
            imageView = itemView.findViewById(R.id.image_view);
        }

        public void bind(Obra obra) {
            titleTextView.setText(obra.getName());
            descriptionTextView.setText(obra.getDescription());
            // Aquí puedes establecer otros atributos de la vista según los datos de la obra
            Picasso.get().load(obra.getImage()).into(imageView);
        }
    }

}
