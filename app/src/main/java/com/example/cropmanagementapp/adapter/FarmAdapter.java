package com.example.cropmanagementapp.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cropmanagementapp.R;
import com.example.cropmanagementapp.model.Farm;

import java.util.List;

public class FarmAdapter extends RecyclerView.Adapter<FarmAdapter.FarmViewHolder> {

    public interface OnFarmClickListener {
        void onFarmClick(Farm farm);
    }

    public interface OnFarmLongClickListener {
        void onFarmLongClick(Farm farm);
    }

    private List<Farm> farms;
    private long currentFarmId;
    private final OnFarmClickListener listener;
    private final OnFarmLongClickListener longClickListener;

    public FarmAdapter(List<Farm> farms, long currentFarmId, OnFarmClickListener listener,
                       OnFarmLongClickListener longClickListener) {
        this.farms = farms;
        this.currentFarmId = currentFarmId;
        this.listener = listener;
        this.longClickListener = longClickListener;
    }

    public void updateData(List<Farm> newFarms, long newCurrentFarmId) {
        this.farms = newFarms;
        this.currentFarmId = newCurrentFarmId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_farm, parent, false);
        return new FarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FarmViewHolder holder, int position) {
        Farm farm = farms.get(position);
        holder.tvFarmName.setText(farm.getName());
        holder.tvFarmLocation.setText(TextUtils.isEmpty(farm.getLocation()) ? "No location set" : farm.getLocation());
        holder.tvFarmCurrentBadge.setVisibility(farm.getId() == currentFarmId ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onFarmClick(farm);
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) longClickListener.onFarmLongClick(farm);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return farms == null ? 0 : farms.size();
    }

    static class FarmViewHolder extends RecyclerView.ViewHolder {
        TextView tvFarmName, tvFarmLocation, tvFarmCurrentBadge;

        FarmViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFarmName = itemView.findViewById(R.id.tvFarmName);
            tvFarmLocation = itemView.findViewById(R.id.tvFarmLocation);
            tvFarmCurrentBadge = itemView.findViewById(R.id.tvFarmCurrentBadge);
        }
    }
}