package com.bedboy.ufebri.doggie.network;

import com.bedboy.ufebri.doggie.data.BaseResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/breeds/image/random/{count}")
    Call<BaseResponse> getAllRandomImages(@Path("count") String count);
}
