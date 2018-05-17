package com.example.yoonc.dmyphoto;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.media.ExifInterface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

/**
 * Created by yoonc on 2018-04-29.
 */

//이미지 url, 각 이미지 날짜, orientation 가져와야함.
    // 저장되어 있는 이미지 url 를 불러와서, 년,월,일로 나누기


public class ImageAdapter extends BaseAdapter {
    String LOGTAG = "imageadapter LOG";


    private Context mContext;
    //외부 저장소 picture directory
    FilesInformations fi = FilesInformations.getInstance();

    //날짜를 가질 header
    String header;
    //날짜 이미지의 uri들
    List<Uri> uris = new ArrayList<>();
    int itemWidth;


    public ImageAdapter(Context c){
        mContext = c;
    }

    @Override
    public int getCount() {return  uris.size();}

    @Override
    public Object getItem(int i) {
       return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            GridViewWithHeaderAndFooter.LayoutParams params = new GridViewWithHeaderAndFooter.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(params);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(1,1,1,1);

            //convertView = new ImageView(mContext);
        }
        else{
            imageView = (ImageView)convertView;
        }
        //Glide.with(mContext).load(uris.get(position)).override(itemWidth,itemWidth).into((ImageView)convertView);
        Glide.with(mContext).load(uris.get(position)).override(50,50).into(imageView);

        //return convertView;
        return imageView;
    }

    public void getIteratorFromFI(String s, List<Uri> u){
        header = s;
        uris = u;
    }

    public void setItemWidth(int deviceWidth) {
        if(MainActivity.currDMY == DMY.DAILY)
            itemWidth = (deviceWidth - 4) / 3;
        else if(MainActivity.currDMY == DMY.MONTHLY)
            itemWidth = (deviceWidth - 6) / 5;
        else if(MainActivity.currDMY == DMY.YEARLY)
            itemWidth = (deviceWidth- 8) / 7;
    }
}
