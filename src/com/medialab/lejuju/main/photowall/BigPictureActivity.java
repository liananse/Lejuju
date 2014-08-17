package com.medialab.lejuju.main.photowall;

import net.tsz.afinal.FinalBitmap;
import uk.co.senab.photoview.PhotoView;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.model.FriendsModel;
import com.medialab.lejuju.util.UConstants;

public class BigPictureActivity extends MBaseActivity{

	private FriendsModel mFriendsModel;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_big_picture);
		mFriendsModel = (FriendsModel) getIntent().getSerializableExtra(UConstants.FRIENDS_KEY);
		mFinalBitmap = FinalBitmap.create(this);
		findViewByIdMethod();
		setListener();
		initView();
	}

	private PhotoView mPhotoView;
	private ImageView photosdetail_back;
	
	private void findViewByIdMethod()
	{
		mPhotoView = (PhotoView) findViewById(R.id.image);
		
		photosdetail_back = (ImageView) findViewById(R.id.photosdetail_back);
	}
	
	private FinalBitmap mFinalBitmap;
	
	private void initView()
	{
		if (mFriendsModel != null)
		{
			mFinalBitmap.display(mPhotoView, mFriendsModel.head_pic);
		}
	}
	
	private void setListener()
	{
		photosdetail_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				BigPictureActivity.this.finish();
			}
		});
	}
	
}
