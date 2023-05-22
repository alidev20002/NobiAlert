package com.example.nobialert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CoinAdapter extends ArrayAdapter<Coin> {

    private ArrayList<Coin> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView tname;
        TextView tprice;
        TextView tlim;
        ImageView icon;
    }

    public CoinAdapter(ArrayList<Coin> data, Context context) {
        super(context, R.layout.rowview, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Coin dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.rowview, parent, false);
            viewHolder.tname = convertView.findViewById(R.id.row_name);
            viewHolder.tprice = convertView.findViewById(R.id.row_price);
            viewHolder.tlim = convertView.findViewById(R.id.row_lim);
            viewHolder.icon = convertView.findViewById(R.id.row_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tname.setText(dataModel.getName());
        viewHolder.tprice.setText("" + dataModel.getPrice());
        viewHolder.tlim.setText(dataModel.getLimit());
        viewHolder.icon.setImageResource(dataModel.getIcon());
        // Return the completed view to render on screen
        return convertView;
    }
}
