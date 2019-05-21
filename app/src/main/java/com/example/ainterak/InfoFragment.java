package com.example.ainterak;

/**
 * Code from https://github.com/Appolica/InteractiveInfoWindowAndroid/blob/master/InteractiveInfoWindowAndroid/sample/src/main/java/com/appolica/sample/fragments/FormFragment.java
 * Modified
 * **/

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class InfoFragment extends Fragment {

    private BuskeRepository buskeRepository = new BuskeRepository(getContext());
    private Buske buske;

    public static InfoFragment newInstance(Buske buske) {
        InfoFragment infoFragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putParcelable("buske", buske);
        infoFragment.setArguments(args);
        return infoFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.marker_info, container, false);
        Bundle bundle = this.getArguments();
        this.buske = bundle.getParcelable("buske");
                    TextView title = (TextView) view.findViewById(R.id.marker_text);
                    TextView description = (TextView) view.findViewById(R.id.marker_description);
                    title.setText(buske.name);
                    description.setText(buske.description);
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final View.OnClickListener onClickListenerDelete = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.buske_delete_title)
                        .setMessage(R.string.buske_delete_description)
                        .setPositiveButton(R.string.yes, (DialogInterface dialog, int which) -> {
                            buskeRepository.delete(buske);
                        }).setNegativeButton(R.string.no, (DialogInterface dialog, int which) -> {
                        });
                builder.show();
            }
        };
        view.findViewById(R.id.marker_delete).setOnClickListener(onClickListenerDelete);
        final View.OnClickListener onClickListenerCompass = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CompassActivity.class);
                intent.putExtra("com.example.ainterak.BUSKE", buske);
                startActivity(intent);
            }
        };
        view.findViewById(R.id.marker_compass).setOnClickListener(onClickListenerCompass);
    }
}
