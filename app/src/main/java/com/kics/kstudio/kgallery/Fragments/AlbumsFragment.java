package com.kics.kstudio.kgallery.Fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.kics.kstudio.kgallery.Activities.MainActivity;
import com.kics.kstudio.kgallery.Adapters.AlbumAdapter;
import com.kics.kstudio.kgallery.Adapters.AlbumPicsDateAdapter;
import com.kics.kstudio.kgallery.Interfaces.I_UiSingleToMainAlbum;
import com.kics.kstudio.kgallery.Interfaces.I_UpdateAlbums;
import com.kics.kstudio.kgallery.Misc.Constants;
import com.kics.kstudio.kgallery.Misc.Utils;
import com.kics.kstudio.kgallery.R;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumsFragment extends Fragment implements I_UiSingleToMainAlbum, I_UpdateAlbums {
    GridView galleryGridView;
    RecyclerView recyclerView;
    Context context;
    TextView textView;
    public static I_UpdateAlbums i_updateAlbums;
    public static I_UiSingleToMainAlbum IUiSingleToMainAlbum;
    AlbumAdapter adapter;
    AlbumPicsDateAdapter albumPicsDateAdapter;

    ArrayList<HashMap<String, String>> albumList;


    public AlbumsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        IUiSingleToMainAlbum = this;
        i_updateAlbums = this;
        context = getActivity();
        View rootView = inflater.inflate(R.layout.gal_fragment_albums, container, false);
        textView = rootView.findViewById(R.id.n_found);
        galleryGridView = (GridView) rootView.findViewById(R.id.galleryGridView);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        albumList = Constants.albumList;
        bindGridviewAlbums();
        return rootView;
    }

    private void bindGridviewSingleAlbums(String albumFound) {
        galleryGridView.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager coolLayoutManager = new LinearLayoutManager(context);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(coolLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        albumPicsDateAdapter = new AlbumPicsDateAdapter(getActivity(), Utils.getAlbumSearch(albumFound));
        recyclerView.setAdapter(albumPicsDateAdapter);
    }


    public void bindGridviewAlbums() {
        if (albumList != null) {
            if (albumList.size() > 0) {
                Log.d("hhT", "onCompleteAlbumList: " + "UPdate in binding");
                textView.setVisibility(View.GONE);
                adapter = new AlbumAdapter(getActivity(), albumList);
                galleryGridView.setAdapter(adapter);
                galleryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    public void onItemClick(AdapterView<?> parent, View view,
                                            final int position, long id) {
                        //Constants.bundle.putString("Album", albumList.get(position).get(Constants.KEY_ALBUM));
                        Constants.single_album_pass = true;
                        // MainActivity.viewPager.getAdapter().notifyDataSetChanged();
                        change(albumList.get(position).get(Constants.KEY_ALBUM));
                    }
                });

            } else {
                galleryGridView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            }
        }
    }


    void change(String album) {
        if (album.equals("Camera")) {
            MainActivity.viewPager.setCurrentItem(0);
            if (Constants.Camera_photos_list.size() > 0) {
                PhotosFragment.scrollerToZero.scroll();
            }
            Constants.single_album_pass = false;
        } else if (Constants.single_album_pass) {
            if (album.length() > 12) {
                MainActivity.textView.setText(album.substring(0, 12).concat("..."));
            } else {
                MainActivity.textView.setText(album);
            }
            bindGridviewSingleAlbums(album);
        }
    }

    @Override
    public void changeBack() {
        Constants.single_album_pass = false;
        recyclerView.setVisibility(View.INVISIBLE);
        galleryGridView.setVisibility(View.VISIBLE);
    }

    @Override
    public void update() {
        albumList = Constants.albumList;
        adapter = new AlbumAdapter(getActivity(), albumList);
        galleryGridView.setAdapter(adapter);
    }

    @Override
    public void updateInnerAlbum() {
        albumPicsDateAdapter = new AlbumPicsDateAdapter(getActivity(), Constants.Current_album_photos_list);
        recyclerView.setAdapter(albumPicsDateAdapter);
    }

}
