package com.bedboy.ufebri.retrofitimages.Utils;

import com.bedboy.ufebri.retrofitimages.entity.Images;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by user on 5/7/18.
 */

public interface ApiService {
    @GET("api/breed/boxer/images")
    Call<Images> getImages();
}
