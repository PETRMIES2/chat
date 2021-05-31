package com.sopeapp.tabs.popular;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sope.domain.category.CategoryType;
import com.sope.domain.websocket.CategoryDTO;
import com.sope.domain.websocket.WebSocketMessageType;
import com.sopeapp.R;
import com.sopeapp.SopeApplication;
import com.sopeapp.globaldrawer.GlobalClickListener;
import com.sopeapp.tabs.general.GeneralChatSubscription;
import com.sopeapp.tabs.general.GeneralDatamanager;
import com.sopeapp.tabs.general.GeneralListSubscription;
import com.sopeapp.websocket.WebSocketMessageManager;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

public class PopularFragment extends Fragment {

    @BindView(R.id.popular_activity_listview)
    RecyclerView popularCategoryRecycleView;
    private List<CategoryDTO> popularCategories = new LinkedList<>();

    private CompositeSubscription compositeSubscription;
    private PopularDatamanager popularDatamanager;

    public PopularFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.popular__fragment, container, false);

        ButterKnife.bind(this, rootView);

        ((SopeApplication) getActivity().getApplication()).getRestComponent().inject(this);

        popularDatamanager = new PopularDatamanager(this);

        LinearLayoutManager channelLayoutManager = new LinearLayoutManager(getActivity());
        popularCategoryRecycleView.setLayoutManager(channelLayoutManager);
        popularCategoryRecycleView.setHasFixedSize(true);
        popularCategoryRecycleView.setAdapter(popularDatamanager);

        popularCategoryRecycleView.addOnItemTouchListener(
                new GlobalClickListener(getActivity(), (view, position) -> {
                    CategoryDTO categoryDTO = popularCategories.get(position);
                    Log.i("INFO", "Clicked:  " + categoryDTO.getName());
                    WebSocketMessageManager.getChatsFor(categoryDTO, getType(categoryDTO.getCategoryType()));
                }));

        compositeSubscription = new CompositeSubscription();

        return rootView;
    }

    @Override
    public void onStart() {
        compositeSubscription.add(SopeApplication.getInstance().getTabSubscription().subscribe(new PopularListSubscription(this)));
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


    private WebSocketMessageType getType(CategoryType categoryType) {
        switch(categoryType) {
            case EVENT: return WebSocketMessageType.GET_EVENTS_CHATS;
            case GENERAL: return WebSocketMessageType.GET_GENERAL_CHATS;
            case SHOW: return WebSocketMessageType.GET_TVSHOW_CHATS;
        }
        // FIXME this is failure if we get here
        return WebSocketMessageType.GET_EVENTS_CHATS;
    }



    public List<CategoryDTO> getPopularCategories() {
        return popularCategories;
    }
    public PopularDatamanager getPopularDatamanager() {
        return popularDatamanager;
    }

}
