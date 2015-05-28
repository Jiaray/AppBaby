package com.app.Common.SelectMultiImg;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.AppBabySH.R;

import java.util.LinkedList;
import java.util.List;

public class SelectMultiImgAdapter extends SelectMultiImgDirAdapter<String>
{

	/**
	 * 用戶選擇的圖片，存儲為圖片的完整路徑
	 */
	public static List<String> mSelectedImage = new LinkedList<String>();

	public static Integer MaxNum;
	private Context mcontext;

	/**
	 * 文件夾路徑
	 */
	private String mDirPath;

	public SelectMultiImgAdapter(Context context, List<String> mDatas, int itemLayoutId,
								 String dirPath)
	{
		super(context, mDatas, itemLayoutId);
		mcontext = context;
		this.mDirPath = dirPath;
	}

	@Override
	public void convert(final SelectMultiImgViewHolder helper, final String item)
	{
		final String photoPath = mDirPath.equals("all") ? item : mDirPath + "/" + item;
		//設置no_pic
		helper.setImageResource(R.id.imgSelectMultiImgItemContent, R.drawable.selectmultiimage_loading);
		//設置no_selected
		helper.setImageResource(R.id.imgbSelectMultiImgItemSelectIcon,
				R.drawable.selectmultiimage_unselected);
		//設置圖片
		helper.setImageByUrl(R.id.imgSelectMultiImgItemContent, photoPath);

		final ImageView mImageView = helper.getView(R.id.imgSelectMultiImgItemContent);
		final ImageView mSelect = helper.getView(R.id.imgbSelectMultiImgItemSelectIcon);

		mImageView.setColorFilter(null);
		//設置ImageView的點擊事件
		mImageView.setOnClickListener(new OnClickListener()
		{
			//選擇，則將圖片變暗，反之反之
			@Override
			public void onClick(View v)
			{
				// 已經選擇過該圖片
				if (mSelectedImage.contains(photoPath))
				{
					mSelectedImage.remove(photoPath);
					mSelect.setImageResource(R.drawable.selectmultiimage_unselected);
					mImageView.setColorFilter(null);
				} else
				// 未選擇該圖片
				{
					if(mSelectedImage.size() >= MaxNum){
						Toast.makeText(mcontext.getApplicationContext(), "已达上限无法选取!", Toast.LENGTH_SHORT).show();
						return;
					}
					mSelectedImage.add(photoPath);
					mSelect.setImageResource(R.drawable.selectmultiimage_selected);
					mImageView.setColorFilter(Color.parseColor("#77000000"));
				}
			}
		});

		/**
		 * 已經選擇過的圖片，顯示出選擇過的效果
		 */
		if (mSelectedImage.contains(photoPath))
		{
			mSelect.setImageResource(R.drawable.selectmultiimage_selected);
			mImageView.setColorFilter(Color.parseColor("#77000000"));
		}

	}
}
