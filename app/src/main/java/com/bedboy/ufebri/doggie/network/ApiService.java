package com.bedboy.ufebri.doggie.network;

import com.bedboy.ufebri.doggie.data.BaseResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("api/breed/boxer/images")
    Call<BaseResponse> getImages();
}
