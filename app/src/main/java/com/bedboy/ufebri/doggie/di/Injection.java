package com.bedboy.ufebri.doggie.di;

import android.content.Context;

import com.bedboy.ufebri.doggie.data.DoggieRepository;
import com.bedboy.ufebri.doggie.data.source.local.LocalDataSource;
import com.bedboy.ufebri.doggie.data.source.local.room.DoggieDatabase;
import com.bedboy.ufebri.doggie.data.source.remote.RemoteDataSource;
import com.bedboy.ufebri.doggie.utils.AppExecutors;

public class Injection {

    public static DoggieRepository provideRepository(Context context) {
        DoggieDatabase database = DoggieDatabase.getInstance(context);
        LocalDataSource localDataSource = LocalDataSource.getInstance(database.doggieDao());
        RemoteDataSource remoteDataSource = RemoteDataSource.getInstance();
        AppExecutors appExecutors = new AppExecutors();
        return DoggieRepository.getInstance(remoteDataSource, localDataSource, appExecutors);
    }
}
