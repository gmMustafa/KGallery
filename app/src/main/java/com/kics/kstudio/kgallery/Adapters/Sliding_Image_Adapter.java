package com.kics.kstudio.kgallery.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;

import com.bumptech.glide.Glide;
import com.kics.kstudio.kgallery.DataModels.Photo_Item;
import com.kics.kstudio.kgallery.R;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 8/17/2018.
 */

public class Sliding_Image_Adapter extends PagerAdapter {
    Context context;
    List<Photo_Item> dataList;
    LayoutInflater inflater;
    List<String> types;
    private int position2 = 1;
    private MediaController controller = null;


    public Sliding_Image_Adapter(Context context, List<Photo_Item> IMAGES) {
        this.context = context;
        this.dataList = IMAGES;
        inflater = LayoutInflater.from(context);
        types = new ArrayList<>();
        types.add(".tif");
        types.add(".tiff");
        types.add(".bmp");
        types.add(".jpg");
        types.add(".jpeg");
        types.add(".gif");
        types.add(".png");
        types.add(".eps");

        controller = new MediaController(context);

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        Log.e("PTYPE::", dataList.get(position).getKEY_TYPE());
        if (types.contains(dataList.get(position).getKEY_TYPE().toLowerCase())) {

            View imageLayout = inflater.inflate(  R.layout.gal_single_image_show_in_pager, view, false);
            assert imageLayout != null;
            com.jsibbold.zoomage.ZoomageView imageView = (com.jsibbold.zoomage.ZoomageView) imageLayout
                    .findViewById(R.id.imageView);
            try {
                Glide.with(context)
                        .load(new File(dataList.get(position).getKEY_OLD_PATH())) // Uri of the picture
                        .into(imageView);
                imageView.reset();
            } catch (Exception e) {
            }
            view.addView(imageLayout, 0);
            return imageLayout;
        } else {
            View videolayout = inflater.inflate(R.layout.gal_single_video_show_in_pager, view, false);
            assert videolayout != null;
            ImageView overlay = videolayout.findViewById(R.id.overlay);
            overlay.setVisibility(View.VISIBLE);
            overlay.setBackgroundResource(R.drawable.play);

            ImageView imageView = videolayout.findViewById(R.id.imageViewVideo);
            try {
                Glide.with(context)
                        .load(new File(dataList.get(position).getKEY_OLD_PATH())) // Uri of the picture
                        .into(imageView);
            } catch (Exception e) {
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final File videoFile = new File(dataList.get(position).getKEY_OLD_PATH());
                    Uri fileUri = FileProvider.getUriForFile(context,context.getResources().getString(R.string.authority), videoFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(fileUri, "video/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//DO NOT FORGET THIS EVER
                    context.startActivity(intent);
                }
            });
            view.addView(videolayout, 0);
            return videolayout;
        }
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }



    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
