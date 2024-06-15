package com.example.museumapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.museumapp.Activities.Obras;
import com.example.museumapp.Models.Museum;
import com.example.museumapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MuseosAdapter extends RecyclerView.Adapter<MuseosAdapter.MuseosViewHolder> {
    private List<Museum> museos;
    private Context context;

    public MuseosAdapter(List<Museum> museos, Context context) {
        this.museos= museos;
        this.context = context;
    }

    @Override
    public MuseosAdapter.MuseosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_museo, parent, false);
        return new MuseosAdapter.MuseosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MuseosAdapter.MuseosViewHolder holder, int position) {
        Museum museo = museos.get(position);
        holder.bind(museo,context);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Obras.class);
            intent.putExtra("museum_id", museo.getId());
            intent.putExtra("museum_name", museo.getName());
            context.startActivity(intent);
        });
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

        public void bind(Museum museo, Context context) {
            titleTextView.setText(museo.getName());
            String address = museo.getAddress();

            descriptionTextView.setText(address);
            descriptionTextView.setTextColor(context.getColor(android.R.color.holo_blue_dark));

            descriptionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(mapIntent);
                    } else {
                        // Si no está instalada la app de Google Maps, puedes mostrar un mensaje o intentar abrir el navegador
                        Uri webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(address));
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
                        context.startActivity(webIntent);
                    }
                }
            });
            Picasso.get()
                    .load(museo.getImage())
                    .resize(300, 300)  // Redimensiona la imagen a 300x300 píxeles
                    .centerCrop()     // Opcional: Recorta la imagen para llenar el `ImageView` manteniendo la relación de aspecto
                    .error(R.drawable.default_art_icon) // Imagen de fallback en caso de error
                    .into(imageView);


            }
    }
}
