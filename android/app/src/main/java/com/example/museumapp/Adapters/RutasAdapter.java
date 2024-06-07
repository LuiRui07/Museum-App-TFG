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

import com.example.museumapp.Activities.InsideMuseum;
import com.example.museumapp.Activities.Museos;
import com.example.museumapp.Activities.Obras;
import com.example.museumapp.Models.Museum;
import com.example.museumapp.Models.Route;
import com.example.museumapp.R;
import com.example.museumapp.Service.MuseumService;
import com.example.museumapp.SharedData;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class RutasAdapter extends RecyclerView.Adapter<RutasAdapter.RutasViewHolder> {
    private List<Route> rutas;

    public Context context;

    public int tipo;

    public SharedData data = SharedData.getInstance();

    public RutasAdapter(List<Route> rutas, Context context, int tipo) {
        this.rutas= rutas;
        this.context = context;
        this.tipo = tipo;
    }

    @Override
    public RutasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recorrido, parent, false);
        return new RutasViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RutasViewHolder holder, int position) {
        Route ruta = rutas.get(position);
        holder.bind(ruta);

        holder.itemView.setOnClickListener(v -> {
            if (tipo == 1){
                Intent intent = new Intent(context, InsideMuseum.class); // Ver recorrido
                intent.putExtra("map",data.getMuseo().getMap());
                intent.putExtra("location", data.getMuseo().getLocation().getCoordinates());
                intent.putExtra("routeSelected", true);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rutas.size();
    }

    public static class RutasViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        public MuseumService museumService;

        public RutasViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);

            museumService = new MuseumService(context);
        }

        public void bind(Route ruta) {
            titleTextView.setText(ruta.getName());
            museumService.getMuseumFromId(ruta.getMuseum(), new MuseumService.MuseumCallback() {
                @Override
                public void onSuccess(Museum museum, int tipo, List<Museum> museos, Context context) {
                    descriptionTextView.setText(museum.getName());
                }

                @Override
                public void onFailure(String errorMessage) {
                    descriptionTextView.setText("Información del museo no disponible");
                }
            });
        }
    }
}
