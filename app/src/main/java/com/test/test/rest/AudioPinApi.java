package com.test.test.rest;

import android.content.res.Resources;
import com.test.test.util.RequestHelper;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by shahed on 27/08/2016.
 */
public class AudioPinApi {

    private AudioPinApiInterface mApiService;
    private Resources mResources;

    /**
     * Gets the instance of the api
     * @return returns api instance
     */
    public static AudioPinApi getInstance(){
        return AudioPinApiHolder.instance;
    }

    /**
     * Constructor
     */
    public AudioPinApi(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AudioPinApiHelper.getBasrUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mApiService = retrofit.create(AudioPinApiInterface.class);
    }

    public Call<Object> getEnrollmentInfo(String client_id){
        String header = RequestHelper.authHeader;
        return mApiService.getEnrollmentInfo(header, client_id);
    }

    public Call<Object> authToken(){
         String clientid = AudioPinApiHelper.getClientId();
         String clientS = AudioPinApiHelper.getClientSecret();
         return mApiService.getAuthToken(clientid, clientS);
     }

    private static class AudioPinApiHolder {
        public static final AudioPinApi instance = new AudioPinApi();
    }

}
