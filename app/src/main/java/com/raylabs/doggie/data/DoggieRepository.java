package com.raylabs.doggie.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.raylabs.doggie.config.Constant;
import com.raylabs.doggie.data.source.local.LocalDataSource;
import com.raylabs.doggie.data.source.local.entity.DoggieEntity;
import com.raylabs.doggie.data.source.remote.ApiResponse;
import com.raylabs.doggie.data.source.remote.RemoteDataSource;
import com.raylabs.doggie.utils.AppExecutors;
import com.raylabs.doggie.vo.Resource;

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
    public LiveData<Resource<List<DoggieEntity>>> getAllImage(String countItem) {
        return new NetworkBoundResource<List<DoggieEntity>, List<String>>(appExecutors) {

            @Override
            protected LiveData<List<DoggieEntity>> loadFromDB() {
                return localDataSource.getAllDoggie("for-you");
            }

            @Override
            protected Boolean shouldFetch(List<DoggieEntity> data) {
                return (data == null) || (data.size() == 0);
            }

            @Override
            protected LiveData<ApiResponse<List<String>>> createCall() {
                return remoteDataSource.getAllImage(countItem);
            }

            /**
             * Regex For get Type Dog
             */
            @Override
            protected void saveCallResult(List<String> data) {
                ArrayList<DoggieEntity> doggieList = new ArrayList<>();
                for (String response : data) {
                    DoggieEntity doggie = new DoggieEntity(response.split("/")[4], response, "for-you");
                    doggieList.add(doggie);
                }
                localDataSource.insertDoggie(doggieList);
            }
        }.asLiveData();
    }

    @Override
    public LiveData<Resource<List<DoggieEntity>>> getLikedImage(String countItem) {
        return new NetworkBoundResource<List<DoggieEntity>, List<String>>(appExecutors) {

            @Override
            protected LiveData<List<DoggieEntity>> loadFromDB() {
                return localDataSource.getAllDoggie("liked");
            }

            @Override
            protected Boolean shouldFetch(List<DoggieEntity> data) {
                return (data == null) || (data.size() == 0);
            }

            @Override
            protected LiveData<ApiResponse<List<String>>> createCall() {
                return remoteDataSource.getAllImage(countItem);
            }

            /**
             * Regex For get Type Dog
             */
            @Override
            protected void saveCallResult(List<String> data) {
                ArrayList<DoggieEntity> doggieList = new ArrayList<>();
                for (String response : data) {
                    DoggieEntity doggie = new DoggieEntity(response.split("/")[4], response, "liked");
                    doggieList.add(doggie);
                }
                localDataSource.insertDoggie(doggieList);
            }
        }.asLiveData();
    }

    @Override
    public LiveData<Resource<List<DoggieEntity>>> getPopularImage(String countItem) {
        return new NetworkBoundResource<List<DoggieEntity>, List<String>>(appExecutors) {

            @Override
            protected LiveData<List<DoggieEntity>> loadFromDB() {
                return localDataSource.getAllDoggie("popular");
            }

            @Override
            protected Boolean shouldFetch(List<DoggieEntity> data) {
                return (data == null) || (data.size() == 0);
            }

            @Override
            protected LiveData<ApiResponse<List<String>>> createCall() {
                return remoteDataSource.getAllImage(countItem);
            }

            /**
             * Regex For get Type Dog
             */
            @Override
            protected void saveCallResult(List<String> data) {
                ArrayList<DoggieEntity> doggieList = new ArrayList<>();
                for (String response : data) {
                    DoggieEntity doggie = new DoggieEntity(response.split("/")[4], response, "popular");
                    doggieList.add(doggie);
                }
                localDataSource.insertDoggie(doggieList);
            }
        }.asLiveData();
    }

    @Override
    public LiveData<Resource<List<DoggieEntity>>> getCategories() {
        return new NetworkBoundResource<List<DoggieEntity>, List<String>>(appExecutors) {

            @Override
            protected LiveData<List<DoggieEntity>> loadFromDB() {
                return localDataSource.getCategoriesDogie();
            }

            @Override
            protected Boolean shouldFetch(List<DoggieEntity> data) {
                return (data == null) || (data.size() == 0);
            }

            @Override
            protected LiveData<ApiResponse<List<String>>> createCall() {
                return remoteDataSource.getAllImage(Constant.IMAGE_ITEM_COUNT_LOADED);
            }

            /**
             * Regex For get Type Dog
             */
            @Override
            protected void saveCallResult(List<String> data) {
                ArrayList<DoggieEntity> doggieList = new ArrayList<>();
                for (String response : data) {
                    DoggieEntity doggie = new DoggieEntity(response.split("/")[4], response, "for-you");
                    doggieList.add(doggie);
                }
                localDataSource.insertDoggie(doggieList);
            }
        }.asLiveData();
    }
}
