package com.example.projectprm392.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.projectprm392.Model.Location;
import com.example.projectprm392.R;

import java.util.List;

public class LocationAdapter extends BaseAdapter {
    private Context context;
    private List<Location> locationList;

    public LocationAdapter(Context context, List<Location> locationList) {
        this.context = context;
        this.locationList = locationList;
    }

    @Override
    public int getCount() {
        return locationList.size();
    }

    @Override
    public Object getItem(int position) {
        return locationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.location_item, parent, false);
            holder = new ViewHolder();
            holder.tvLocationName = convertView.findViewById(R.id.tvLocationName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Location location = locationList.get(position);
        holder.tvLocationName.setText(location.getName());

        return convertView;
    }

    static class ViewHolder {
        TextView tvLocationName;
    }

    public void updateList(List<Location> newList) {
        this.locationList = newList;
        notifyDataSetChanged();
    }
}
