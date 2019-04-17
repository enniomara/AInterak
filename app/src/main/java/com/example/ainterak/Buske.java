package com.example.ainterak;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * @author chrillebile, Christian Bilevits
 * @since 2019-04-17
 */
@Entity(tableName = "buske")
public class Buske {

    @PrimaryKey
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "coords")
    public int coords;

    @ColumnInfo(name = "description")
    public String description;
}
