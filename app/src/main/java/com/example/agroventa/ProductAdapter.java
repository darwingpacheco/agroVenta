package com.example.agroventa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> productList;
    private OnClickListener listener;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    public interface OnClickListener {
        void onClick(View view, Product obj, int position);
    }

    public void setOnClickListener(final OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.titleTextView.setText(product.getTitle());
        holder.descriptionTextView.setText(product.getDescription());
        holder.productImageView.setImageResource(product.getImageResourceId());

        // Aquí se establece el evento de clic
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(v, product, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        ImageView productImageView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.product_title);
            descriptionTextView = itemView.findViewById(R.id.product_description);
            productImageView = itemView.findViewById(R.id.product_image);
        }
    }
}
