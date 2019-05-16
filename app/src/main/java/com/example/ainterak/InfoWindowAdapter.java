package com.example.ainterak;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final Context context;

    public InfoWindowAdapter(Context context){
        this.context = context.getApplicationContext();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.marker_info,null);
        TextView text = (TextView) v.findViewById(R.id.marker_text);
        ImageButton edit = (ImageButton) v.findViewById(R.id.marker_edit);
        ImageButton delete = (ImageButton) v.findViewById(R.id.marker_delete);
        text.setText(marker.getTitle());
        return v;
    }
}
