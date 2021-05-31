package com.sopeapp.tabs.tvshow;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sope.domain.websocket.CategoryDTO;
import com.sopeapp.R;
import com.sopeapp.SopeApplication;


public class TvShowDatamanager extends RecyclerView.Adapter<TvShowDatamanager.RecyclerViewHolder> {
    private TvFragment tvShowFragment;

    public TvShowDatamanager(TvFragment tvFragment) {
        this.tvShowFragment = tvFragment;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        ImageView tvIcon;
        TextView showName;
        TextView showParticipants;
        TextView airtime;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            showName = (TextView) itemView.findViewById(R.id.channel_show_name);
            showParticipants = (TextView) itemView.findViewById(R.id.channel_show_participants);
            airtime = (TextView) itemView.findViewById(R.id.channel_show_time);
            tvIcon = (ImageView) itemView.findViewById(R.id.channel_tv_icon);
        }
    }

    @Override
    public TvShowDatamanager.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View channelObject = LayoutInflater.from(parent.getContext()).inflate(R.layout.tvshow_fragment_item_layout, parent, false);
        return new RecyclerViewHolder(channelObject);
    }

    @Override
    public void onBindViewHolder(TvShowDatamanager.RecyclerViewHolder viewHolder, int position) {

        final CategoryDTO tvShow = tvShowFragment.getTvShow().get(position);
        viewHolder.showName.setText(tvShow.getName());
        viewHolder.showParticipants.setText(tvShow.getParticipants());
        viewHolder.airtime.setText(tvShow.getShowAirTime());
        // this should be cached
        int iconId = 0;
        if (tvShow.getIcon() != null) {
            iconId = tvShowFragment.getResources().getIdentifier(tvShow.getIcon(), "drawable", SopeApplication.getAppContext().getPackageName());
        }
        if (iconId == 0) {
            iconId = tvShowFragment.getResources().getIdentifier("unknown", "drawable", SopeApplication.getAppContext().getPackageName());
        }
        viewHolder.tvIcon.setImageResource(iconId);
    }

    @Override
    public int getItemCount() {
        return tvShowFragment.getTvShow().size();
    }
}
