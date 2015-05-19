package com.app.Common;


import org.json.JSONArray;

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

    private JSONArray ChildinfoAry;

    public JSONArray getChildinfoAry() {
        return ChildinfoAry;
    }

    public void setChildinfoAry(JSONArray $Childinfo) {
        ChildinfoAry = $Childinfo;
    }

    private JSONArray ClassInfoAry;

    public JSONArray getClassInfoAry() {
        return ClassInfoAry;
    }

    public void setClassInfoAry(JSONArray $ClassInfo) {
        ClassInfoAry = $ClassInfo;
    }

    private JSONArray PushInfoAry;

    public JSONArray getPushInfoAry() {
        return PushInfoAry;
    }

    public void setPushInfoAry(JSONArray $PushInfo) {
        PushInfoAry = $PushInfo;
    }


}
