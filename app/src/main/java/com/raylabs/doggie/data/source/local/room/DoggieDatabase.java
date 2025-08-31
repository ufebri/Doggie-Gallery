package com.raylabs.doggie.data.source.local.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.raylabs.doggie.data.source.local.entity.DoggieEntity;

@Database(entities = {DoggieEntity.class}, version = 1, exportSchema = false)
public abstract class DoggieDatabase extends RoomDatabase {
    public abstract DoggieDao doggieDao();

    private static volatile DoggieDatabase INSTANCE;

    public static DoggieDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (DoggieDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DoggieDatabase.class, "Doggie.db").build();
            }
        }
        return INSTANCE;
    }
}
