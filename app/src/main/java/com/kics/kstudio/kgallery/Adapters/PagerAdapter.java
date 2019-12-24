package com.kics.kstudio.kgallery.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kics.kstudio.kgallery.Fragments.AlbumsFragment;
import com.kics.kstudio.kgallery.Fragments.FavoritesFragment;
import com.kics.kstudio.kgallery.Fragments.PhotosFragment;
import com.kics.kstudio.kgallery.Fragments.PrivateFragmentILock;
import com.kics.kstudio.kgallery.Misc.Constants;


public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new PhotosFragment();

            case 1:
//                if (!Constants.single_album_pass) {
                AlbumsFragment albumsFragment = new AlbumsFragment();
                albumsFragment.setArguments(Constants.bundle);
                return albumsFragment;
                /*} else {
                    SinngleAlbumShowFragement tab2 = new SinngleAlbumShowFragement();
                    tab2.setArguments(Constants.bundle);
                    return tab2;
                }*/

            case 2:
                return new FavoritesFragment();

            case 3:
                return new PrivateFragmentILock();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}



/*
Log.d("Pager", "   Pager Adapter: " + Constants.single_album_pass);
                /*if (Constants.single_album_pass) {
                    Log.d("Pager", "RELOAD: " + Constants.single_album_pass);
                    return POSITION_NONE;
                }
                return POSITION_UNCHANGED;


        if (object instanceof AlbumsFragment) {
                // POSITION_NONE makes it possible to reload the PagerAdapter
                return POSITION_NONE;
                } else {
                // POSITION_NONE means something like: this fragment is no longer valid
                // triggering the ViewPager to re-build the instance of this fragment.
                return POSITION_UNCHANGED; // don't force a reload

                }
 */
