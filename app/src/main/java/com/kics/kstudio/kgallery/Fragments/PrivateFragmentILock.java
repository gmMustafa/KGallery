package com.kics.kstudio.kgallery.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kics.kstudio.kgallery.Adapters.Priva8PicsAdapter;
import com.kics.kstudio.kgallery.DataBase.GallerySqlite;
import com.kics.kstudio.kgallery.Interfaces.I_LockUnlock;
import com.kics.kstudio.kgallery.Interfaces.I_UpdateUI;
import com.kics.kstudio.kgallery.Misc.Constants;
import com.kics.kstudio.kgallery.R;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;


/**
 * A simple {@link Fragment} subclass.
 */
public class PrivateFragmentILock extends Fragment implements I_LockUnlock, I_UpdateUI {

    public static I_LockUnlock ILock_unlock;
    public static I_UpdateUI i_updateUI;
    RecyclerView recyclerView;
    TextView textView;
     com.kics.kstudio.kgallery.Numpad.GalleryNumPad numPad;

    GallerySqlite gallerySqlite;
    public static Boolean change = false;
    Priva8PicsAdapter priva8PicsAdapter;

    public PrivateFragmentILock() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ILock_unlock = this;
        i_updateUI = this;
        gallerySqlite = new GallerySqlite(getActivity());
        getdata();

        View rootView = inflater.inflate(R.layout.gal_fragment_private, container, false);
        numPad = rootView.findViewById(R.id.numpad);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_private);
        textView = rootView.findViewById(R.id.n_found);
        bind();
        return rootView;
    }

    private void getdata() {
        try {
            Constants.private_list = gallerySqlite.getPrivatePics();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void Lock() {
        numPad.setVisibility(View.VISIBLE);
        Constants.pass = false;
    }

    @Override
    public void Unlock() {
        numPad.setVisibility(View.GONE);
        bind();
    }

    void bind() {
        Log.d("Data", "Load:  :" + Constants.private_list.size());

        Constants.private_list.clear();
        getdata();

        if (Constants.private_list.size() > 0) {
            Log.d("Data", "Unlock: IN :" + Constants.private_list.size());
            textView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), Constants.span_Count));
            recyclerView.setNestedScrollingEnabled(false);
            priva8PicsAdapter = new Priva8PicsAdapter(getActivity());
            recyclerView.setAdapter(priva8PicsAdapter);

        } else {
            textView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void update() {
        bind();
    }
}
