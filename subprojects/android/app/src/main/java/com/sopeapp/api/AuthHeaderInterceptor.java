package com.sopeapp.api;

import com.sopeapp.SharedPreferencesManager;
import com.sopeapp.SopeApplication;
import com.sope.domain.user.UserDTO;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthHeaderInterceptor implements Interceptor {

    public static final String X_AUTH_TOKEN = "X-Auth-Token";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        UserDTO user = SharedPreferencesManager.getUser(SopeApplication.getInstance().getApplicationContext());
        Request.Builder requestBuilder = request.newBuilder();
        if (user != null && user.getToken() != null) {
            requestBuilder.header(X_AUTH_TOKEN, user.getToken());
        }

        return chain.proceed(requestBuilder.build());
    }
}
