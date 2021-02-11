package com.example.ex121;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 *  * @author		Shahar Yani
 *  * @version  	1.0
 *  * @since		20/01/2021
 *
 *  * This CustomAdapter.class is a new adapter in order to design one item in the CustomListView object.
 *  */
public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> nameList, details;
    LayoutInflater inflater;

    public CustomAdapter(Context applicationContext, ArrayList<String> nameList, ArrayList<String> details) {
        this.context = applicationContext;
        this.nameList = nameList;
        this.details = details;
        this.inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return nameList.size();
    }

    @Override
    public Object getItem(int position) {
        return nameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.custom_lv_layout, null);

        TextView studentName = convertView.findViewById(R.id.studentName);
        TextView detailsTv = convertView.findViewById(R.id.detailsTv);

        studentName.setText(nameList.get(position));
        detailsTv.setText(details.get(position));

        return convertView;
    }
}
