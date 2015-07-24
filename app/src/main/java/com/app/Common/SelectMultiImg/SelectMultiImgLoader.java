package com.app.Common.SelectMultiImg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class SelectMultiImgLoader
{
	/**
	 * 圖片緩存的核心類
	 */
	private LruCache<String, Bitmap> mLruCache;
	/**
	 * 線程池
	 */
	private ExecutorService mThreadPool;
	/**
	 * 線程池的線程數量，默認為1
	 */
	private int mThreadCount = 1;
	/**
	 * 隊列的調度方式
	 */
	private Type mType = Type.LIFO;
	/**
	 * 任務隊列
	 */
	private LinkedList<Runnable> mTasks;
	/**
	 * 輪詢的線程
	 */
	private Thread mPoolThread;
	private Handler mPoolThreadHander;

	/**
	 * 運行在UI線程的handler，用於給ImageView設置圖片
	 */
	private Handler mHandler;

	/**
	 * 引入一個值為1的信號量，防止mPoolThreadHander未初始化完成
	 */
	private volatile Semaphore mSemaphore = new Semaphore(0);

	/**
	 * 引入一個值為1的信號量，由於線程池內部也有一個阻塞線程，防止加入任務的速度過快，使LIFO效果不明顯
	 */
	private volatile Semaphore mPoolSemaphore;

	private static SelectMultiImgLoader mInstance;

	/**
	 * 隊列的調度方式
	 *
	 * @author zhy
	 *
	 */
	public enum Type
	{
		FIFO, LIFO
	}


	/**
	 * 單例獲得該實例對象
	 *
	 * @return
	 */
	public static SelectMultiImgLoader getInstance()
	{

		if (mInstance == null)
		{
			synchronized (SelectMultiImgLoader.class)
			{
				if (mInstance == null)
				{
					mInstance = new SelectMultiImgLoader(1, Type.LIFO);
				}
			}
		}
		return mInstance;
	}

	private SelectMultiImgLoader(int threadCount, Type type)
	{
		init(threadCount, type);
	}

	private void init(int threadCount, Type type)
	{
		// loop thread
		mPoolThread = new Thread()
		{
			@Override
			public void run()
			{
				Looper.prepare();

				mPoolThreadHander = new Handler()
				{
					@Override
					public void handleMessage(Message msg)
					{
						mThreadPool.execute(getTask());
						try
						{
							mPoolSemaphore.acquire();
						} catch (InterruptedException e)
						{
						}
					}
				};
				// 釋放一個信號量
				mSemaphore.release();
				Looper.loop();
			}
		};
		mPoolThread.start();

		// 獲取應用程序最大可用內存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;// 預設8
		mLruCache = new LruCache<String, Bitmap>(cacheSize)
		{
			@Override
			protected int sizeOf(String key, Bitmap value)
			{
				return value.getRowBytes() * value.getHeight();
			};
		};

		mThreadPool = Executors.newFixedThreadPool(threadCount);
		mPoolSemaphore = new Semaphore(threadCount);
		mTasks = new LinkedList<Runnable>();
		mType = type == null ? Type.LIFO : type;

	}

	/**
	 * 加載圖片
	 *
	 * @param path
	 * @param imageView
	 */
	public void loadImage(final String path, final ImageView imageView)
	{
		// set tag
		imageView.setTag(path);
		// UI線程
		if (mHandler == null)
		{
			mHandler = new Handler()
			{
				@Override
				public void handleMessage(Message msg)
				{
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					ImageView imageView = holder.imageView;
					Bitmap bm = holder.bitmap;
					String path = holder.path;
					if (imageView.getTag().toString().equals(path))
					{
						imageView.setImageBitmap(bm);
					}
				}
			};
		}

		Bitmap bm = getBitmapFromLruCache(path);
		if (bm != null)
		{
			ImgBeanHolder holder = new ImgBeanHolder();
			holder.bitmap = bm;
			holder.imageView = imageView;
			holder.path = path;
			Message message = Message.obtain();
			message.obj = holder;
			mHandler.sendMessage(message);
		} else
		{
			addTask(new Runnable()
			{
				@Override
				public void run()
				{

					ImageSize imageSize = getImageViewWidth(imageView);

					int reqWidth = imageSize.width;
					int reqHeight = imageSize.height;

					Bitmap bm = decodeSampledBitmapFromResource(path, reqWidth,
							reqHeight);
					addBitmapToLruCache(path, bm);
					ImgBeanHolder holder = new ImgBeanHolder();
					holder.bitmap = getBitmapFromLruCache(path);
					holder.imageView = imageView;
					holder.path = path;
					Message message = Message.obtain();
					message.obj = holder;
					// Log.e("TAG", "mHandler.sendMessage(message);");
					mHandler.sendMessage(message);
					mPoolSemaphore.release();
				}
			});
		}

	}

	/**
	 * 添加一個任務
	 *
	 * @param runnable
	 */
	private synchronized void addTask(Runnable runnable)
	{
		try
		{
			// 請求信號量，防止mPoolThreadHander為null
			if (mPoolThreadHander == null)
				mSemaphore.acquire();
		} catch (InterruptedException e)
		{
		}
		mTasks.add(runnable);

		mPoolThreadHander.sendEmptyMessage(0x110);
	}

	/**
	 * 取出一個任務
	 *
	 * @return
	 */
	private synchronized Runnable getTask()
	{
		if (mType == Type.FIFO)
		{
			return mTasks.removeFirst();
		} else if (mType == Type.LIFO)
		{
			return mTasks.removeLast();
		}
		return null;
	}

	/**
	 * 單例獲得該實例對象
	 *
	 * @return
	 */
	public static SelectMultiImgLoader getInstance(int threadCount, Type type)
	{

		if (mInstance == null)
		{
			synchronized (SelectMultiImgLoader.class)
			{
				if (mInstance == null)
				{
					mInstance = new SelectMultiImgLoader(threadCount, type);
				}
			}
		}
		return mInstance;
	}


	/**
	 * 根據ImageView獲得適當的壓縮的寬和高
	 *
	 * @param imageView
	 * @return
	 */
	private ImageSize getImageViewWidth(ImageView imageView)
	{
		ImageSize imageSize = new ImageSize();
		final DisplayMetrics displayMetrics = imageView.getContext()
				.getResources().getDisplayMetrics();
		final LayoutParams params = imageView.getLayoutParams();

		int width = params.width == LayoutParams.WRAP_CONTENT ? 0 : imageView
				.getWidth(); // Get actual image width
		if (width <= 0)
			width = params.width; // Get layout width parameter
		if (width <= 0)
			width = getImageViewFieldValue(imageView, "mMaxWidth"); // Check
		// maxWidth
		// parameter
		if (width <= 0)
			width = displayMetrics.widthPixels;
		int height = params.height == LayoutParams.WRAP_CONTENT ? 0 : imageView
				.getHeight(); // Get actual image height
		if (height <= 0)
			height = params.height; // Get layout height parameter
		if (height <= 0)
			height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check
		// maxHeight
		// parameter
		if (height <= 0)
			height = displayMetrics.heightPixels;

		// 20150721 Ray 將縮圖比例再度壓縮 減輕記憶體使用 避免閃退
		width = width/3;
		height = height/3;

		imageSize.width = width;
		imageSize.height = height;
		return imageSize;

	}

	/**
	 * 從LruCache中獲取一張圖片，如果不存在就返回null。
	 */
	private Bitmap getBitmapFromLruCache(String key)
	{
		return mLruCache.get(key);
	}

	/**
	 * 往LruCache中添加一張圖片
	 *
	 * @param key
	 * @param bitmap
	 */
	private void addBitmapToLruCache(String key, Bitmap bitmap)
	{
		if (getBitmapFromLruCache(key) == null)
		{
			if (bitmap != null)
				mLruCache.put(key, bitmap);
		}
	}

	/**
	 * 計算inSampleSize，用於壓縮圖片
	 *
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private int calculateInSampleSize(BitmapFactory.Options options,
									  int reqWidth, int reqHeight)
	{
		// 源圖片的寬度
		int width = options.outWidth;
		int height = options.outHeight;
		int inSampleSize = 1;

		if (width > reqWidth && height > reqHeight)
		{
			// 計算出實際寬度和目標寬度的比率
			int widthRatio = Math.round((float) width / (float) reqWidth);
			int heightRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = Math.max(widthRatio, heightRatio);
		}
		return inSampleSize;
	}

	/**
	 * 根據計算的inSampleSize，得到壓縮後圖片
	 *
	 * @param pathName
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private Bitmap decodeSampledBitmapFromResource(String pathName,
												   int reqWidth, int reqHeight)
	{
		// 第一次解析將inJustDecodeBounds設置為true，來獲取圖片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);
		// 調用上面定義的方法計算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// 使用獲取到的inSampleSize值再次解析圖片
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);
		return bitmap;
	}

	private class ImgBeanHolder
	{
		Bitmap bitmap;
		ImageView imageView;
		String path;
	}

	private class ImageSize
	{
		int width;
		int height;
	}

	/**
	 * 反射獲得ImageView設置的最大寬度和高度
	 *
	 * @param object
	 * @param fieldName
	 * @return
	 */
	private static int getImageViewFieldValue(Object object, String fieldName)
	{
		int value = 0;
		try
		{
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = (Integer) field.get(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE)
			{
				value = fieldValue;

				Log.e("TAG", value + "");
			}
		} catch (Exception e)
		{
		}
		return value;
	}

}
