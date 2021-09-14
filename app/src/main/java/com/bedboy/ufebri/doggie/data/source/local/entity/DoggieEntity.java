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

    @NonNull
    @ColumnInfo(name = "tag")
    private final String tag;


    public DoggieEntity(@NonNull String type, @NonNull String link, @NonNull String tag) {
        this.type = type;
        this.link = link;
        this.tag = tag;
    }

    @NonNull
    public String getType() {
        return type;
    }

    @NonNull
    public String getLink() {
        return link;
    }

    @NonNull
    public String getTag() {
        return tag;
    }
}
