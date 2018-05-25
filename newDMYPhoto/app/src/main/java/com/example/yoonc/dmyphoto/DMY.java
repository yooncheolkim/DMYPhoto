package com.example.yoonc.dmyphoto;

/**
 * Created by yoonc on 2018-05-13.
 */


//android enum 관련 performace
// https://android.jlelse.eu/android-performance-avoid-using-enum-on-android-326be0794dc3
// 다음부터는 enum 사용하지 말자...
public enum DMY {
    DAILY(3),
    MONTHLY(5),
    YEARLY(7);

    int columsNum;

    DMY(int value){
        this.columsNum = value;
    }

    public int getColumsNum(){return columsNum;}

}
