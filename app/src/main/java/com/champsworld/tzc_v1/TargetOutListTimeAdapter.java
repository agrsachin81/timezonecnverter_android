package com.champsworld.tzc_v1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TargetOutListTimeAdapter extends RecyclerView.Adapter<TargetOutListTimeAdapter.ViewHolder> {

    private List<ResultItem> resultList;

    public TargetOutListTimeAdapter(List<ResultItem> resultList) {
        this.resultList = resultList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResultItem item = resultList.get(position);
        holder.locationTextView.setText(item.getLocation());
        holder.convertedTimeTextView.setText(item.getConvertedTime());
        holder.timezoneTextView.setText(item.getTimezone());
        holder.dateTextView.setText(item.getDate());
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView locationTextView;
        public TextView convertedTimeTextView;
        public TextView timezoneTextView;
        public TextView dateTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            convertedTimeTextView = itemView.findViewById(R.id.convertedTimeTextView);
            timezoneTextView = itemView.findViewById(R.id.timezoneTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}
