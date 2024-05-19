package com.example.museumapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.museumapp.Models.Museum;
import com.example.museumapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MuseosAdapter extends RecyclerView.Adapter<MuseosAdapter.MuseosViewHolder> {

    private List<Museum> museos;

    public MuseosAdapter(List<Museum> museos) {
        this.museos= museos;
    }

    @Override
    public MuseosAdapter.MuseosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_obra, parent, false);
        return new MuseosAdapter.MuseosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MuseosAdapter.MuseosViewHolder holder, int position) {
        Museum museo = museos.get(position);
        holder.bind(museo);
    }

    @Override
    public int getItemCount() {
        return museos.size();
    }

    public static class MuseosViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        ImageView imageView;

        public MuseosViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);
            imageView = itemView.findViewById(R.id.image_view);
        }

        public void bind(Museum museo) {
            titleTextView.setText(museo.getName());
            //Picasso.get().load(obra.getImage()).into(imageView);
        }
    }
}
