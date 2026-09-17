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
import com.example.cropmanagementapp.model.IncomeLog;

import java.util.List;
import java.util.Locale;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {

    private List<IncomeLog> incomeList;

    public IncomeAdapter(List<IncomeLog> incomeList) {
        this.incomeList = incomeList;
    }

    public void updateData(List<IncomeLog> newList) {
        this.incomeList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_income, parent, false);
        return new IncomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
        IncomeLog income = incomeList.get(position);
        double amount = 0;
        try { amount = Double.parseDouble(income.getTotalAmount()); } catch (NumberFormatException ignored) { }

        holder.tvIncomeAmount.setText(String.format(Locale.getDefault(), "KES %.2f", amount));
        holder.tvIncomeDate.setText(DateUtils.toDisplayFormat(income.getIncomeDate()));
        holder.tvIncomeBuyer.setText(TextUtils.isEmpty(income.getBuyerName())
                ? "Buyer not specified" : "Buyer: " + income.getBuyerName());
        holder.tvIncomeStatus.setText(income.getReceivedStatus());
    }

    @Override
    public int getItemCount() {
        return incomeList == null ? 0 : incomeList.size();
    }

    static class IncomeViewHolder extends RecyclerView.ViewHolder {
        TextView tvIncomeAmount, tvIncomeDate, tvIncomeBuyer, tvIncomeStatus;

        IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIncomeAmount = itemView.findViewById(R.id.tvIncomeAmount);
            tvIncomeDate = itemView.findViewById(R.id.tvIncomeDate);
            tvIncomeBuyer = itemView.findViewById(R.id.tvIncomeBuyer);
            tvIncomeStatus = itemView.findViewById(R.id.tvIncomeStatus);
        }
    }
}