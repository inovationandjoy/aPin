package com.test.test.rest;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by shahed on 14/07/2016.
 */

public interface AudioPinApiInterface {
    @GET("/api/v1/enrollments/{client_id}")
    Call<Object> getEnrollmentInfo(@Header("Authorization") String authorization, @Path("client_id") String client_id);

    @FormUrlEncoded
    @POST("oauth/client_credential/accesstoken?grant_type=client_credentials")
    Call<Object> getAuthToken(@Field("client_id") String id, @Field("client_secret") String secret);

}
