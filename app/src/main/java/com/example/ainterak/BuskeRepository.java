package com.example.ainterak;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

public class BuskeRepository {
    private AppDatabase appDatabase;

    public BuskeRepository(Context context) {
        appDatabase = AppDatabase.getInstance(context);
    }

    public void create(final Buske buske) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                appDatabase.buskeDao().insertBuske(buske);
                return null;
            }
        }.execute();
    }

    public LiveData<List<Buske>> findAll() {
        return appDatabase.buskeDao().getAll();
    }
}
