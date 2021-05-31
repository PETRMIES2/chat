package com.sopeapp.tabs.general;


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

public class GeneralFragment extends Fragment {

    @BindView(R.id.general_activity_listview)
    RecyclerView mainCategoryRecycleView;
    private List<CategoryDTO> generalCategories = new LinkedList<>();


    private GeneralDatamanager generalDatamanager;
    private CompositeSubscription compositeSubscription;
    public GeneralFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.general_fragment, container, false);

        ButterKnife.bind(this, rootView);

        ((SopeApplication) getActivity().getApplication()).getRestComponent().inject(this);

        generalDatamanager = new GeneralDatamanager(this);

        LinearLayoutManager channelLayoutManager = new LinearLayoutManager(getActivity());
        mainCategoryRecycleView.setLayoutManager(channelLayoutManager);
        mainCategoryRecycleView.setHasFixedSize(true);
        mainCategoryRecycleView.setAdapter(generalDatamanager);

        mainCategoryRecycleView.addOnItemTouchListener(
                new GlobalClickListener(getActivity(), (view, position) -> {
                    Log.i("INFO", "Clicked:  " + generalCategories.get(position).getName());
                    WebSocketMessageManager.getChatsFor(generalCategories.get(position), WebSocketMessageType.GET_GENERAL_CHATS);
                }));
        compositeSubscription = new CompositeSubscription();

        return rootView;
    }


    @Override
    public void onStart() {
        compositeSubscription.add(SopeApplication.getInstance().getTabSubscription().subscribe(new GeneralListSubscription(this)));
        compositeSubscription.add(SopeApplication.getInstance().getCategoryChatSubscription().subscribe( new GeneralChatSubscription(this)));
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

    public List<CategoryDTO> getGeneralCategories() {
        return generalCategories;
    }
    public GeneralDatamanager getGeneralDatamanager() {
        return generalDatamanager;
    }

}
