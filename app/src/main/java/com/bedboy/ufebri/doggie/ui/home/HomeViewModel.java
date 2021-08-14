package com.bedboy.ufebri.doggie.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bedboy.ufebri.doggie.data.BaseResponse;
import com.bedboy.ufebri.doggie.network.ApiConfig;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {

    private static final String TAG = "MainViewModel";

    private final MutableLiveData<List<String>> link = new MutableLiveData<>();
    public LiveData<List<String>> getImageLink() {
        return link;
    }

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading() {
        return _isLoading;
    }

    public HomeViewModel() {
        getImage();
    }

    public final void getImage() {
        _isLoading.setValue(true);
        Call<BaseResponse> client = ApiConfig.getApiService().getImages();
        client.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                _isLoading.setValue(false);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        link.setValue(response.body().getMessage());
                    }
                } else {
                    if (response.body() != null) {
                        Log.e(TAG, "onResponse: " + response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                _isLoading.setValue(false);
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}
