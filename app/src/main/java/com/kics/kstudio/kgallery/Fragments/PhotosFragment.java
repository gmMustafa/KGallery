package com.kics.kstudio.kgallery.Fragments;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kics.kstudio.kgallery.Adapters.PicsDateAdapter;
import com.kics.kstudio.kgallery.Interfaces.I_SingleUpdate;
import com.kics.kstudio.kgallery.Interfaces.ScrollerToZero;
import com.kics.kstudio.kgallery.Misc.Constants;
import com.kics.kstudio.kgallery.Misc.Utils;
import com.kics.kstudio.kgallery.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */

public class PhotosFragment extends Fragment implements ScrollerToZero {


    PicsDateAdapter picsDateAdapter;
    RecyclerView recyclerView;
    public static ScrollerToZero scrollerToZero;
    LinearLayoutManager linearLayoutManager;
    TextView textView;
    Context context;

    public PhotosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.gal_fragment_photo, container, false);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        textView = rootView.findViewById(R.id.n_found);
        context = getActivity();
        scrollerToZero = this;
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!Utils.hasPermissions(context, PERMISSIONS)) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, Constants.REQUEST_PERMISSION_KEY);
        } else {
            bindGridview();
        }
        return rootView;
    }


    public void bindGridview() {
        if (Constants.Camera_photos_list != null) {
            if (Constants.Camera_photos_list.size() > 0) {
                textView.setVisibility(View.GONE);
                linearLayoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(linearLayoutManager);
                picsDateAdapter = new PicsDateAdapter(getActivity());
                recyclerView.setAdapter(picsDateAdapter);
            } else {
                textView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        } else {
            textView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void scroll() {
        linearLayoutManager.scrollToPositionWithOffset(0, 0);
    }



/*
    @Override
    public void updateAll() {
        picsDateAdapter.notifyDataSetChanged();
    }

    @Override
    public void DeleteSinglePhoto(int main) {
        picsDateAdapter.notifyItemChanged(main);
    }
*/

}


