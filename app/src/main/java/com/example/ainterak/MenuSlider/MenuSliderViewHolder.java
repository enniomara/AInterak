package com.example.ainterak.MenuSlider;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ainterak.Buske;
import com.example.ainterak.R;

public class MenuSliderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public interface OnClickListener {
        void handleBuskeClick(Buske buske);
    }

    // each data item is just a string in this case
    public TextView textView;
    public ImageView imageView;
    private OnClickListener onClickListener;
    private Buske buske;

    public MenuSliderViewHolder(View v) {
        super(v);
        v.setOnClickListener(this);
        textView = v.findViewById(R.id.card_title);
        /*imageView = v.findViewById(R.id.card_image);*/
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setBuske(Buske buske) {
        this.buske = buske;
    }

    @Override
    public void onClick(View v) {
        if (onClickListener != null && buske != null) {
            onClickListener.handleBuskeClick(buske);
        }
        Log.w(
                MenuSliderViewHolder.class.toString(),
                "Could not handle buskeclick. OnClickListener or buske may be null"
        );
    }
}

