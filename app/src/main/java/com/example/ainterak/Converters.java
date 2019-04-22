package com.example.ainterak;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * @author chrillebile, Christian Bilevits
 * @since 2019-04-17
 */
public class Converters {
    @TypeConverter
    public Long dateToTimestamp(Date date){
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public Date fromTimestamp(Long time){
        return time == null ? null : new Date(time);
    }
}
