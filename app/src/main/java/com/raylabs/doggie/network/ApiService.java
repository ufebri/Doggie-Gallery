package com.raylabs.doggie.network;

import com.raylabs.doggie.data.BaseResponse;
import com.raylabs.doggie.network.response.BreedListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/breeds/image/random/{count}")
    Call<BaseResponse> getAllRandomImages(@Path("count") String count);

    @GET("api/breeds/list/all")
    Call<BreedListResponse> getAllBreeds();

    @GET("api/breed/{breed}/images/random/{count}")
    Call<BaseResponse> getRandomImagesByBreed(@Path("breed") String breed, @Path("count") int count);

    @GET("api/breed/{breed}/{subBreed}/images/random/{count}")
    Call<BaseResponse> getRandomImagesBySubBreed(@Path("breed") String breed, @Path("subBreed") String subBreed, @Path("count") int count);
}
