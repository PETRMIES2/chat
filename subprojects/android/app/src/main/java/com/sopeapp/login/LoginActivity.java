package com.sopeapp.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.sope.domain.user.UserDTO;
import com.sopeapp.R;
import com.sopeapp.SharedPreferencesManager;
import com.sopeapp.SopeApplication;
import com.sopeapp.tabs.MainTabActivity;
import com.sopeapp.utilities.FirebaseTokenUpdateRunnable;
import com.sopeapp.utilities.SopeApiRunnableManager;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    private AsyncTask authenticationTask = null;

    // UI references.
    @BindView(R.id.old_account_email_field)
    TextInputEditText emailView;
    @BindView(R.id.old_account_username_field)
    TextInputEditText usernameView;
    @BindView(R.id.old_account_password_field)
    TextInputEditText passwordView;

    @BindView(R.id.old_account_login_button)
    Button loginButton;

    @BindView(R.id.old_account_login_form)
    View loginFormView;

    LoginActivity loginActivity;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.old_account_login_activity);

        SopeApplication.getInstance().getRestComponent().inject(this);
        ButterKnife.bind(this);

        progress = new ProgressDialog(this);
        progress.setMessage("Wait");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        loginActivity = this;

        loginButton.setOnClickListener(view ->attemptLogin());


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    private void attemptLogin() {
        if (authenticationTask != null) {
            return;
        }

        UserDTO user = validateFields();
        if (user != null) {
            progress.show();
            authenticationTask = new LoginTask(new LoginCallBackHandler());
            authenticationTask.execute(new UserDTO[]{user});
        }
    }

    private UserDTO validateFields() {
        // Reset errors.
        emailView.setError(null);
        passwordView.setError(null);

        // Store values at the time of the login attempt.
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        String username = usernameView.getText().toString();

        UserDTO user = new UserDTO();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);


        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(), getString(R.string.login_error_message_field_username), Toast.LENGTH_LONG).show();
            return null;
        }
        if (TextUtils.isEmpty(email) || !isEmailValid(email)) {
            Toast.makeText(getApplicationContext(), getString(R.string.login_error_message_field_required), Toast.LENGTH_LONG).show();
            return null;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), getString(R.string.login_error_message_password), Toast.LENGTH_LONG).show();
            return null;
        }

        return user;

    }

    private boolean isEmailValid(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public class LoginCallBackHandler implements LoginCallback {
        public void onPostExecute(final UserDTO user) {
            authenticationTask = null;

            if (user != null && user.getToken() != null && user.getToken() != "") {
                Log.i("LoginActivity", "Received token from backened " + user.getToken());

                user.setFirebasetoken(FirebaseInstanceId.getInstance().getToken());
                SopeApiRunnableManager.getInstance().execute(new FirebaseTokenUpdateRunnable(user));
                SharedPreferencesManager.saveUser(getApplicationContext(), user);

                Intent moveToChannelViewIntent = new Intent(loginActivity, MainTabActivity.class);
                startActivityForResult(moveToChannelViewIntent, 600);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                progress.hide();
            } else {
                progress.hide();
                passwordView.setError(getString(R.string.login_verification_failed));
                passwordView.requestFocus();
            }

        }

        @Override
        public void onCancelled() {
            authenticationTask = null;

        }

    }
}

