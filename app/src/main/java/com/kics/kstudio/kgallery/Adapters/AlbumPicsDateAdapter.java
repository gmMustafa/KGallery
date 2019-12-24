package com.kics.kstudio.kgallery.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kics.kstudio.kgallery.Asyn_Functions.AddMultiplePrivate;
import com.kics.kstudio.kgallery.Asyn_Functions.MultipleDelete;
import com.kics.kstudio.kgallery.DataModels.PicsWithDates;
import com.kics.kstudio.kgallery.DataModels.Positions;
import com.kics.kstudio.kgallery.Fragments.FavoritesFragment;
import com.kics.kstudio.kgallery.Fragments.PrivateFragmentILock;
import com.kics.kstudio.kgallery.Interfaces.I_CheckBoxChange;
import com.kics.kstudio.kgallery.Interfaces.I_Multiple;
import com.kics.kstudio.kgallery.Misc.Constants;
import com.kics.kstudio.kgallery.R;

import java.util.ArrayList;
import java.util.List;

import static com.kics.kstudio.kgallery.Fragments.AlbumsFragment.IUiSingleToMainAlbum;


/**
 * Created by HP on 8/13/2018.
 */

public class AlbumPicsDateAdapter extends RecyclerView.Adapter<AlbumPicsDateAdapter.PicViewHolder> implements I_CheckBoxChange, I_Multiple {


    private List<PicsWithDates> data;
    private Activity context;
    public static I_CheckBoxChange paAlbum_ICheckBoxChange;
    public static I_Multiple album_Multiple;
    private List<AlbumPicsAdapter> adapterslist;


    public AlbumPicsDateAdapter(Activity a, List<PicsWithDates> d) {
        this.context = a;
        data = d;
        adapterslist = new ArrayList<>();
        Constants.Current_album_photos_list = data;
        adapterslist = new ArrayList<>();
        paAlbum_ICheckBoxChange = this;
        album_Multiple = this;

    }

    @NonNull
    @Override
    public PicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gal_photos_date_recyler_view, parent, false);
        return new PicViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PicViewHolder holder, int position) {
        holder.textView.setText(data.get(position).getDate());
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setLayoutManager(new GridLayoutManager(context, Constants.span_Count));
        AlbumPicsAdapter adapter = new AlbumPicsAdapter(context, data.get(position).getPics(), position);
        adapterslist.add(adapter);
        holder.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void on_bx_Change() {
        for (int i = 0; i < Constants.positions.size(); i++) {
            adapterslist.get(Constants.positions.get(i).getMain()).notifyItemChanged(Constants.positions.get(i).getChild());
        }
    }

    @Override
    public void RemoveData() {
        Log.d("hhT", "RemoveData: ");
        Log.d("hhT", "Positions: List_size:" + Constants.positions.size());
        for (int i = 0; i < Constants.positions.size(); i++) {
            Positions pos = Constants.positions.get(i);
            Log.d("hhT", "RemoveData: " + pos.getMain() + " " + pos.getChild());
            deleteAtIndex(pos.getMain(), pos.getPath());
        }
        DeleteFile();
    }

    private void deleteAtIndex(int mainIndex, String path) {
        Log.d("hhT", "deleteAtIndex: Before:::size:" + Constants.Current_album_photos_list.size());
        if (Constants.Current_album_photos_list.get(mainIndex).getPics().size() == 1) {
            Constants.Current_album_photos_list.remove(mainIndex);
            if (Constants.Current_album_photos_list.size() == 0) {
                IUiSingleToMainAlbum.changeBack();
            }
        } else {
            for (int i = 0; i < Constants.Current_album_photos_list.get(mainIndex).getPics().size(); i++) {
                if (Constants.Current_album_photos_list.get(mainIndex).getPics().get(i).getKEY_OLD_PATH().equals(path)) {
                    Log.d("hhT", "deleteAtIndex: Child::" + i + " main" + mainIndex + " path:" + Constants.Current_album_photos_list.get(mainIndex).getPics().get(i).getKEY_OLD_PATH());
                    Log.d("hhT", "deleteAtIndex: Bs:" + Constants.Current_album_photos_list.get(mainIndex).getPics().size());
                    Constants.Current_album_photos_list.get(mainIndex).getPics().remove(i);
                    Log.d("hhT", "deleteAtIndex: As:" + Constants.Current_album_photos_list.get(mainIndex).getPics().size());
                    break;
                }
            }
        }
        Log.d("hhT", "deleteAtIndex: size:" + Constants.Current_album_photos_list.size());
    }


    private void DeleteFile() {
        try {
            Thread.sleep(100);
            Log.d("hhT", "DeleteFile: " + Constants.Selected_list.size());
            new MultipleDelete(context, Constants.Selected_list, new MultipleDelete.OnCompleteMultipleDelete() {
                @Override
                public void onComplete() {
                    if (FavoritesFragment.i_updateUI_fvr8 != null) {
                        FavoritesFragment.i_updateUI_fvr8.update();
                    }
                }
            }).executeTask();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void RemoveDataAndPrivateize() {
        try {
            Thread.sleep(100);
            Log.d("hhT", "DeleteFile: " + Constants.Selected_list.size());
            new AddMultiplePrivate(context, Constants.Selected_list, new AddMultiplePrivate.OnCompleteMultiplePrivateAdd() {
                @Override
                public void onComplete() {
                    if (FavoritesFragment.i_updateUI_fvr8 != null) {
                        FavoritesFragment.i_updateUI_fvr8.update();
                    }

                    if (PrivateFragmentILock.i_updateUI != null) {
                        PrivateFragmentILock.i_updateUI.update();
                    }
                }
            }).executeTask();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }this.RemoveData();
    }


    static class PicViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        RecyclerView recyclerView;

        PicViewHolder(View itemView) {
            super(itemView);
            this.recyclerView = itemView.findViewById(R.id.pics_recyler_view);
            this.textView = itemView.findViewById(R.id.textview_date);
        }
    }

}
