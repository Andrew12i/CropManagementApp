package com.example.cropmanagementapp.adapter;

import android.text.TextUtils;
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

public class ArchivedCropAdapter extends RecyclerView.Adapter<ArchivedCropAdapter.ArchivedViewHolder> {

    public interface OnArchivedCropClickListener {
        void onCropClick(Crop crop);
    }

    private List<Crop> crops;
    private final OnArchivedCropClickListener listener;

    public ArchivedCropAdapter(List<Crop> crops, OnArchivedCropClickListener listener) {
        this.crops = crops;
        this.listener = listener;
    }

    public void updateData(List<Crop> newCrops) {
        this.crops = newCrops;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArchivedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_archived_crop, parent, false);
        return new ArchivedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArchivedViewHolder holder, int position) {
        Crop crop = crops.get(position);
        String variety = crop.getVariety();
        String nameLine = TextUtils.isEmpty(variety) ? crop.getCropName() : crop.getCropName() + " (" + variety + ")";
        holder.tvCropName.setText(nameLine);
        holder.tvPlotName.setText("Plot: " + crop.getPlotName());
        holder.tvHarvestedDate.setText("Harvested: " + DateUtils.toDisplayFormat(crop.getHarvestedDate()));
        holder.tvYieldAmount.setText("Yield: " + (TextUtils.isEmpty(crop.getYieldAmount()) ? "Not recorded" : crop.getYieldAmount()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCropClick(crop);
        });
    }

    @Override
    public int getItemCount() {
        return crops == null ? 0 : crops.size();
    }

    static class ArchivedViewHolder extends RecyclerView.ViewHolder {
        TextView tvCropName, tvPlotName, tvHarvestedDate, tvYieldAmount;

        ArchivedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCropName = itemView.findViewById(R.id.tvCropName);
            tvPlotName = itemView.findViewById(R.id.tvPlotName);
            tvHarvestedDate = itemView.findViewById(R.id.tvHarvestedDate);
            tvYieldAmount = itemView.findViewById(R.id.tvYieldAmount);
        }
    }
}