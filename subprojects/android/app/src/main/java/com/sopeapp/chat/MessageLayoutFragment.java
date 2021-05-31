package com.sopeapp.chat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sope.domain.user.UserDTO;
import com.sope.domain.websocket.ChatDTO;
import com.sope.domain.websocket.SopeSocketMessage;
import com.sopeapp.R;
import com.sopeapp.SharedPreferencesManager;
import com.sopeapp.SopeApplication;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageLayoutFragment extends Fragment {

    @BindView(R.id.chat_activity_infromation_participants)
    TextView chatParticipants;

    @BindView(R.id.chat_header)
    TextView chatHeader;

    @BindView(R.id.add_chat_icon)
    ImageView addChatImage;

    @BindView(R.id.chat_activity_infromation_person_icon)
    ImageView userIcon;

    LinearLayoutManager chatMessageLayout;
    private ChatMessageDatamanager chatMessageDatamanager;

    @BindView(R.id.chat_list_recycler_view)
    RecyclerView chatRecycleMessageView;

    ViewPager chatContainer;
    public static final String CHAT_PAGE = "page";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chat_message_layout, container, false);
        ButterKnife.bind(this, rootView);
        chatContainer = (ViewPager) container;

        chatMessageLayout = new LinearLayoutManager(getActivity());

        chatRecycleMessageView.setLayoutManager(chatMessageLayout);
        chatRecycleMessageView.setHasFixedSize(true); // to improve performance

        chatRecycleMessageView.setAdapter(chatMessageDatamanager);

        chatHeader.setOnClickListener(view -> {
            ChatRenameHeaderDialog dialog = new ChatRenameHeaderDialog();
            dialog.setTargetFragment(this);
            dialog.show(getActivity().getFragmentManager(), "ChangeHeader");
        });

        addChatImage.setOnClickListener(view -> {
            NewChatDialog dialog = new NewChatDialog();
            dialog.show(getActivity().getFragmentManager(), "NewChatHeader");
            dialog.setTargetFragment(this);
        });
        updateFramentData();

        chatParticipants.setText("#");

        userIcon.setOnClickListener(view -> {
            ChatActivity chatActivity = ((ChatActivity) getActivity());
            if (chatActivity.isDrawerOpen()) {
                chatActivity.closeDrawer();
            } else {
                chatActivity.openDrawer();
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserDTO user = SharedPreferencesManager.getUser(SopeApplication.getAppContext());
        Log.i("Mess.lay.frag:onCreate", SharedPreferencesManager.getCurrentChat(SopeApplication.getAppContext()).chatHeader);

        chatMessageDatamanager = new ChatMessageDatamanager(user.getUsername(), this);

        chatMessageDatamanager.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                chatRecycleMessageView.scrollToPosition(chatRecycleMessageView.getAdapter().getItemCount() - 1);
            }
        });

    }


    public void updateFramentData() {
        int chatPageFromInitialization = this.getArguments().getInt(CHAT_PAGE);

        SopeSocketMessage sopeMessage = SharedPreferencesManager.getSopeMessage(getContext());
        ChatDTO chatPage = sopeMessage.chatList.getChats().get(chatPageFromInitialization);
//        Log.i("ChatFrag", " " + chatPage.chatNumber);
        chatHeader.setText(chatPage.chatHeader);
        chatMessageDatamanager.notifyDataSetChanged();
        chatRecycleMessageView.smoothScrollToPosition(chatMessageDatamanager.getItemCount());
    }

    public void newChatCreated(int page) {

        chatContainer.setCurrentItem(page, true);
    }

}
