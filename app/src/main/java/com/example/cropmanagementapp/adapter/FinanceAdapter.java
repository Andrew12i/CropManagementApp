package com.example.cropmanagementapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cropmanagementapp.R;
import com.example.cropmanagementapp.model.FinanceEntry;

import java.util.List;
import java.util.Locale;

public class FinanceAdapter extends RecyclerView.Adapter<FinanceAdapter.FinanceViewHolder> {

    private List<FinanceEntry> entries;

    public FinanceAdapter(List<FinanceEntry> entries) {
        this.entries = entries;
    }

    public void updateData(List<FinanceEntry> newEntries) {
        this.entries = newEntries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FinanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_finance_crop, parent, false);
        return new FinanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FinanceViewHolder holder, int position) {
        FinanceEntry entry = entries.get(position);
        holder.tvFinanceCropName.setText(entry.getCropName() + " — " + entry.getPlotName());
        holder.tvFinanceIncome.setText(String.format(Locale.getDefault(), "Income: KES %.2f", entry.getTotalIncome()));
        holder.tvFinanceExpense.setText(String.format(Locale.getDefault(), "Expenses: KES %.2f", entry.getTotalExpense()));

        double net = entry.getNetProfit();
        holder.tvFinanceNet.setText(String.format(Locale.getDefault(), "Net: KES %.2f", net));
        holder.tvFinanceNet.setTextColor(net >= 0
                ? holder.itemView.getResources().getColor(R.color.green_primary)
                : holder.itemView.getResources().getColor(R.color.red_overdue));
    }

    @Override
    public int getItemCount() {
        return entries == null ? 0 : entries.size();
    }

    static class FinanceViewHolder extends RecyclerView.ViewHolder {
        TextView tvFinanceCropName, tvFinanceIncome, tvFinanceExpense, tvFinanceNet;

        FinanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFinanceCropName = itemView.findViewById(R.id.tvFinanceCropName);
            tvFinanceIncome = itemView.findViewById(R.id.tvFinanceIncome);
            tvFinanceExpense = itemView.findViewById(R.id.tvFinanceExpense);
            tvFinanceNet = itemView.findViewById(R.id.tvFinanceNet);
        }
    }
}