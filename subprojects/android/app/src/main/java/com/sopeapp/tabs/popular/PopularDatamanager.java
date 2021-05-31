package com.sopeapp.tabs.popular;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sope.domain.websocket.CategoryDTO;
import com.sopeapp.R;
import com.sopeapp.SopeApplication;


public class PopularDatamanager extends RecyclerView.Adapter<PopularDatamanager.RecyclerViewHolder> {

    private PopularFragment popularFragment;

    public PopularDatamanager(PopularFragment popularFragment) {
        this.popularFragment = popularFragment;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView categoryName;
        ImageView categoryIcon;
        TextView categoryParticipants;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            categoryName = (TextView) itemView.findViewById(R.id.main_category_name);
            categoryIcon = (ImageView) itemView.findViewById(R.id.main_category_icon);
            categoryParticipants = (TextView) itemView.findViewById(R.id.main_category_participants);
        }
    }

    @Override
    public PopularDatamanager.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.general_fragment_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(PopularDatamanager.RecyclerViewHolder viewHolder, int position) {

        final CategoryDTO generalDTO = popularFragment.getPopularCategories().get(position);
        viewHolder.categoryName.setText(generalDTO.getName());

        int iconId = 0;
        if (generalDTO.getIcon() != null) {
            iconId = popularFragment.getResources().getIdentifier(generalDTO.getIcon(), "drawable", SopeApplication.getAppContext().getPackageName());
        }
        if (iconId == 0) {
            iconId = popularFragment.getResources().getIdentifier("unknown", "drawable", SopeApplication.getAppContext().getPackageName());
        }


        viewHolder.categoryIcon.setImageResource(iconId);
        viewHolder.categoryParticipants.setText("" + generalDTO.getUsers());
    }

    @Override
    public int getItemCount() {
        return popularFragment.getPopularCategories().size();
    }
}
