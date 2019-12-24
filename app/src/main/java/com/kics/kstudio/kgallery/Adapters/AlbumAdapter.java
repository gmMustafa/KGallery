package com.kics.kstudio.kgallery.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kics.kstudio.kgallery.Misc.Constants;
import com.kics.kstudio.kgallery.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by HP on 7/28/2018.
 */

public class AlbumAdapter extends BaseAdapter {
    private ArrayList<HashMap<String, String>> data;
    Activity a;

    public AlbumAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        this.a = a;
        data = d;
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        AlbumViewHolder holder = null;
        if (convertView == null) {
            holder = new AlbumViewHolder();
            convertView = LayoutInflater.from(a).inflate(
                    R.layout.gal_album_row, parent, false);

            holder.galleryImage = (ImageView) convertView.findViewById(R.id.galleryImage);
            holder.gallery_count = (TextView) convertView.findViewById(R.id.gallery_count);
            holder.gallery_title = (TextView) convertView.findViewById(R.id.gallery_title);

            convertView.setTag(holder);
        } else {
            holder = (AlbumViewHolder) convertView.getTag();
        }
        holder.galleryImage.setId(position);
        holder.gallery_count.setId(position);
        holder.gallery_title.setId(position);

        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);
        try {
            holder.gallery_title.setText(song.get(Constants.KEY_ALBUM));
            holder.gallery_count.setText(song.get(Constants.KEY_COUNT));

            Glide.with(a)
                    .load(new File(song.get(Constants.KEY_PATH))) // Uri of the picture
                    .into(holder.galleryImage);


        } catch (Exception e) {
        }
        return convertView;
    }


    class AlbumViewHolder {
        ImageView galleryImage;
        TextView gallery_count, gallery_title;
    }
}
