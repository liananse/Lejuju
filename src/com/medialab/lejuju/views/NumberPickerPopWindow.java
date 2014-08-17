package com.medialab.lejuju.views;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.medialab.lejuju.R;
import com.medialab.lejuju.util.UTools;

public class NumberPickerPopWindow extends PopupWindow implements OnClickListener {
	
	private Context mContext;
	private View mView;
	private Button mSubmitButton;
	private Button mCancelButton;
	private DateNumericAdapter monthAdapter;
	private DateNumericAdapter dayAdapter;
	private DateNumericAdapter yearAdapter;
	private DateNumericAdapter hourAdapter;
	private DateNumericAdapter minuteAdapter;
	
	private WheelView mYear;
	private WheelView mMonth;
	private WheelView mDay;
	private WheelView mHour;
	private WheelView mMinute;
	
	private ViewFlipper viewfipper;
	
	protected OnClickListener timeClickListener;
	
	public NumberPickerPopWindow(Context context, OnClickListener timeClickListener)
	{
		super(context);
		
		mContext = context;
		this.timeClickListener = timeClickListener;
		mView = LayoutInflater.from(context).inflate(R.layout.number_picker_view, null);
		
		initView();
	}
	
	private void initView()
	{
		// 选择
		mCancelButton = (Button) mView.findViewById(R.id.cancel);
		mSubmitButton = (Button) mView.findViewById(R.id.submit);
		
		mCancelButton.setOnClickListener(this);
		
		mSubmitButton.setOnClickListener(timeClickListener);
		
		UTools.UI.fitViewByWidth(mContext, mCancelButton, 142, 73, 640);
		UTools.UI.fitViewByWidth(mContext, mSubmitButton, 142, 73, 640);
		
		// 
		mYear = (WheelView) mView.findViewById(R.id.year);
		mMonth = (WheelView) mView.findViewById(R.id.month);
		mDay = (WheelView) mView.findViewById(R.id.day);
		
		mHour = (WheelView) mView.findViewById(R.id.hour);
		mMinute = (WheelView) mView.findViewById(R.id.minute);
		
		viewfipper = new ViewFlipper(mContext);
		viewfipper.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		////
		Calendar calendar = Calendar.getInstance();
		
		int curYear = calendar.get(Calendar.YEAR);
		int curMonth = calendar.get(Calendar.MONTH);
		int curDay = calendar.get(calendar.DAY_OF_MONTH) - 1;
		int curHour = calendar.getTime().getHours();
		int curMinute = calendar.getTime().getMinutes();
		
		OnWheelChangedListener listener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				updateDays(mYear, mMonth, mDay);
			}
		};
		////
		// year

		yearAdapter = new DateNumericAdapter(mContext, curYear, curYear+100);
		mYear.setViewAdapter(yearAdapter);
		mYear.setCurrentItem(curYear);
		mYear.addChangingListener(listener);
		
		// month
		
		monthAdapter = new DateNumericAdapter(mContext, 1, 12);
//		monthAdapter.setTextType("月");
		mMonth.setViewAdapter(monthAdapter);
		mMonth.setCurrentItem(curMonth);
		mMonth.addChangingListener(listener);
		
		// day
		
		updateDays(mYear, mMonth, mDay);
		mDay.setCurrentItem(curDay);
		updateDays(mYear, mMonth, mDay);
		mDay.addChangingListener(listener);
		
		// hour
		
		hourAdapter = new DateNumericAdapter(mContext, 0, 23);
//		hourAdapter.setTextType("时");
		mHour.setViewAdapter(hourAdapter);
		mHour.setCurrentItem(curHour);
		mHour.addChangingListener(listener);
		
		// minute
		
		minuteAdapter = new DateNumericAdapter(mContext, 0, 59);
//		minuteAdapter.setTextType("分");
		mMinute.setViewAdapter(minuteAdapter);
		mMinute.setCurrentItem(curMinute);
		mMinute.addChangingListener(listener);
		
		viewfipper.addView(mView);
		viewfipper.setFlipInterval(6000000);
		
		mSubmitButton.setTag((mYear.getCurrentItem()+calendar.get(Calendar.YEAR)) + "-" + (mMonth.getCurrentItem() + 1) + "-" + (mDay.getCurrentItem() + 1) + " " + mHour.getCurrentItem() + ":" + mMinute.getCurrentItem());
		this.setContentView(viewfipper);
		this.setWidth(LayoutParams.FILL_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		ColorDrawable dw = new ColorDrawable(0x00000000);
		this.setBackgroundDrawable(dw);
		this.update();
		
	}
	
	private void updateDays(WheelView year, WheelView month, WheelView day) 
	{

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + year.getCurrentItem());
		calendar.set(Calendar.MONTH, month.getCurrentItem());

		int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		dayAdapter = new DateNumericAdapter(mContext, 1, maxDays);
//		dayAdapter.setTextType("日");
		day.setViewAdapter(dayAdapter);
		int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
		day.setCurrentItem(curDay - 1, true);
		
		mSubmitButton.setTag((mYear.getCurrentItem()+calendar.get(Calendar.YEAR)) + "-" + (mMonth.getCurrentItem() + 1) + "-" + (mDay.getCurrentItem() + 1) + " " + mHour.getCurrentItem() + ":" + mMinute.getCurrentItem());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mSubmitButton)
		{
			// return time
			
		}
		else if (v == mCancelButton)
		{
			// dismiss the popwindow
			this.dismiss();
		}
	}
	
	private class DateNumericAdapter extends NumericWheelAdapter {
		int currentItem;

		public DateNumericAdapter(Context context, int minValue, int maxValue) {
			super(context, minValue, maxValue);
		}

		protected void configureTextView(TextView view) {
			super.configureTextView(view);
			view.setTypeface(Typeface.SANS_SERIF);
		}

		public CharSequence getItemText(int index) {
			currentItem = index;
			return super.getItemText(index);
		}

	}


}
