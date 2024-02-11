package com.champsworld.tzc_v1.manage;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.champsworld.tzc_v1.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomArrayAdapter extends ArrayAdapter<String> {
    private final Map<String, Integer> positionMap = new HashMap<>();
    private AtomicInteger selectedItem = new AtomicInteger(-1);
    private final String[] items ;

    public CustomArrayAdapter(Context context,int itemId, String[] items) {
        super(context, itemId, items);
        this.items = items;
        buildPositionMap(items);
    }

    private void buildPositionMap(String[] items) {
        for (int i = 0; i < items.length; i++) {
            positionMap.put(items[i], i);
        }
    }

    @Override
    public int getPosition(@Nullable String item) {
        Integer position = positionMap.get(item);
        return position != null ? position : AdapterView.INVALID_POSITION;
    }

    public String getSelectedItemString(){
        if(selectedItem.get() < 0) return null;
        if(selectedItem.get() < items.length)
            return items[selectedItem.get()];
        return null;
    }

    public void setSelectedItem(int position) {
        selectedItem.set(position);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        if (position == selectedItem.get()) {
            // Highlight the selected item here
            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.highlightColor));
        } else {
            // Reset the background color for other items
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        return view;
    }
}
