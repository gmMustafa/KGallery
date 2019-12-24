package com.kics.kstudio.kgallery.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.kics.kstudio.kgallery.Activities.ImageShowActivity;
import com.kics.kstudio.kgallery.Activities.MainActivity;
import com.kics.kstudio.kgallery.Asyn_Functions.AddMultiplePrivate;
import com.kics.kstudio.kgallery.Asyn_Functions.MultipleDelete;
import com.kics.kstudio.kgallery.DataModels.Photo_Item;
import com.kics.kstudio.kgallery.DataModels.PicsWithDates;
import com.kics.kstudio.kgallery.DataModels.Positions;
import com.kics.kstudio.kgallery.Fragments.FavoritesFragment;
import com.kics.kstudio.kgallery.Fragments.PrivateFragmentILock;
import com.kics.kstudio.kgallery.Interfaces.I_CheckBoxChange;
import com.kics.kstudio.kgallery.Interfaces.I_Multiple;
import com.kics.kstudio.kgallery.Misc.Constants;
import com.kics.kstudio.kgallery.Misc.Utils;
import com.kics.kstudio.kgallery.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by HP on 8/15/2018.
 */

public class Favr8PicsAdapter extends RecyclerView.Adapter<Favr8PicsAdapter.PicViewHolder> implements I_CheckBoxChange, I_Multiple {
    public static I_CheckBoxChange favr8_cbxs;
    List<String> types;

    public static I_Multiple i_multiple_fvr8;

    Activity a;

    public Favr8PicsAdapter(Activity a) {
        this.a = a;
        i_multiple_fvr8 = this;
        favr8_cbxs = this;
        types = new ArrayList<>();
        types.add(".tif");
        types.add(".tiff");
        types.add(".bmp");
        types.add(".jpg");
        types.add(".jpeg");
        types.add(".gif");
        types.add(".png");
        types.add(".eps");
    }

    @Override
    public PicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gal_pic_single_view, parent, false);
        PicViewHolder myViewHolder = new PicViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PicViewHolder holder, final int position) {
        final Photo_Item item;
        item = Constants.favr8_list.get(position);
        try {

            holder.Image.setVisibility(View.INVISIBLE);
            RequestOptions options = new RequestOptions()
                    .format(DecodeFormat.PREFER_RGB_565)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(a)
                    .load(item.getImage())
                    .apply(options)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.Image.setBackgroundResource(R.drawable.blank);
                            holder.process.setVisibility(View.INVISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.Image.setVisibility(View.VISIBLE);
                            holder.process.setVisibility(View.INVISIBLE);
                            return false;
                        }
                    })
                    .into(holder.Image);

            if (!types.contains(item.getKEY_TYPE().toLowerCase())) {
                holder.overlay.setVisibility(View.VISIBLE);
                holder.overlay.setBackgroundResource(R.drawable.play);
            } else if (item.getKEY_TYPE().toLowerCase().equals(".gif")) {
                holder.overlay.setVisibility(View.VISIBLE);
                holder.overlay.setBackgroundResource(R.drawable.gifplay);
            } else {
                holder.overlay.setVisibility(View.INVISIBLE);
            }


            if (!types.contains(item.getKEY_TYPE().toLowerCase())) {
                holder.overlay.setVisibility(View.VISIBLE);
                holder.overlay.setBackgroundResource(R.drawable.play);
            } else if (item.getKEY_TYPE().toLowerCase().equals(".gif")) {
                holder.overlay.setVisibility(View.VISIBLE);
                holder.overlay.setBackgroundResource(R.drawable.gifplay);
            } else {
                holder.overlay.setVisibility(View.INVISIBLE);
            }


            if (Constants.Selected_list.contains(item)) {
                holder.cbx.setSelected(true);
                holder.cbx.setVisibility(View.VISIBLE);
                holder.shadow.setVisibility(View.VISIBLE);
            } else {
                holder.cbx.setSelected(false);
                holder.cbx.setVisibility(View.INVISIBLE);
                holder.shadow.setVisibility(View.INVISIBLE);
            }

            holder.Image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Constants.is_private = false;
                    if (Constants.selection) {
                        if (holder.cbx.isSelected()) {
                            holder.cbx.setSelected(false);
                            holder.cbx.setVisibility(View.INVISIBLE);
                            holder.shadow.setVisibility(View.INVISIBLE);
                            Constants.Selected_list.remove(item);

                            Log.d("Position", " on remove: " + getChildPosition(item));
                            Constants.positions.remove(new Positions(getChildPosition(item), item.getKEY_OLD_PATH()));
                        } else {
                            holder.cbx.setSelected(true);
                            holder.cbx.setVisibility(View.VISIBLE);
                            holder.shadow.setVisibility(View.VISIBLE);

                            if (!Constants.Selected_list.contains(item)) {
                                Constants.Selected_list.add(item);
                                Constants.positions.add(new Positions(getChildPosition(item), item.getKEY_OLD_PATH()));
                                Log.d("Position", " on add: " + getChildPosition(item));

                            }
                        }
                    } else {
                        Constants.Fragment_name = a.getResources().getString(R.string.tab_favorite);
                        ImageShowActivity.fvr8time = true;
                        Intent intent = new Intent(a, ImageShowActivity.class);
                        ImageShowActivity.setDatatoShow(Constants.favr8_list);
                        intent.putExtra("Pos", getChildPosition(getChildObject(Constants.favr8_list.get(position).getKEY_OLD_PATH())));
                        a.startActivity(intent);
                    }

                    if (Constants.Selected_list.size() == 0) {
                        Constants.is_private = false;
                        Constants.selection = false;
                        MainActivity.i_toolbarchange.ChangeBTB();
                    }
                }


            });

            holder.Image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Constants.is_private = false;
                    MainActivity.i_toolbarchange.ChangeTB();
                    Constants.Fragment_name = a.getResources().getString(R.string.tab_favorite);
                    if (!holder.cbx.isSelected()) {
                        holder.cbx.setSelected(true);
                        holder.cbx.setVisibility(View.VISIBLE);
                        holder.shadow.setVisibility(View.VISIBLE);
                        Constants.selection = true;
                        Constants.Selected_list.add(item);
                        Constants.positions.add(new Positions(getChildPosition(item), item.getKEY_OLD_PATH()));
                        Log.d("Position", " on long pressed: " + getChildPosition(item));
                    }
                    return true;
                }
            });


        } catch (Exception e) {
        }

    }


    public int getChildPosition(Photo_Item item) {
        for (int i = 0; i < Constants.favr8_list.size(); i++) {
            if (Constants.favr8_list.get(i).getKEY_OLD_PATH().equals(item.getKEY_OLD_PATH())) {
                return i;
            }
        }
        return -1;
    }


    public Photo_Item getChildObject(String path) {

        for (int i = 0; i < Constants.favr8_list.size(); i++) {
            if (Constants.favr8_list.get(i).getKEY_OLD_PATH().equals(path)) {
                return Constants.favr8_list.get(i);
            }
        }
        return null;
    }


    @Override
    public int getItemCount() {
        return Constants.favr8_list.size();
    }

    @Override
    public void on_bx_Change() {
        if (Constants.positions.size() > 0) {
            for (int i = 0; i < Constants.positions.size(); i++) {
                notifyItemChanged(Constants.positions.get(i).getChild());
            }
        }
    }


    private void RemoveDataFromFragmentsForFavorites() {
        for (int i = 0; i < Constants.Selected_list.size(); i++) {
            String album = Constants.Selected_list.get(i).getKEY_ALBUM();
            if (album.equals("Camera")) {
                SearchForMainPositionInCamera(Constants.Selected_list.get(i));
            } else {
                SearchForMainPositionInAlbums(album, Constants.Selected_list.get(i));
            }
        }
        //Remove From Camera Fragment
        PicsDateAdapter.IMultiple.RemoveData();
    }

    private void SearchForMainPositionInAlbums(String album, Photo_Item photo_item) {
        List<PicsWithDates> list = Utils.getAlbumSearch(album);
        boolean flag = true;
        assert list != null;
        for (int i = 0; i < list.size() && flag; i++) {
            for (int j = 0; j < list.get(i).getPics().size(); j++) {
                if (list.get(i).getPics().get(j).getKEY_OLD_PATH()
                        .equals(photo_item.getKEY_OLD_PATH())) {
                    list.get(i).getPics().remove(j);
                    flag = false;
                    break;
                }
            }
        }
    }

    private void SearchForMainPositionInCamera(Photo_Item photo_item) {
        boolean flag = true;
        for (int i = 0; i < Constants.Camera_photos_list.size() && flag; i++) {
            for (int j = 0; j < Constants.Camera_photos_list.get(i).getPics().size(); j++) {
                if (Constants.Camera_photos_list.get(i).getPics().get(j).getKEY_OLD_PATH()
                        .equals(photo_item.getKEY_OLD_PATH())) {
                    Constants.positions.add(new Positions(i, j, Constants.Camera_photos_list.get(i).getPics().get(j).getKEY_OLD_PATH()));
                    flag = false;
                    break;
                }
            }
        }
    }

    @Override
    public void RemoveData() {
        for (int i = 0; i < Constants.positions.size(); i++) {
            Positions pos = Constants.positions.get(i);
            Log.d("Position", "RemoveData: " + pos.getMain() + " " + pos.getChild());
            deleteAtndex(pos.getChild(), pos.getPath());
        }
        Constants.positions.clear();
        RemoveDataFromFragmentsForFavorites();
        DeleteFile();
    }

    private void DeleteFile() {
        try {
            Thread.sleep(100);
            Log.d("hhT", "DeleteFile: " + Constants.Selected_list.size());
            new MultipleDelete(a, Constants.Selected_list, new MultipleDelete.OnCompleteMultipleDelete() {
                @Override
                public void onComplete() {
                }
            }).executeTask();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void deleteAtndex(int child, String path) {
        for (int i = 0; i < Constants.favr8_list.size(); i++) {
            Log.d("CSAWA", "deleteAtndex: " + Constants.favr8_list.get(i).getKEY_OLD_PATH());
        }
        int pos = getChildPosition(getChildObject(path));
        Log.d("CSAWA", "deleteAtndex: " + pos);
        Constants.favr8_list.remove(pos);
        notifyItemRangeChanged(child, Constants.favr8_list.size());
    }

    @Override
    public void RemoveDataAndPrivateize() {
        this.RemoveData();
        PrivateFile();
    }

    private void PrivateFile() {
        new AddMultiplePrivate(a, Constants.Selected_list, new AddMultiplePrivate.OnCompleteMultiplePrivateAdd() {
            @Override
            public void onComplete() {
                Log.d("CDWWFS", "onComplete");
                if (FavoritesFragment.i_updateUI_fvr8 != null) {
                    Log.d("CDWWFS", "onComplete: checkF");
                    FavoritesFragment.i_updateUI_fvr8.update();
                }

                if (PrivateFragmentILock.i_updateUI != null) {
                    Log.d("CDWWFS", "onComplete: checkP");
                    PrivateFragmentILock.i_updateUI.update();
                }
            }
        }).executeTask();
    }

    class PicViewHolder extends RecyclerView.ViewHolder {
        ImageView Image;
        ImageView overlay;
        ImageView process;
        ImageView cbx;
        View shadow;

        PicViewHolder(View itemView) {
            super(itemView);
            Image = itemView.findViewById(R.id.imageView);
            overlay = itemView.findViewById(R.id.overlay);
            process = itemView.findViewById(R.id.processing);
            cbx = itemView.findViewById(R.id.cbx);
            shadow = itemView.findViewById(R.id.shadow);

        }
    }


}
