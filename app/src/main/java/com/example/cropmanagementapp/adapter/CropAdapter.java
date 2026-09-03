package com.example.cropmanagementapp.adapter;

import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cropmanagementapp.R;
import com.example.cropmanagementapp.catalog.CropImageResolver;
import com.example.cropmanagementapp.db.DateUtils;
import com.example.cropmanagementapp.model.Crop;

import java.util.List;

public class CropAdapter extends RecyclerView.Adapter<CropAdapter.CropViewHolder> {

    public interface OnCropClickListener {
        void onCropClick(Crop crop);
    }

    private List<Crop> crops;
    private final OnCropClickListener listener;

    public CropAdapter(List<Crop> crops, OnCropClickListener listener) {
        this.crops = crops;
        this.listener = listener;
    }

    public void updateData(List<Crop> newCrops) {
        this.crops = newCrops;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CropViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_crop, parent, false);
        return new CropViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CropViewHolder holder, int position) {
        Crop crop = crops.get(position);

        String variety = crop.getVariety();
        String nameLine = TextUtils.isEmpty(variety) ? crop.getCropName() : crop.getCropName() + " (" + variety + ")";
        holder.tvCropName.setText(nameLine);

        int imageRes = CropImageResolver.resolve(holder.itemView.getContext(), crop.getCropName(), crop.getCategory());
        holder.ivCropThumbnail.setImageResource(imageRes);

        holder.tvPlotName.setText("Plot: " + crop.getPlotName());
        holder.tvHarvestDate.setText("Harvest: " + DateUtils.toDisplayFormat(crop.getExpectedHarvestDate()));

        int daysLeft = DateUtils.daysUntil(crop.getExpectedHarvestDate());
        String badgeText;
        int color;

        if (daysLeft == Integer.MIN_VALUE) {
            badgeText = "-";
            color = 0xFF616161;
        } else if (daysLeft < 0) {
            badgeText = Math.abs(daysLeft) + "d overdue";
            color = 0xFFB71C1C;
        } else if (daysLeft == 0) {
            badgeText = "Today!";
            color = 0xFFE65100;
        } else if (daysLeft <= 7) {
            badgeText = daysLeft + "d left";
            color = 0xFFE65100;
        } else {
            badgeText = daysLeft + "d left";
            color = 0xFF1B5E20;
        }

        holder.tvStatusBadge.setText(badgeText);
        GradientDrawable bg = (GradientDrawable) holder.tvStatusBadge.getBackground().mutate();
        bg.setColor(color);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCropClick(crop);
        });
    }

    @Override
    public int getItemCount() {
        return crops == null ? 0 : crops.size();
    }

    static class CropViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCropThumbnail;
        TextView tvCropName, tvPlotName, tvHarvestDate, tvStatusBadge;

        CropViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCropThumbnail = itemView.findViewById(R.id.ivCropThumbnail);
            tvCropName = itemView.findViewById(R.id.tvCropName);
            tvPlotName = itemView.findViewById(R.id.tvPlotName);
            tvHarvestDate = itemView.findViewById(R.id.tvHarvestDate);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
        }
    }
}