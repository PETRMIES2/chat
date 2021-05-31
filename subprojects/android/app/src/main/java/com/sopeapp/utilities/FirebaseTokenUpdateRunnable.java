package com.sopeapp.utilities;

import android.util.Log;

import com.sopeapp.SopeApplication;
import com.sopeapp.api.UserService;
import com.sope.domain.user.UserDTO;

import retrofit2.Call;

public class FirebaseTokenUpdateRunnable implements Runnable{

    private UserDTO user;

    public FirebaseTokenUpdateRunnable(UserDTO user) {
        this.user = user;
    }

    @Override
    public void run() {

        try {
            UserService userService = SopeApplication.getInstance().getRetrofit().create(UserService.class);
            Call<Void> tokenQuery = userService.updateUser(user);
            tokenQuery.execute();
        } catch (Exception e) {
            Log.e("TokenUpdate", "Cannot update fireasetoken", e);
        }

    }
}
