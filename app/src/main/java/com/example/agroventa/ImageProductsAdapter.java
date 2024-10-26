package com.example.agroventa;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ImageProductsAdapter extends RecyclerView.Adapter<ImageProductsAdapter.ImageViewHolder> {
    private List<Uri> imagesList;
    private Context context;

    public ImageProductsAdapter(List<Uri> imagesList) {
        this.imagesList = imagesList != null ? imagesList : new ArrayList<>(); // Inicializar con una lista vacía si es null
    }

    @Override
    public int getItemCount() {
        return imagesList.size(); // Ahora no debería haber NullPointerException
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.imageView.setImageURI(imagesList.get(position));
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewItem);
        }
    }
}