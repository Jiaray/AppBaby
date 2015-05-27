package lazylist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;

public class FileCache {
    final private String TAG = "FileCache";
    private static FileCache fileCache; // 本类的引用
    final private String FOLDERNAME = "AppBabySH";
    private String strCachePath, strImgBoxPath; // 图片保存的路径
    private String strJsonPath;// Json保存的路径
    private File cacheDir;
    private File imgBoxDir;
    private File jsonDir;

    public String getCachePath() {
        return strCachePath;
    }

    public String getImgBoxPath() {
        return strImgBoxPath;
    }

    private FileCache() {
        // this.context = context;
        String strPathHead = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            strPathHead = Environment.getExternalStorageDirectory().toString();
        } else {
            strPathHead = "/data/data/com.app.AppBabySH";
        }
        strCachePath = strPathHead + "/" + FOLDERNAME + "/cache/";
        cacheDir = new File(strCachePath);
        if (!cacheDir.exists())cacheDir.mkdirs();

        strImgBoxPath = strPathHead + "/" + FOLDERNAME + "/image/";
        imgBoxDir = new File(strImgBoxPath);
        if (!imgBoxDir.exists())imgBoxDir.mkdirs();

        strJsonPath = strPathHead + "/" + FOLDERNAME + "/json/";
        jsonDir = new File(strJsonPath);
        if (!jsonDir.exists())jsonDir.mkdirs();
    }

   /* public FileCache(Context context) {
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "LazyList");
        else
            cacheDir = context.getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
    }*/

    public File getFile(String url) {
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        System.out.println("zyo------FileCache: get file url - " + url + " url.hashCode:" + url.hashCode());
        String filename = String.valueOf(url.hashCode());
        //Another possible solution (thanks to grantland)
        // String filename = URLEncoder.encode(url);
        //System.out.println("zyo------FileCache: filename - " + filename);
        File f = new File(cacheDir, filename);
        System.out.println("zyo------FileCache: get file - " + f);
        return f;
    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }


    public static FileCache getInstance() {
        if (null == fileCache) {
            fileCache = new FileCache();
        }
        return fileCache;
    }

    public boolean saveData(String strApiUrl, String dataJson, String imgurl, Bitmap bmp) {
        String fileName = this.toHexString(strApiUrl);
        String imgName = imgurl.substring(
                imgurl.lastIndexOf('/') + 2,
                imgurl.length());
        File fTXT = new File(strJsonPath + fileName + ".txt");
        File fImg = new File(strCachePath + imgName);
        this.writeToFile(dataJson, fTXT);
        this.writeToFile(bmp, fImg);
        return true;
    }

    /**
     * 保存json数据
     */
    public boolean savaJsonData(String strApiUrl, String dataJson) {
        String fileName = this.toHexString(strApiUrl);
        File fTXT = new File(strJsonPath + fileName + ".txt");
        if (fTXT.exists()) {
            fTXT.delete();
        }
        this.writeToFile(dataJson, fTXT);
        return true;
    }

    // 用图片的URL来命名图片，并保存图片
    public boolean savaBmpData(String imgurl, Bitmap bmp) {
        String imgName = imgurl.substring(
                imgurl.lastIndexOf('/') + 1,
                imgurl.length());
        File fImg = new File(strCachePath + imgName);
        if (fImg.exists()) {
            fImg.delete();
        }
        this.writeToFile(bmp, fImg);
        return true;
    }

    public boolean savaBmpData1(String imgurl, Bitmap bmp) {
        String imgName = imgurl.substring(
                imgurl.lastIndexOf('/') + 1,
                imgurl.length());
        File fImg = new File(strImgBoxPath + imgName);
        if (fImg.exists()) {
            fImg.delete();
        }
        this.writeToFile(bmp, fImg);
        return true;
    }

    // 自己给图片命名并保存图片
    public boolean saveBmpDataByName(String bmpName, Bitmap bmp) {
        File fImg = new File(strCachePath + bmpName);
        if (fImg.exists()) {
            fImg.delete();
        }
        this.writeToFile(bmp, fImg);
        return true;
    }

    /**
     * 参数fileName需与 fileName和saveData()方法中的fileName参数一致时，才能读出与保存时一致的数据
     */
    public String getJson(String strApiUrl) {
        String fileName = this.toHexString(strApiUrl);
        File file = new File(strJsonPath + fileName + ".txt");
        StringBuffer sb = new StringBuffer();
        if (file.exists()) {
            Reader reader = null;
            try {
                reader = new java.io.FileReader(file);
                BufferedReader br = new BufferedReader(reader);
                String str;
                while (null != (str = br.readLine())) {
                    sb.append(str);
                }
                return sb.toString();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 传入图片的URL地址，来获得Bitmap
     */
    public Bitmap getBmp(String imgurl) {
        //Log.v(TAG, "getBmp : imgurl:" + imgurl);
        String imgName = imgurl.substring(
                imgurl.lastIndexOf('/') + 1,
                imgurl.length());

        File imgFile = new File(strCachePath + imgName);
        if (imgFile.exists()) {
            FileInputStream fis;
            try {
                //Log.v(TAG, "getBmp : imgFile.exists");
                fis = new FileInputStream(imgFile);
                //Log.v(TAG, "getBmp : imgFile : " + fis);
                return BitmapFactory.decodeStream(fis);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                Log.v(TAG, "getBmp : FileNotFoundException : " + e);
                e.printStackTrace();
            }
        } else
            Log.v("提醒", "要请求的图片文件不存在");
        return null;
    }

    // 通过图片名字来获得图片
    public Bitmap getBmpByName(String bmpName) {
        File imgFile = new File(strCachePath + bmpName);
        if (imgFile.exists()) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(imgFile);
                return BitmapFactory.decodeStream(fis);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Log.v("提醒", "要请求的图片文件不存在");
        }
        return null;
    }

    /**
     * 传入图片的URL来获得图片文件
     */
    public File getImgFile(String imgurl) {
        String imgName = imgurl.substring(
                imgurl.lastIndexOf('/') + 2,
                imgurl.length());
        File imgFile = new File(strCachePath + imgName);
        return imgFile;
    }

    private boolean writeToFile(String strData, File file) {
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(strData.getBytes());
            bos.flush();
            bos.close();
            return true;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private boolean writeToFile(Bitmap bmp, File file) {
        if (file.exists()) {
            file.delete();
        }
        String name = file.getName();
        String geShi = name.substring(name.lastIndexOf('.'), name.length());

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            if (null != bmp) {
                if (".JPEG".equalsIgnoreCase(geShi)
                        || ".JPG".equalsIgnoreCase(geShi)) {
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    bos.flush();
                    bos.close();
                } else if (".PNG".equalsIgnoreCase(geShi)) {
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    bos.flush();
                    bos.close();
                }
                return true;
            } else
                bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.v("错误", "图片写入缓存文件错误");
                }
            }
        }
        return false;
    }

    public boolean clearImgByImgUrl(String imgurl) {
        File imgFile = this.getImgFile(imgurl);
        if (imgFile.exists()) {
            imgFile.delete();
            return true;
        }
        return false;
    }

    /**
     * 根据路径删除图片
     *
     * @param path
     */
    public void deleteTempFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 删除SD卡上的全部缓存
     */
    public int clearAllData() {
        File imgDir = new File(strCachePath);
        File txtDir = new File(strJsonPath);
        File[] imgFiles = imgDir.listFiles();
        File[] txtFiles = txtDir.listFiles();
        int m = imgFiles.length;
        int x = txtFiles.length;

        int g = 0;
        int t = 0;
        for (int i = 0; i < m; i++) {
            if (imgFiles[i].exists()) {
                if (imgFiles[i].delete())
                    g++;
            } else
                g++;

        }
        for (int i = 0; i < x; i++) {
            if (txtFiles[i].exists()) {
                if (txtFiles[i].delete()) {
                    t++;
                }
            } else
                t++;
        }
        if (g == m && t == x) {
            return 1;
        }
        return 0;
    }
     //  0x表示十六进制

    private String toHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return "0x" + str;// 0x表示十六进制
    }

    // 转换十六进制编码为字符串
    private String toStringHex(String s) {
        if ("0x".equals(s.substring(0, 2))) {
            s = s.substring(2);
        }
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "utf-8");// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;

       }
    //  複製檔案
    public boolean copyFile(File source, File dest) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(source));
            bos = new BufferedOutputStream(new FileOutputStream(dest, false));

            byte[] buf = new byte[1024];
            bis.read(buf);

            do {
                bos.write(buf);
            } while(bis.read(buf) != -1);
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                return false;
            }
        }

        return true;
    }

    // 移動檔案 WARNING ! Inefficient if source and dest are on the same filesystem !
    public boolean moveFile(File source, File dest) {
        return copyFile(source, dest) && source.delete();
    }
}