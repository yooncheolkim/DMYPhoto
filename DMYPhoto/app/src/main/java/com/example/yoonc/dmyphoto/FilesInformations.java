package com.example.yoonc.dmyphoto;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifImageDirectory;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by yoonc on 2018-05-01.
 */

//여기에 전체 file들의 정보를 가지고 있어야 겠당...
//여기서 필터링 해서 imageAdapter 에 넘겨주기.
public class FilesInformations {
    private String LOGTAG = "FIlesInformations LOG";

    public class FileInformations{
        Uri uri;
        Date date;
        public FileInformations(Uri u, Date d){uri = u; date = d;}
    }

    public Context mContext;
    private File directoryFolder;
    //directory path
    private String folderPath;

    //picture directory에 있는 이미지에 대한 file 객체
    private File[] files;
    private String filePath;

    //이미지 파일 전체에 대한 정보.
    private List<FileInformations> filesInformations = new ArrayList<>();

    //조건에 대한 파일 정보를 그룹핑
    //날짜에 따라 uri를 그룹핑함.
    public Map<String,List<Uri>> groupingResultDaily = new HashMap<String,List<Uri>>();
    public Map<String,List<Uri>> groupingResultMonthly = new HashMap<String,List<Uri>>();
    public Map<String,List<Uri>> groupingResultYearly = new HashMap<String,List<Uri>>();

    //각 map 에 대한 정렬된 Key 에 대한 iterator 반환.
    public Iterator<String> iteratordailyKeys;
    public Iterator<String> iteratormonthlyKeys;
    public Iterator<String> iteratoryearlyKeys;


    private FilesInformations(Context c)
    {
        mContext = c;
        //외부 공용 디렉토리 의 DCIM 디렉토리에대한 path를 얻음.
        directoryFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        folderPath = directoryFolder.getAbsolutePath();

        Log.d(LOGTAG,folderPath);
        files = directoryFolder.listFiles();

        Log.d(LOGTAG, "files length : " + files.length);
        getFilesInformations();
        gruoping();
    }

    //싱글톤을 위함
    public static FilesInformations getInstance(){
        return LazyHolder.INSTANCE;
    }
    private static class LazyHolder{
        private static final FilesInformations INSTANCE = new FilesInformations(MainActivity.getAppContext());
    }


    //파일 date, url 가져오기
    private void getFilesInformations(){
        String[] projection = {MediaStore.Images.Media._ID
                ,MediaStore.Images.Media.DATA
                ,MediaStore.Images.Media.DISPLAY_NAME};
        Cursor cursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,null,null,"_id asc limit 100");

        if(cursor!= null && cursor.moveToFirst()){
            int idColumnIndex = cursor.getColumnIndex(projection[0]);
            int dataColumnIndex = cursor.getColumnIndex(projection[1]);
            int displayNameColumnIndex = cursor.getColumnIndex(projection[2]);

            while(cursor.moveToNext()){
                String id = cursor.getString(idColumnIndex);
                Uri thumnailUrl = getThumbnailUrl(id);
                String displayName = cursor.getString(displayNameColumnIndex);
                String fileUrl = cursor.getString(dataColumnIndex);
                Date date = extractExifDateTime(fileUrl);
                filesInformations.add(new FileInformations(thumnailUrl,date));
            }
        }
    }

    private Uri getThumbnailUrl(String id){
        Uri thumbnailUri = Uri.EMPTY;
        String[] projection2 = {MediaStore.Images.Thumbnails.DATA};
        Cursor thumbanilCursor = mContext.getContentResolver().query(
                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                projection2,
                MediaStore.Images.Thumbnails.IMAGE_ID + "=?",
                new String[]{id},
                null);
        if(thumbanilCursor == null){}
        else if(thumbanilCursor.moveToFirst()){
            int thumbnailColumnIndex = thumbanilCursor.getColumnIndex(projection2[0]);
            String thumbnailPath = thumbanilCursor.getString(thumbnailColumnIndex);
            thumbanilCursor.close();
            thumbnailUri = Uri.parse(thumbnailPath);
        }else{
            MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(), Long.parseLong(id), MediaStore.Images.Thumbnails.MINI_KIND, null);
            thumbanilCursor.close();
            thumbnailUri = getThumbnailUrl(id);
        }
        return thumbnailUri;
    }

    //목록 미리 만들어 놓는게 좋을까? pinch 할때마다 가져오는게 좋을까?
    public void gruoping(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

        for(FileInformations f : filesInformations){
            //fileinformations의 date 포맷을 string으로 변경후, hashmap을 이용하여, 확인후 저장
            String dateTemp = dateFormat.format(f.date);
            if(groupingResultDaily.containsKey(dateTemp)){
                groupingResultDaily.get(dateTemp).add(f.uri);
            }
            else{
                List<Uri> list = new ArrayList<>();
                list.add(f.uri);
                groupingResultDaily.put(dateTemp,list);
            }
        }

        for(FileInformations f : filesInformations){
            String monthTemp = monthFormat.format(f.date);
            if(groupingResultMonthly.containsKey(monthTemp)){
                groupingResultMonthly.get(monthTemp).add(f.uri);
            }
            else{
                List<Uri> list = new ArrayList<>();
                list.add(f.uri);
                groupingResultMonthly.put(monthTemp,list);
            }
        }


        for(FileInformations f : filesInformations){
            String yearTemp = yearFormat.format(f.date);
            if(groupingResultYearly.containsKey(yearTemp)){
                groupingResultYearly.get(yearTemp).add(f.uri);
            }
            else{
                List<Uri> list = new ArrayList<>();
                list.add(f.uri);
                groupingResultYearly.put(yearTemp,list);
            }
        }
        setIterator();
    }

    //TreeMap을 이용한 iterator 정렬
    public void setIterator(){
        TreeMap<String,List<Uri>> tm = new TreeMap<>(groupingResultDaily);
        iteratordailyKeys = tm.keySet().iterator();
        tm = new TreeMap<>(groupingResultMonthly);
        iteratormonthlyKeys = tm.keySet().iterator();
        tm = new TreeMap<>(groupingResultYearly);
        iteratoryearlyKeys = tm.keySet().iterator();
    }


    private Date extractExifDateTime(String imagePath) {
        Log.d("exif", "Attempting to extract EXIF date/time from image at " + imagePath);
        Date datetime = new Date(0); // or initialize to null, if you prefer
        try {
            Metadata metadata = JpegMetadataReader.readMetadata(new File(imagePath));
            // these are listed in order of preference
            int[] datetimeTags = new int[] { ExifImageDirectory.TAG_DATETIME_ORIGINAL,
                    ExifImageDirectory.TAG_DATETIME,
                    ExifImageDirectory.TAG_DATETIME_DIGITIZED };

            for (Directory directory : metadata.getDirectories()) {
                for (int tag : datetimeTags) {
                    if (directory.containsTag(tag)) {
                        Log.d("exif", "Using tag " + directory.getTagName(tag) + " for timestamp");
                        SimpleDateFormat exifDatetimeFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault());
                        datetime = exifDatetimeFormat.parse(directory.getString(tag));

                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.w("exif", "Unable to extract EXIF metadata from image at " + imagePath, e);
        }
        return datetime;
    }

    public class ThumbnailUri{
        Uri thumbnailUri;
        public ThumbnailUri(Uri uri){
            thumbnailUri = uri;
        }
    }


}
