package com.baidu.navi.sdkdemo;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.navi.location.BDLocation;
import com.baidu.navi.location.BDLocationListener;
import com.baidu.navi.location.LocationClient;
import com.baidu.navi.location.LocationClientOption;
import com.baidu.navisdk.model.MainMapModel;

public class FirstMainActivity extends Activity implements
		OnGetGeoCoderResultListener {

	TextView tv_show1, tv_show2, tv_show3, tv_show4;
	EditText et_from, et_to;
	Button but_show;
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	GeoCoder mSearch = null;

	// MapView mMapView;
	// BaiduMap mBaiduMap;
	// TextView tv_show;
	// UI相关
	boolean isFirstLoc = true;// 是否首次定位
	public static final String MY_PREFS = "MyPrefs";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.first_main);
		// 地理反编译
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);

		tv_show1 = (TextView) findViewById(R.id.tv_show1);
		tv_show2 = (TextView) findViewById(R.id.tv_show2);
		tv_show3 = (TextView) findViewById(R.id.tv_show3);
		tv_show4 = (TextView) findViewById(R.id.tv_show4);
		et_from = (EditText) findViewById(R.id.et_from);
		et_to = (EditText) findViewById(R.id.et_to);
		but_show = (Button) findViewById(R.id.but_show);

//	    String str_to = et_to.getText().toString();
//		mSearch.geocode(new GeoCodeOption().city("天津").address(str_to));
		but_show.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				String str_to = et_to.getText().toString();
				mSearch.geocode(new GeoCodeOption().city("天津市").address(str_to));
				mLocClient.requestLocation();
				new Timer().schedule(new TimerTask() {

					@Override
					public void run() {
						// TODO 自动生成的方法存根
						Intent intent = new Intent(FirstMainActivity.this,
								DemoMainActivity.class);
//						intent.putExtra("from_note",et_from.getText().toString());//出发点地名
//					    intent.putExtra("to_note",et_to.getText().toString());//终点地名
//					    System.out.println("出发点是："+et_from.getText().toString()+"终点是："+et_to.getText().toString());
//					    
//					    intent.putExtra("d1", tv_show1.getText().toString());//取得出发点Latitude
//						intent.putExtra("d2", tv_show2.getText().toString());//取得出发点longitude
//						intent.putExtra("c1", tv_show3.getText().toString());//取得终点Latitude
//						intent.putExtra("c2", tv_show4.getText().toString());//取得终点longitude
						//将起点和终点坐标存进共享数据结构当中
						Data.setFrom_note(et_from.getText().toString());//出发点地名
						Data.setTo_note(et_to.getText().toString());//终点地名
						Data.setD1(Double.parseDouble(tv_show1.getText().toString()));
						Data.setD2(Double.parseDouble(tv_show2.getText().toString()));
						Data.setC1(Double.parseDouble(tv_show3.getText().toString()));
						Data.setC2(Double.parseDouble(tv_show4.getText().toString()));
						
						
						/////////////////////测试坐标取值的代码////////////////////////////
						 System.out.println("起点是："+et_from.getText().toString() +
						 "终点是："+et_to.getText().toString() +" 》》》"+
						 tv_show1.getText().toString()+" 》》》" +
						 tv_show2.getText().toString() +" 》》》"+
						 tv_show3.getText().toString() +" 》》》"+
						 tv_show4.getText().toString());
						///////////////////////////////////////////////////////////////////
						System.out.println(">>>>>>>>>>>>获取了坐标信息>>>>>>>>>>>>>");
						//首选项存值
						
						SharedPreferences mySharedPreferences = getSharedPreferences(
								MY_PREFS, Activity.MODE_PRIVATE);

						SharedPreferences.Editor editor = mySharedPreferences
								.edit();

						editor.putString("from_note", et_from.getText().toString());

						editor.commit();

//						boolean isTrue = mySharedPreferences.getBoolean(
//								"isTrue", false);

						startActivity(intent);
					}
				}, 1000);

			}
		});

		mCurrentMode = LocationMode.NORMAL;
		mCurrentMarker = null;

		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);// 定位频率
		option.setAddrType("all");// 解决location.getAddrStr()为空的问题
		mLocClient.setLocOption(option);
		mLocClient.start();
		
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();

			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				System.out.println(location.getLongitude() + "---"
						+ location.getLatitude() + "----city-----");

			}

			// System.out.println(location.getAddrStr() + "---addr----");
			et_from.setText(location.getAddrStr());
			tv_show1.setText("" + location.getLatitude());
			tv_show2.setText("" + location.getLongitude());
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}

	}

	@Override
	protected void onPause() {
		// mMapView.onPause();

		super.onPause();
	}

	@Override
	protected void onResume() {
		// mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mLocClient.stop();
		// 退出时销毁定位
		// mLocClient.stop();
		// 关闭定位图层
		// mBaiduMap.setMyLocationEnabled(false);
		// mMapView.onDestroy();
		// mMapView = null;
		super.onDestroy();
	}

	// 地理反编译
	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		// TODO 自动生成的方法存根
		if (arg0 == null || arg0.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(FirstMainActivity.this, "抱歉，未能找到结果",
					Toast.LENGTH_LONG).show();
			return;
		}
		// mBaiduMap.clear();
		// mBaiduMap.addOverlay(new MarkerOptions().position(arg0.getLocation())
		// .icon(BitmapDescriptorFactory
		// .fromResource(R.drawable.icon_marka)));
		// mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(arg0.getLocation()));
		String strInfo = String.format("纬度：%f 经度：%f",
				arg0.getLocation().latitude, arg0.getLocation().longitude);
		// Toast.makeText(MainActivity.this, strInfo, Toast.LENGTH_LONG).show();
		tv_show3.setText("" + arg0.getLocation().latitude);
		tv_show4.setText("" + arg0.getLocation().longitude);
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
		// TODO 自动生成的方法存根

	}

}
