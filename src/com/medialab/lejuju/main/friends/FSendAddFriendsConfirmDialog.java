package com.medialab.lejuju.main.friends;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.medialab.lejuju.R;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTextUtils;

public class FSendAddFriendsConfirmDialog extends DialogFragment implements OnClickListener
{
	private Context context;
	private int fanId;
	private EditText confirmMsg;
	private Button sendBtn;
	private Button cancelBtn;
	private HHttpDataLoader loader = new HHttpDataLoader();
	
	public void setFandId(int fanId)
	{
		this.fanId = fanId;
	}
	
	public void setContext(Context context)
	{
		this.context = context;
	}

	@Override
	public void onCreate(Bundle arg0)
	{
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.confirmDialog);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.activity_add_friends_dialog, container,false);
		sendBtn = (Button) view.findViewById(R.id.send_btn);
		sendBtn.setOnClickListener(this);
		
		cancelBtn = (Button) view.findViewById(R.id.cancel_btn);
		cancelBtn.setOnClickListener(this);

		confirmMsg = (EditText) view.findViewById(R.id.confirm_message_et);
		return view;
	}

	@Override
	public void onClick(View v)
	{
		if(v == sendBtn)
		{
			Map<String,String> params = new HashMap<String,String>();
			params.put("fans_id", fanId+"");
			params.put("verify_info", confirmMsg.getText().toString());
			loader.postData(UConstants.ADD_FRIENDS_URL, params, context, new HDataListener()
			{

				@Override
				public void onFinish(String source)
				{
					try
					{
						JSONObject json = new JSONObject(source);
						if(UTextUtils.isSuccess(json))
						{
							FSendAddFriendsConfirmDialog.this.dismiss();
						}
						else
						{
							Toast.makeText(context, "发送失败", Toast.LENGTH_LONG).show();
						}
					}
					catch (JSONException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}

				@Override
				public void onFail(String msg)
				{
					Toast.makeText(context, "发送失败，请检查网络状态。", Toast.LENGTH_LONG).show();
				}
				
			});
		}
		else if(v == cancelBtn)
		{
			FSendAddFriendsConfirmDialog.this.dismiss();
		}

	}
	
}
