package com.kics.kstudio.kgallery.Fragments;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kics.kstudio.kgallery.Adapters.Favr8PicsAdapter;
import com.kics.kstudio.kgallery.Interfaces.I_UpdateUI;
import com.kics.kstudio.kgallery.Misc.Constants;
import com.kics.kstudio.kgallery.Misc.Utils;
import com.kics.kstudio.kgallery.R;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment implements I_UpdateUI {


    Favr8PicsAdapter favr8PicsAdapter;
    public static I_UpdateUI i_updateUI_fvr8;

    public FavoritesFragment() {
        // Required empty public constructor
    }


    RecyclerView recyclerView;
    RelativeLayout relativeLayout;
    TextView textView;
    Context context;

    public static ArrayList<HashMap<String, String>> picList = new ArrayList<HashMap<String, String>>();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        i_updateUI_fvr8=this;
        relativeLayout = (RelativeLayout) inflater.inflate(R.layout.gal_fragment_favorites, container, false);
        View rootView = inflater.inflate(R.layout.gal_fragment_favorites, container, false);
        textView = rootView.findViewById(R.id.n_found);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        context = getActivity();

        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!Utils.hasPermissions(context, PERMISSIONS)) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, Constants.REQUEST_PERMISSION_KEY);
        } else {
            bindGridview();
        }
        return rootView;
    }


    public void bindGridview() {
        bind();
    }

    void bind() {
        if (Constants.favr8_list.size() > 0) {
            setUI();
        } else {
            textView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void update() {
        Log.d("ffT", "updating: ls::" + Constants.favr8_list.size());
        bind();
    }

    void setUI(){
        textView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), Constants.span_Count));
        recyclerView.setNestedScrollingEnabled(false);
        favr8PicsAdapter = new Favr8PicsAdapter(getActivity());
        recyclerView.setAdapter(favr8PicsAdapter);
    }
}
