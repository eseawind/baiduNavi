package com.baidu.navi.sdkdemo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.BNaviPoint;
import com.baidu.navisdk.BaiduNaviManager.OnStartNavigationListener;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams.NE_RoutePlan_Mode;
import com.bluetooth.proj.SearchDeviceActivity;


/**
 * GPS导航Demo
 */
public class RouteGuideDemo extends Activity{
//    private BNaviPoint mStartPoint = new BNaviPoint(116.30142, 40.05087,
//            "天津理工大学", BNaviPoint.CoordinateType.GCJ02);
	private BNaviPoint mStartPoint ;
    private BNaviPoint mEndPoint ;
//    = new BNaviPoint(116.39750, 39.90882,
//            "天津工业大学", BNaviPoint.CoordinateType.GCJ02);
//    private List<BNaviPoint> mViaPoints = new ArrayList<BNaviPoint>();
    private Button mBtnAddViaPoint;
    private Button naviInfoshow;//获得显示文字导航信息的button
    private Button bluetooth;//获得转入蓝牙通信的按钮
//    Intent intent;
//    String from_note = null;
//    String to_note = null;
//    double d1 = 0;
//    double d2 = 0;
//    double c1 = 0;
//    double c2 = 0;
    public static final String MY_PREFS = "MyPrefs";
    
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_routeguide);
		SharedPreferences mySharedPreferences = getSharedPreferences(
				MY_PREFS, Activity.MODE_PRIVATE);

//		SharedPreferences.Editor editor = mySharedPreferences
//				.edit();
//		editor.commit();

//		from_note = mySharedPreferences.getString("from_note", null);
//		System.out.println("&&&&&&&&&&&&&&"+from_note);
//		//此处取得intent中的值
//		intent = getIntent();
//		from_note = intent.getStringExtra("from_note").toString();//起点地址
//		to_note = intent.getStringExtra("to_note").toString();//终点地址
//		d1 = Double.parseDouble(intent.getStringExtra("d1").toString());//起点纬度
//		d2 = Double.parseDouble(intent.getStringExtra("d2").toString());//起点经度
//		c1 = Double.parseDouble(intent.getStringExtra("c1").toString());//终点纬度
//		c2 = Double.parseDouble(intent.getStringExtra("c1").toString());//终点经度
//		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//		System.out.println(from_note + to_note +" kkkk"+ d1+" kkkk" + d2 +" kkkk"+ c1 +" kkkk"+ c2);
		
//		 mStartPoint = new BNaviPoint(116.30142, 40.05087,
//		            "天津理工大学", BNaviPoint.CoordinateType.GCJ02);
//		 
//		 mEndPoint = new BNaviPoint(116.39750, 39.90882,
//	            "天津工业大学", BNaviPoint.CoordinateType.GCJ02);
		
		Button btnStartNavigation = (Button)findViewById(R.id.button_navigation);
		btnStartNavigation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Data.cleanAll();
				System.out.println("******************************1");
//			    if (mViaPoints.size() == 0) {
			        launchNavigator();
//			    } else {
//			        launchNavigatorViaPoints();
//			    }
			}
		});
		
		findViewById(R.id.start_nav2_btn).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                if (mViaPoints.size() == 0) {
                    launchNavigator2();
//                } else {
//                    launchNavigatorViaPoints();
//                }
            }
        });
		System.out.println("******************************0");
//		mBtnAddViaPoint = (Button) findViewById(R.id.add_via_btn); 
//		mBtnAddViaPoint.setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View arg0) {
//                    addViaPoint();
//                }
//            });
		naviInfoshow=(Button)findViewById(R.id.button_getNavi);//获得显示文本导航信息按钮
		naviInfoshow.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), Data.getNaviInfo(), Toast.LENGTH_SHORT).show();
			}
		});
		bluetooth = (Button)findViewById(R.id.button_bluetooth);
		bluetooth.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(RouteGuideDemo.this,SearchDeviceActivity.class);
				startActivity(intent);
			}
		});
	}
		 

//    private void addViaPoint() {
//        EditText viaXEditText = (EditText) findViewById(R.id.et_via_x);
//        EditText viaYEditText = (EditText) findViewById(R.id.et_via_y);
//        double latitude = 0, longitude = 0;
//        try {
//            latitude = Integer.parseInt(viaXEditText.getText().toString());
//            longitude = Integer.parseInt(viaYEditText.getText().toString());
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//        }
//        // 默认坐标系为GCJ
////        BNaviPoint viaPoint = new BNaviPoint(longitude/1e5, latitude/1e5,
////                "途经点" + (mViaPoints.size()+1));
////        mViaPoints.add(viaPoint);
////        Toast.makeText(this, "已添加途经点：" + viaPoint.getName(),
////                Toast.LENGTH_SHORT).show();
////        if (mViaPoints.size() >= 3) {
////            mBtnAddViaPoint.setEnabled(false);
////        }
//    }

	/**
	 * 启动GPS导航. 前置条件：导航引擎初始化成功
	 */
	private void launchNavigator(){
		//这里给出一个起终点示例，实际应用中可以通过POI检索、外部POI来源等方式获取起终点坐标
		BaiduNaviManager.getInstance().launchNavigator(this,
				Data.getD1(), Data.getD2(),Data.getFrom_note(), 								//此处取得起点坐标和地址
		        Data.getC1(), Data.getC2(),Data.getTo_note(),									//此处取得终点坐标和地址
				NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME, 		 //算路方式
				true, 									   		 //真实导航
				BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, //在离线策略
				new OnStartNavigationListener() {				 //跳转监听
					
					@Override
					public void onJumpToNavigator(Bundle configParams) {
						Intent intent = new Intent(RouteGuideDemo.this, BNavigatorActivity.class);
						intent.putExtras(configParams);
						System.out.println("******************************2");
				        startActivity(intent);
					}
					
					@Override
					public void onJumpToDownloader() {
					}
				});
	}
	
    /**
     * 指定导航起终点启动GPS导航.起终点可为多种类型坐标系的地理坐标。
     * 前置条件：导航引擎初始化成功
     */
    private void launchNavigator2(){
        //这里给出一个起终点示例，实际应用中可以通过POI检索、外部POI来源等方式获取起终点坐标
        BNaviPoint startPoint = new BNaviPoint(117.155971,39.093136,
                "天津理工大学", BNaviPoint.CoordinateType.BD09_MC);
        BNaviPoint endPoint = new BNaviPoint(117.246565,39.139605,
                "天津工业大学", BNaviPoint.CoordinateType.BD09_MC);

        BaiduNaviManager.getInstance().launchNavigator(this,
                startPoint,                                      //起点（可指定坐标系）
                endPoint,                                        //终点（可指定坐标系）
                NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME,       //算路方式
                true,                                            //真实导航
                BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, //在离线策略
                new OnStartNavigationListener() {                //跳转监听
                    
                    @Override
                    public void onJumpToNavigator(Bundle configParams) {
                        Intent intent = new Intent(RouteGuideDemo.this, BNavigatorActivity.class);
                        intent.putExtras(configParams);
                        System.out.println("******************************3");
                        startActivity(intent);
                    }
                    
                    @Override
                    public void onJumpToDownloader() {
                    }
                });
    }

    /**
     * 增加一个或多个途经点，启动GPS导航. 
     * 前置条件：导航引擎初始化成功
     */
    private void launchNavigatorViaPoints(){
        //这里给出一个起终点示例，实际应用中可以通过POI检索、外部POI来源等方式获取起终点坐标
//        BNaviPoint startPoint = new BNaviPoint(113.97348, 22.53951, "白石洲");
//        BNaviPoint endPoint   = new BNaviPoint(113.92576, 22.48876, "蛇口");
//        BNaviPoint viaPoint1 = new BNaviPoint(113.94104, 22.54343, "国人通信大厦");
//        BNaviPoint viaPoint2 = new BNaviPoint(113.94863, 22.53873, "中国银行科技园支行");
        List<BNaviPoint> points = new ArrayList<BNaviPoint>();
        points.add(mStartPoint);
        points.addAll(null);
        points.add(mEndPoint);
        BaiduNaviManager.getInstance().launchNavigator(this,
                points,                                          //路线点列表
                NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME,       //算路方式
                true,                                            //真实导航
                BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, //在离线策略
                new OnStartNavigationListener() {                //跳转监听
                    
                    @Override
                    public void onJumpToNavigator(Bundle configParams) {
                        Intent intent = new Intent(RouteGuideDemo.this, BNavigatorActivity.class);
                        intent.putExtras(configParams);
                        System.out.println("******************************4");
                        startActivity(intent);
                    }
                    
                    @Override
                    public void onJumpToDownloader() {
                    }
                });
    }
}
