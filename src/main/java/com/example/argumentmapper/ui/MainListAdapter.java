package com.example.argumentmapper.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.argumentmapper.ArgumentMap;
import com.example.argumentmapper.R;

import java.util.List;

public class MainListAdapter extends ArrayAdapter<ArgumentMap> {
    private final LayoutInflater inflater;

    private static class ArgumentMapViewHolder {
        TextView vTopic;
    }

    MainListAdapter(Context context, List<ArgumentMap> items)
    {
        super(context, R.layout.map_card, items);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ArgumentMap map = getItem(position);
        ArgumentMapViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ArgumentMapViewHolder();
            convertView = inflater.inflate(R.layout.map_card, parent, false);
            viewHolder.vTopic = (TextView) convertView.findViewById(R.id.topic);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ArgumentMapViewHolder) convertView.getTag();
        }
        viewHolder.vTopic.setText(map.getTopic());
        return convertView;
    }
}
