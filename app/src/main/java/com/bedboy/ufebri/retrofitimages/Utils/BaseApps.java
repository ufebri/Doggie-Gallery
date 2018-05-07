package com.bedboy.ufebri.retrofitimages.Utils;

import android.app.Application;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by user on 5/7/18.
 */

public class BaseApps extends Application {

    public static ApiService service;

    @Override
    public void onCreate() {
        super.onCreate();
        service = getRetrofit().create(ApiService.class);
    }

    private Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("https://dog.ceo/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}
