package com.example.ainterak;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * @author chrillebile, Christian Bilevits
 * @since 2019-04-17
 */
@Dao
public interface BuskeDao {
    @Query("SELECT * FROM buske")
    LiveData<List<Buske>> getAll();

    @Insert
    void insertBuske(Buske buske);

    @Delete
    void deleteBuske(Buske buske);

    @Update
    void updateBuske(Buske buske);
}
