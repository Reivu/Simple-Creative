package com.project.simplecreative.Model;

import retrofit2.Call;
import retrofit2.http.*;

public interface RetrofitInterface {

    @GET("/user/{ID}.json")
    Call<UserModel> listUser(@Path ("ID") String ID,
                             @Query("appid") String appid);

//    @PATCH("/user/{ID}.json")
//    Call<UserModel> patchUser(@Path ("ID") String ID,
//                              @Query("appid") String appid,
//                              @Query("Website") String Website,
//                              @Query("Description") String Description,
//                              @Query("Phone_Number") long Phone_Number);

}
