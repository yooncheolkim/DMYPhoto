package com.example.yoonc.dmyphoto;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

/**
 * Created by yoonc on 2018-04-29.
 */

//thumbnail uri, date string
public class ImageAdapter extends BaseAdapter {
    String LOGTAG = "imageadapter LOG";


    private Context mContext;
    FilesInformations fi = FilesInformations.getInstance();

    //날짜를 가질 header
    String header;
    //thumbnail Uris
    List<Uri> uris = new ArrayList<>();

    int itemWidth;

    public ImageAdapter(Context c){
        mContext = c;
    }

    @Override
    public int getCount() {return  uris.size();}

    @Override
    public Uri getItem(int i) {
       return uris.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    //왜 이미지가 그려지지 않는걸까...??
    //글라이드 버전 높여보기...
    //heap??gc 코드 빼고 다시 해보자...
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.image_holder,parent,false);

            holder.item_image = (ImageView)convertView.findViewById(R.id.iv);

            GridViewWithHeaderAndFooter.LayoutParams params = new GridViewWithHeaderAndFooter.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            convertView.setLayoutParams(params);

            convertView.setPadding(1,1,1,1);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            holder.item_image.setLayoutParams(params1);
            holder.item_image.setAdjustViewBounds(true);
            holder.item_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        Uri uri = getItem(position);
        holder.item_image.setImageURI(uri);

        return convertView;
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

    private class ViewHolder{
        public ImageView item_image;
    }
}
