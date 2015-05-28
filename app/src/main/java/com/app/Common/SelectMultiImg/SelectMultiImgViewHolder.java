package com.app.Common.SelectMultiImg;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.Common.SelectMultiImg.SelectMultiImgLoader.Type;

public class SelectMultiImgViewHolder
{
	private final SparseArray<View> mViews;
	private int mPosition;
	private View mConvertView;

	private SelectMultiImgViewHolder(Context context, ViewGroup parent, int layoutId,
									 int position)
	{
		this.mPosition = position;
		this.mViews = new SparseArray<View>();
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
				false);
		// setTag
		mConvertView.setTag(this);
	}

	/**
	 * 拿到一個ViewHolder對象
	 *
	 * @param context
	 * @param convertView
	 * @param parent
	 * @param layoutId
	 * @param position
	 * @return
	 */
	public static SelectMultiImgViewHolder get(Context context, View convertView,
								 ViewGroup parent, int layoutId, int position)
	{
		SelectMultiImgViewHolder holder = null;
		if (convertView == null)
		{
			holder = new SelectMultiImgViewHolder(context, parent, layoutId, position);
		} else
		{
			holder = (SelectMultiImgViewHolder) convertView.getTag();
			holder.mPosition = position;
		}
		return holder;
	}

	public View getConvertView()
	{
		return mConvertView;
	}

	/**
	 * 通過控件的Id獲取對於的控件，如果沒有則加入views
	 *
	 * @param viewId
	 * @return
	 */
	public <T extends View> T getView(int viewId)
	{
		View view = mViews.get(viewId);
		if (view == null)
		{
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}

	/**
	 * 為TextView設置字符串
	 *
	 * @param viewId
	 * @param text
	 * @return
	 */
	public SelectMultiImgViewHolder setText(int viewId, String text)
	{
		TextView view = getView(viewId);
		view.setText(text);
		return this;
	}

	/**
	 * 為ImageView設置圖片
	 *
	 * @param viewId
	 * @param drawableId
	 * @return
	 */
	public SelectMultiImgViewHolder setImageResource(int viewId, int drawableId)
	{
		ImageView view = getView(viewId);
		view.setImageResource(drawableId);

		return this;
	}

	/**
	 * 為ImageView設置圖片
	 *
	 * @param viewId
	 * @param bm
	 * @return
	 */
	public SelectMultiImgViewHolder setImageBitmap(int viewId, Bitmap bm)
	{
		ImageView view = getView(viewId);
		view.setImageBitmap(bm);
		return this;
	}

	/**
	 * 為ImageView設置圖片
	 *
	 * @param viewId
	 * @param url
	 * @return
	 */
	public SelectMultiImgViewHolder setImageByUrl(int viewId, String url)
	{
		SelectMultiImgLoader.getInstance(3, Type.LIFO).loadImage(url, (ImageView) getView(viewId));
		return this;
	}

	public int getPosition()
	{
		return mPosition;
	}

}
