package com.sopeapp.login;

import android.os.AsyncTask;
import android.util.Log;

import com.sopeapp.SopeApplication;
import com.sopeapp.api.UserService;
import com.sope.domain.user.TokenDTO;
import com.sope.domain.user.UserDTO;

import retrofit2.Call;

public class LoginTask extends AsyncTask<UserDTO, Void, UserDTO> {

    private final LoginCallback loginCallBack;

    public LoginTask(LoginCallback loginCallBack) {
        this.loginCallBack = loginCallBack;
    }

    @Override
    protected UserDTO doInBackground(UserDTO... users) {
        try {
            UserDTO user = users[0];
            UserService userService = SopeApplication.getInstance().getRetrofit().create(UserService.class);
            Call<TokenDTO> tokenQuery = userService.authenticate(user);
            TokenDTO result = tokenQuery.execute().body();
            if (result != null) {
                user.setToken(result.getToken());
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("LOGIN EXCEPTION", e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(final UserDTO user) {
        loginCallBack.onPostExecute(user);
    }

    @Override
    protected void onCancelled() {
        loginCallBack.onCancelled();

    }
}

