package com.raylabs.doggie.data.source.local.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.raylabs.doggie.data.source.local.entity.BreedCategoryEntity;
import com.raylabs.doggie.data.source.local.entity.DoggieEntity;

@Database(entities = {DoggieEntity.class, BreedCategoryEntity.class}, version = 2, exportSchema = false)
public abstract class DoggieDatabase extends RoomDatabase {
    public abstract DoggieDao doggieDao();

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `breed_categories` (" +
                    "`id` TEXT NOT NULL, " +
                    "`breed` TEXT NOT NULL, " +
                    "`subBreed` TEXT, " +
                    "`displayName` TEXT NOT NULL, " +
                    "`previewImageUrl` TEXT, " +
                    "`lastRefreshTimestamp` INTEGER NOT NULL, " +
                    "PRIMARY KEY(`id`))");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_breed_categories_displayName` ON `breed_categories` (`displayName`)");
        }
    };

    private static volatile DoggieDatabase INSTANCE;

    public static DoggieDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (DoggieDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DoggieDatabase.class, "Doggie.db")
                        .addMigrations(MIGRATION_1_2)
                        .build();
            }
        }
        return INSTANCE;
    }

    public abstract BreedCategoryDao breedCategoryDao();
}
