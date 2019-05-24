package com.example.ainterak;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/*Code is taken from https://www.binpress.com/android-recyclerview-cardview-guide/ with some slight adaptions*/
public class BuskeAdapter extends RecyclerView.Adapter<BuskeAdapter.MyViewHolder> {
    private Buske[] buskeList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public ImageView imageView;

        public MyViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.card_title);
            /*imageView = v.findViewById(R.id.card_image);*/
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public BuskeAdapter(Buske[] buskeList) {
        this.buskeList = buskeList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BuskeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.setText(buskeList[position].name);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return buskeList.length;
    }
}