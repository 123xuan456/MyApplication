package com.example.administrator.paipai.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.paipai.R;
import com.example.administrator.paipai.data.DataModel;
import com.example.administrator.paipai.view.MyHScrollView;

import java.util.List;

public class MyAdapter extends BaseAdapter {
	private Context context;
	private View top_layout;

	public List<DataModel> hostBaseData;

	public synchronized void setHostBaseData(List<DataModel> hostBaseData) {
		this.hostBaseData = hostBaseData;
		this.notifyDataSetChanged();
	}

	public MyAdapter(Context context, View top_layout) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.top_layout = top_layout;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return hostBaseData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return hostBaseData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHoder viewHoder = null;
		if (convertView == null) {
			viewHoder = new ViewHoder();
			convertView = LinearLayout.inflate(context, R.layout.item, null);
			MyHScrollView horizontalScrollView_item_excel_chart = (MyHScrollView) convertView
					.findViewById(R.id.horizontalScrollView_excel);
			MyHScrollView headSrcrollView = (MyHScrollView) top_layout
					.findViewById(R.id.horizontalScrollView_excel);
			headSrcrollView.AddScrollViewObserver(new ScrollViewObserverImp(
					horizontalScrollView_item_excel_chart));

			viewHoder.host_top_bar_1 = (TextView) convertView
					.findViewById(R.id.host_top_bar_1);
			viewHoder.host_top_bar_2 = (TextView) convertView
					.findViewById(R.id.host_top_bar_2);
			viewHoder.host_top_bar_3 = (TextView) convertView
					.findViewById(R.id.host_top_bar_3);

			viewHoder.host_top_bar_4 = (TextView) convertView
					.findViewById(R.id.host_top_bar_4);
			viewHoder.host_top_bar_5 = (TextView) convertView
					.findViewById(R.id.host_top_bar_5);

			viewHoder.host_top_bar_6 = (TextView) convertView
					.findViewById(R.id.host_top_bar_6);
			viewHoder.host_top_bar_7 = (TextView) convertView
					.findViewById(R.id.host_top_bar_7);

			convertView.setTag(viewHoder);
		} else {
			viewHoder = (ViewHoder) convertView.getTag();
		}
		viewHoder.host_top_bar_1.setText(hostBaseData.get(position)
				.getOrder());
		viewHoder.host_top_bar_2.setText(hostBaseData.get(position).getTime());

		viewHoder.host_top_bar_3.setText(hostBaseData.get(position)
				.getMoney());
		viewHoder.host_top_bar_4.setText(hostBaseData.get(position)
				.getState());
		viewHoder.host_top_bar_5.setText(hostBaseData.get(position)
				.getPay());
		viewHoder.host_top_bar_6.setText(hostBaseData.get(position)
				.getLocation());
		viewHoder.host_top_bar_7.setText(hostBaseData.get(position).getNumber());
		return convertView;
	}

	public class ViewHoder {
		TextView  host_top_bar_1, host_top_bar_2,
				host_top_bar_3, host_top_bar_4, host_top_bar_5, host_top_bar_6,
				host_top_bar_7;

	}

	class ScrollViewObserverImp implements MyHScrollView.ScrollViewObserver {
		MyHScrollView mScrollViewArg;

		public ScrollViewObserverImp(MyHScrollView scrollViewar) {
			mScrollViewArg = scrollViewar;
		}

		@Override
		public void onScrollChanged(int l, int t, int oldl, int oldt) {
			mScrollViewArg.smoothScrollTo(l, t);
		}
	};

}
