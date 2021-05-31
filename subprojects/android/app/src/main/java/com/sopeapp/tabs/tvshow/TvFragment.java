package com.sopeapp.tabs.tvshow;


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

public class TvFragment extends Fragment {
    @BindView(R.id.tv_show_listview)
    RecyclerView channelRecycleView;
    private List<CategoryDTO> tvShow = new LinkedList<>();

    private CompositeSubscription compositeSubscription;
    private TvShowDatamanager tvTvShowDatamanager;

    public TvFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tvshow_fragment, container, false);
        ButterKnife.bind(this, rootView);
        ((SopeApplication) getActivity().getApplication()).getRestComponent().inject(this);

        LinearLayoutManager channelLayoutManager = new LinearLayoutManager(getActivity());
        channelRecycleView.setLayoutManager(channelLayoutManager);
        channelRecycleView.setHasFixedSize(true); // to improve performance
        tvTvShowDatamanager = new TvShowDatamanager(this);
        channelRecycleView.setAdapter(tvTvShowDatamanager); // the data manager is assigner to the RV
        channelRecycleView.addOnItemTouchListener( // and the click is handled
                new GlobalClickListener(getActivity(), (view, position) -> {
                    Log.i("INFO", "Clicked:  " + tvShow.get(position).getName());
                    WebSocketMessageManager.getChatsFor(tvShow.get(position), WebSocketMessageType.GET_TVSHOW_CHATS);
                }));

        compositeSubscription = new CompositeSubscription();

        return rootView;
    }


    @Override
    public void onStart() {
        compositeSubscription.add(SopeApplication.getInstance().getTabSubscription().subscribe(new TvListSubscription(this)));
        compositeSubscription.add(SopeApplication.getInstance().getCategoryChatSubscription().subscribe( new TvChatSubscription(this)));
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

    public List<CategoryDTO> getTvShow() {
        return tvShow;
    }

    public TvShowDatamanager getTvTvShowDatamanager() {
        return tvTvShowDatamanager;
    }

}
