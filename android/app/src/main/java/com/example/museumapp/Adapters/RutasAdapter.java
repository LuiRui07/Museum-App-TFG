package com.example.museumapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.museumapp.Activities.Obras;
import com.example.museumapp.Models.Museum;
import com.example.museumapp.Models.Route;
import com.example.museumapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RutasAdapter extends RecyclerView.Adapter<RutasAdapter.RutasViewHolder> {
    private List<Route> rutas;
    private Context context;

    public RutasAdapter(List<Route> rutas) {
        this.rutas= rutas;
    }

    @Override
    public RutasAdapter.RutasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recorrido, parent, false);
        return new RutasAdapter.RutasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RutasAdapter.RutasViewHolder holder, int position) {
        Route ruta = rutas.get(position);
        holder.bind(ruta);

        holder.itemView.setOnClickListener(v -> {
            /*Intent intent = new Intent(context, Obras.class); // Ver recorrido
            intent.putExtra("museum_id", museo.getId());
            intent.putExtra("museum_name", museo.getName());
            context.startActivity(intent);

             */
        });
    }

    @Override
    public int getItemCount() {
        return rutas.size();
    }

    public static class RutasViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;

        public RutasViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);
        }

        public void bind(Route ruta) {
            titleTextView.setText(ruta.getMuseo().getName());
            titleTextView.setText(ruta.getMuseo().getName());
        }
    }
}
