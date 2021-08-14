package com.bedboy.ufebri.doggie.data.source.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "doggieEntities")
public class DoggieEntity {



    @NonNull
    @ColumnInfo(name = "type")
    private final String type;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "link")
    private final String link;


    public DoggieEntity(@NonNull String type, @NonNull String link) {
        this.type = type;
        this.link = link;
    }

    @NonNull
    public String getType() {
        return type;
    }

    @NonNull
    public String getLink() {
        return link;
    }
}
