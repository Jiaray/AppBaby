package com.app.Common.SelectMultiImg;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.app.AppBabySH.R;

import java.util.List;

public class SelectMultiImgDirPopupWindow extends BasePopupWindowForListView<SelectMultiImgDirItem> {
    private ListView mListDir;

    public SelectMultiImgDirPopupWindow(int width, int height,
                                        List<SelectMultiImgDirItem> datas, View convertView) {
        super(convertView, width, height, true, datas);
    }

    @Override
    public void initViews() {
        mListDir = (ListView) findViewById(R.id.lvSelectMultiImgDir);
        mListDir.setAdapter(new SelectMultiImgDirAdapter<SelectMultiImgDirItem>(context, mDatas,
                R.layout.selectmultiimage_dir_item) {
            @Override
            public void convert(SelectMultiImgViewHolder helper, SelectMultiImgDirItem item) {
                helper.setText(R.id.txtSelectMultiImgDirItemName, item.getName());
                helper.setImageByUrl(R.id.imgSelectMultiImgDirItemImg, item.getFirstImagePath());
                helper.setText(R.id.txtSelectMultiImgDirItemCount, "共 " + item.getCount() + " 张");
            }
        });
    }

    public interface OnImageDirSelected {
        void selected(SelectMultiImgDirItem floder);
    }

    private OnImageDirSelected mImageDirSelected;

    public void setOnImageDirSelected(OnImageDirSelected mImageDirSelected) {
        this.mImageDirSelected = mImageDirSelected;
    }

    @Override
    public void initEvents() {
        mListDir.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (mImageDirSelected != null) {
                    mImageDirSelected.selected(mDatas.get(position));
                }
            }
        });
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void beforeInitWeNeedSomeParams(Object... params) {
        // TODO Auto-generated method stub
    }

}
