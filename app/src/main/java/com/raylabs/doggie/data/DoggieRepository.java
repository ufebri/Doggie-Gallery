package com.raylabs.doggie.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;

import com.raylabs.doggie.data.paging.BreedImagesPagingSource;
import com.raylabs.doggie.data.source.local.BreedCategoryLocalDataSource;
import com.raylabs.doggie.data.source.local.LocalDataSource;
import com.raylabs.doggie.data.source.local.entity.DoggieEntity;
import com.raylabs.doggie.data.source.remote.ApiResponse;
import com.raylabs.doggie.data.source.remote.RemoteDataSource;
import com.raylabs.doggie.utils.AppExecutors;
import com.raylabs.doggie.vo.BreedCategory;
import com.raylabs.doggie.vo.Resource;

import java.util.ArrayList;
import java.util.List;

import kotlinx.coroutines.flow.Flow;

public class DoggieRepository implements DoggieDataSource {

    private volatile static DoggieRepository INSTANCE = null;

    private static final int CATEGORY_PAGE_SIZE = 20;
    private static final int DEFAULT_BREED_IMAGE_PAGE_SIZE = 10;

    private final RemoteDataSource remoteDataSource;
    private final LocalDataSource localDataSource;
    private final AppExecutors appExecutors;
    private final BreedCategoryRepositoryHelper breedCategoryHelper;

    private DoggieRepository(@NonNull Context context,
                             @NonNull RemoteDataSource remoteDataSource,
                             LocalDataSource localDataSource,
                             BreedCategoryLocalDataSource breedCategoryLocalDataSource,
                             AppExecutors appExecutors) {
        this.remoteDataSource = remoteDataSource;
        this.localDataSource = localDataSource;
        this.appExecutors = appExecutors;
        this.breedCategoryHelper = new BreedCategoryRepositoryHelper(
                context,
                remoteDataSource,
                breedCategoryLocalDataSource,
                new PagingConfig(CATEGORY_PAGE_SIZE, CATEGORY_PAGE_SIZE, false)
        );
        this.breedCategoryHelper.scheduleBackgroundSync();
    }

    public static DoggieRepository getInstance(Context context,
                                               RemoteDataSource remoteDataSource,
                                               LocalDataSource localDataSource,
                                               BreedCategoryLocalDataSource breedCategoryLocalDataSource,
                                               AppExecutors appExecutors) {
        if (INSTANCE == null) {
            synchronized (DoggieRepository.class) {
                INSTANCE = new DoggieRepository(context, remoteDataSource, localDataSource, breedCategoryLocalDataSource, appExecutors);
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
    public Flow<PagingData<BreedCategory>> getCategories() {
        return breedCategoryHelper.pager();
    }

    public void requestCategoryPreview(BreedCategory category) {
        breedCategoryHelper.requestPreview(category);
    }

    @Override
    public Flow<PagingData<String>> getBreedImages(String breed, String subBreed, int pageSize) {
        int effectivePageSize = pageSize > 0 ? pageSize : DEFAULT_BREED_IMAGE_PAGE_SIZE;
        Pager<Integer, String> pager = new Pager<>(
                new PagingConfig(
                        effectivePageSize,
                        effectivePageSize,
                        false
                ),
                () -> new BreedImagesPagingSource(remoteDataSource, breed, subBreed, effectivePageSize)
        );
        return pager.getFlow();
    }

}
