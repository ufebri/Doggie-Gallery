package com.bedboy.ufebri.doggie.data.source.remote;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bedboy.ufebri.doggie.data.BaseResponse;
import com.bedboy.ufebri.doggie.network.ApiConfig;

import java.util.List;

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

    public LiveData<ApiResponse<List<String>>> getAllImage() {
        MutableLiveData<ApiResponse<List<String>>> resultImage = new MutableLiveData<>();
        Call<BaseResponse> client = ApiConfig.getApiService().getImages();
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
}
