package com.sopeapp;

import com.sopeapp.chat.ChatActivity;
import com.sopeapp.login.CreateAccountActivity;
import com.sopeapp.login.LoginActivity;
import com.sopeapp.tabs.MainTabActivity;
import com.sopeapp.tabs.events.EventFragment;
import com.sopeapp.tabs.general.GeneralFragment;
import com.sopeapp.tabs.popular.PopularFragment;
import com.sopeapp.tabs.tvshow.TvFragment;
import com.sopeapp.websocket.WebSocketService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, RestApiModule.class})
public interface RestComponent {
    void inject(ChatActivity chatActivity);
    void inject(CreateAccountActivity loginActivity);
    void inject(SopeApplication application);
    void inject(MainTabActivity mainCategoryActivity);
    void inject(LoginActivity loginActivity);
    void inject(TvFragment tvFragment);
    void inject(EventFragment eventFragment);
    void inject(GeneralFragment generalFragment);
    void inject(PopularFragment popularFragment);
    void inject(WebSocketService websocketService);
}
