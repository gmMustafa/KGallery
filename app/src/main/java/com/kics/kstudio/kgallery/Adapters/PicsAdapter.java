package com.kics.kstudio.kgallery.Adapters;

import android.content.Context;
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
import com.kics.kstudio.kgallery.DataModels.Photo_Item;
import com.kics.kstudio.kgallery.DataModels.Positions;
import com.kics.kstudio.kgallery.Misc.Constants;
import com.kics.kstudio.kgallery.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 8/15/2018.
 */

public class PicsAdapter extends RecyclerView.Adapter<PicsAdapter.PicViewHolder> {


    List<String> types;
    Context a;
    int mainPosition;
    List<Photo_Item> data;


    public PicsAdapter(Context c, List<Photo_Item> d, int position) {
        this.a = c;
        data = d;

        mainPosition = position;
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
    public void onBindViewHolder(final PicViewHolder holder, final int position) {
        final Photo_Item item;
        item = data.get(position);data.get(position).setKEY_MAIN_POS(mainPosition);
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

                            Log.d("Position", " on remove: " + mainPosition + " " + getChildPosition(item));
                            Constants.positions.remove(new Positions(mainPosition, getChildPosition(item), item.getKEY_OLD_PATH()));
                        } else {
                            holder.cbx.setSelected(true);
                            holder.cbx.setVisibility(View.VISIBLE);
                            holder.shadow.setVisibility(View.VISIBLE);

                            if (!Constants.Selected_list.contains(item)) {
                                Constants.Selected_list.add(item);
                                Constants.positions.add(new Positions(mainPosition, getChildPosition(item), item.getKEY_OLD_PATH()));
                                Log.d("Position", " on add: " + mainPosition + " " + getChildPosition(item));

                            }
                        }
                    } else {
                        Constants.is_private = false;
                        Constants.Fragment_name = a.getResources().getString(R.string.tab_photo);
                        ImageShowActivity.fvr8time = false;
                        Intent intent = new Intent(a, ImageShowActivity.class);
                        ImageShowActivity.setDatatoShow(getAllPicsConverted());
                        Log.d("PPS", "onClick: org:" + position + " data:" + data.get(position).getKEY_OLD_PATH());
                        Log.d("PPS", "onClick: dri:" + getPositon(data.get(position).getKEY_OLD_PATH()));
                        intent.putExtra("Pos", getPositon(data.get(position).getKEY_OLD_PATH()));
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
                    Constants.Fragment_name = a.getResources().getString(R.string.tab_photo);

                    MainActivity.i_toolbarchange.ChangeTB();
                    if (!holder.cbx.isSelected()) {
                        holder.cbx.setSelected(true);
                        holder.cbx.setVisibility(View.VISIBLE);
                        holder.shadow.setVisibility(View.VISIBLE);
                        Constants.selection = true;

                        Constants.Selected_list.add(item);
                        Constants.positions.add(new Positions(mainPosition, getChildPosition(item), item.getKEY_OLD_PATH()));
                        Log.d("Position", " on long pressed: " + mainPosition + " " + getChildPosition(item));
                    }
                    return true;
                }
            });


        } catch (Exception e) {
        }
    }

    private List<Photo_Item> getAllPicsConverted() {
        List<Photo_Item> allList = new ArrayList<>();
        for (int i = 0; i < Constants.Camera_photos_list.size(); i++) {
            allList.addAll(Constants.Camera_photos_list.get(i).getPics());
        }
        return allList;
    }

    private Integer getPositon(String key_path) {
       List<Photo_Item>list =getAllPicsConverted();
        for (int i = 0; i < list.size(); i++)
            if (key_path.equals(list.get(i).getKEY_OLD_PATH())) {
                return i;
            }
        return 0;
    }

    public int getChildPosition(Photo_Item item) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) == item) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return data.size();
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