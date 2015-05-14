package com.app.AppBabySH.item;

import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by ray on 2015/5/11.
 */
public class MomentsItem {
    public String CIRCLE_ID,USER_ID,NIC_NAME,USER_AVATAR,CLASS_ID,CLASS_NAME,SCHOOL_NAME,DESCRIPTION,CIRCLE_TYPE,LATITUDE,LONGITUDE,ENTRY_DATE,ENTRY_TIME;
    public ArrayList<JSONObject> ATCH = new ArrayList<JSONObject>();
    public ArrayList<JSONObject> REPLY = new ArrayList<JSONObject>();
    public ArrayList<JSONObject> GOOD = new ArrayList<JSONObject>();
}
