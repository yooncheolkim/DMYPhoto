package com.example.yoonc.dmyphoto;

/**
 * Created by yoonc on 2018-05-13.
 */

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
