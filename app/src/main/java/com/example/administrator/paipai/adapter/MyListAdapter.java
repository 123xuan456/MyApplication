package com.example.administrator.paipai.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.paipai.R;
import com.example.administrator.paipai.data.BluetoothDeviceDetail;

import java.util.ArrayList;

/**
 * Created by liguo on 2016/12/2.
 */

public class MyListAdapter extends BaseAdapter {
	private ArrayList<BluetoothDeviceDetail> devices;

	private LayoutInflater mInflator;
	private Context mContext;

	public MyListAdapter(ArrayList<BluetoothDeviceDetail> devicesList,
						 Context context, LayoutInflater Inflator) {
		super();
		devices = devicesList;
		mContext = context;
		mInflator = Inflator;
	}

	public void addDevice(BluetoothDeviceDetail device) {
		if (!devices.contains(device)) {
			devices.add(device);
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return devices.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return devices.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		TabelViewCell tabelViewCell;
		// General ListView optimization code.
		if (view == null) {
			view = mInflator.inflate(R.layout.scan_devices_item, null);
			tabelViewCell = new TabelViewCell();
			tabelViewCell.tv_name = (TextView) view.findViewById(R.id.tv_name);
			tabelViewCell.tv_addr = (TextView) view.findViewById(R.id.tv_addr);
			tabelViewCell.tv_rssi = (TextView) view.findViewById(R.id.tv_rssi);
			tabelViewCell.pb_rssi = (ProgressBar) view
					.findViewById(R.id.pb_rssi);
			view.setTag(tabelViewCell);
		} else {
			tabelViewCell = (TabelViewCell) view.getTag();
		}

		BluetoothDeviceDetail device = devices.get(position);
		final String deviceName = device.name;
		final String deviceAdd = device.address;
		int deviceRssi = device.rssi;
		// Log.i("anqii", "name=" + deviceName + ", deviceAddr=" + deviceAdd);
		if (deviceName != null && deviceName.length() > 0) {
			tabelViewCell.tv_name.setText(deviceName);
		} else {
			tabelViewCell.tv_name.setText(mContext.getResources().getString(
					R.string.unknow_name));
		}
		if (deviceAdd != null && deviceAdd.length() > 0) {
			tabelViewCell.tv_addr.setText(deviceAdd);
		} else {
			tabelViewCell.tv_addr.setText(mContext.getResources().getString(
					R.string.unknow_address));
		}
		// iBeacon 隐藏或显示
		if (deviceRssi <= -100) {
			deviceRssi = -100;
			tabelViewCell.tv_rssi.setTextColor(Color.RED);
		} else if (deviceRssi > 0) {
			deviceRssi = 0;
			tabelViewCell.tv_rssi.setTextColor(Color.GRAY);
		} else {
			tabelViewCell.tv_rssi.setTextColor(Color.GRAY);
		}
		String str_rssi = "(" + deviceRssi + ")";
		if (str_rssi.equals("(-100)")) {
			str_rssi = "null";
		}
		tabelViewCell.tv_rssi.setText(str_rssi);
		tabelViewCell.pb_rssi.setProgress(100 + deviceRssi);
		return view;
	}

	// cell
	static class TabelViewCell {
		TextView tv_name;
		TextView tv_addr;
		TextView tv_rssi;
		ProgressBar pb_rssi;
	}
}
