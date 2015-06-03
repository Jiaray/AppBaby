package com.app.Common;


import com.app.AppBabySH.item.ClassItem;

import org.json.JSONArray;

import java.util.ArrayList;

public class UserData {
    public String UserName;

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String UserPW;

    public String getUserPW() {
        return UserPW;
    }

    public void setUserPW(String userPW) {
        UserPW = userPW;
    }

    public String UserID;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String $UserID) {
        UserID = $UserID;
    }

    private JSONArray BaseInfoAry;

    public JSONArray getBaseInfoAry() {
        return BaseInfoAry;
    }

    public void setBaseInfoAry(JSONArray $BaseInfoAry) {
        BaseInfoAry = $BaseInfoAry;
    }

    public ArrayList<ClassItem> ClassAryList;

    private JSONArray PushInfoAry;

    public JSONArray getPushInfoAry() {
        return PushInfoAry;
    }

    public void setPushInfoAry(JSONArray $PushInfo) {
        PushInfoAry = $PushInfo;
    }


}
