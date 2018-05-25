package com.example.yoonc.dmyphoto;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.GridLayout;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import dev.dworks.libs.astickyheader.SectionedGridAdapter;
import dev.dworks.libs.astickyheader.SimpleSectionedGridAdapter;

import dev.dworks.libs.astickyheader.SimpleSectionedGridAdapter;
import dev.dworks.libs.astickyheader.SimpleSectionedGridAdapter.Section;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    private static final String LOGTAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_CODE = 1;
    private GridView gridView;
    private ImageAdapter mAdapter;
    private ArrayList<Section> sections = new ArrayList<Section>();
    private DMY currDMY = DMY.DAILY;
    FilesInformations fi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
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
                //퍼미션 허락 되면, init 시작.
            }
        }
        if (!neededPermissions.isEmpty()) {
            requestPermissions(neededPermissions.toArray(new String[]{}),
                    MY_PERMISSIONS_REQUEST_ACCESS_CODE);
        }
        init();
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

    private void init()
    {
        fi = FilesInformations.getInstance(this);
        gridView = (GridView)findViewById(R.id.grid);
        gridView.setNumColumns(currDMY.getColumsNum());
        mAdapter = new ImageAdapter(this);
        mAdapter.getHeaderNamesAndPositions(currDMY);

        for(int i = 0 ; i < mAdapter.mHeaderPositions.size()-1 ; i++){
            sections.add(new Section(mAdapter.mHeaderPositions.get(i),mAdapter.mHeaderNames.get(i)));
        }
        SimpleSectionedGridAdapter simpleSectionedGridAdapter = new SimpleSectionedGridAdapter(this,mAdapter,
                R.layout.grid_item_header,R.id.header_layout,R.id.header);
        simpleSectionedGridAdapter.setGridView(gridView);
        simpleSectionedGridAdapter.setSections(sections.toArray(new Section[0]));
        gridView.setAdapter(simpleSectionedGridAdapter);

    }



        /* About Pinch in out */

    public void setZoomIn(){
        fi.setIterator();
        switch (currDMY){
            case YEARLY: break;
            case MONTHLY:
                currDMY = DMY.YEARLY;
                mAdapter.getHeaderNamesAndPositions(currDMY);
                gridView.removeAllViewsInLayout();
                sections.clear();
                //gridView.removeAllViews();
                init();
                break;
            case DAILY:
                currDMY = DMY.MONTHLY;
                mAdapter.getHeaderNamesAndPositions(currDMY);
                gridView.removeAllViewsInLayout();
                sections.clear();
                //gridView.removeAllViews();
                init();
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
                mAdapter.getHeaderNamesAndPositions(currDMY);
                gridView.removeAllViewsInLayout();
                sections.clear();
                //gridView.removeAllViews();
                init();
                break;
            case YEARLY:
                currDMY = DMY.MONTHLY;
                mAdapter.getHeaderNamesAndPositions(currDMY);
                gridView.removeAllViewsInLayout();
                sections.clear();
                //gridView.removeAllViews();
                init();
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
