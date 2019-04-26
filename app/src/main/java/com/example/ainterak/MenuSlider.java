package com.example.ainterak;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MenuSlider {

    private Activity activity;
    private SlidingUpPanelLayout mLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public MenuSlider(Activity activity) {
        this.activity = activity;
    }

    public void initSlider() {
        mLayout = (SlidingUpPanelLayout) activity.findViewById(R.id.sliding_layout);
        if (mLayout != null) {
            mLayout.setAnchorPoint(0.5f); // slide up 50% then stop
        }
        recyclerView = (RecyclerView) activity.findViewById(R.id.menu_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(new String[]{"test", "test2", "test3", "djkfölskjiohfhxckvjbnkfjdseiortueroupowipterjgldk"});
        recyclerView.setAdapter(mAdapter);
    }

    public void setNewDataset(String[] dataset) {
        mAdapter = new MyAdapter(dataset);
        recyclerView.setAdapter(mAdapter);
    }
}
