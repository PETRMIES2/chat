package com.sopeapp.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.sope.domain.user.UserDTO;
import com.sopeapp.R;
import com.sopeapp.SharedPreferencesManager;
import com.sopeapp.SopeApplication;
import com.sopeapp.api.UserService;
import com.sopeapp.tabs.MainTabActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class CreateAccountActivity extends AppCompatActivity {

    // UI references.
    @BindView(R.id.username)
    TextInputEditText usernameTextField;

    @BindView(R.id.login_with_old_account)
    Button loginWithOldAccount;
    @BindView(R.id.create_account_button)
    Button createAccountButton;

    @Inject
    Retrofit retrofit;

    CreateAccountActivity createAccountActivity;

    private Looper usernamecheckLooper;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_activity);

        SopeApplication.getInstance().getRestComponent().inject(this);
        ButterKnife.bind(this);

        SopeApplication.BackgroundThread backgroundThread = new SopeApplication.BackgroundThread();
        backgroundThread.start();
        usernamecheckLooper = backgroundThread.getLooper();

        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.creating_account));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        createAccountActivity = this;

        loginWithOldAccount.setOnClickListener(view -> {
            Intent openLoginActivity = new Intent(createAccountActivity, LoginActivity.class);
            startActivity(openLoginActivity);
        });

        createAccountButton.setOnClickListener(view -> {
            if (usernameTextField.getText().length() == 0) {
                Toast.makeText(getApplicationContext(), getString(R.string.insert_username_to_create_account), Toast.LENGTH_LONG).show();
                return;
            }
            checkUsernameAvailability();
        });
        usernameTextField.requestFocus();
    }

    private void checkUsernameAvailability() {
        progress.setMessage(getString(R.string.checking_username_availability));
        progress.show();

        Observable.defer(() -> {
            try {
                UserService userService = retrofit.create(UserService.class);
                Call<Void> query = userService.isUsernameAvailable(usernameTextField.getText().toString());
                int code = query.execute().code();
                return Observable.just(code);
            } catch (Exception e) {
                return Observable.just(null);
            }
        }).subscribeOn(AndroidSchedulers.from(usernamecheckLooper)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        progress.hide();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Integer result) {
                        if (result == null || result != 200) {
                            Toast.makeText(getApplicationContext(), getString(R.string.username_alread_taken), Toast.LENGTH_LONG).show();
                            usernameTextField.requestFocus();
                        } else {
                            createUser();
                        }
                    }

                });

    }

    private void moveToShowActivity() {
        Intent moveToChannelViewIntent = new Intent(createAccountActivity, MainTabActivity.class);
        startActivity(moveToChannelViewIntent);
        usernamecheckLooper.quitSafely();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }


    private void createUser() {
        final String username = usernameTextField.getText().toString();
        if (!TextUtils.isEmpty(username)) {
            Observable.defer(() -> {
                try {
                    UserDTO user = new UserDTO();
                    user.setUsername(username);
                    UserService userService = retrofit.create(UserService.class);
                    Call<UserDTO> createUser = userService.createUser(user);
                    UserDTO userDTO = createUser.execute().body();
                    return Observable.just(userDTO);
                } catch (Exception e) {
                    return Observable.just(null);
                }
            }).subscribeOn(AndroidSchedulers.from(usernamecheckLooper)).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<UserDTO>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(UserDTO user) {
                            if (user != null) {
                                SharedPreferencesManager.saveUser(getApplicationContext(), user);
                                moveToShowActivity();

                            }
                        }

                    });


        }

    }

}

