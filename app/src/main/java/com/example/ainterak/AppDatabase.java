package com.example.ainterak;

/**
 * @author chrillebile, Christian Bilevits
 * @since 2019-04-17
 */

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * To create a database instance use following example:
 *  AppDatabase db = Room.databaseBuilder(getApplicationContext(),
 *         AppDatabase.class, "database-name").build();
 */
@Database(entities = {Buske.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BuskeDao buskeDao();
}
