package lazylist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.app.AppBabySH.R;
import com.app.Common.AsyncImageLoader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {

    MemoryCache memoryCache = new MemoryCache();
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    Handler handler = new Handler();//handler to display images in UI thread
    public int setImagWidth = 0;
    public int setImagHeight = 0;
    public int compreeNum = 20;
    public boolean webNotCompress = false;
    AsyncImageLoader asyncImageLoader;

    private static ImageLoader imageLoader; // 本类的引用
    public static ImageLoader getInstance() {
        if (null == imageLoader) {
            imageLoader = new ImageLoader();
        }
        return imageLoader;
    }

    public ImageLoader() {
        executorService = Executors.newFixedThreadPool(5);
    }

    final int stub_id = R.drawable.doroto_loadimag;

    public Bitmap DisplayImage(String url, ImageView imageView) {
        String geturltitle = url.substring(0, 4);
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (geturltitle.equals("http") || geturltitle.equals("Http")) {
            if (bitmap != null)
                imageView.setImageBitmap(bitmap);
            else {
                queuePhoto(url, imageView);
                imageView.setImageResource(stub_id);
            }
        } else {
            if (bitmap != null)
                imageView.setImageBitmap(bitmap);
            else {
                //System.out.println("zyo------bitmap in sdcard");
//            	bitmap= getimage(url);
//            	memoryCache.put(url, bitmap);
//            	imageView.setImageBitmap(bitmap);
                queuePhoto(url, imageView);
//                imageView.setImageResource(stub_id);
            }

        }

        return bitmap;
    }


    public void DisplayRoundedCornerImage(String url, ImageView imageView) {
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null)
            imageView.setImageBitmap(getRoundedCornerBitmap(bitmap, 300));
        else {
            PhotoToLoad p = new PhotoToLoad(url, imageView);
            p.roundedCorner = true;
            executorService.submit(new PhotosLoader(p));
            imageView.setImageResource(stub_id);
        }
    }

    /**
     * 取得圖片檔案 (壓縮過後)
     *
     * @param url        檔案位置
     * @param imgwidth   圖片寬
     * @param imgheight  圖片高
     * @param compreenum 壓縮品質
     * @return
     */
    public Bitmap DisPlayImageFile(String url, int imgwidth, int imgheight, int compreenum) {
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap == null) {

            setImagWidth = imgwidth;
            setImagHeight = imgheight;
            compreeNum = compreenum;
            bitmap = getimage(url);
        }

        return bitmap;
    }


    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    private Bitmap getBitmap(String url) {
        File f = FileCache.getInstance().getFile(url);
        //from SD cache
        Bitmap b = decodeFile(f);
        //System.out.println("-----zyo "+b);
        if (b != null)
            return b;
        String geturltitle = url.substring(0, 4);
        if (geturltitle.equals("http") || geturltitle.equals("Http")) {
            //from web
            try {
                Bitmap bitmap = null;
                URL imageUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);
                conn.setInstanceFollowRedirects(true);
                InputStream is = conn.getInputStream();
                OutputStream os = new FileOutputStream(f);
                Utils.CopyStream(is, os);
                os.close();
                conn.disconnect();
                bitmap = decodeFile(f);
                return bitmap;
            } catch (Throwable ex) {
                ex.printStackTrace();
                if (ex instanceof OutOfMemoryError)
                    memoryCache.clear();
                return null;
            }
        } else {
            Bitmap bitmap = getimage(url);
            return bitmap;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();

            //Find the correct scale value. It should be the power of 2.
            int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            if (webNotCompress) {
                REQUIRED_SIZE = 40;
                width_tmp /= 16;
                height_tmp /= 16;
            }

            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                //System.out.println("-----zyo width_tmp:"+width_tmp+" REQUIRED_SIZE:"+REQUIRED_SIZE);
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }


            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;
        public boolean roundedCorner;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
            roundedCorner = false;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            try {
                if (imageViewReused(photoToLoad)) return;
                Bitmap bmp = getBitmap(photoToLoad.url);
                memoryCache.put(photoToLoad.url, bmp);
                if (imageViewReused(photoToLoad)) return;
                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url)) return true;
        return false;
    }

    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad)) return;
            if (bitmap != null) {
                if (photoToLoad.roundedCorner) {
                    photoToLoad.imageView.setImageBitmap(getRoundedCornerBitmap(bitmap, 300));
                } else {
                    photoToLoad.imageView.setImageBitmap(bitmap);
                }
            } else {
                photoToLoad.imageView.setImageResource(stub_id);
            }
        }
    }

    public void clearCache() {
        memoryCache.clear();
        FileCache.getInstance().clear();
    }


    /*比例縮放*/
    private Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为  
        float hh = 800f;//这里设置高度为800f  
        float ww = 480f;//这里设置宽度为480f  
        if (setImagWidth != 0) ww = setImagWidth;
        if (setImagHeight != 0) hh = setImagHeight;
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
        int be = 1;//be=1表示不缩放  
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放  
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放  
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例  
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩  
    }

    /*壓縮質量*/
    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, compreeNum, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10  
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    //圓角轉換函式，帶入Bitmap圖片及圓角數值則回傳圓角圖，回傳Bitmap再置入ImageView
    public static Bitmap getRoundedCornerBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if (bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sbmp = bmp;
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
                sbmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffa19774;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() / 2 + 0.7f,
                sbmp.getWidth() / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);
        return output;
    }



    /*載入網路圖片並存入緩存中*/
    public void DisplayWebUrlImage(String $url, ImageView $imageView) {
       // $imageView.setImageResource(stub_id);
        Bitmap bmpFromSD = FileCache.getInstance().getBmp($url);
        if (null != bmpFromSD) {
            $imageView.setImageBitmap(bmpFromSD);
        } else {
            asyncImageLoader = new AsyncImageLoader();
            Drawable cachedImage = asyncImageLoader.loaDrawable($url, new AsyncImageCallBack($url, $imageView));
            if (cachedImage != null) {
                Bitmap bitmap = drawToBmp(cachedImage);
                $imageView.setImageBitmap(bitmap);
            }
        }
    }

    class AsyncImageCallBack implements AsyncImageLoader.ImageCallBack {
        private String strUrl;
        private ImageView imgV;

        public AsyncImageCallBack(String $url, ImageView $imageView) {
            strUrl = $url;
            imgV = $imageView;
        }

        @Override
        public void imageLoaded(Drawable imageDrawable) {
            Bitmap bitmap = drawToBmp(imageDrawable);
            FileCache.getInstance().savaBmpData(strUrl, bitmap);// 先缓存起来
            imgV.setImageBitmap(bitmap);
            imgV.setScaleType(ImageView.ScaleType.MATRIX);
        }
    }


    /**
     * Drawable转换成Bitmap
     *
     * @param d
     * @return
     */
    public Bitmap drawToBmp(Drawable d) {
        if (null != d) {
            BitmapDrawable bd = (BitmapDrawable) d;
            return bd.getBitmap();
        }
        return null;
    }

}
