package com.example.ainterak;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * @author chrillebile, Christian Bilevits
 * @since 2019-04-17
 */
@Entity(tableName = "buske")
public class Buske implements Parcelable {

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

    public Buske() {
    }

    public Buske(Parcel in) {
        this.id = in.readInt();
        this.date = (Date) in.readSerializable();
        this.name = in.readString();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.description = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeSerializable(date);
        parcel.writeString(name);
        parcel.writeDouble(longitude);
        parcel.writeDouble(latitude);
        parcel.writeString(description);
    }

    public static final Parcelable.Creator<Buske> CREATOR = new Parcelable.Creator<Buske>() {
        public Buske createFromParcel(Parcel in) {
            return new Buske(in);
        }

        @Override
        public Buske[] newArray(int i) {
            return new Buske[i];
        }
    };
}

