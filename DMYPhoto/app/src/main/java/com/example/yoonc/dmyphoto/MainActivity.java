package com.example.yoonc.dmyphoto;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    private static final String LOGTAG = "MAINACTIVITY";
    private static Context sContext;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_CODE = 1;
    public static DMY currDMY = DMY.DAILY;
    public static FilesInformations fi;
    public static ScrollView scrollView;
    public static int displayWidthSize;
    public static LinearLayout linearLayout;

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(LOGTAG,"onresume!");
        fi.setIterator();
        setScrollViewGridView();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sContext = getApplicationContext();
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        checkPermissions();
        fi = FilesInformations.getInstance();
        displayWidthSize = getDeviceWidthSize(this);

        linearLayout = new LinearLayout(sContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(params);
        scrollView.addView(linearLayout);
        setScrollViewGridView();
    }
    public static Context getAppContext(){
        return sContext;
    }


    public void setScrollViewGridView(){
        Iterator<String> iteratorKey = fi.iteratordailyKeys;

        if(currDMY == DMY.DAILY)
            iteratorKey = fi.iteratordailyKeys;
        else if(currDMY == DMY.MONTHLY)
            iteratorKey = fi.iteratormonthlyKeys;
        else if(currDMY == DMY.YEARLY)
            iteratorKey = fi.iteratoryearlyKeys;

        while(iteratorKey.hasNext()) {
            GridViewWithHeaderAndFooter gridView = new GridViewWithHeaderAndFooter(this);
            gridView.setNumColumns(currDMY.getColumsNum());
            //gridView.setVerticalScrollBarEnabled(false);
            //gridView.setClickable(false);
            //gridView.setFocusable(false);
            //정렬된 날짜의 string을 가지고 있는 키
            String key = iteratorKey.next();

            //header 설정
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View headerView = layoutInflater.inflate(R.layout.layout_header,null,false);
            TextView headerText = (TextView)headerView.findViewById(R.id.text);
            headerText.setText(key);
            gridView.addHeaderView(headerView);


            ImageAdapter adapter = new ImageAdapter(this);
            if(currDMY == DMY.DAILY) adapter.getIteratorFromFI(key,fi.groupingResultDaily.get(key));
            else if(currDMY == DMY.MONTHLY) adapter.getIteratorFromFI(key,fi.groupingResultMonthly.get(key));
            else if(currDMY == DMY.YEARLY) adapter.getIteratorFromFI(key,fi.groupingResultYearly.get(key));
            adapter.setItemWidth(getDeviceWidthSize(this));
            gridView.setAdapter(adapter);

            Log.d(LOGTAG,"adapter size = " + adapter.getCount());

            int imageHeight = (displayWidthSize - (currDMY.getColumsNum() +1))/currDMY.getColumsNum();
            //이미지의 열 + padding
            int totalheight = imageHeight *(adapter.getCount() / currDMY.getColumsNum()) + (1*adapter.getCount() / currDMY.getColumsNum() +1);
            if(adapter.getCount() % currDMY.getColumsNum() != 0) totalheight += imageHeight;
            headerText.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            totalheight += headerText.getMeasuredHeight();

            Log.d(LOGTAG,"totalheight = " + totalheight);

            //scrollview안에 있는 LinearLayout에 gridView 넣기
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    totalheight);

            linearLayout.addView(gridView,params);
        }
    }



    public static int getDeviceWidthSize(Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_CODE: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    checkPermissions();
                }
            }
        }
    }
    /**
     * checking  permissions at Runtime.
     */
    private void checkPermissions() {
        final String[] requiredPermissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };
        final List<String> neededPermissions = new ArrayList<>();
        for (final String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    permission) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(permission);
            }
        }
        if (!neededPermissions.isEmpty()) {
            requestPermissions(neededPermissions.toArray(new String[]{}),
                    MY_PERMISSIONS_REQUEST_ACCESS_CODE);
        }
    }



    /* About Pinch in out */

    public void setZoomIn(){
        fi.setIterator();
        switch (currDMY){
            case YEARLY: break;
            case MONTHLY:
                currDMY = DMY.YEARLY;
                linearLayout.removeAllViews();
                setScrollViewGridView();
                break;
            case DAILY:
                currDMY = DMY.MONTHLY;
                linearLayout.removeAllViews();
                setScrollViewGridView();
                break;
        }
    }
    public void setZoomOut(){
        fi.setIterator();
        switch (currDMY) {
            case DAILY:
                break;
            case MONTHLY:
                currDMY = DMY.DAILY;
                linearLayout.removeAllViews();
                setScrollViewGridView();
                break;
            case YEARLY:
                currDMY = DMY.MONTHLY;
                linearLayout.removeAllViews();
                setScrollViewGridView();
                break;
        }
    }

    //드래그 모드인지 핀치줌 모드인지 구분
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    //드래그시 좌표 저장
    int posX1 = 0, posX2 = 0, posY1 = 0, posY2 = 0;

    //핀치시 두 좌표간의 거리 저장
    float oldDist = 1f;
    float newDist = 1f;

    public void setPinchInOutTouch(MotionEvent event){
        int act = event.getAction();
        String strMsg = "";
        Log.d(LOGTAG,"touch!!");
        switch (act & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN: //첫번째 손가락 터치(드래그 용도)
                posX1 = (int) event.getX();
                posY1 = (int) event.getY();
                Log.d("zoom", "mode=DRAG" );
                mode = DRAG;
                break;
            case MotionEvent.ACTION_MOVE:
                if(mode == DRAG) { // 드래그 중
                    posX2 = (int) event.getX();
                    posY2 = (int) event.getY();
                    if (Math.abs(posX2 - posX1) > 20 || Math.abs(posY2 - posY1) > 20) {
                        posX1 = posX2;
                        posY1 = posY2;
                        strMsg = "drag";
                        Log.d(LOGTAG, strMsg);
                    }
                }
                else if(mode == ZOOM){ // 핀치중
                    newDist = spacing(event);
                    Log.d("zoom", "newDist=" + newDist);
                    Log.d("zoom", "oldDist=" + oldDist);
                    if (newDist - oldDist > 700) { // zoom in
                        oldDist = newDist;
                        strMsg = "zoom in";
                        Log.d(LOGTAG,strMsg);
                        setZoomIn();
                    } else if(oldDist - newDist > 700) { // zoom out
                        oldDist = newDist;
                        strMsg = "zoom out";
                        Log.d(LOGTAG,strMsg);
                        setZoomOut();
                    }
                }
                break;
            case MotionEvent.ACTION_UP: // 첫번째 손가락 떼었을 경우
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //두번째 손가락 터치(손가락 2개를 인식하였기 때문에 핀치 줌으로 판별
                mode = ZOOM;

                newDist = spacing(event);
                oldDist = spacing(event);

                Log.d("zoom", "newDist=" + newDist);
                Log.d("zoom", "oldDist=" + oldDist);
                Log.d("zoom", "mode=ZOOM");
                break;
            case MotionEvent.ACTION_CANCEL:
            default:
                break;
        }
    }

    private float spacing(MotionEvent event){
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x*x + y*y);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        setPinchInOutTouch(event);
        return super.dispatchTouchEvent(event);
    }
}
