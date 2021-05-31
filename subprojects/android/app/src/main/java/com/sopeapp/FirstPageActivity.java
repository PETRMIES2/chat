package com.sopeapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.sope.domain.user.UserDTO;
import com.sopeapp.login.CreateAccountActivity;
import com.sopeapp.login.LoginCallback;
import com.sopeapp.login.LoginTask;
import com.sopeapp.tabs.MainTabActivity;
import com.sopeapp.utilities.FirebaseTokenUpdateRunnable;
import com.sopeapp.utilities.SopeApiRunnableManager;

public class FirstPageActivity extends AppCompatActivity {

    private FirstPageActivity root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = this;
        UserDTO user = SharedPreferencesManager.getUser(getApplicationContext());
        Intent intent;
        if (user != null) {
            intent = new Intent(this, MainTabActivity.class);
            new LoginTask(new RefreshUser()).execute(new UserDTO[]{user});
        } else {
            intent = new Intent(this, CreateAccountActivity.class);
        }
        startActivity(intent);
        finish();
    }

    public class RefreshUser implements LoginCallback {
        @Override
        public void onCancelled() {

        }

        public void onPostExecute(final UserDTO user) {

            if (user != null && user.getToken() != null && user.getToken() != "") {
                Log.i("RefresUser", "Received token from backend " + user.getToken());

                user.setFirebasetoken(FirebaseInstanceId.getInstance().getToken());
                SopeApiRunnableManager.getInstance().execute(new FirebaseTokenUpdateRunnable(user));
                SharedPreferencesManager.saveUser(getApplicationContext(), user);
            } else {
                startActivity(new Intent(root, CreateAccountActivity.class));
                Toast.makeText(getApplicationContext(), getString(R.string.login_verification_failed), Toast.LENGTH_LONG).show();

            }

        }
    }
}

