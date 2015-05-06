package com.app.AppBabySH;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.app.Common.PullDownView;
import com.app.Common.ScrollOverListView;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment{
	private View rootView;
	private PullDownView pullDownView;//上啦
	private ListView testV;
	private ScrollOverListView listView;
	private MyAdapter adapter;
	private List<String> arrays;
	private LayoutInflater _inflater;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		_inflater = inflater;
		rootView = inflater.inflate(R.layout.chat_fragment, null);
		//ViewGroup parent = (ViewGroup) rootView.getParent();
		pullDownView = (PullDownView) rootView.findViewById(R.id.pulldownview);
		pullDownView.enableAutoFetchMore(true, 0);
		listView = pullDownView.getListView();
		adapter = new MyAdapter();
		listView.setAdapter(adapter);
		initArrays(new Handler(){//初始化
			@Override
			public void handleMessage(Message msg) {
				arrays = (List<String>) msg.obj;
				System.out.println("初始化"+arrays.toString());
				adapter.notifyDataSetChanged();
				pullDownView.notifyDidDataLoad(false);
			}
		});

		pullDownView.setOnPullDownListener(new PullDownView.OnPullDownListener() {

			@Override
			public void onRefresh() {//刷新
				getNewString(new Handler(){
					@Override
					public void handleMessage(Message msg) {
						arrays.add(0, (String) msg.obj);
						System.out.println("刷新"+(String) msg.obj);
						adapter.notifyDataSetChanged();
						pullDownView.notifyDidRefresh(arrays.isEmpty());
					}
				});
			}

			@Override
			public void onLoadMore() {//加?更多
				getNewString(new Handler(){
					@Override
					public void handleMessage(Message msg) {
						arrays.add((String) msg.obj);
						adapter.notifyDataSetChanged();
						pullDownView.notifyDidLoadMore(arrays.isEmpty());
						System.out.println("加?更多"+arrays.isEmpty());
					}
				});
			}
		});
		return rootView;
	}

	private void initArrays(final Handler handler) {
		new Thread(new Runnable() {//初始化加?

			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					Thread.interrupted();
					e.printStackTrace();
				}

				List<String> as = new ArrayList<String>();
				as.add("first");
				as.add("second");
				as.add("third");
				as.add("four");
				as.add("five");
				as.add("first");
				as.add("second");
				as.add("third");
				as.add("four");
				as.add("five");
				as.add("first");
				as.add("second");
				as.add("third");
				as.add("four");
				as.add("five");
				as.add("first");
				as.add("second");
				as.add("third");
				as.add("four");
				as.add("five");
				as.add("first");
				as.add("second");
				as.add("third");
				as.add("four");
				as.add("five");

				handler.obtainMessage(0, as).sendToTarget();
			}
		}).start();
	}

	private void getNewString(final Handler handler) {
		new Thread(new Runnable() {//刷新

			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					Thread.interrupted();
					e.printStackTrace();
				}
				handler.obtainMessage(0, "New Text " + System.currentTimeMillis()).sendToTarget();
			}
		}).start();
	}


	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return arrays == null ? 0 : arrays.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = _inflater.inflate(R.layout.item_list, null);
				holder.textView = (TextView) convertView.findViewById(R.id.text);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.textView.setText(arrays.get(position));

			return convertView;
		}
	}

	private static class ViewHolder {
		TextView textView;
	}
}