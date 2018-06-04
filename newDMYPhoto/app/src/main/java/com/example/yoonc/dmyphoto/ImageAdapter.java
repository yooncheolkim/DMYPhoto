package com.example.yoonc.dmyphoto;

import android.content.Context;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import dev.dworks.libs.astickyheader.ui.SquareImageView;

/**
 * Created by yoonc on 2018-05-25.
 */

public class ImageAdapter extends BaseAdapter {
    String LOGTAG = "imageadapter LOG";

    private Context mContext;
    private LayoutInflater mInfalter;
    FilesInformations fi = FilesInformations.getInstance(mContext);
    ArrayList<String> mHeaderNames;
    ArrayList<Integer> mHeaderPositions;

    public ImageAdapter(Context c){
        mContext = c;
        mInfalter = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void getHeaderNamesAndPositions(DMY currDMY){
        if(currDMY == DMY.DAILY){
            mHeaderNames = fi.mHeaderNames_daily;
            mHeaderPositions = fi.mHeaderPositions_daily;
        }else if(currDMY == DMY.MONTHLY){
            mHeaderNames = fi.mHeaderNames_monthly;
            mHeaderPositions = fi.mHeaderPositions_monthly;
        }else if(currDMY == DMY.YEARLY){
            mHeaderNames = fi.mHeaderNames_yearly;
            mHeaderPositions = fi.mHeaderPositions_yearly;
        }
    }

    @Override
    public int getCount() {
        return fi.filesInformations.size();
    }

    @Override
    public Object getItem(int position) {
        return fi.filesInformations.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        ImageView image;
        if(converView == null) {
            converView = mInfalter.inflate(R.layout.grid_item,parent,false);
        }
        image = ViewHolder.get(converView,R.id.image);
        image.setPadding(1,1,1,1);
        image.setImageURI(fi.filesInformations.get(position).uri);
        //Glide.with(mContext).load(fi.filesInformations.get(position).uri).into(image);
        return converView;
    }


    public static class ViewHolder{
        @SuppressWarnings("unchecked")
        public static <T extends View> T get(View view, int id){
            SparseArray<View> viewHolder =(SparseArray<View>) view.getTag();
            if(viewHolder == null){
                viewHolder = new SparseArray<View>();
                view.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if(childView == null){
                childView = view.findViewById(id);
                viewHolder.put(id,childView);
            }
            return (T) childView;
        }
    }
}
