package com.app.Common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.zip.CRC32;

@SuppressLint("SimpleDateFormat")
public class ComFun {
    public static String byte2HexString(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String stmp = Integer.toHexString(b[i] & 0xff);
            if (stmp.length() == 1)
                sb.append("0" + stmp);
            else
                sb.append(stmp);
        }
        return sb.toString();
    }

    public static byte[] String2Byte(String hexString) {
        if (hexString.length() % 2 == 1)
            return null;
        byte[] ret = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            ret[i / 2] = Integer.decode("0x" + hexString.substring(i, i + 2))
                    .byteValue();
        }
        return ret;
    }

    public static String ObjectToString(Object obj) {
        if (obj != null) {
            return obj.toString().trim();
        }
        return "";
    }

    public static String Md5(String str, boolean chk) {

        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");

                buf.append(Integer.toHexString(i));
            }
            if (chk) {
                result = buf.toString().substring(8, 24);// 16bit
            } else {
                result = buf.toString();// 32bit
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result.toUpperCase();
    }

    public static int StringToInt(String str) {
        int num = 0;
        if (str == null) {
            return num;
        }

        if (ObjectToString(str).equals("")) {
            return num;
        }
        try {
            num = (int) Double.parseDouble(str);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return num;
    }

    public static double StringToDouble(String str) {
        double num = 0;
        if (str == null) {
            return num;
        }

        if (ObjectToString(str).equals("")) {
            return num;
        }
        try {
            num = Double.parseDouble(str);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return num;
    }

    public static String DspDate(String date) {
        return DspDate(date, "yyyyMMdd", "yyyy-MM-dd");
    }

    public static String DspDate(Date date, String outFormat) {
        SimpleDateFormat out = new SimpleDateFormat(outFormat, Locale.CHINA);
        return out.format(date);
    }

    public static String DspDate(String date, String inFormat, String outFormat) {

        try {
            Date wInDate = null;
            SimpleDateFormat in = new SimpleDateFormat(inFormat, Locale.CHINA);
            SimpleDateFormat out = new SimpleDateFormat(outFormat, Locale.CHINA);
            wInDate = in.parse(date);
            return out.format(wInDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 取得當前日期  yyyyMMdd
     *
     * @return
     */
    public static String GetNowDate() {
        String nowDate = "";
        // 先行定義時間格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        // 取得現在時間
        Date dt = new Date();
        // 透過SimpleDateFormat的format方法將Date轉為字串
        nowDate = sdf.format(dt);
        return nowDate;
    }

    /**
     * 取得當前時間  HHmmss
     *
     * @return
     */
    public static String GetNowTime(String type) {
        String nowDate = "";
        // 先行定義時間格式
        SimpleDateFormat sdf = new SimpleDateFormat(type);
        // 取得現在時間
        Date dt = new Date();
        // 透過SimpleDateFormat的format方法將Date轉為字串
        nowDate = sdf.format(dt);
        return nowDate;
    }

    /**
     * 取得加減後的時間(天數)
     *
     * @param _subNum (加減的天數)
     * @return
     */
    public static String GetSubDate(int _subNum) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date dt = new Date();
        //新增一個Calendar,並且指定時間
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        calendar.add(Calendar.DAY_OF_MONTH, _subNum);
        Date tdt = calendar.getTime();//取得加減過後的Date
        //依照設定格式取得字串
        String beforeTime = sdf.format(tdt);
        return beforeTime;
    }

    /**
     * 獲得該月天數
     *
     * @param date
     * @return
     */
    public static int GetDayOfMonthCount(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date dt = new Date();
        int endday = 0;
        try {
            dt = sdf.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dt);
            endday = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);//获得本月的天数
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return endday;
    }

    /**
     * 取得星期
     *
     * @param date
     * @return
     */
    public static String DspWeek(String date, String inFormat) {
        String Week = "";
        Calendar c = Calendar.getInstance();
        try {
            SimpleDateFormat in = new SimpleDateFormat(inFormat, Locale.CHINA);
            Date wInDate = in.parse(date);
            c.setTime(wInDate);
            //Log.v("Zyo", "ComFun- DspWeek:" + c.get(Calendar.DAY_OF_WEEK));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            Week += "週日";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 2) {
            Week += "週一";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 3) {
            Week += "週二";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 4) {
            Week += "週三";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 5) {
            Week += "週四";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 6) {
            Week += "週五";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 7) {
            Week += "週六";
        }
        return Week;
    }

    public static String DspMoney(String str) {
        DecimalFormat fmt = new DecimalFormat("##,###,###,###,###");
        double d;
        try {
            d = Double.parseDouble(str);
            return "￥" + fmt.format(d);
        } catch (Exception e) {
            e.printStackTrace();
            return "￥0";
        }
    }

    public static String DspNumber(String str) {
        DecimalFormat fmt = new DecimalFormat("##,###,###,###,###");
        double d;
        try {
            d = Double.parseDouble(str);
            return fmt.format(d);
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    public static int DspInt(Object obj) {
        int d;
        try {
            d = Integer.parseInt(obj.toString());
            return d;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String DspIntString(Object obj) {
        int d;
        try {
            d = Integer.parseInt(obj.toString());
            return String.valueOf(d);
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    /**
     * 判断是否有网络连接
     *
     * @param context
     * @return
     */
    public static boolean checkNetworkState(Context context) {
        // // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
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
        return false;
    }

    /**
     * 判断WIFI网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static long calculateCrc32(byte[] data) {
        CRC32 crc = new CRC32();
        crc.update(data);
        return crc.getValue();
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 取得當前日期
     * HL
     *
     * @return
     */
    public static String GetNowDate(String str) {
        String nowDate = "";
        // 先行定義時間格式
        SimpleDateFormat sdf = new SimpleDateFormat(str);
        // 取得現在時間
        Date dt = new Date();
        // 透過SimpleDateFormat的format方法將Date轉為字串
        nowDate = sdf.format(dt);
        return nowDate;
    }

    /**
     * 取得参数时间格式化
     * HL
     *
     * @return
     */
    public static String GetNowDate(String str, Date dt) {
        String nowDate = "";
        // 先行定義時間格式
        SimpleDateFormat sdf = new SimpleDateFormat(str);
        // 取得現在時間
        // 透過SimpleDateFormat的format方法將Date轉為字串
        nowDate = sdf.format(dt);
        return nowDate;
    }

    /**
     * 日期月份减一个月
     * HL
     *
     * @param datetime 日期(2014-11)
     * @return 2014-10
     */
    public static String dateFormat(String datetime, String format1, String format2, int monty) {
        SimpleDateFormat sdf = new SimpleDateFormat(format1);
        Date date = null;
        try {
            date = sdf.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        cl.add(Calendar.MONTH, monty);
        date = cl.getTime();
        return GetNowDate(format2, date);
    }

//
//    /**
//     * Drawable 转换Bitmap
//     * HL
//     *
//     * @param datetime 日期(2014-11)
//     * @return 2014-10
//     */
//    public static Bitmap drawableToBitamp(Drawable drawable) {
//        BitmapDrawable bd = (BitmapDrawable) drawable;
//        return bd.getBitmap();
//    }


    /**
     * 取得銀幕寬高
     *
     * @return
     */
    /*public static double[] getScreenPos(MainActivity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);//获得屏幕参数
        int screenWidth = dm.widthPixels;           //获得屏幕宽度  
        int screenHeight = dm.heightPixels;     //获得屏幕高度
        double[] aaa = {screenWidth, screenHeight};
        return aaa;
    }*/


}
