package com.example.ainterak;

/**
 * @author chrillebile, Christian Bilevits
 * @since 2019-04-17
 */

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

/**
 * To create a database instance use following example:
 *  AppDatabase db = Room.databaseBuilder(getApplicationContext(),
 *         AppDatabase.class, "database-name").build();
 *
 *  Read more about TypeConverters:
 *      https://developer.android.com/training/data-storage/room/referencing-data
 */
@Database(entities = {Buske.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract BuskeDao buskeDao();
}
