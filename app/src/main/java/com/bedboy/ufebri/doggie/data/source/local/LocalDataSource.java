package com.bedboy.ufebri.doggie.data.source.local;

import androidx.lifecycle.LiveData;

import com.bedboy.ufebri.doggie.data.source.local.entity.DoggieEntity;
import com.bedboy.ufebri.doggie.data.source.local.room.DoggieDao;

import java.util.List;

public class LocalDataSource {

    private static LocalDataSource INSTANCE;
    private final DoggieDao doggieDao;


    public LocalDataSource(DoggieDao doggieDao) {
        this.doggieDao = doggieDao;
    }

    public static LocalDataSource getInstance(DoggieDao doggieDao) {
        if (INSTANCE == null) {
            INSTANCE = new LocalDataSource(doggieDao);
        }
        return INSTANCE;
    }

    public LiveData<List<DoggieEntity>> getAllDoggie(String tagName) {
        return doggieDao.getDataDoggie(tagName);
    }

    public LiveData<List<DoggieEntity>> getCategoriesDogie() {
        return doggieDao.getCategories();
    }

    public void insertDoggie(List<DoggieEntity> doggie) {
        doggieDao.insertDoggie(doggie);
    }

}
