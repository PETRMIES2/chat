package com.sopeapp.chat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sope.domain.firebase.MessageToChat;
import com.sope.domain.user.UserDTO;
import com.sope.domain.websocket.CategoryDTO;
import com.sope.domain.websocket.ChatDTO;
import com.sope.domain.websocket.SopeSocketMessage;
import com.sopeapp.websocket.WebSocketMessageManager;
import com.sopeapp.R;
import com.sopeapp.SharedPreferencesManager;
import com.sopeapp.SopeApplication;
import com.sopeapp.tabs.MainTabActivity;
import com.sopeapp.utilities.ChatPagination;
import com.sopeapp.utilities.SendMessageRunnable;
import com.sopeapp.utilities.SopeApiRunnableManager;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

public class ChatActivity extends FragmentActivity implements
        EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    MessageLayoutPageAdapter messageLayoutPageAdapter;
    ViewPager viewPager;

    @BindView(R.id.chat_activity_back_icon)
    ImageView backButton;

    @BindView(R.id.chat_activity_channel_showname)
    TextView showName;

    private CompositeSubscription compositeSubscription;

    @BindView(R.id.chatMessageBox)
    EmojiconEditText chatMessageInput;


    @BindView(R.id.chatSendMessageButton)
    ImageView sendMessageIcon;

    @BindView(R.id.emojicons)
    RelativeLayout emoticonPopup;


    @BindView(R.id.emojiButton)
    ImageView emojiButton;

    @BindView(R.id.emojiTextKeyboard)
    TextView emojiTextKeyboard;

    // FIXME
    private String username;

    @BindView(R.id.chat_drawer_layout)
    DrawerLayout userDrawerLayout;
    @BindView(R.id.chat_left_drawer)
    ListView userDrawerList;

    private boolean drawerOpen = false;
    private ChatActivity chatActivity;

    private enum SendState {VIDEO, MESSAGE}

    private SendState current = SendState.VIDEO;
    static final int REQUEST_VIDEO_CAPTURE = 1;
    private ChatDrawerItemAdapter chatDrawerAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        ButterKnife.bind(this);
        ((SopeApplication) getApplication()).getRestComponent().inject(this);
        chatActivity = this;
        messageLayoutPageAdapter = new MessageLayoutPageAdapter(getSupportFragmentManager());

        chatDrawerAdapter = new ChatDrawerItemAdapter(this, R.layout.chat_drawer_item, new LinkedList<>());


        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(messageLayoutPageAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                WebSocketMessageManager.subscribeToChat(position, getApplicationContext());
                fetchOldMessages();
                chatDrawerAdapter.userList.clear();
                chatDrawerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        UserDTO user = SharedPreferencesManager.getUser(getApplicationContext());
        username = user.getUsername();

        updateChat();
        chatMessageInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 0) {
                    sendMessageIcon.setVisibility(View.VISIBLE);
                    sendMessageIcon.setImageResource(R.drawable.message_send_icon);
                    current = SendState.MESSAGE;
                } else {
                    sendMessageIcon.setVisibility(View.INVISIBLE);
//                    sendMessageIcon.setImageResource(R.drawable.record_video_button);
                    current = SendState.VIDEO;
                }
            }
        });

        backButton.setOnClickListener(view -> backToChannelList());


        emojiButton.setOnClickListener(view -> {
            hideKeyboard();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                if (emoticonPopup.getVisibility() == View.GONE) {
                    showEmojiPopUp(true);
                } else {
                    showEmojiPopUp(false);
                }
            }, 100);
        });
        emojiTextKeyboard.setOnClickListener(view -> {
            if (emoticonPopup.getVisibility() == View.GONE) {
                showEmojiPopUp(true);
            } else {
                showEmojiPopUp(false);
            }
            showKeyboard();
        });


        setEmojiconFragment(false);

        chatMessageInput.setOnClickListener(view -> showEmojiPopUp(false));
        sendMessageIcon.setOnClickListener(view -> {
            if (current == SendState.MESSAGE) {
                String messageToSend = chatMessageInput.getText().toString();
                if (!TextUtils.isEmpty(messageToSend)) {

                    SopeSocketMessage sopeMessage = SharedPreferencesManager.getSopeMessage(getApplicationContext());
                    ChatPagination.setChat(sopeMessage.chatList, viewPager.getCurrentItem());
                    ChatDTO currentChat = ChatPagination.getCurrentChat(sopeMessage.chatList);

                    MessageToChat message = new MessageToChat(chatMessageInput.getText().toString(), username, new Date(), currentChat.chatNumber, currentChat.chatHeader);
                    SopeApiRunnableManager.getInstance().execute(new SendMessageRunnable(message));
                    chatMessageInput.setText("");

                    refreshChatFragment(message);

                }
            } else {
//                int permissionCheck = ContextCompat.checkSelfPermission(chatActivity, Manifest.permission.CAMERA);
//                if (permissionCheck == PackageManager.PERMISSION_GRANTED && chatActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
//                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                    if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
//                        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
//                    }
//                } else {
//                    requestPermission(Manifest.permission.CAMERA);
//                }
            }
        });

        compositeSubscription = new CompositeSubscription();
        userdrawer(chatDrawerAdapter);

        fetchOldMessages();
        //getWindow().setBackgroundDrawableResource(R.drawable.chatbackground);

    }

    private static final int CAMERA_CAPTURE_PERMISSION = 0;

    private void requestPermission(String permission) {
        if (ContextCompat.checkSelfPermission(chatActivity, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(chatActivity, permission)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(chatActivity, new String[]{permission}, CAMERA_CAPTURE_PERMISSION);

                // CAMERA_CAPTURE_PERMISSION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_CAPTURE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void fetchOldMessages() {
        SopeSocketMessage sopeMessage = SharedPreferencesManager.getSopeMessage(SopeApplication.getAppContext());
        ChatDTO currentChat = ChatPagination.getCurrentChat(sopeMessage.chatList);
        if (currentChat != null) {
            WebSocketMessageManager.getOldMessages(currentChat);

        }
    }

    private void userdrawer(final ChatDrawerItemAdapter chatDrawerAdapter) {


        userDrawerList.setAdapter(chatDrawerAdapter);
        userDrawerList.setOnItemClickListener(new ChatItemClickListener());
        userDrawerLayout.addDrawerListener(new ActionBarDrawerToggle(this, userDrawerLayout, null, R.string.app_name, R.string.app_name) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                drawerOpen = false;
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                drawerOpen = true;
            }
        });


    }

    public boolean isDrawerOpen() {
        return drawerOpen;
    }

    public void openDrawer() {
        userDrawerLayout.openDrawer(Gravity.LEFT);
    }

    public void closeDrawer() {
        userDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    private class ChatItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        }

    }

    private void refreshChatFragment(MessageToChat message) {
        ChatMessageContainer.postMessage(message);

        int chatPageFromInitialization = viewPager.getCurrentItem();
        SopeSocketMessage sopeMessage = SharedPreferencesManager.getSopeMessage(getApplicationContext());
        ChatDTO chatPage = sopeMessage.chatList.getChats().get(chatPageFromInitialization);
        if (message.getChatNumber().equals(chatPage.chatNumber)) {
            chatPage.chatHeader = message.getHeader();
            SharedPreferencesManager.updateSocketMessage(getApplicationContext(), sopeMessage);
            runOnUiThread(() -> {
                chatDrawerAdapter.notifyDataSetChanged();
                messageLayoutPageAdapter.notifyDataSetChanged();
            });
        }


    }


    private void backToChannelList() {
        SopeSocketMessage sopeMessage = SharedPreferencesManager.getSopeMessage(getApplicationContext());
        ChatDTO currentChat = ChatPagination.getCurrentChat(sopeMessage.chatList);

        WebSocketMessageManager.unsubscribeChat(currentChat, sopeMessage.category);
        Intent moveToShowActivity = new Intent(chatActivity, MainTabActivity.class);

        startActivityForResult(moveToShowActivity, 600);

        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }


    public void showEmojiPopUp(boolean showEmoji) {

        //layout_sendmessage
        Rect r = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        RelativeLayout chatRootLayout = (RelativeLayout) findViewById(R.id.chat_layout);
        int screenHeight = chatRootLayout.getRootView().getHeight();
        int keyboardHeight = screenHeight - (r.bottom);


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int deviceHeight = size.y;
        Log.e("Device Height", String.valueOf(deviceHeight));

        if (keyboardHeight < 150) {
            emoticonPopup.getLayoutParams().height = (int) (deviceHeight / 2.5); // Setting the height of emoticonPopup

        } else {
            emoticonPopup.getLayoutParams().height = keyboardHeight - 100;
        }

        emoticonPopup.requestLayout();
        if (showEmoji) {
            emoticonPopup.setVisibility(View.VISIBLE);
            emojiTextKeyboard.setVisibility(View.VISIBLE);
            emojiButton.setVisibility(View.GONE);
        } else {
            emoticonPopup.setVisibility(View.GONE);
            emojiTextKeyboard.setVisibility(View.GONE);
            emojiButton.setVisibility(View.VISIBLE);

        }

    }

    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public void showKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    private void setEmojiconFragment(boolean useSystemDefault) {
        EmojiconsFragment emojiconFragment = EmojiconsFragment.newInstance(useSystemDefault);
        getSupportFragmentManager().beginTransaction().replace(R.id.emojicons, emojiconFragment).commit();
    }

    // Ihan kauheaa koodia
    public void updateChat() {
        SopeSocketMessage sopeMessage = SharedPreferencesManager.getSopeMessage(getApplicationContext());
        CategoryDTO categoryDTO = sopeMessage.category;


        // Typerä switch, joka pitää poistaa (Nopeasti valmista. ;))
        switch (categoryDTO.getCategoryType()) {
            case SHOW: {
                showName.setText(categoryDTO.getCompany().toUpperCase() + ": " + categoryDTO.getName());
                break;
            }
            case GENERAL: {
                showName.setText(categoryDTO.getName());
                showName.setTextSize(22);
                showName.setTypeface(null, Typeface.BOLD);
                break;
            }
            case EVENT: {
                showName.setText(categoryDTO.getName());
                showName.setTextSize(22);
                showName.setTypeface(null, Typeface.BOLD);
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (emoticonPopup.getVisibility() == View.VISIBLE) {
            showEmojiPopUp(false);
        } else {
            backToChannelList();
        }
    }

    Subscriber<MessageToChat> messageToChatSubscriber = new Subscriber<MessageToChat>() {

        @Override
        public void onCompleted() {
            Log.d("mes", "Ok");

        }

        @Override
        public void onError(Throwable e) {
            Log.d("mes", "error");
            e.printStackTrace();
        }

        @Override
        public void onNext(MessageToChat messageToChat) {
            if (messageToChat != null && !messageToChat.getAuthor().equals(username)) {
                refreshChatFragment(messageToChat);
            }
        }
    };
    Subscriber<SopeSocketMessage> socketChatSubsciber = new Subscriber<SopeSocketMessage>() {
        @Override
        public void onCompleted() {
            Log.d("Event", "onCompleted()");
        }

        @Override
        public void onError(Throwable e) {
            Log.e("Event", "onError()", e);
        }

        @Override
        public void onNext(SopeSocketMessage socketMessage) {
            if ((socketMessage.messageType.isUserJoined() || socketMessage.messageType.isRemoveUserFromChat()) && !socketMessage.messageToChat.getAuthor().equals(username)) {
                runOnUiThread(() -> {
                    chatDrawerAdapter.notifyDataSetChanged();
                });
            }

            if (socketMessage.messageType.isUserListUpdate() && socketMessage.usersInChat != null) {

                chatDrawerAdapter.userList.clear();
                for (String user : socketMessage.usersInChat) {
                    chatDrawerAdapter.userList.add(new ChatDrawerItem(R.drawable.user_icon, user));
                }

                SharedPreferencesManager.updateChatUserCount(SopeApplication.getAppContext(), chatDrawerAdapter.userList.size());
                runOnUiThread(() -> {
                    chatDrawerAdapter.notifyDataSetChanged();
                });
            }
            if (socketMessage.messageType.isGetOldMessages()) {
                SopeSocketMessage sopeMessage = SharedPreferencesManager.getSopeMessage(getApplicationContext());
                final ChatDTO currentChat = ChatPagination.getCurrentChat(sopeMessage.chatList);

                List<MessageToChat> currentMessages = ChatMessageContainer.getAllMessagesToChat(currentChat.chatNumber);
                currentMessages.clear();
                currentMessages.addAll(socketMessage.oldMessages);
            }
            if (socketMessage.messageType.isChatCreated()) {
                SopeSocketMessage sopeMessage = SharedPreferencesManager.getSopeMessage(SopeApplication.getAppContext());

                ChatDTO chatDTO = socketMessage.chat;

                WebSocketMessageManager.joinChat(chatDTO, sopeMessage.category);

                sopeMessage.chatList.getChats().add(chatDTO);
                sopeMessage.chatList.setSelectedChat(sopeMessage.chatList.getChats().size() - 1);

                Log.i("Info", "Created new chat");
                runOnUiThread(() -> {
                    int fragmentCurrentPageIndex = viewPager.getCurrentItem();
                    SharedPreferencesManager.updateSocketMessage(SopeApplication.getAppContext(), sopeMessage);
                    messageLayoutPageAdapter.notifyDataSetChanged();
                    MessageLayoutFragment fragment = messageLayoutPageAdapter.getFragment(fragmentCurrentPageIndex);
                    if (fragment != null) {
                        fragment.newChatCreated(messageLayoutPageAdapter.getCount() - 1);
                    }
                });
            }
        }


    };

    @Override
    public void onStart() {
        compositeSubscription.add(SopeApplication.getInstance().getTabSubscription().subscribe(socketChatSubsciber));
        compositeSubscription.add(SopeApplication.getInstance().messageSubscription().subscribe(messageToChatSubscriber));
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


    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(chatMessageInput);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(chatMessageInput, emojicon);
    }


}
