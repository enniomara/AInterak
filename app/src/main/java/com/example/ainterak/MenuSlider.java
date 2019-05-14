package com.example.ainterak;

import android.arch.lifecycle.Observer;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.SupportActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

public class MenuSlider {

    private SupportActivity activity;
    private SlidingUpPanelLayout mLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private BuskeRepository buskeRepository;

    public MenuSlider(SupportActivity activity, BuskeRepository buskeRepository) {
        this.activity = activity;
        this.buskeRepository = buskeRepository;
    }

    public void initSlider() {
        mLayout = (SlidingUpPanelLayout) activity.findViewById(R.id.sliding_layout);
        if (mLayout != null) {
            mLayout.setAnchorPoint(0.3f); // slide up 30% then stop
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            mLayout.setCoveredFadeColor(Color.argb(0,0,0,0));
        }
        recyclerView = (RecyclerView) activity.findViewById(R.id.menu_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);

        buskeRepository.findAll().observe(activity, new Observer<List<Buske>>() {
            @Override
            public void onChanged(@Nullable List<Buske> buskar) {
                if (buskar == null) {
                    return;
                }

                ArrayList<String> names = new ArrayList<>();
                for (Buske buske : buskar) {
                    names.add(buske.name);
                }

                setNewDataset(names.toArray(new String[buskar.size()]));
            }
        });
    }

    public void setNewDataset(String[] dataset) {
        mAdapter = new MyAdapter(dataset);
        recyclerView.setAdapter(mAdapter);
    }
}
