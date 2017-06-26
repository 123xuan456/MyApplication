package com.example.administrator.paipai.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/*
 * 
 * */
public class InterceptScrollContainerItem extends LinearLayout {

	public InterceptScrollContainerItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public InterceptScrollContainerItem(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	//
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		// return super.dispatchTouchEvent(ev);
		Log.i("pdwy", "ScrollContainer dispatchTouchEvent");
		// return true;
		return super.dispatchTouchEvent(ev);

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		// return super.onInterceptTouchEvent(ev);
		Log.i("pdwy", "ScrollContainer onInterceptTouchEvent");
		return true;

		// return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		Log.i("pdwy", "ScrollContainer onTouchEvent");
		return super.onTouchEvent(event);
		// return true;
	}
}
