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
import com.example.cropmanagementapp.model.ActivityLog;

import java.util.List;

public class ActivityLogAdapter extends RecyclerView.Adapter<ActivityLogAdapter.ActivityViewHolder> {

    private List<ActivityLog> activities;

    public ActivityLogAdapter(List<ActivityLog> activities) {
        this.activities = activities;
    }

    public void updateData(List<ActivityLog> newActivities) {
        this.activities = newActivities;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        ActivityLog log = activities.get(position);
        holder.tvActivityType.setText(log.getActivityType());
        holder.tvActivityDate.setText(DateUtils.toDisplayFormat(log.getActivityDate()));

        if (!TextUtils.isEmpty(log.getExpenseAmount())) {
            holder.tvActivityExpense.setText("Spent: KES " + log.getExpenseAmount());
            holder.tvActivityExpense.setVisibility(View.VISIBLE);
        } else {
            holder.tvActivityExpense.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(log.getNotes())) {
            holder.tvActivityNotes.setText(log.getNotes());
            holder.tvActivityNotes.setVisibility(View.VISIBLE);
        } else {
            holder.tvActivityNotes.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return activities == null ? 0 : activities.size();
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView tvActivityType, tvActivityDate, tvActivityExpense, tvActivityNotes;

        ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvActivityType = itemView.findViewById(R.id.tvActivityType);
            tvActivityDate = itemView.findViewById(R.id.tvActivityDate);
            tvActivityExpense = itemView.findViewById(R.id.tvActivityExpense);
            tvActivityNotes = itemView.findViewById(R.id.tvActivityNotes);
        }
    }
}