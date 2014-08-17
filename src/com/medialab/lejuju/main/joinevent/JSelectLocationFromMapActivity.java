package com.medialab.lejuju.main.joinevent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionInfo;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.medialab.lejuju.MBaseActivity;
import com.medialab.lejuju.R;
import com.medialab.lejuju.util.UTools;

public class JSelectLocationFromMapActivity extends MBaseActivity implements OnClickListener{

	BMapManager mBMapMan;
	MapView mMapView;
	LocationClient mLocClient;
	LocationData locData;
	private MapController mMapController;
	boolean isFirstLoc = true;
	boolean isLocationClientStop = false;
	public MyLocationListenner myListener = new MyLocationListenner();
	
	
	private PopupOverlay pop;
	private OverlayItem mCurItem;
	
	// poi search
	private TextView  popupText;
	private View viewCache;
	private View popupInfo;
	private ImageView popArrow;
	
	private AutoCompleteTextView keyWordView;
	private MKSearch mSearch = null;
	private ArrayAdapter<String> sugAdapter = null;
	private static String mCity = "全国";
	
	private Intent mIntent;
	
	private GestureDetector mGestureDetector;
	private OnTouchListener mOnTouchListener;
	private MKMapViewListener mMapListener;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		mBMapMan=new BMapManager(getApplication());
		mBMapMan.init("394913EBF515CFF13E810AE6287690BC8E9B189E", null);  
		
		setContentView(R.layout.activity_select_location_from_map);
		
		mIntent = this.getIntent();
		initHeaderBar();
		initView();
		
	}
	
	// 初始化header bar
	private View mBackView;
	
	private ImageView mBackImageView;
	
	private void initHeaderBar()
	{
		mBackView = findViewById(R.id.back_btn);
		
		mBackImageView = (ImageView) findViewById(R.id.back_btn_center);
		
		UTools.UI.fitViewByWidth(this, mBackView, 130, 62, 640);
		
		UTools.UI.fitViewByWidth(this, mBackImageView, 18, 32, 640);
		
		mBackView.setOnClickListener(this);
	}
	
	private void initView()
	{
		// auto complete keywords
		keyWordView = (AutoCompleteTextView) findViewById(R.id.searchkey);
		sugAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line);
		keyWordView.setAdapter(sugAdapter);
		
		mGestureDetector = new GestureDetector(mGestureListener);
		
		mOnTouchListener = new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return mGestureDetector.onTouchEvent(event);
			}
		};
		
		mMapListener = new MKMapViewListener()
		{
			@Override
			public void onMapMoveFinish() {
			}
			
			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) 
			{
				MyOverlay myOverlay = new MyOverlay(getResources().getDrawable(R.drawable.icon_marka), mMapView);
				
				GeoPoint p1 = mapPoiInfo.geoPt;
		        OverlayItem item = new OverlayItem(p1,mapPoiInfo.strText,"");
		        myOverlay.addItem(item);
		        
		        mMapView.getOverlays().clear();  
		    	mMapView.getOverlays().add(myOverlay);  
		    	mMapView.refresh(); 
		    	
		    	// 显示地点位置名称
		    	showPop(item);
			}

			@Override
			public void onGetCurrentMap(Bitmap b) {
			}

			@Override
			public void onMapAnimationFinish() {
			}
		};
		
		/**
         * 当输入关键字变化时，动态更新建议列表
         */
		keyWordView.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable arg0) 
			{
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) 
			{
				
			}
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3)
			{
				 if ( cs.length() <=0 )
				 {
					 return ;
				 }
                 mSearch.suggestionSearch(cs.toString(), mCity);				
			}
        });

		viewCache = getLayoutInflater().inflate(R.layout.location_pop_view, null);
        popupInfo = (View) viewCache.findViewById(R.id.popinfo);
        popupText = (TextView) viewCache.findViewById(R.id.textcache);
        popArrow = (ImageView) viewCache.findViewById(R.id.pop_add_loc);
        
        UTools.UI.fitViewByWidth(this, popArrow, 18, 32, 640);
		mSearch = new MKSearch();
        mSearch.init(mBMapMan, mMkSearchListener);
        
		// 地图初始化
        mMapView = (MapView)findViewById(R.id.bmapView);
        mMapController = mMapView.getController();
        mMapView.getController().setZoom(14);
        mMapView.getController().enableClick(true);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setOnTouchListener(mOnTouchListener);
        mMapView.regMapViewListener(mBMapMan, mMapListener);
        
        // 定位初始化
        mLocClient = new LocationClient(this);
        locData = new LocationData();
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setPriority(LocationClientOption.NetWorkFirst);	//设置定位优先级
        option.setScanSpan(1000);		
        mLocClient.setLocOption(option);
        mLocClient.start();
       
		/**
         * 创建一个popupoverlay
         */
        PopupClickListener popListener = new PopupClickListener(){
			@Override
			public void onClickedPopup(int index) 
			{
				if ( index == 0){
					//更新item位置
					pop.hidePop();
					Bundle bundle = new Bundle();
					bundle.putString("location_name", mCurItem.getTitle());
					// 维度
					bundle.putInt("location_latitude", mCurItem.getPoint().getLatitudeE6());
					// 经度
					bundle.putInt("location_longitude", mCurItem.getPoint().getLongitudeE6());
					
					mIntent.putExtras(bundle);
					JSelectLocationFromMapActivity.this.setResult(RESULT_OK, mIntent);
					JSelectLocationFromMapActivity.this.finish();
					
				}
				else if(index == 2){
					//更新图标
				}
			}
        };
        pop = new PopupOverlay(mMapView,popListener);
		
	}
	
	MKSearchListener mMkSearchListener = new MKSearchListener() {
		
		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
			// TODO Auto-generated method stub
			if ( res == null || res.getAllSuggestions() == null)
			{
        		return ;
        	}
        	sugAdapter.clear();
        	for ( MKSuggestionInfo info : res.getAllSuggestions())
        	{
        		if ( info.key != null)
        		    sugAdapter.add(info.key);
        	}
        	sugAdapter.notifyDataSetChanged();
		}
		
		@Override
		public void onGetPoiResult(MKPoiResult res, int type, int error) {
			// TODO Auto-generated method stub
			// 错误号可参考MKEvent中的定义
            if (error != 0 || res == null) 
            {
                Toast.makeText(JSelectLocationFromMapActivity.this, R.string.select_location_no_location_find, Toast.LENGTH_LONG).show();
                return;
            }
            // 将地图移动到第一个POI中心点
            if (res.getCurrentNumPois() > 0) 
            {
                // 将poi结果显示到地图上
            	MyOverlay poiOverlay = new MyOverlay(getResources().getDrawable(R.drawable.icon_marka), mMapView);  
            	for(MKPoiInfo info : res.getAllPoi() )
            	{  
            		GeoPoint p1 = new GeoPoint ((int)(info.pt.getLatitudeE6()),(int)(info.pt.getLongitudeE6()));
                    OverlayItem item = new OverlayItem(p1,info.name,"");
                    poiOverlay.addItem(item);
                }  
            	mMapView.getOverlays().clear();  
            	mMapView.getOverlays().add(poiOverlay);  
            	mMapView.refresh();  
            	//当ePoiType为2（公交线路）或4（地铁线路）时， poi坐标为空  
            	for(MKPoiInfo info : res.getAllPoi() )
            	{  
	            	if ( info.pt != null )
	            	{  
		            	mMapView.getController().animateTo(info.pt);  
		                OverlayItem item = new OverlayItem(info.pt,info.name,"");
		                
		                showPop(item);
		            	break;  
	            	}  
            	}  
            }
            else if (res.getCityListNum() > 0) 
            {
            	Toast.makeText(JSelectLocationFromMapActivity.this, R.string.select_location_no_location_find, Toast.LENGTH_LONG).show();
            }
		}
		
		@Override
		public void onGetPoiDetailSearchResult(int type, int error) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onGetAddrResult(MKAddrInfo res, int error) {
			// TODO Auto-generated method stub
			if (res.type == MKAddrInfo.MK_REVERSEGEOCODE)
			{
				//反地理编码：通过坐标点检索详细地址及周边poi
				mCity = res.addressComponents.city;
				
				MyOverlay myOverlay = new MyOverlay(getResources().getDrawable(R.drawable.icon_marka), mMapView);
				OverlayItem item = new OverlayItem(res.geoPt,res.strAddr,"");
	            myOverlay.addItem(item);
	            
	            mMapView.getOverlays().clear();  
	        	mMapView.getOverlays().add(myOverlay);  
	        	mMapView.refresh(); 

	        	// 显示地点位置名称
				showPop(item);
			}
		}
	};
	
	OnGestureListener mGestureListener = new OnGestureListener()
	{

		@Override
		public boolean onDown(MotionEvent arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
				float arg3) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			int x = (int)e.getX();
			int y = (int)e.getY();
			GeoPoint mGeoPoint = mMapView.getProjection().fromPixels(x , y);
			mSearch.reverseGeocode(mGeoPoint);
		}

		@Override
		public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
				float arg3) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onSingleTapUp(MotionEvent arg0) {
			// TODO Auto-generated method stub
			return false;
		}
	};
	
	/**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {
    	
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || isLocationClientStop)
                return ;
            try {
				locData.latitude = location.getLatitude();
				locData.longitude = location.getLongitude();
				//如果不显示定位精度圈，将accuracy赋值为0即可
				locData.accuracy = location.getRadius();
				locData.direction = location.getDerect();
				GeoPoint p1 = new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6));
				mSearch.reverseGeocode(p1);
				
				if (isFirstLoc){
					//移动地图到定位点
				    mMapController.animateTo(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            //首次定位完成
            isFirstLoc = false;
            isLocationClientStop = true;
            mLocClient.stop();
            
        }
        
        public void onReceivePoi(BDLocation poiLocation) 
        {
            if (poiLocation == null)
            {
                return ;
            }
        }
    }
	
  	@Override
    protected void onPause() {
    	isLocationClientStop = true;
        mMapView.onPause();
        super.onPause();
    }
    
    @Override
    protected void onResume() {
    	isLocationClientStop = false;
        mMapView.onResume();
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
    	//退出时销毁定位
        if (mLocClient != null)
            mLocClient.stop();
        isLocationClientStop = true;
        mMapView.destroy();
        super.onDestroy();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	mMapView.onSaveInstanceState(outState);
    	
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	mMapView.onRestoreInstanceState(savedInstanceState);
    }
    
    /**
     * 影响搜索按钮点击事件
     * @param v
     */
    public void searchButtonProcess(View v) 
    {
          EditText editSearchKey = (EditText)findViewById(R.id.searchkey);
//          mSearch.poiSearchNearBy(editSearchKey.getText().toString(), mCurItem.getPoint(), 5000);  
          
          mSearch.poiSearchInCity(mCity, 
                  editSearchKey.getText().toString());
    }
    
    public class MyOverlay extends ItemizedOverlay{

		public MyOverlay(Drawable defaultMarker, MapView mapView)
		{
			super(defaultMarker, mapView);
		}
		
		@Override
		public boolean onTap(int index)
		{
			showPop(getItem(index));
			return true;
		}
		
		@Override
		public boolean onTap(GeoPoint pt , MapView mMapView){
			if (pop != null)
			{
                pop.hidePop();
			}
			return false;
		}
    	
    }
    
    public static Bitmap getBitmapFromView(View view) 
    {
        view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache(true);
        return bitmap;
	}
    
    private void showPop(OverlayItem item)
    {
    	mCurItem = item ;
    	
    	if (pop != null)
		{
            pop.hidePop();
		}
    	
    	if (mLocClient != null)
            mLocClient.stop();
    	
		popupText.setText(item.getTitle());
		Bitmap[] bitMaps={		
		    getBitmapFromView(popupInfo)		
	    };
	    pop.showPopup(bitMaps,item.getPoint(),80);
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mBackView)
		{
			this.finish();
		}
	}
}


