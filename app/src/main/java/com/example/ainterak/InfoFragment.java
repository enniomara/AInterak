package com.example.ainterak;

/**
 * Code from https://github.com/Appolica/InteractiveInfoWindowAndroid/blob/master/InteractiveInfoWindowAndroid/sample/src/main/java/com/appolica/sample/fragments/FormFragment.java
 * Modified
 * **/

import android.content.DialogInterface;
import android.os.Bundle;
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

    public static InfoFragment newInstance(int id) {
        InfoFragment infoFragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        infoFragment.setArguments(args);
        return infoFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.marker_info, container, false);
        Bundle bundle = this.getArguments();
        int id = bundle.getInt("id");
        buskeRepository.findAll().observe(this, (List<Buske> buskar) -> {
            for (Buske buske: buskar) {
                if(buske.id == id) {
                    this.buske = buske;
                    TextView title = (TextView) view.findViewById(R.id.marker_text);
                    title.setText(buske.name);
                }
            }
        });
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
    }
}
