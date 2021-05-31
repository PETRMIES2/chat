package com.sopeapp.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.sopeapp.SharedPreferencesManager;
import com.sopeapp.SopeApplication;
import com.sope.domain.user.UserDTO;
import com.sopeapp.utilities.FirebaseTokenUpdateRunnable;
import com.sopeapp.utilities.SopeApiRunnableManager;

public class SopeFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "SopeFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        System.out.println(refreshedToken);

        updateToken(refreshedToken);
    }

    private void updateToken(String token) {
        try {
            UserDTO user = SharedPreferencesManager.getUser(SopeApplication.getInstance().getApplicationContext());
            if (user != null) {
                SopeApiRunnableManager.getInstance().execute(new FirebaseTokenUpdateRunnable(user));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("LOGIN EXCEPTION", e.getMessage());
        }


    }
}
