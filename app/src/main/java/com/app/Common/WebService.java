package com.app.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.os.Handler;
import android.util.Log;

public class WebService {
    private static final String TAG = "WebService";
    public final static Handler handler = new Handler();

    /**
     * 登入
     *
     * @param id
     * @param user
     * @param pswd
     * @param d_type
     * @param d_ver
     * @param d_token
     * @param p1
     * @param p2
     * @param callBack
     */
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

    /**
     * 取得使用者詳細資訊
     *
     * @param id
     * @param $userName
     * @param $userID
     * @param callBack
     */
    public static void GetUserInfo(String id, String $userName, String $userID, WebCallback callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("User_Name", $userName);
        map.put("User_ID", $userID);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Get_User_Info ", map, callBack);
    }


    /*========  班級圈 Moments  ========*/
    //  取得班級圈列表-依班級
    public static void GetCircleListClass(String id, String $userID, String $classID, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("User_ID", $userID);
        map.put("Class_ID", $classID);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Get_Circle_List_Class", map, $callBack);
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
    public static void SetCircleGood(String id, String $circleID, String $userID, WebCallback $callBack) {
        Log.v(TAG, "$circleID:" + $circleID + " $userID:" + $userID);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Circle_ID", $circleID);
        map.put("User_ID", $userID);
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

    //  班級圈收藏到成長檔案
    public static void SetCircleKeepToGrow(String id, String $circleID, String $userID, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Circle_ID", $circleID);
        map.put("User_ID", $userID);
        map.put("Grow_From", "CIRCLE");
        map.put("CheckKey", "");
        GetJson(id, "Baby_Set_Circle_KeepTo_Grow", map, $callBack);
    }

    //  班級圈回覆
    public static void SetCircleReply(String id, String $circleID, String $userID, String $replydesc, String $atreplysn, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Circle_ID", $circleID);
        map.put("User_ID", $userID);
        map.put("Reply_Desc", $replydesc);
        map.put("AT_Reply_SN", $atreplysn);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Set_Circle_Reply", map, $callBack);
    }


    /*========  頻道 News  ========*/
    //  取得頻道列表
    public static void GetNews(String id, String $userID, String $userType, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("User_ID", $userID);
        map.put("User_Type", $userType);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Get_Channel_List", map, $callBack);
    }

    //  取得或設定喜歡收藏的資訊
    public static void GetSetChannelFavGood(String $webName, String id, String $userID, String $chanlID, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("User_ID", $userID);
        map.put("Channel_ID", $chanlID);
        map.put("CheckKey", "");
        GetJson(id, $webName, map, $callBack);
    }







    /*========  共用 Common  ========*/

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
                    if (error.equals("10200")) {

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


    /*public static void ExeSql(final String id, final String Sql,
                              final WebCallback callBack) {
        new Thread(new Runnable() {
            public void run() {
                int wExeRows = 0;
                // System.out.println(">>>>>>>>>>>>>>>" +1);
                String str = WebServiceFunc("ExecuteString", Sql);
                // System.out.println(">>>>>>>>>>>>>>>" + str);
                try {
                    JSONObject JsonObj = new JSONObject(str);
                    // System.out.println(">>>>>>>>>>>>>>>" + JsonObj);
                    String error = JsonObj.optJSONObject("errors").optString(
                            "error");
                    if (error.equals("10200")) {
                        wExeRows = JsonObj.optJSONObject("values").optInt(
                                "value");
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                final Object Rs = wExeRows;
                handler.post(new Runnable() {
                    public void run() {
                        callBack.CompleteCallback(id, Rs);
                    }
                });
            }
        }).start();
    }*/

    /*public static void GetJsonArray(final String id, final String Sql,
                                    final WebCallback callBack) {

        new Thread(new Runnable() {
            public void run() {
                ArrayList<String> bLst = new ArrayList<String>();
                String str = WebServiceFunc("SelectList", Sql);
                try {
                    JSONObject JsonObj = new JSONObject(str);
                    String error = JsonObj.optJSONObject("errors").optString(
                            "error");
                    if (error.equals("10200")) {

                        JSONArray values = JsonObj.optJSONObject("values")
                                .optJSONArray("value");
                        for (int i = 0; i < values.length(); i++) {
                            bLst.add(values.getString(i));
                        }
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                final Object Rs = bLst;
                handler.post(new Runnable() {
                    public void run() {
                        callBack.CompleteCallback(id, Rs);
                    }
                });
            }
        }).start();
        ;
    }*/

    /*public static void GetJsonTable(final String id, final String Sql,
                                    final WebCallback callBack) {

        new Thread(new Runnable() {
            public void run() {
                ArrayList<HashMap<String, String>> bLst = new ArrayList<HashMap<String, String>>();
                String str = WebServiceFunc("SelectDataTable", Sql);
                try {
                    JSONObject JsonObj = new JSONObject(str);
                    String error = JsonObj.optJSONObject("errors").optString(
                            "error");
                    if (error.equals("10200")) {
                        JSONArray values = JsonObj.optJSONObject("values")
                                .optJSONArray("value");
                        for (int rowCnt = 0; rowCnt < values.length(); rowCnt++) {
                            HashMap<String, String> RowData = new HashMap<String, String>();

                            JSONObject rowObj = values.getJSONObject(rowCnt);
                            Iterator keys = rowObj.keys();
                            while (keys.hasNext()) {
                                String key = (String) keys.next();
                                RowData.put(key, rowObj.getString(key));
                            }
                            bLst.add(RowData);
                        }
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    // e.printStackTrace();
                }
                final Object Rs = bLst;
//				  System.out.println("<<<<<<<<<<<<<<<<<" + Sql);
//				  System.out.println("<<<<<<<<<<<<<<<<<" + Rs);
                handler.post(new Runnable() {
                    public void run() {
                        // System.out.println("<<<<<<<<<<<<<<<<<");
                        callBack.CompleteCallback(id, Rs);
                    }
                });
            }
        }).start();
        ;
    }*/

    /*public static void GetJsonString(final String id, final String Sql,
                                     final WebCallback callBack) {

        new Thread(new Runnable() {
            public void run() {
                String bStr = null;
                String str = WebServiceFunc("SelectString", Sql);
                try {
                    JSONObject JsonObj = new JSONObject(str);
                    String error = JsonObj.optJSONObject("errors").optString(
                            "error");
                    if (error.equals("10200")) {

                        bStr = JsonObj.optJSONObject("values").optString(
                                "value");

                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                final Object Rs = bStr;
                handler.post(new Runnable() {
                    public void run() {
                        callBack.CompleteCallback(id, Rs);
                    }
                });
            }
        });
    }*/







    /*private static String WebServiceFunc(String MethodName, String Sql) {
        String result = null;

        String NameSpace = "http://tempuri.org/";
        String u = UserMstr.gwSvr_Url;

        String webService = "/appservice.asmx";
        String url = u + webService;
        String soapAction = NameSpace + MethodName;

        int RunCnt = 0;

        while (RunCnt <= 5) {
            RunCnt++;

            try {
                SoapObject request = new SoapObject(NameSpace, MethodName);// NameSpace
//				String user = UserMstr.userData.getUserID();

                String user = "";

                if (UserMstr.userData == null) {
                    user = "82215008";
                } else {
                    user = UserMstr.userData.getUserID();
                }
                if (user == null || user == "") {
                    user = "82215008";
                }

                String appKey = ComFun.Md5("Zyo" + user + "Soft", false);
                String Group = "Android";
                request.addProperty("appkey", appKey);
                request.addProperty("user", user);
                request.addProperty("sql", Sql);
                request.addProperty("group", Group);
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
    }*/

    public interface WebCallback {
        public void CompleteCallback(String id, Object obj);
    }
}
