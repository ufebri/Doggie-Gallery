package com.bedboy.ufebri.doggie.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bedboy.ufebri.doggie.data.source.local.LocalDataSource;
import com.bedboy.ufebri.doggie.data.source.local.entity.DoggieEntity;
import com.bedboy.ufebri.doggie.data.source.remote.ApiResponse;
import com.bedboy.ufebri.doggie.data.source.remote.RemoteDataSource;
import com.bedboy.ufebri.doggie.utils.AppExecutors;
import com.bedboy.ufebri.doggie.vo.Resource;

import java.util.ArrayList;
import java.util.List;

public class DoggieRepository implements DoggieDataSource {

    private volatile static DoggieRepository INSTANCE = null;

    private final RemoteDataSource remoteDataSource;
    private final LocalDataSource localDataSource;
    private final AppExecutors appExecutors;

    private DoggieRepository(@NonNull RemoteDataSource remoteDataSource, LocalDataSource localDataSource, AppExecutors appExecutors) {
        this.remoteDataSource = remoteDataSource;
        this.localDataSource = localDataSource;
        this.appExecutors = appExecutors;
    }

    public static DoggieRepository getInstance(RemoteDataSource remoteDataSource, LocalDataSource localDataSource, AppExecutors appExecutors) {
        if (INSTANCE == null) {
            synchronized (DoggieRepository.class) {
                INSTANCE = new DoggieRepository(remoteDataSource, localDataSource, appExecutors);
            }
        }
        return INSTANCE;
    }

    @Override
    public LiveData<Resource<List<String>>> getAllImage() {
        return new NetworkBoundResource<List<DoggieEntity>, List<String>>(appExecutors) {

            @Override
            protected LiveData<List<DoggieEntity>> loadFromDB() {
                return null;
            }

            @Override
            protected Boolean shouldFetch(List<DoggieEntity> data) {
                return null;
            }

            @Override
            protected LiveData<ApiResponse<List<String>>> createCall() {
                return null;
            }

            @Override
            protected void saveCallResult(List<String> data) {

            }
        }.asLiveData();
}
