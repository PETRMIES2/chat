package com.sopeapp.api;

import com.sope.domain.user.TokenDTO;
import com.sope.domain.user.UserDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {

    @POST("public/authenticate")
    Call<TokenDTO> authenticate(@Body UserDTO user);

    @GET("public/available/{username}")
    Call<Void> isUsernameAvailable(@Path("username") String username);


    @POST("public/create")
    Call<UserDTO> createUser(@Body UserDTO user);


    @PUT("users/update")
    Call<Void> updateUser(@Body UserDTO user);

}
