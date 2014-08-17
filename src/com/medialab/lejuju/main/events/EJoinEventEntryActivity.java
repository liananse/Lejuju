package com.medialab.lejuju.main.events;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.MMainTabActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.main.events.adapter.EEventFragmentAdapter;
import com.medialab.lejuju.main.events.fragment.EFriendsEventFragment;
import com.medialab.lejuju.main.events.fragment.ENearbyEventFragment;
import com.medialab.lejuju.main.myevent.fragment.MAttendEventFragment;
import com.medialab.lejuju.util.UConstants;

public class EJoinEventEntryActivity extends MBaseActivity{

	private ViewPager mPager;
	private ArrayList<Fragment> fragmentsList;
	private Button friends_fragment_tv;
	private Button nearby_fragment_tv;
	private Button my_fragment_tv;
	
	public ImageView top_image_poster;
	public int item_top = 0;
	public View header_barView;
	public LinearLayout content_view;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_join_event_entry);
		top_image_poster = (ImageView) findViewById(R.id.top_image_poster);
		content_view = (LinearLayout) findViewById(R.id.content_view);
		header_barView = findViewById(R.id.header_bar);
		top_image_poster.setVisibility(View.GONE);
		initView();
		EAnimations.initEAnimation(this);
	}
	
	private void initView()
	{
		mPager = (ViewPager) findViewById(R.id.vPager);
		fragmentsList = new ArrayList<Fragment>();
		
		friends_fragment_tv = (Button) findViewById(R.id.friends_fragment_tv);
		nearby_fragment_tv = (Button) findViewById(R.id.nearby_fragment_tv);
		my_fragment_tv = (Button) findViewById(R.id.my_fragment_tv);
		
		
		friends_fragment_tv.setOnClickListener(new MyOnClickListener(0));
		nearby_fragment_tv.setOnClickListener(new MyOnClickListener(1));
		my_fragment_tv.setOnClickListener(new MyOnClickListener(2));
		
		nearby_fragment_tv.setBackgroundResource(R.drawable.btn_near_fragment_normal);
    	friends_fragment_tv.setBackgroundResource(R.drawable.btn_friends_fragment_press);
    	my_fragment_tv.setBackgroundResource(R.drawable.btn_my_fragment_normal);
    	
		Fragment friendsEventFragment = new EFriendsEventFragment();
        Fragment nearByEventFragment = new ENearbyEventFragment();
        Fragment mMAttendEventFragment = new MAttendEventFragment();
        
        fragmentsList.add(friendsEventFragment);
        fragmentsList.add(nearByEventFragment);
        fragmentsList.add(mMAttendEventFragment);
        
        mPager.setAdapter(new EEventFragmentAdapter(getSupportFragmentManager(), fragmentsList));
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}
	
	public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
        }
    };
    public class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            switch (arg0) {
            case 0:
            	nearby_fragment_tv.setBackgroundResource(R.drawable.btn_near_fragment_normal);
            	friends_fragment_tv.setBackgroundResource(R.drawable.btn_friends_fragment_press);
            	my_fragment_tv.setBackgroundResource(R.drawable.btn_my_fragment_normal);
                break;
            case 1:
            	nearby_fragment_tv.setBackgroundResource(R.drawable.btn_near_fragment_press);
            	friends_fragment_tv.setBackgroundResource(R.drawable.btn_friends_fragment_normal);
            	my_fragment_tv.setBackgroundResource(R.drawable.btn_my_fragment_normal);
                break;
            case 2:
            	nearby_fragment_tv.setBackgroundResource(R.drawable.btn_near_fragment_normal);
            	friends_fragment_tv.setBackgroundResource(R.drawable.btn_friends_fragment_normal);
            	my_fragment_tv.setBackgroundResource(R.drawable.btn_my_fragment_press);
                break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {
		case RESULT_OK:
			if (requestCode == UConstants.EVENT_DETAIL_REQUESTCODE)
			{
				EAnimations.endItemAnimation(item_top, top_image_poster);
				if (getParent() instanceof MMainTabActivity)
				{
					((MMainTabActivity) getParent()).animationBackMainTab();
				}
			}
			break;

		default:
			break;
		}
	}
	
	
	
}
