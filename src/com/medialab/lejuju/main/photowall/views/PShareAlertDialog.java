package com.medialab.lejuju.main.photowall.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.medialab.lejuju.R;
import com.medialab.lejuju.util.UTools;

public final class PShareAlertDialog {

	public interface OnAlertSelectId {
		void onClick(int whichButton);
	}
	
	private PShareAlertDialog()
	{
		
	}
	
	public static Dialog showAlert(final Context context, final OnAlertSelectId alertDo) {
		return showAlert(context,alertDo, null);
	}

	public static Dialog showAlert(final Context context, final OnAlertSelectId alertDo, OnCancelListener cancelListener) {
		final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.alert_share_dialog, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);

		ImageView share_email = (ImageView) layout.findViewById(R.id.share_email_iv);
		ImageView share_message = (ImageView) layout.findViewById(R.id.share_message_iv);
		ImageView share_weixin_friends = (ImageView) layout.findViewById(R.id.share_weixin_friends_iv);
		ImageView share_weixin_timeline = (ImageView) layout.findViewById(R.id.share_weixin_timeline_iv);
		
		UTools.UI.fitViewByWidth(context, share_email, 120, 120, 640);
		UTools.UI.fitViewByWidth(context, share_message, 120, 120, 640);
		UTools.UI.fitViewByWidth(context, share_weixin_friends, 120, 120, 640);
		UTools.UI.fitViewByWidth(context, share_weixin_timeline, 120, 120, 640);
		
		View share_email_view = layout.findViewById(R.id.share_email_item);
		View share_message_view = layout.findViewById(R.id.share_message_item);
		View share_weixin_friends_view = layout.findViewById(R.id.share_weixin_friends_item);
		View share_weixin_timeline_view = layout.findViewById(R.id.share_weixin_timeline_item);
		
		share_email_view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				alertDo.onClick(1);
				dlg.dismiss();
			}
		});
		share_message_view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDo.onClick(2);
				dlg.dismiss();
			}
		});
		
		share_weixin_friends_view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDo.onClick(3);
				dlg.dismiss();
			}
		});
		
		share_weixin_timeline_view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDo.onClick(4);
				dlg.dismiss();
			}
		});
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}
}
