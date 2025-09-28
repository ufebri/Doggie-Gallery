package com.raylabs.doggie.di;

import android.content.Context;

import com.raylabs.doggie.data.DoggieRepository;
import com.raylabs.doggie.data.source.local.BreedCategoryLocalDataSource;
import com.raylabs.doggie.data.source.local.LocalDataSource;
import com.raylabs.doggie.data.source.local.room.DoggieDatabase;
import com.raylabs.doggie.data.source.remote.RemoteDataSource;
import com.raylabs.doggie.utils.AppExecutors;

public class Injection {

    public static DoggieRepository provideRepository(Context context) {
        DoggieDatabase database = DoggieDatabase.getInstance(context);
        LocalDataSource localDataSource = LocalDataSource.getInstance(database.doggieDao());
        BreedCategoryLocalDataSource breedCategoryLocalDataSource = new BreedCategoryLocalDataSource(database.breedCategoryDao());
        RemoteDataSource remoteDataSource = RemoteDataSource.getInstance();
        AppExecutors appExecutors = new AppExecutors();
        return DoggieRepository.getInstance(context.getApplicationContext(), remoteDataSource, localDataSource, breedCategoryLocalDataSource, appExecutors);
    }
}
