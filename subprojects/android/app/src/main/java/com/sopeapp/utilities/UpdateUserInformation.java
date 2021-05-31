package com.sopeapp.utilities;

import android.util.Log;

import com.sopeapp.SopeApplication;
import com.sopeapp.api.UserService;
import com.sope.domain.user.UserDTO;

import java.io.IOException;

import retrofit2.Call;

public class UpdateUserInformation implements Runnable {
    private UserDTO userDTO;

    public UpdateUserInformation(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    @Override
    public void run() {

        try {
            UserService userService = SopeApplication.getInstance().getRetrofit().create(UserService.class);
            Call<Void> query = userService.updateUser(userDTO);
            query.execute();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("POSTING MESSAGE ERROR", e.getMessage());
        }

    }
}
