package com.raylabs.doggie.data.source.remote;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.raylabs.doggie.data.BaseResponse;
import com.raylabs.doggie.network.ApiConfig;
import com.raylabs.doggie.network.ApiService;
import com.raylabs.doggie.network.response.BreedListResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RemoteDataSource {

    private static RemoteDataSource INSTANCE;
    private final String TAG = getClass().getSimpleName();

    public static RemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RemoteDataSource();
        }
        return INSTANCE;
    }

    public LiveData<ApiResponse<List<String>>> getAllImage(String countItem) {
        MutableLiveData<ApiResponse<List<String>>> resultImage = new MutableLiveData<>();
        Call<BaseResponse> client = ApiConfig.getApiService().getAllRandomImages(countItem);
        client.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        resultImage.setValue(ApiResponse.success(response.body().getMessage()));
                    }
                } else Log.e(TAG, "onResponse: " + response.message());
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
        return resultImage;
    }

    public LiveData<ApiResponse<Map<String, List<String>>>> getAllBreeds() {
        MutableLiveData<ApiResponse<Map<String, List<String>>>> result = new MutableLiveData<>();
        Call<BreedListResponse> client = ApiConfig.getApiService().getAllBreeds();
        client.enqueue(new Callback<BreedListResponse>() {
            @Override
            public void onResponse(Call<BreedListResponse> call, Response<BreedListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMessage() != null) {
                    result.setValue(ApiResponse.success(response.body().getMessage()));
                } else {
                    Log.e(TAG, "getAllBreeds onResponse: " + response.message());
                    result.setValue(ApiResponse.error(response.message(), null));
                }
            }

            @Override
            public void onFailure(Call<BreedListResponse> call, Throwable t) {
                Log.e(TAG, "getAllBreeds onFailure: " + t.getMessage());
                result.setValue(ApiResponse.error(t.getMessage(), null));
            }
        });
        return result;
    }

    public List<String> getRandomImagesSync(String breed, String subBreed, int count) {
        ApiService service = ApiConfig.getApiService();
        Call<BaseResponse> call;
        if (subBreed == null || subBreed.isEmpty()) {
            call = service.getRandomImagesByBreed(breed, count);
        } else {
            call = service.getRandomImagesBySubBreed(breed, subBreed, count);
        }
        try {
            Response<BaseResponse> response = call.execute();
            if (response.isSuccessful() && response.body() != null && response.body().getMessage() != null) {
                return response.body().getMessage();
            } else {
                Log.e(TAG, "getRandomImagesSync failed. breed=" + breed + " sub=" + subBreed + " message=" + response.message());
            }
        } catch (Exception e) {
            Log.e(TAG, "getRandomImagesSync exception for breed=" + breed + " sub=" + subBreed, e);
        }
        return new ArrayList<>();
    }

    public Map<String, List<String>> getAllBreedsSync() {
        ApiService service = ApiConfig.getApiService();
        Call<BreedListResponse> call = service.getAllBreeds();
        try {
            Response<BreedListResponse> response = call.execute();
            if (response.isSuccessful() && response.body() != null && response.body().getMessage() != null) {
                return response.body().getMessage();
            } else {
                Log.e(TAG, "getAllBreedsSync failed. message=" + response.message());
            }
        } catch (Exception e) {
            Log.e(TAG, "getAllBreedsSync exception", e);
        }
        return Collections.emptyMap();
    }
}
