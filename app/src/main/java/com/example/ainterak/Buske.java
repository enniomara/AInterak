package com.example.ainterak;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * @author chrillebile, Christian Bilevits
 * @since 2019-04-17
 */
@Entity(tableName = "buske")
public class Buske {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "create_date")
    public Date date = new Date(System.currentTimeMillis());

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "longitude")
    public double longitude;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "description")
    public String description;
}
