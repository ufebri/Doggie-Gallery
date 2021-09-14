package com.bedboy.ufebri.doggie.data.source.local.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.bedboy.ufebri.doggie.data.source.local.entity.DoggieEntity;

import java.util.List;

@Dao
public interface DoggieDao {

    @Query("SELECT * FROM doggieEntities WHERE doggieEntities.tag LIKE :tagName")
    LiveData<List<DoggieEntity>> getDataDoggie(String tagName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDoggie(List<DoggieEntity> doggie);
}
