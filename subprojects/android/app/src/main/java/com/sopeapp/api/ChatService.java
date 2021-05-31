package com.sopeapp.api;

import com.sope.domain.firebase.MessageToChat;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ChatService {


    @POST("messages")
    Call<Void> sendMessage(@Body MessageToChat messageToChat);

}


