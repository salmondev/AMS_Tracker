package com.example.administrator.myapplicationqr;

import android.content.Context;

public class ImageButtonText extends android.support.v7.widget.AppCompatImageButton {

    public int MAP_RID;
    public int POS_X;
    public int POS_Y;
    public int DAY;
    public int MONTH;
    public int YEAR;

    public int ITEM_RID;
    public long UNIQUE_ID;
    public String SERIAL;
    public String ITEM_NAME;

    public int OWNER_RID;
    public long OWNER_UID;
    public String FIRST_NAME;
    public String LAST_NAME;

    public ImageButtonText(Context context, int MAP_RID, int POS_X, int POS_Y, int DAY, int MONTH, int YEAR,
                           int ITEM_RID, long UNIQUE_ID, String SERIAL, String ITEM_NAME, int OWNER_RID, int OWNER_UID, String FIRST_NAME, String LAST_NAME) {
        super(context);
        this.MAP_RID = MAP_RID;
        this.POS_X  = POS_X;
        this.POS_Y  = POS_Y;
        this.DAY  = DAY;
        this.MONTH  = MONTH;
        this.YEAR  = YEAR;

        this.ITEM_RID  = ITEM_RID;
        this.UNIQUE_ID  = UNIQUE_ID;
        this.SERIAL  = SERIAL;
        this.ITEM_NAME  = ITEM_NAME;

        this.OWNER_RID  = OWNER_RID;
        this.OWNER_UID  = OWNER_UID;
        this.FIRST_NAME  = FIRST_NAME;
        this.LAST_NAME  = LAST_NAME;
    }



}
