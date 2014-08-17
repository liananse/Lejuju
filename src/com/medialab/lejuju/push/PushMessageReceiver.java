package com.medialab.lejuju.push;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.medialab.lejuju.util.BaiduUtils;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTools;

/**
 * Push消息处理receiver
 */
public class PushMessageReceiver extends BroadcastReceiver
{
	/** TAG to Log */
	public static final String TAG = PushMessageReceiver.class.getSimpleName();
	public static boolean isRetry = false;
	AlertDialog.Builder builder;

	/**
	 * 
	 * 
	 * @param context
	 *            Context
	 * @param intent
	 *            接收的intent
	 */
	@Override
	public void onReceive(final Context context, Intent intent)
	{

		Log.d(TAG, ">>> Receive intent: \r\n" + intent);

		if (intent.getAction().equals(PushConstants.ACTION_MESSAGE))
		{
			//百度默认消息的跳转
			// 获取消息内容
			String message = intent.getExtras().getString(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
			JSONObject json = null;
			Message msg = null;
			try
			{
				json = new JSONObject(message);
				msg = new Message(json);
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			// 消息的用户自定义内容读取方式
			AndroidNotify notify = new AndroidNotify();
			notify.notify(context, msg);

		}
		else if (intent.getAction().equals(PushConstants.ACTION_RECEIVE))
		{
			// 处理绑定等方法的返回数据
			// PushManager.startWork()的返回值通过PushConstants.METHOD_BIND得到

			// 获取方法
			final String method = intent.getStringExtra(PushConstants.EXTRA_METHOD);
			// 方法返回错误码。若绑定返回错误（非0），则应用将不能正常接收消息。
			// 绑定失败的原因有多种，如网络原因，或access token过期。
			// 请不要在出错时进行简单的startWork调用，这有可能导致死循环。
			// 可以通过限制重试次数，或者在其他时机重新调用来解决。
			final int errorCode = intent.getIntExtra(PushConstants.EXTRA_ERROR_CODE, PushConstants.ERROR_SUCCESS);

			// 返回内容
			final String content = new String(intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT));

			// 用户在此自定义处理消息,以下代码为demo界面展示用
//			Log.d(TAG, "onMessage: method : " + method);
//			Log.d(TAG, "onMessage: result : " + errorCode);
//			Log.d(TAG, "onMessage: content : " + content);
//			Toast.makeText(
//					context,
//					"method : " + method + "\n result: " + errorCode
//							+ "\n content = " + content, Toast.LENGTH_SHORT)
//					.show();
			try
			{
				JSONObject jsonContent = new JSONObject(content);
				JSONObject params = jsonContent	.getJSONObject("response_params");
				String appid = params.getString("appid");
				String channelid = params.getString("channel_id");
				String userid = params.getString("user_id");

				SharedPreferences.Editor mEditor = UTools.Storage.getSharedPreEditor(context, UConstants.BASE_PREFS_NAME);
				mEditor.putString(UConstants.BAIDU_USER_ID, userid);
				mEditor.putString(UConstants.BAIDU_CHANNEL_ID, channelid);
				mEditor.commit();
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				if(!isRetry)
				{
					PushManager.startWork(context,PushConstants.LOGIN_TYPE_API_KEY, BaiduUtils.getMetaValue(context, "api_key"));
					isRetry = true;
				}
			}

			// Intent responseIntent = null;
			// responseIntent = new Intent(Utils.ACTION_RESPONSE);
			// responseIntent.putExtra(Utils.RESPONSE_METHOD, method);
			// responseIntent.putExtra(Utils.RESPONSE_ERRCODE, errorCode);
			// responseIntent.putExtra(Utils.RESPONSE_CONTENT, content);
			// responseIntent.setClass(context, PushDemoActivity.class);
			// responseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// context.startActivity(responseIntent);

			// 可选。通知用户点击事件处理
		}
		else if (intent.getAction().equals(PushConstants.ACTION_RECEIVER_NOTIFICATION_CLICK))
		{
			//处理自定义跳转
//			Log.d(TAG, "intent=" + intent.toUri(0));
//
//			Intent aIntent = new Intent();
//			aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			aIntent.setClass(context, CustomActivity.class);
//			String title = intent
//					.getStringExtra(PushConstants.EXTRA_NOTIFICATION_TITLE);
//			aIntent.putExtra(PushConstants.EXTRA_NOTIFICATION_TITLE, title);
//			String content = intent
//					.getStringExtra(PushConstants.EXTRA_NOTIFICATION_CONTENT);
//			aIntent.putExtra(PushConstants.EXTRA_NOTIFICATION_CONTENT, content);
//
//			context.startActivity(aIntent);
		}
	}

}
