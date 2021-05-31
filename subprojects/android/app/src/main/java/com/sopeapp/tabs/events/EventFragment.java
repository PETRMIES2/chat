package com.sopeapp.tabs.events;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sope.domain.websocket.CategoryDTO;
import com.sope.domain.websocket.WebSocketMessageType;
import com.sopeapp.R;
import com.sopeapp.SopeApplication;
import com.sopeapp.globaldrawer.GlobalClickListener;
import com.sopeapp.websocket.WebSocketMessageManager;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

public class EventFragment extends Fragment {
    @BindView(R.id.event_listview)
    RecyclerView eventRecyleView;
    private List<CategoryDTO> events = new LinkedList<>();

    private EventDatamanager eventDatamanager;
    private CompositeSubscription compositeSubscription;

    public EventFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.event_fragment, container, false);
        ButterKnife.bind(this, rootView);
        ((SopeApplication) getActivity().getApplication()).getRestComponent().inject(this);

        LinearLayoutManager channelLayoutManager = new LinearLayoutManager(getActivity());
        eventRecyleView.setLayoutManager(channelLayoutManager);
        eventRecyleView.setHasFixedSize(true); // to improve performance

        eventDatamanager = new EventDatamanager(this);
        eventRecyleView.setAdapter(eventDatamanager); // the data manager is assigner to the RV

        eventRecyleView.addOnItemTouchListener( // and the click is handled
                new GlobalClickListener(getActivity(), (view, position) -> {
                    Log.i("INFO", "Clicked:  " + events.get(position).getName());
                    WebSocketMessageManager.getChatsFor(events.get(position), WebSocketMessageType.GET_EVENTS_CHATS);

                }));

        compositeSubscription = new CompositeSubscription();

        return rootView;

    }

    @Override
    public void onStart() {
        compositeSubscription.add(SopeApplication.getInstance().getTabSubscription().subscribe(new EventListSubscription(this)));
        compositeSubscription.add(SopeApplication.getInstance().getCategoryChatSubscription().subscribe(new EventChatSubscription(this)));
        super.onStart();

    }

    @Override
    public void onStop() {
        compositeSubscription.clear();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        compositeSubscription.clear();
        super.onDestroy();
    }

    public List<CategoryDTO> getEvents() {
        return events;
    }

    public EventDatamanager getEventDatamanager() {
        return eventDatamanager;
    }
}
