package com.medialab.lejuju.main.userinfo;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.http.HHttpDataLoader;
import com.medialab.lejuju.http.HHttpDataLoader.HDataListener;
import com.medialab.lejuju.util.UConstants;
import com.medialab.lejuju.util.UTools;

public class UserInfoEditSignatureActivity extends MBaseActivity implements OnClickListener{
	
	private TextView mLeftCountTextView;
	private EditText mEditText;
	private static final int MAX_COUNT = 140;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_edit_signature);
		initHeaderBar();
		initView();
		
	}
	
	// 初始化header bar
	private View mBackView;
	private View mOkView;
	
	private ImageView mBackImageView;
	private ImageView mOkImageView;
	
	private void initHeaderBar()
	{
		mBackView = findViewById(R.id.back_btn);
		mOkView = findViewById(R.id.ok_btn);
		
		mBackImageView = (ImageView) findViewById(R.id.back_btn_center);
		mOkImageView = (ImageView) findViewById(R.id.ok_btn_center);
		
		UTools.UI.fitViewByWidth(this, mBackView, 130, 62, 640);
		UTools.UI.fitViewByWidth(this, mOkView, 130, 62, 640);
		
		UTools.UI.fitViewByWidth(this, mBackImageView, 18, 32, 640);
		UTools.UI.fitViewByWidth(this, mOkImageView, 45, 28, 640);
		
		mBackView.setOnClickListener(this);
		mOkView.setOnClickListener(this);
	}
		
	private void initView()
	{
		mLeftCountTextView = (TextView) findViewById(R.id.left_count_text_view);
		mEditText = (EditText) findViewById(R.id.signature_edit_text);
		
		mEditText.addTextChangedListener(mTextWatcher);
		mEditText.setSelection(mEditText.length());
		
		setLeftCount();
	}

	private TextWatcher mTextWatcher = new TextWatcher() {

		private int editStart;

		private int editEnd;

		public void afterTextChanged(Editable s) {
			editStart = mEditText.getSelectionStart();
			editEnd = mEditText.getSelectionEnd();

			// 先去掉监听器，否则会出现栈溢出
			mEditText.removeTextChangedListener(mTextWatcher);

			// 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
			// 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
			while (calculateLength(s.toString()) > MAX_COUNT) { // 当输入字符个数超过限制的大小时，进行截断操作
				s.delete(editStart - 1, editEnd);
				editStart--;
				editEnd--;
			}
			mEditText.setText(s);
			mEditText.setSelection(editStart);

			// 恢复监听器
			mEditText.addTextChangedListener(mTextWatcher);

			setLeftCount();
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

	};
	
	/**
	 * @author liananse
	 * @param c
	 * @return
	 * 2013-7-8
	 */
	private long calculateLength(CharSequence c) {
		double len = 0;
		for (int i = 0; i < c.length(); i++) {
			int tmp = (int) c.charAt(i);
			if (tmp > 0 && tmp < 127) {
				len += 0.5;
			} else {
				len++;
			}
		}
		return Math.round(len);
	}

	/**
	 * @author liananse
	 * 2013-7-8
	 */
	private void setLeftCount() {
		mLeftCountTextView.setText(String.valueOf((MAX_COUNT - getInputCount())));
	}

	/**
	 * @author liananse
	 * @return
	 * 2013-7-8
	 */
	private long getInputCount() {
		return calculateLength(mEditText.getText().toString());
	}

	private HHttpDataLoader mDataLoader = new HHttpDataLoader();
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mBackView)
		{
			this.finish();
		}
		else if (v == mOkView)
		{
			if (!mEditText.getText().toString().trim().equals(""))
			{
				Map<String, String> params = new HashMap<String, String>();
				params.put("content", mEditText.getText().toString());
				
				mDataLoader.postData(UConstants.OPINION_URL, params, this, new HDataListener() {
					
					@Override
					public void onFinish(String source) {
						// TODO Auto-generated method stub
						Gson gson = new Gson();
						try {
							TempModel mTempModel = gson.fromJson(source, new TypeToken<TempModel>(){}.getType());
							
							if(mTempModel != null)
							{
								
								if (mTempModel.result.equals("success"))
								{
									Toast.makeText(UserInfoEditSignatureActivity.this, "意见反馈发送成功，我们会尽快处理", Toast.LENGTH_SHORT).show();
									UserInfoEditSignatureActivity.this.finish();
								}
								else {
									Toast.makeText(UserInfoEditSignatureActivity.this, mTempModel.message, Toast.LENGTH_SHORT).show();
								}
							}
							else
							{
								Toast.makeText(UserInfoEditSignatureActivity.this, "数据解析出错", Toast.LENGTH_SHORT).show();
							}
						} catch (JsonSyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(UserInfoEditSignatureActivity.this, "数据解析出错", Toast.LENGTH_SHORT).show();
						}
					}
					
					@Override
					public void onFail(String msg) {
						// TODO Auto-generated method stub
						Toast.makeText(UserInfoEditSignatureActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
					}
				});
			}
			else {
				Toast.makeText(this, "请输入你的意见或建议", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	class TempModel
	{
		String result = "";
		String message = "";
	}
}
