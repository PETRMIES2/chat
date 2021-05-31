package com.sopeapp.tabs.events;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sope.domain.websocket.CategoryDTO;
import com.sopeapp.R;
import com.sopeapp.SopeApplication;


public class EventDatamanager extends RecyclerView.Adapter<EventDatamanager.RecyclerViewHolder> {
    private EventFragment eventFragment;

    public EventDatamanager(EventFragment eventFragment) {
        this.eventFragment = eventFragment;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView eventName;
        TextView showParticipants;
        TextView from;
        TextView to;
        TextView place;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            eventName = (TextView) itemView.findViewById(R.id.event_name);
            showParticipants = (TextView) itemView.findViewById(R.id.event_participants);
            from = (TextView) itemView.findViewById(R.id.event_start);
            to = (TextView) itemView.findViewById(R.id.event_end);
            icon = (ImageView) itemView.findViewById(R.id.event_icon);
            place = (TextView) itemView.findViewById(R.id.event_place);

        }
    }

    @Override
    public EventDatamanager.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View channelObject = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_fragment_item_layout, parent, false);
        return new RecyclerViewHolder(channelObject);
    }

    @Override
    public void onBindViewHolder(EventDatamanager.RecyclerViewHolder viewHolder, int position) {

        final CategoryDTO events = eventFragment.getEvents().get(position);
        viewHolder.eventName.setText(events.getName());
        viewHolder.showParticipants.setText(events.getParticipants());
        viewHolder.from.setText(events.getStartTime());
        viewHolder.to.setText(events.getEndTime());
        viewHolder.place.setText(events.getPlace());
        int iconId = 0;
        if (events.getIcon() != null) {
            iconId = eventFragment.getResources().getIdentifier(events.getIcon(), "drawable", SopeApplication.getAppContext().getPackageName());
        }
        if (iconId == 0) {
            iconId = eventFragment.getResources().getIdentifier("unknown", "drawable", SopeApplication.getAppContext().getPackageName());
        }
        viewHolder.icon.setImageResource(iconId);
    }

    @Override
    public int getItemCount() {
        return eventFragment.getEvents().size();
    }
}
