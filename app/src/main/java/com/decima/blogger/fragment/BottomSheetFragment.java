package com.decima.blogger.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.decima.blogger.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class BottomSheetFragment extends BottomSheetDialogFragment {

    private OnActionSelect listenr;

    public BottomSheetFragment() {
        // Required empty public constructor
    }


    public static BottomSheetFragment newInstance() {
        BottomSheetFragment fragment = new BottomSheetFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);

        view.findViewById(R.id.img_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listenr != null)
                    listenr.onSelectAction(1);
            }
        });


        view.findViewById(R.id.img_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listenr != null)
                    listenr.onSelectAction(2);
            }
        });



        return view;
    }


    public void setListenr(OnActionSelect listenr){
        this.listenr = listenr;
    }

    public interface OnActionSelect{
        void onSelectAction(int action);
    }
}