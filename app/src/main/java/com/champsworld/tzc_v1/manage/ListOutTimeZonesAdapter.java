package com.champsworld.tzc_v1.manage;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.champsworld.tzc_v1.R;
import com.champsworld.tzc_v1.ResultItem;

import java.util.List;

public class ListOutTimeZonesAdapter extends RecyclerView.Adapter<ListOutTimeZonesAdapter.ViewHolder> {

    private List<ResultItem> resultList;
    private final TimeZoneEditor editor;
    public ListOutTimeZonesAdapter(List<ResultItem> resultList, TimeZoneEditor editor) {
        this.resultList = resultList;
        this.editor = editor;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.prefs_out_tz_editor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Other code...

        ResultItem item = resultList.get(position);
        holder.locationTextView.setText(item.getLocation());
        holder.timezoneTextView.setText(item.getTimezone());
            
        // Set click listener for the "Edit" button
        holder.editEntryButton.setOnClickListener(v -> {
            // Handle click on the "Edit" button
            // Fetch the corresponding item from your dataset based on the position
            ResultItem selectedItem = resultList.get(position);
            // Perform any action you need with the selectedItem
            // For example, you might open an editing activity with this item
            editor.editItem(selectedItem, (b, s) -> System.out.println("editing complete"));
        });

        // Set click listener for the "Remove" button
        holder.delEntryButton.setOnClickListener(v -> {
            // Handle click on the "Remove" button
            // Fetch the corresponding item from your dataset based on the position
            ResultItem selectedItem = resultList.get(position);
            // Perform any action you need with the selectedItem
            // For example, you might remove the item from the dataset and notify the adapter
            editor.deleteItem(selectedItem, (b, s)->{
                System.out.println("delete complete");
            });
        });
    }


    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView locationTextView;
        public TextView timezoneTextView;
        public Button editEntryButton;
        public Button delEntryButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            timezoneTextView = itemView.findViewById(R.id.timezoneTextView);
            editEntryButton = itemView.findViewById(R.id.editEntryButton);
            delEntryButton = itemView.findViewById(R.id.delEntryButton);
        }
    }


}


