package com.example.ainterak.MenuSlider;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ainterak.Buske;
import com.example.ainterak.R;

/*Code is taken from https://www.binpress.com/android-recyclerview-cardview-guide/ with some slight adaptions*/
public class BuskeAdapter extends RecyclerView.Adapter<MenuSliderViewHolder> {
    private Buske[] buskeList;
    private MenuSliderViewHolder.OnClickListener clickListener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public BuskeAdapter(Buske[] buskeList, MenuSliderViewHolder.OnClickListener listener) {
        this.buskeList = buskeList;
        this.clickListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MenuSliderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);

        MenuSliderViewHolder vh = new MenuSliderViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MenuSliderViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.setText(buskeList[position].name);
        holder.setBuske(buskeList[position]);
        holder.setOnClickListener(clickListener);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return buskeList.length;
    }
}