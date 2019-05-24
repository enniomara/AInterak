package com.example.ainterak;

import android.arch.lifecycle.Observer;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.SupportActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

public class MenuSlider {

    private SupportActivity activity;
    private SlidingUpPanelLayout mLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    static final float anchorPoint = 0.3f;

    private BuskeRepository buskeRepository;

    public MenuSlider(SupportActivity activity, BuskeRepository buskeRepository) {
        this.activity = activity;
        this.buskeRepository = buskeRepository;
    }

    public void initSlider() {
        mLayout = (SlidingUpPanelLayout) activity.findViewById(R.id.sliding_layout);
        if (mLayout != null) {
            mLayout.setAnchorPoint(anchorPoint); // slide up 30% then stop
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            mLayout.setCoveredFadeColor(Color.argb(0, 0, 0, 0));
        }
        recyclerView = (RecyclerView) activity.findViewById(R.id.menu_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);

        // Register slider that changes drag icon from arrow to bar-shaped depending on panel offset
        this.registerPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                TextView textView = activity.findViewById(R.id.myBushes);
                if (slideOffset != 0) {
                    textView.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_remove_black_24dp,
                            0,
                            0);
                } else {
                    textView.setCompoundDrawablesWithIntrinsicBounds(0,
                            R.drawable.ic_keyboard_arrow_up_black_24dp,
                            0,
                            0);
                }
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

            }
        });

        buskeRepository.findAll().observe(activity, new Observer<List<Buske>>() {
            @Override
            public void onChanged(@Nullable List<Buske> buskar) {
                if (buskar == null) {
                    return;
                }

                setNewDataset(buskar.toArray(new Buske[0]));
            }
        });
    }

    /**
     * Register a new listener.
     *
     * @param panelSlideListener
     */
    public void registerPanelSlideListener(SlidingUpPanelLayout.PanelSlideListener panelSlideListener) {
        mLayout.addPanelSlideListener(panelSlideListener);
    }

    public void setNewDataset(Buske[] dataset) {
        mAdapter = new BuskeAdapter(dataset);
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * Set the slider's panel state to collapsed.
     */
    public void collapseSlider() {
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }
}
