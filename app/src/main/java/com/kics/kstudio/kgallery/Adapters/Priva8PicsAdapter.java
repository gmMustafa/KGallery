package com.kics.kstudio.kgallery.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.kics.kstudio.kgallery.AsynTask_Load.LoadAlbumsThread;
import com.kics.kstudio.kgallery.Asyn_Functions.MultipleDelete;
import com.kics.kstudio.kgallery.Asyn_Functions.RemoveMultiplePrivate;
import com.kics.kstudio.kgallery.DataBase.GallerySqlite;
import com.kics.kstudio.kgallery.DataModels.Photo_Item;
import com.kics.kstudio.kgallery.DataModels.PicsWithDates;
import com.kics.kstudio.kgallery.DataModels.Positions;
import com.kics.kstudio.kgallery.Fragments.PrivateFragmentILock;
import com.kics.kstudio.kgallery.Interfaces.I_CheckBoxChange;
import com.kics.kstudio.kgallery.Interfaces.I_PrivateMultiple;
import com.kics.kstudio.kgallery.Misc.Constants;
import com.kics.kstudio.kgallery.Misc.Utils;
import com.kics.kstudio.kgallery.R;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.kics.kstudio.kgallery.Fragments.AlbumsFragment.i_updateAlbums;

/**
 * Created by HP on 8/15/2018.
 */

public class Priva8PicsAdapter extends RecyclerView.Adapter<Priva8PicsAdapter.PicViewHolder> implements I_PrivateMultiple, I_CheckBoxChange {

    List<String> types;


    public static I_PrivateMultiple IMultiple;
    public static I_CheckBoxChange private_notify;
    Activity a;
    GallerySqlite gallerySqlite;

    public Priva8PicsAdapter(Activity a) {
        private_notify = this;
        IMultiple = this;
        this.a = a;
        gallerySqlite = new GallerySqlite(a);
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
        Log.e("private", "Creating");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gal_pic_single_view, parent, false);
        PicViewHolder myViewHolder = new PicViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final PicViewHolder holder, final int position) {

        final Photo_Item item;
        item = Constants.private_list.get(position);
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
                        Constants.is_private = true;
                        Constants.Fragment_name = a.getResources().getString(R.string.tab_private);
                        ImageShowActivity.fvr8time = false;
                        Intent intent = new Intent(a, ImageShowActivity.class);
                        ImageShowActivity.setDatatoShow(Constants.private_list);
                        intent.putExtra("Pos", getChildPosition(getChildObject(Constants.private_list.get(position).getKEY_SEC_PATH())));
                        a.startActivity(intent);

                    }

                    if (Constants.Selected_list.size() == 0) {
                        Constants.is_private = true;
                        Constants.selection = false;
                        MainActivity.i_toolbarchange.ChangeBTB();
                    }
                }


            });

            holder.Image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Constants.is_private = true;
                    MainActivity.i_toolbarchange.ChangeTB();
                    Constants.Fragment_name = a.getResources().getString(R.string.tab_private);
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
        for (int i = 0; i < Constants.private_list.size(); i++) {
            if (Constants.private_list.get(i).getKEY_OLD_PATH().equals(item.getKEY_OLD_PATH())) {
                Log.d("Position", "getChildPosition: " + i);
                return i;
            }
        }
        return -1;
    }


    public Photo_Item getChildObject(String path) {
        for (int i = 0; i < Constants.private_list.size(); i++) {
            if (Constants.private_list.get(i).getKEY_SEC_PATH().equals(path)) {
                return Constants.private_list.get(i);
            }
        }
        return null;
    }

    public Photo_Item getChildObjecttoDelete(String path) {
        Log.d("Position", "getChildObject: " + path + " \n");
        for (int i = 0; i < Constants.private_list.size(); i++) {
            Log.d("Position", "getChildObject: " + Constants.private_list.get(i).getKEY_OLD_PATH());
            if (Constants.private_list.get(i).getKEY_OLD_PATH().equals(path)) {
                Log.d("Position", "getChildObject: " + i);
                return Constants.private_list.get(i);
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return Constants.private_list.size();
    }

    @Override
    public void RemoveData() {
        for (int i = 0; i < Constants.positions.size(); i++) {
            Positions pos = Constants.positions.get(i);
            Log.d("Position", "RemoveData: " + pos.getMain() + " " + pos.getChild());
            deleteAtndex(pos.getChild(), pos.getPath());
        }

        DeleteFile();
        Constants.positions.clear();

    }

    private void deleteAtndex(int child, String path) {
        int index = getChildPosition(getChildObjecttoDelete(path));
        Log.d("Position", "deleteAtndex: " + index);
        try {
            gallerySqlite.RemovePrivatephoto(Constants.private_list.get(index), false);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Constants.private_list.remove(index);
        notifyItemRangeChanged(child, Constants.private_list.size());
    }

    @Override
    public void RemovePrivate() {
        for (int i = 0; i < Constants.positions.size(); i++) {
            Positions pos = Constants.positions.get(i);
            Log.d("Position", "RemoveData: " + pos.getMain() + " " + pos.getChild());
            deleteAtndex(pos.getChild(), pos.getPath());
        }
        Constants.positions.clear();
        RemovePrivateFile();

    }


    private void DeleteFile() {
        new MultipleDelete(a, Constants.Selected_list, new MultipleDelete.OnCompleteMultipleDelete() {
            @Override
            public void onComplete() {
            }
        }).executeTask();
    }

    private void changeAlbumsViews() {
        new LoadAlbumsThread(Constants.main_context
                , new LoadAlbumsThread.OnAyscronusCallCompleteListener() {
            @Override
            public void onCompleteAlbumList(ArrayList<HashMap<String, String>> albumList) {
                Constants.albumList = albumList;
                if (i_updateAlbums != null) {
                    i_updateAlbums.update();
                    i_updateAlbums.updateInnerAlbum();
                }
            }
        });
    }

    private void RemovePrivateFile() {
        new RemoveMultiplePrivate(a, Constants.Selected_list, new RemoveMultiplePrivate.OnCompleteMultipleRemovePrivate() {
            @Override
            public void onComplete() {
                if (PrivateFragmentILock.i_updateUI != null) {
                    PrivateFragmentILock.i_updateUI.update();
                }
                if (Constants.isChangeAlbum) {
                    changeAlbumsViews();
                    Constants.isChangeAlbum = false;
                }
                if (Constants.isChangeCamera) {
                    if (PicsDateAdapter.i_cameraUpdate != null) {
                        PicsDateAdapter.i_cameraUpdate.NotifyAllChanges();
                    }
                    changeAlbumsViews();
                    Constants.isChangeCamera = false;
                }

            }
        }).executeTask();
    }

    @Override
    public void on_bx_Change() {
        if (Constants.positions.size() > 0) {
            for (int i = 0; i < Constants.positions.size(); i++) {
                notifyItemChanged(Constants.positions.get(i).getChild());
            }
        }
    }

    class PicViewHolder extends RecyclerView.ViewHolder {
        ImageView Image;
        ImageView overlay;
        ImageView process;
        ImageView cbx;
        View shadow;

        public PicViewHolder(View itemView) {
            super(itemView);
            Image = itemView.findViewById(R.id.imageView);
            overlay = itemView.findViewById(R.id.overlay);
            process = itemView.findViewById(R.id.processing);
            cbx = itemView.findViewById(R.id.cbx);
            shadow = itemView.findViewById(R.id.shadow);

        }
    }
}
