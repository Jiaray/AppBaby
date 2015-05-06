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
    public final static Handler handler = new Handler();

    /**
     * 登入
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
        //Log.v("zyo","WebService_Login : "+id+","+user+","+pswd+","+d_type+","+d_ver+","+d_token+","+p1+","+p2);
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
     * 頻道
     * @param id
     * @param $userID
     * @param $userType
     * @param $callBack
     */
    public static void GetNews(String id, String $userID, String $userType, WebCallback $callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("User_ID", $userID);
        map.put("User_Type", $userType);
        map.put("CheckKey", "");
        GetJson(id, "Baby_Get_Channel_List", map, $callBack);
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
                        System.out.println(">>>>>>>>>>>>>>>JsonObj:" + jsonValue);
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
