package com.sopeapp.login;

import com.sope.domain.user.UserDTO;

/**
 * Created by petra on 15.6.2016.
 */

public interface LoginCallback {
    void onCancelled();

    void onPostExecute(UserDTO user);
}
