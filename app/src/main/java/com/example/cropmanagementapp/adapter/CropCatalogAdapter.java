package com.example.cropmanagementapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cropmanagementapp.R;
import com.example.cropmanagementapp.catalog.CropCatalogItem;
import com.example.cropmanagementapp.catalog.CropImageResolver;

import java.util.List;

/** Binds the grid of crop cards in the Browse Crops screen. */
public class CropCatalogAdapter extends RecyclerView.Adapter<CropCatalogAdapter.CatalogViewHolder> {

    public interface OnCatalogItemClickListener {
        void onItemClick(CropCatalogItem item);
    }

    private List<CropCatalogItem> items;
    private final OnCatalogItemClickListener listener;

    public CropCatalogAdapter(List<CropCatalogItem> items, OnCatalogItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void updateData(List<CropCatalogItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CatalogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_crop_catalog, parent, false);
        return new CatalogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogViewHolder holder, int position) {
        CropCatalogItem item = items.get(position);
        holder.tvCropName.setText(item.getName());
        int imageRes = CropImageResolver.resolve(holder.itemView.getContext(), item.getName(), item.getCategory());
        holder.ivCropImage.setImageResource(imageRes);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class CatalogViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCropImage;
        TextView tvCropName;

        CatalogViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCropImage = itemView.findViewById(R.id.ivCropImage);
            tvCropName = itemView.findViewById(R.id.tvCropName);
        }
    }
}