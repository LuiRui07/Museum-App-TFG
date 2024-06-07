package com.example.museumapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.museumapp.Activities.InsideMuseum;
import com.example.museumapp.Models.Museum;
import com.example.museumapp.Models.Obra;
import com.example.museumapp.Models.Route;
import com.example.museumapp.R;
import com.example.museumapp.Service.MuseumService;
import com.example.museumapp.Service.ObraService;
import com.example.museumapp.SharedData;

import java.util.ArrayList;
import java.util.List;

public class RutasAdapter extends RecyclerView.Adapter<RutasAdapter.RutasViewHolder> {

    private List<Route> rutas;
    private Context context;
    private int tipo;
    private MuseumService museumService;
    private ObraService obraService;

    private SharedData data = SharedData.getInstance();

    public RutasAdapter(List<Route> rutas, Context context, int tipo) {
        this.rutas = rutas;
        this.context = context;
        this.tipo = tipo;
        this.museumService = new MuseumService(context);
        this.obraService = new ObraService(context);
    }

    @NonNull
    @Override
    public RutasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recorrido, parent, false);
        return new RutasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RutasViewHolder holder, int position) {
        Route ruta = rutas.get(position);
        holder.bind(ruta);

        // Verificar si la ruta está expandida para mostrar las obras y el botón de inicio de recorrido
        if (ruta.isExpanded()) {
            holder.obrasRecyclerView.setVisibility(View.VISIBLE);
            holder.loadObras(ruta.getArts());

            // Mostrar el botón "Iniciar Recorrido" solo si tipo == 1
            if (tipo == 1) {
                holder.startRouteButton.setVisibility(View.VISIBLE);
                holder.startRouteButton.setOnClickListener(v -> {
                    Intent intent = new Intent(context, InsideMuseum.class); // Ver recorrido
                    intent.putExtra("map", data.getMuseo().getMap());
                    intent.putExtra("location", data.getMuseo().getLocation().getCoordinates());
                    intent.putExtra("routeSelected", true);
                    context.startActivity(intent);
                });
            } else {
                holder.startRouteButton.setVisibility(View.GONE);
            }
        } else {
            holder.obrasRecyclerView.setVisibility(View.GONE);
            holder.startRouteButton.setVisibility(View.GONE);
        }

        // Configurar el clic en el itemView para expandir/reducir la vista cuando tipo != 1
        holder.itemView.setOnClickListener(v -> {
                ruta.setExpanded(!ruta.isExpanded());
                notifyItemChanged(position);
        });
    }


    @Override
    public int getItemCount() {
        return rutas.size();
    }

    public class RutasViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView descriptionTextView;
        RecyclerView obrasRecyclerView;
        Button startRouteButton;
        ObrasAdapter obrasAdapter;
        List<Obra> obrasList = new ArrayList<>();

        public RutasViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);
            obrasRecyclerView = itemView.findViewById(R.id.obras_recycler_view);
            startRouteButton = itemView.findViewById(R.id.start_route_button);
            obrasRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            obrasAdapter = new ObrasAdapter(obrasList, "", context);
            obrasRecyclerView.setAdapter(obrasAdapter);
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

        public void loadObras(List<String> obraIds) {
            obrasList.clear();
            for (String obraId : obraIds) {
                obraService.getArtFromId(obraId, new ObraService.ObraCallback() {
                    @Override
                    public void onSuccess(List<Obra> obras, Obra obra) {
                        if (obra != null) {
                            obrasList.add(obra);
                            obrasAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }
    }
}
