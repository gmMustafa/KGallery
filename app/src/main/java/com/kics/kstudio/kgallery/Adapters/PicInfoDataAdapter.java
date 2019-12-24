package com.kics.kstudio.kgallery.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.kics.kstudio.kgallery.DataModels.PicDetailsInfo;
import com.kics.kstudio.kgallery.R;

import java.util.List;

/**
 * Created by HP on 8/17/2018.
 */

public class PicInfoDataAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    List<PicDetailsInfo> data;
    TextView title;
    TextView details;

    public PicInfoDataAdapter(Context context, List<PicDetailsInfo> data) {
        this.context = context;
        this.data = data;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(view==null)
        {
            view=layoutInflater.inflate( R.layout.gal_simple_info_view,viewGroup,false);
        }
            title=(TextView)view.findViewById(R.id.title_info);
            details=(TextView)view.findViewById(R.id.details);
            title.setText(data.get(i).getTitle());
            details.setText(data.get(i).getDetails());
        return view;
    }
}
