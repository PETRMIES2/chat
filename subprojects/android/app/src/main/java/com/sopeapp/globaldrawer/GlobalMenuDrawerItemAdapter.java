package com.sopeapp.globaldrawer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sopeapp.R;

import java.util.List;

public class GlobalMenuDrawerItemAdapter extends ArrayAdapter<GlobalMenuDrawerItem> {

    Context mContext;
    int layoutResourceId;
    List<GlobalMenuDrawerItem> drawerItems;

    public GlobalMenuDrawerItemAdapter(Context mContext, int layoutResourceId, List drawerItems) {

        super(mContext, layoutResourceId, (GlobalMenuDrawerItem[]) drawerItems.toArray(new GlobalMenuDrawerItem[drawerItems.size()]));
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.drawerItems = drawerItems;
    }

    @Override
    public View getView(int position, View listItem, ViewGroup parent) {

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        ImageView imageViewIcon = (ImageView) listItem.findViewById(R.id.imageViewIcon);
        TextView textViewName = (TextView) listItem.findViewById(R.id.textViewName);

        GlobalMenuDrawerItem drawerItem = drawerItems.get(position);


        imageViewIcon.setImageResource(drawerItem.icon);
        textViewName.setText(drawerItem.name);

        return listItem;
    }
}