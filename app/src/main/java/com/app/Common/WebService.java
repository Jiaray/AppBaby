package com.app.Common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

public class WebService {
    private static final String TAG = "WebService";
    public final static Handler handler = new Handler();

    //  登入
    public static void Login(String id, String user, String pswd, String d_type, String d_ver, String d_token, String p1, String p2, WebCallback callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("User_Name", user);
        map.put("Password", pswd);
        map.put("Device_Type", d_type);
        map.put("Device_Version", d_ver);
        map.put("Device_Token", d_token);
        map.put("Device_Param_1", p1);
        map.put("Device_Param_2", p2);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Get_Login", map, callBack);
    }

    //  取得使用者詳細資訊
    public static void GetUserInfo(String id, String $userName, String $userID, WebCallback callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("User_Name", $userName);
        map.put("User_ID", $userID);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Get_User_Info ", map, callBack);
    }

    /*========  註冊  ========*/
    //  激活
    public static void GetActivate(String id, String $activity, String $classID, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Activity_No", $activity);
        map.put("Mobile_No", $classID);
        map.put("Validate_Type", "");
        map.put("CheckKey", "");
        Log.i(TAG, "GetActivate map:" + map);
        GetJson(id, "Baby_Get_Activate", map, $callBack);
    }

    //  驗證
    public static void GetValidate(String id, String $activity, String $validateNo, String $validatetype, String $password, String $nickName, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Activity_No", $activity);
        map.put("Validate_No", $validateNo);
        map.put("Validate_Type", $validatetype);
        map.put("Password", $password);
        map.put("Nick_Name", $nickName);
        map.put("CheckKey", "");
        Log.i(TAG, "GetValidate map:" + map);
        GetJson(id, "Baby_Get_Validate", map, $callBack);
    }

    //  註冊 (parent only)
    public static void SetRegister(String id, String $activity, String $relationship, String $password, String $nickName, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Activity_No", $activity);
        map.put("Relationship", $relationship);
        map.put("Password", $password);
        map.put("Nick_Name", $nickName);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Set_Register", map, $callBack);
    }

    //  忘記密碼驗證
    public static void GetPasswordValidate(String id, String $mobile_No, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Mobile_No", $mobile_No);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Get_Password_Validate", map, $callBack);
    }

    //  密碼重置
    public static void SetPasswordReset(String id, String $mobile_No, String $validateNo, String $password, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Mobile_No", $mobile_No);
        map.put("Validate_No", $validateNo);
        map.put("Password", $password);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Set_Password_Reset", map, $callBack);
    }

    //  無法註冊反饋提交
    public static void SetFeedback(String id,
                                   String $source, String $name, String $mobile_No,
                                   String $school, String $class, String $email,
                                   String $student, String $activity_No, String $content, String $user_ID,
                                   WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Source", $source);
        map.put("Name", $name);
        map.put("Mobile_No", $mobile_No);
        map.put("School", $school);
        map.put("Class", $class);
        map.put("Email", $email);
        map.put("Student", $student);
        map.put("Activity_No", $activity_No);
        map.put("Content", $content);
        map.put("User_ID", $user_ID);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Set_Feedback", map, $callBack);
    }

    /*========  班級圈 Moments  ========*/
    //  取得班級圈列表-依班級
    public static void GetCircleListClass(String id, String $userID, String $classID,String $pageIndex,String $pageSize, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("User_ID", $userID);
        map.put("Class_ID", $classID);
        map.put("Page_Index", $pageIndex);
        map.put("Page_Size", $pageSize);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Get_Circle_List_Class", map, $callBack);
    }

    //  取得班及圈列表-依個人
    public static void GetCircleListPersonal(String id, String $userID, String $classID,String $pageIndex,String $pageSize, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("User_ID", $userID);
        map.put("Class_ID", $classID);
        map.put("Page_Index", $pageIndex);
        map.put("Page_Size", $pageSize);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Get_Circle_List_Personal", map, $callBack);
    }

    //  取得班及圈列表-最新 (Push未讀的)
    public static void GetCircleListNotRead(String id, String $userID, String $classID,String $pageIndex,String $pageSize, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("User_ID", $userID);
        map.put("Class_ID", $classID);
        map.put("Page_Index", $pageIndex);
        map.put("Page_Size", $pageSize);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Get_Circle_List_Not_Read", map, $callBack);
    }

    //  班級圈刪除 (發表人才能刪)
    public static void SetCircleDelete(String id, String $circleID, String $userID, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Circle_ID", $circleID);
        map.put("User_ID", $userID);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Set_Circle_Delete", map, $callBack);
    }

    //  班級圈是否已按讚
    public static void GetCircleHadGood(String id, String $circleID, String $userID, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Circle_ID", $circleID);
        map.put("User_ID", $userID);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Get_Circle_Had_Good", map, $callBack);
    }

    //  班級圈按讚
    public static void SetCircleGood(String id, String $circleID, String $userID, String $type, WebCallback $callBack) {
        Log.v(TAG, "$circleID:" + $circleID + " $userID:" + $userID);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Circle_ID", $circleID);
        map.put("User_ID", $userID);
        map.put("Type", $type);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Set_Circle_Good", map, $callBack);
    }

    //  班級圈是否已收藏到成長檔案
    public static void GetCircleHadKeepToGrow(String id, String $circleID, String $userID, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Circle_ID", $circleID);
        map.put("User_ID", $userID);
        map.put("Grow_From", "CIRCLE");
        map.put("CheckKey", "");
        GetJson(id, "Baby_Get_Circle_Had_KeepTo_Grow", map, $callBack);
    }

    //  班級圈被收藏的個數
    public static void GetCircleKeepToGrowCount(String id, String $circleID, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Circle_ID", $circleID);
        map.put("Grow_From", "CIRCLE");
        map.put("CheckKey", "");
        GetJson(id, "Baby_Get_Circle_KeepTo_Grow_Count", map, $callBack);
    }

    //  班級圈收藏到成長檔案
    public static void SetCircleKeepToGrow(String id, String $circleID, String $userID, String $type, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Circle_ID", $circleID);
        map.put("User_ID", $userID);
        map.put("Grow_From", "CIRCLE");
        map.put("Type", $type);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Set_Circle_KeepTo_Grow", map, $callBack);
    }

    //  班級圈回覆
    public static void SetCircleReply(String id, String $apn, String $circleID, String $userID, String $replydesc, String $atreplysn, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("APN", $apn);
        map.put("Circle_ID", $circleID);
        map.put("User_ID", $userID);
        map.put("Reply_Desc", $replydesc);
        map.put("AT_Reply_SN", $atreplysn);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Set_Circle_Reply", map, $callBack);
    }

    //  班級圈新增
    public static void SetCircleNew(String id, String $apn, String $userID, String $classID,
                                    String $description, String $circle_Type, String $latitude,
                                    String $longitude, String $atch_Cnt, String $atch_Info, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("APN", $apn);
        map.put("User_ID", $userID);
        map.put("Class_ID", $classID);
        map.put("Description", $description);
        map.put("Circle_Type", $circle_Type);
        map.put("Latitude", $latitude);
        map.put("Longitude", $longitude);
        map.put("Atch_Cnt", $atch_Cnt);
        map.put("Atch_Info", $atch_Info);
        map.put("CheckKey", "");
        Log.i(TAG, "map:" + map);
        GetJson(id, "Baby_Set_Circle_New", map, $callBack);
    }

    //  UpToken產生
    public static void GetUpToken(String id, String $bucketName, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("bucketName", $bucketName);
        map.put("key", "");
        map.put("CheckKey", "");
        GetStr(id, "Baby_Get_UpToken", map, $callBack);
    }

    /*========  頻道 News  ========*/
    //  取得頻道列表
    public static void GetNews(String id, String $userID, String $userType,String $pageIndex,String $pageSize, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("User_ID", $userID);
        map.put("User_Type", $userType);
        map.put("Page_Index", $pageIndex);
        map.put("Page_Size", $pageSize);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Get_Channel_List", map, $callBack);
    }

    //  取得喜歡收藏的資訊
    public static void GetChannelFavGood(String $webName, String id, String $userID, String $chanlID, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("User_ID", $userID);
        map.put("Channel_ID", $chanlID);
        map.put("CheckKey", "");
        GetJson(id, $webName, map, $callBack);
    }
    //  設定喜歡收藏的資訊
    public static void SetChannelFavGood(String $webName, String id, String $userID, String $chanlID, String $type, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("User_ID", $userID);
        map.put("Channel_ID", $chanlID);
        map.put("Type", $type);
        map.put("CheckKey", "");
        GetJson(id, $webName, map, $callBack);
    }


    /*========  我 Profile (設置)  ========*/
    //  查詢家長所有之學生清單
    public static void GetParentChilds(String id, String $parent_ID, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Parent_ID", $parent_ID);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Get_Parent_Childs", map, $callBack);
    }

    //  查詢教師教學清單
    public static void GetTeacherTeaching(String id, String $teacher_ID, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Teacher_ID", $teacher_ID);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Get_Teacher_Teaching", map, $callBack);
    }

    //  查詢學生歷史清單
    public static void GetStudentHistory(String id, String $school_ID, String $student_ID, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("School_ID", $school_ID);
        map.put("Student_ID", $student_ID);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Get_Student_History", map, $callBack);
    }

    //  我的频道收藏
    public static void GetFavoriteList(String id, String $user_ID, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("User_ID", $user_ID);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Get_Favorite_List", map, $callBack);
    }

    //  修改暱稱
    public static void SetChangeNick(String id, String $user_ID, String $mobile_No, String $nick_Name, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("User_ID", $user_ID);
        map.put("Mobile_No", $mobile_No);
        map.put("Nick_Name", $nick_Name);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Set_Change_Nick", map, $callBack);
    }

    //  修改頭像
    public static void SetChangeAvatar(String id, String $user_ID, String $user_Avatar, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("User_ID", $user_ID);
        map.put("User_Avatar", $user_Avatar);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Set_Change_Avatar", map, $callBack);
    }

    /*========  共用 Common  ========*/

    //  短信接口
    public static void SendMessage(String id, String $mobile_No, String $message, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Mobile_No", $mobile_No);
        map.put("Message", $message);
        map.put("CheckKey", "");
        GetStr(id, "Baby_Send_Message", map, $callBack);
    }

    //  查詢家長與學生關係代碼
    public static void GetParentRelation(String id, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("CheckKey", "");
        GetJson(id, "Baby_Get_Parent_Relation", map, $callBack);
    }

    /**
     * 處理Json
     *
     * @param id
     * @param MethodName
     * @param map
     * @param callBack
     */
    public static void GetJson(final String id, final String MethodName, final HashMap<String, String> map, final WebCallback callBack) {
        new Thread(new Runnable() {
            public void run() {
                String jsonStr = WebServiceFunc(MethodName, map);
//				System.out.println(">>>>>>>>>>>>>>>jsonStr:" + jsonStr);
                Object jsonValue = null;
                try {
                    JSONObject JsonObj = new JSONObject(jsonStr);

                    String error = JsonObj.optJSONObject("errors").optString(
                            "error");
                    if (error.equals("10200")) {//  访问数据库成功

                        JSONObject values = JsonObj.optJSONObject("values");
                        JSONArray valuesAry = JsonObj.optJSONArray("values");
//						System.out.println(">>>>>>>>>>>>>>>values:" + values);
                        /*如果是JsonObj模式*/
                        if (values != null) {
                            if (values.optJSONArray("value") != null) {
                                jsonValue = values.optJSONArray("value");
                            } else if (values.optJSONObject("value") != null) {
                                jsonValue = values.optJSONObject("value");
                            } else if (values.optString("value") != null) {
                                jsonValue = values.optString("value");
                            } else {
                                jsonValue = values.optInt("value");
                            }
                        /*如果是JsonAry模式*/
                        } else if (valuesAry != null) {
                            jsonValue = valuesAry;
                        }
                        //System.out.println(">>>>>>>>>>>>>>>JsonObj:" + jsonValue);
                    }else if (error.equals("10300")) {//    访问数据库成功-无数据返回
                        jsonValue = "Success! No Data Return!";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*非Json格式要自定義*/
                if (MethodName.equals("Get_EndTime") ||
                        MethodName.equals("Get_InternalPermissions") ||
                        MethodName.equals("Get_SchoolPermissions")
                        ) {
                    jsonValue = jsonStr;
                }
                final Object Rs = jsonValue;
                handler.post(new Runnable() {
                    public void run() {
                        callBack.CompleteCallback(id, Rs);
                    }
                });
            }
        }).start();
    }

    public static void GetStr(final String id, final String MethodName, final HashMap<String, String> map, final WebCallback callBack) {
        new Thread(new Runnable() {
            public void run() {
                String jsonStr = WebServiceFunc(MethodName, map);
                final Object Rs = jsonStr;
                handler.post(new Runnable() {
                    public void run() {
                        callBack.CompleteCallback(id, Rs);
                    }
                });
            }
        }).start();
    }

    private static String WebServiceFunc(String MethodName, HashMap<String, String> map) {
        String result = null;
        String NameSpace = "http://tempuri.org/";
        String u = UserMstr.gwSvr_Url;
        String webService = "/appService.asmx";
        String url = u + webService;
        String soapAction = NameSpace + MethodName;
        //Log.v("zyo", "WebService url : " + url);
        int RunCnt = 0;

        while (RunCnt <= 5) {
            RunCnt++;

            try {
                SoapObject request = new SoapObject(NameSpace, MethodName);// NameSpace
                if (map != null) {
                    Iterator<Map.Entry<String, String>> iter = map.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
                        request.addProperty(entry.getKey(), entry.getValue());
                    }
                }

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);

                envelope.dotNet = true;//

                envelope.setOutputSoapObject(request);

                HttpTransportSE ht = new HttpTransportSE(url);
                ht.call(soapAction, envelope);//
                if (envelope.getResponse() != null) {
                    result = envelope.getResponse().toString();
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                result = null;
            }
        }
        return result;
    }

    public interface WebCallback {
        public void CompleteCallback(String id, Object obj);
    }

    //  確認網路連線-获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
    public static boolean isConnected(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        MyAlertDialog.Show(context, "当前网络不可用，请设置后重试！");
        return false;
    }
}
