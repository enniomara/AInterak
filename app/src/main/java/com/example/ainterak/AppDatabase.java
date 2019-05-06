package com.example.ainterak;

/**
 * @author chrillebile, Christian Bilevits
 * @since 2019-04-17
 */

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

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
    private static volatile AppDatabase appDatabase;

    static synchronized AppDatabase getInstance(Context context) {
        if (appDatabase == null) appDatabase = create(context);

        return appDatabase;
    }

    public abstract BuskeDao buskeDao();

    private static AppDatabase create(final Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "database").build();
    }
}
