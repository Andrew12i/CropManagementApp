package com.example.cropmanagementapp.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cropmanagementapp.R;
import com.example.cropmanagementapp.db.DateUtils;
import com.example.cropmanagementapp.model.Crop;

import java.util.List;

/**
 * Binds a list of Crop objects to card rows, showing a colour-coded
 * status badge based on how close the crop is to its expected harvest date.
 */
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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crop, parent, false);
        return new CropViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CropViewHolder holder, int position) {
        Crop crop = crops.get(position);
        holder.tvCropName.setText(crop.getCropName());
        holder.tvPlotName.setText("Plot: " + crop.getPlotName());
        holder.tvHarvestDate.setText("Harvest: " + DateUtils.toDisplayFormat(crop.getExpectedHarvestDate()));

        int daysLeft = DateUtils.daysUntil(crop.getExpectedHarvestDate());
        String badgeText;
        int color;

        if (daysLeft == Integer.MIN_VALUE) {
            badgeText = "-";
            color = 0xFF9E9E9E;
        } else if (daysLeft < 0) {
            badgeText = Math.abs(daysLeft) + "d overdue";
            color = 0xFFC62828; // red
        } else if (daysLeft == 0) {
            badgeText = "Today!";
            color = 0xFFF9A825; // amber
        } else if (daysLeft <= 7) {
            badgeText = daysLeft + "d left";
            color = 0xFFF9A825; // amber
        } else {
            badgeText = daysLeft + "d left";
            color = 0xFF2E7D32; // green
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
        TextView tvCropName, tvPlotName, tvHarvestDate, tvStatusBadge;

        CropViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCropName = itemView.findViewById(R.id.tvCropName);
            tvPlotName = itemView.findViewById(R.id.tvPlotName);
            tvHarvestDate = itemView.findViewById(R.id.tvHarvestDate);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
        }
    }
}