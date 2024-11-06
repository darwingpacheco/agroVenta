package com.example.agroventa;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ImageProductsAdapter extends RecyclerView.Adapter<ImageProductsAdapter.ImageViewHolder> {
    private List<Uri> imagesList;
    private Context context;

    public ImageProductsAdapter(Context context, List<Uri> imagesList) {
        this.context = context;
        this.imagesList = imagesList != null ? imagesList : new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri product = imagesList.get(position);

        if (product != null) {
            Glide.with(context)
                    .load(imagesList.get(position))
                    .placeholder(R.drawable.galeria_de_imagenes)
                    .error(R.drawable.galeria_de_imagenes)
                    .into(holder.imageView);
        } else
            holder.imageView.setImageURI(imagesList.get(position));

        holder.imageView.setOnClickListener(v -> {
            Uri imageUri = imagesList.get(position);
            Intent viewIntent = new Intent(Intent.ACTION_VIEW);
            viewIntent.setDataAndType(imageUri, "image/*");
            viewIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);

            if (viewIntent.resolveActivity(context.getPackageManager()) != null)
                context.startActivity(viewIntent);
            else
                Toast.makeText(context, "No hay aplicaciones de galería instaladas", Toast.LENGTH_SHORT).show();

        });
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewItem);
        }
    }
}