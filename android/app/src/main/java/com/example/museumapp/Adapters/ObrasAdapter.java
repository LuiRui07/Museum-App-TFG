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

import com.example.museumapp.Activities.ObraInfo;
import com.example.museumapp.Activities.Obras;
import com.example.museumapp.Models.Obra;
import com.example.museumapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ObrasAdapter extends RecyclerView.Adapter<ObrasAdapter.ObrasViewHolder> {

    private List<Obra> obras;

    private Context context;

    private  String museumName;

    public ObrasAdapter(List<Obra> obras, String museumName, Context context) {
        this.obras = obras;
        this.context = context;
        this.museumName = museumName;
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

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ObraInfo.class);
            intent.putExtra("obra_id", obra.getId());
            intent.putExtra("museum_name", museumName);
            context.startActivity(intent);
        });
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
            Picasso.get()
                    .load(obra.getImage())
                    .resize(300, 300)  // Redimensiona la imagen a 300x300 píxeles
                    .centerCrop()     // Opcional: Recorta la imagen para llenar el `ImageView` manteniendo la relación de aspecto
                    .error(R.drawable.default_art_icon) // Imagen de fallback en caso de error
                    .into(imageView);
        }
    }

}
