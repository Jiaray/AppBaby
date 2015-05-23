package com.app.Common;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import lazylist.FileCache;

public class AsyncImageLoader {

	public Map<String, SoftReference<Drawable>> imageCache;

	public AsyncImageLoader() {
		imageCache = new HashMap<String, SoftReference<Drawable>>();
	}

	public Drawable loaDrawable(final String imageUrl, final ImageCallBack imageCallBack) {
		Bitmap bmpFromSD = FileCache.getInstance().getBmp(imageUrl);
		if (null != bmpFromSD) {
			return new BitmapDrawable(bmpFromSD);
		}
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if (null != drawable) {
				return drawable;
			}
		}

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				imageCallBack.imageLoaded((Drawable) message.obj);
			}
		};

		new Thread() {
			@Override
			public void run() {
				Drawable drawable = loadImageFromUrl(imageUrl);
				imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
			}
		}.start();
		return null;
	}
	/**
	 * 下??片
	 * @param url
	 * @return
	 */
	public Drawable loadImageFromUrl(String url) {
		URL tempUrl;
		InputStream inputStream = null;
		Drawable drawable = null;

		try {
			tempUrl = new URL(url);

			inputStream = (InputStream) tempUrl.getContent();

//			BitmapFactory.Options o = new BitmapFactory.Options();
//			o.inJustDecodeBounds =true;
//			BitmapFactory.decodeStream(inputStream);

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			drawable = Drawable.createFromStream(inputStream, "src");
		} catch (OutOfMemoryError err) {
			System.out.println("?存溢出...");
		}

		return drawable;
	}

	public interface ImageCallBack {
		public void imageLoaded(Drawable imageDrawable);
	}

}
