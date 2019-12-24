package com.kics.kstudio.kgallery.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kics.kstudio.kgallery.Activities.ListMarkers;
import com.kics.kstudio.kgallery.Adapters.PicsAdapter;
import com.kics.kstudio.kgallery.Asyn_Functions.AddMultiplePrivate;
import com.kics.kstudio.kgallery.Asyn_Functions.MultipleDelete;
import com.kics.kstudio.kgallery.DataModels.PicsWithDates;
import com.kics.kstudio.kgallery.DataModels.Positions;
import com.kics.kstudio.kgallery.Fragments.FavoritesFragment;
import com.kics.kstudio.kgallery.Fragments.PrivateFragmentILock;
import com.kics.kstudio.kgallery.Interfaces.I_CameraUpdate;
import com.kics.kstudio.kgallery.Interfaces.I_CheckBoxChange;
import com.kics.kstudio.kgallery.Interfaces.I_Multiple;
import com.kics.kstudio.kgallery.Misc.Constants;
import com.kics.kstudio.kgallery.R;


import java.util.ArrayList;
import java.util.List;

public class PicsDateAdapter extends RecyclerView.Adapter<PicsDateAdapter.PicViewHolder>
        implements I_CheckBoxChange, I_CameraUpdate, I_Multiple {


    private static List<PicsAdapter> adaptersList;
    private List<PicsWithDates> data;
    Context c;
    public static I_CheckBoxChange pa_ICheckBoxChange;
    public static I_CameraUpdate i_cameraUpdate;
    public static I_Multiple IMultiple;


    public PicsDateAdapter(Context c) {
        pa_ICheckBoxChange = this;
        i_cameraUpdate = this;
        IMultiple = this;
        this.c = c;
        data = Constants.Camera_photos_list;
        Constants.Current_album_photos_list = data;
        adaptersList = new ArrayList<>();
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
        holder.recyclerView.setLayoutManager(new GridLayoutManager(c, Constants.span_Count));
        PicsAdapter picsAdapter = new PicsAdapter(c, data.get(position).getPics(), position);
        adaptersList.add(picsAdapter);
        holder.recyclerView.setAdapter(picsAdapter);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void on_bx_Change() {
        Log.d("Box", "on_bx_Change: " + "changeing");
        for (int i = 0; i < Constants.positions.size(); i++) {
            adaptersList.get(Constants.positions.get(i).getMain()).notifyItemChanged(Constants.positions.get(i).getChild());
        }
    }

    @Override
    public void singleUpdate(int main, int child) {
        adaptersList.get(main).notifyItemInserted(child);
        notifyItemRangeChanged(child, adaptersList.get(main).getItemCount());
    }

    @Override
    public void UpdateAll() {
        //      adaptersList.clear();
//        notifyDataSetChanged();
        notifyItemInserted(0);
    }

    @Override
    public void NotifyAllChanges() {
        notifyDataSetChanged();
    }

    @Override
    public void RemoveData() {
        List<Integer> list = new ArrayList<>();
        Log.d("hhT", "RemoveData: ListSize:" + adaptersList.size());
        SortPosition();
        for (int i = 0; i < Constants.positions.size(); i++) {
            Positions pos = Constants.positions.get(i);
            deleteAtIndex(pos.getMain(), pos.getPath());
        }
        //Error here
        /*for (int i = 0; i < Constants.positions.size(); i++) {
            Log.d("hhT", "RemoveData: pos:" + Constants.positions.get(i).getMain() + " c:" + Constants.positions.get(i).getChild());
            if (Constants.positions.get(i).getChild() > -1) {
                Log.d("hhT", "RemoveData: A_Count:" + adaptersList.get(Constants.positions.get(i).getMain()).getItemCount());
                if (!list.contains(Constants.positions.get(i).getMain())) {
                    adaptersList.get(Constants.positions.get(i).getMain()).notifyItemRangeChanged(Constants.positions.get(i).getChild(),
                            Constants.Camera_photos_list.get(Constants.positions.get(i).getMain()).getPics().size());
                    list.add(Constants.positions.get(i).getMain());
                }
            }
        }*/

        notifyDataSetChanged();

        DeleteFile();
        Constants.positions.clear();
    }

    private void SortPosition() {
        Positions temp;
        for (int i = 0; i < Constants.positions.size() - 1; i++) {
            for (int j = i + 1; j < Constants.positions.size(); j++) {
                if (Constants.positions.get(i).getMain() > Constants.positions.get(j).getMain()) {
                    temp = Constants.positions.get(i);
                    Constants.positions.set(i, Constants.positions.get(j));
                    Constants.positions.set(j, temp);
                }
            }
        }

        for (int i = 0; i < Constants.positions.size() - 1; i++) {
            for (int j = i + 1; j < Constants.positions.size(); j++) {
                if (Constants.positions.get(i).getMain() == Constants.positions.get(j).getMain()) {
                    if (Constants.positions.get(i).getChild() > Constants.positions.get(j).getChild()) {
                        temp = Constants.positions.get(i);
                        Constants.positions.set(i, Constants.positions.get(j));
                        Constants.positions.set(j, temp);
                    }
                }
            }
        }
    }


    @Override
    public void RemoveDataAndPrivateize() {
        PrivateFile();
        this.RemoveData();
    }


    private void deleteAtIndex(int mainIndex, String path) {
        if (Constants.Camera_photos_list.get(mainIndex).getPics().size() == 1) {
            Constants.Camera_photos_list.remove(mainIndex);
        } else {
            for (int i = 0; i < Constants.Camera_photos_list.get(mainIndex).getPics().size(); i++) {
                if (Constants.Camera_photos_list.get(mainIndex).getPics().get(i).getKEY_OLD_PATH().equals(path)) {
                    Constants.Camera_photos_list.get(mainIndex).getPics().remove(i);
                    break;
                }
            }
        }
    }


    private void DeleteFile() {
        try {
            Thread.sleep(100);
            Log.d("hhT", "DeleteFile: " + Constants.Selected_list.size());
            new MultipleDelete(c, Constants.Selected_list, new MultipleDelete.OnCompleteMultipleDelete() {
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

    private void PrivateFile() {
        try {
            Thread.sleep(100);
            Log.d("hhT", "Add Multiple: " + Constants.Selected_list.size());
            new AddMultiplePrivate(c, Constants.Selected_list, new AddMultiplePrivate.OnCompleteMultiplePrivateAdd() {
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
        }
    }


    static class PicViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        RecyclerView recyclerView;


        PicViewHolder(View itemView) {
            super(itemView);
            this.recyclerView = (RecyclerView) itemView.findViewById(R.id.pics_recyler_view);
            this.textView = (TextView) itemView.findViewById(R.id.textview_date);
        }
    }
}
