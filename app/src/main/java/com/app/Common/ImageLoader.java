package com.app.Common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.app.AppBabySH.R;

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
    final private String TAG = "IMGLoader";
    final int stub_id = R.drawable.doroto_loadimag;
    final int stub_id2 = R.drawable.user_default_avatar;
    final int THUMBIMG_SIZE = 200;
    final int VIEWIMG_SIZE = 400;
    MemoryCache memoryCache = new MemoryCache();
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    private ExecutorService executorService;
    private Handler handler = new Handler();//handler to display images in UI thread
    private AsyncImageLoader asyncImageLoader;

    private static ImageLoader imageLoader; // 本类的引用

    public static ImageLoader getInstance() {
        if (null == imageLoader) {
            imageLoader = new ImageLoader();
        }
        return imageLoader;
    }

    public ImageLoader() {
        executorService = Executors.newFixedThreadPool(3);
    }

    //  圖像載入
    public void DisplayImage(String url, ImageView imageView) {
        if (url.equals("")) {
            Log.i(TAG, "DisplayImage url is empty!");
            return;
        }
        String geturltitle = url.substring(0, 4);
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);

        if (geturltitle.equals("http") || geturltitle.equals("Http")) {
            if (bitmap != null) {
                redressPicRotate(FileCache.getInstance().getFile(url).getAbsolutePath(), imageView, bitmap);
                //imageView.setImageBitmap(bitmap);
            } else {
                PhotoToLoad p = new PhotoToLoad(url, imageView);
                executorService.submit(new PhotosLoader(p));
                imageView.setImageResource(stub_id);
            }
        } else {
            if (bitmap != null) {
                redressPicRotate(FileCache.getInstance().getFile(url).getAbsolutePath(), imageView, bitmap);
                //imageView.setImageBitmap(bitmap);
            } else {
                PhotoToLoad p = new PhotoToLoad(url, imageView);
                executorService.submit(new PhotosLoader(p));
            }
        }
    }

    //  圖像載入後顯示圓角
    public void DisplayRoundedCornerImage(String url, ImageView imageView) {
        if (url.equals("")) {
            Log.i(TAG, "DisplayRoundedCornerImage url is empty!");
            return;
        }
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            redressPicRotate(FileCache.getInstance().getFile(url).getAbsolutePath(), imageView, toRoundBitmap(bitmap));
            //imageView.setImageBitmap(toRoundBitmap(bitmap));
        } else {
            PhotoToLoad p = new PhotoToLoad(url, imageView);
            p.roundedCorner = true;
            executorService.submit(new PhotosLoader(p));
        }
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

    //
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            //Log.i(TAG, "PhotosLoader");
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
                    redressPicRotate(FileCache.getInstance().getFile(photoToLoad.url).getAbsolutePath(), photoToLoad.imageView, toRoundBitmap(bitmap));
                } else {
                    redressPicRotate(FileCache.getInstance().getFile(photoToLoad.url).getAbsolutePath(), photoToLoad.imageView, bitmap);
                }
            } else {
                if (photoToLoad.roundedCorner) {
                    photoToLoad.imageView.setImageResource(stub_id2);
                } else {
                    photoToLoad.imageView.setImageResource(stub_id);
                }

            }
        }
    }

    //  取得圖像 Bitmap
    private Bitmap getBitmap(String url) {
        File f = FileCache.getInstance().getFile(url);
        //from SD cache
        Bitmap b = compressionImgFile(f, THUMBIMG_SIZE);
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
                bitmap = compressionImgFile(f, THUMBIMG_SIZE);
                return bitmap;
            } catch (Throwable ex) {
                ex.printStackTrace();
                if (ex instanceof OutOfMemoryError)
                    memoryCache.clear();
                return null;
            }
        } else {
            Bitmap bitmap = compressionImgFile(new File(url), THUMBIMG_SIZE);
            return bitmap;
        }
    }

    //解碼圖像和縮放它來減少內存消耗(decodes image and scales it to reduce memory consumption)
    private Bitmap compressionImgFile(File f, int required_size) {
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();

            //Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;

            while (true) {
                if (width_tmp / 2 < required_size || height_tmp / 2 < required_size)
                    break;
                //System.out.println("-----zyo width_tmp:"+width_tmp+" required_size:"+required_size);
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

    //  載入網路圖片並存入緩存資料夾中
    public void DisplayWebUrlImage(String $url, ImageView $imageView) {
        if ($url.equals("")) {
            Log.i(TAG, "DisplayWebUrlImage url is empty!");
            return;
        }
        File fileFromSD = FileCache.getInstance().getImgFile($url);
        Bitmap bmpFromSD;
        if (null != fileFromSD) {
            bmpFromSD = compressionImgFile(fileFromSD, VIEWIMG_SIZE);
            //$imageView.setImageBitmap(bmpFromSD);
            redressPicRotate(fileFromSD.getAbsolutePath(),$imageView,bmpFromSD);
        } else {
            asyncImageLoader = new AsyncImageLoader();
            Drawable cachedImage = asyncImageLoader.loaDrawable($url, new AsyncImageCallBack($url, $imageView));
            if (cachedImage != null) {
                fileFromSD = FileCache.getInstance().getImgFile($url);
                bmpFromSD = compressionImgFile(fileFromSD, VIEWIMG_SIZE);
                redressPicRotate(fileFromSD.getAbsolutePath(),$imageView,bmpFromSD);
                //$imageView.setImageBitmap(bmpFromSD);
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
            Bitmap bitmap = null;
            if (imageDrawable != null) {
                BitmapDrawable bd = (BitmapDrawable) imageDrawable;
                bitmap = bd.getBitmap();
            }
            FileCache.getInstance().savaBmpData(strUrl, bitmap);// 先缓存起来
            imgV.setImageBitmap(bitmap);
            imgV.setScaleType(ImageView.ScaleType.MATRIX);
        }
    }

    //  清除緩存
    public void clearCache() {
        memoryCache.clear();
    }


    private static final int STROKE_WIDTH = 4;

    //  圓角轉換函式，帶入Bitmap圖片及圓角數值則回傳圓角圖，回傳Bitmap再置入ImageView
    public static Bitmap toRoundBitmap(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            left = 0;
            bottom = width;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(4);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bmp, src, dst, paint);

        //画白色圆圈
        paint.reset();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setAntiAlias(true);
        canvas.drawCircle(width / 2, width / 2, width / 2 - STROKE_WIDTH / 2, paint);
        return output;
    }

    //  將來源檔案壓縮後放置目標位置
    public void AChangeSmallSizeToB(String $oriPath, String $targetPath) {
        try {
            Bitmap bm = ImageLoader.getInstance().getSmallBitmap($oriPath);
            FileOutputStream fos = new FileOutputStream(new File($targetPath));
            bm.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        } catch (Exception e) {
            Log.e(TAG, "ASmallSizeFromB Error : ", e);
        }
    }

    //  根據路徑獲得圖檔，將其壓縮後返回 bitmap
    private Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 960, 1600);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    //  計算圖片的縮放值
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    // 旋轉圖片
    public void redressPicRotate(String $url, ImageView $imgV, Bitmap $bmp) {
        int degree = readPictureDegree($url);
        if (degree <= 0) {
            $imgV.setImageBitmap($bmp);
        } else {
            Matrix matrix = new Matrix();//创建操作图片是用的matrix对象
            matrix.postRotate(degree);//旋转图片动作
            Bitmap resizedBitmap = Bitmap.createBitmap($bmp, 0, 0, $bmp.getWidth(), $bmp.getHeight(), matrix, true);//创建新图片
            $imgV.setImageBitmap(resizedBitmap);
        }
    }

    /**
     * 读取照片exif信息中的旋转角度
     * @param path 照片路径
     * @return角度
     */
    public int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
}
