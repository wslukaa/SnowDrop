package com.example.helloworld;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import com.example.app.MyApplication;
import com.example.bean.City;
import com.example.bean.TodayWeather;
import com.example.bean.WeatherInfo;
import com.example.fragment.FirstWeatherFragment;
import com.example.fragment.SecondWeatherFragment;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class MainActivity extends FragmentActivity implements
		View.OnClickListener {

	private static final String TAG = "MainAc";
	private TextView titleCity, cityTv, timeTv, humidityTv, weekTv, pmDataTv,
			temperatureTv, climateTv, windTv;

	private ImageView  mCityManagerBtn, baseImageView,touxiangView,
			shareImageView, updateImageView;

	private WeatherInfo mWeatherinfo;
	private WeatherPagerAdapter mWeatherPagerAdapter;
	private ViewPager mViewPager;
	private List<Fragment> fragments;
	private String url = "http://www.weather.com.cn/data/sk/101190201.html";

	public LocationClient mLocationClient = null;
	private FeedbackAgent agent;
	private String str, cityString,addrStr,tempStr;
	private String city="无锡市";
	final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share",RequestType.SOCIAL);;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		Log.i(TAG, "MainActivity-->onCreate()");
		initView();
		
		MobclickAgent.updateOnlineConfig( this );
		agent = new FeedbackAgent(this);
		agent.sync();
		
		// 首先在您的Activity中添加如下成员变量
		
		// 设置分享内容
		mController.setShareContent("Welcome To SpringSpirit Weather!!! ");
		// 设置分享图片, 参数2为图片的url地址
		//mController.setShareMedia(new UMImage(getActivity(), "http://www.umeng.com/images/pic/banner_module_social.png"));
		
		fragments = new ArrayList<Fragment>();
		fragments.add(new FirstWeatherFragment());
		fragments.add(new SecondWeatherFragment());
		mViewPager = (ViewPager) this.findViewById(R.id.viewpager);
		mWeatherPagerAdapter = new WeatherPagerAdapter(
				getSupportFragmentManager(), fragments);
		mViewPager.setAdapter(mWeatherPagerAdapter);

		mCityManagerBtn = (ImageView) this.findViewById(R.id.t_city);
		mCityManagerBtn.setOnClickListener(this);
		baseImageView = (ImageView) this.findViewById(R.id.t_base);
		shareImageView = (ImageView) this.findViewById(R.id.t_share);
		updateImageView = (ImageView) this.findViewById(R.id.t_update);
		touxiangView=(ImageView) this.findViewById(R.id.t_touxiang);
		baseImageView.setOnClickListener(this);
		shareImageView.setOnClickListener(this);
		updateImageView.setOnClickListener(this);
		touxiangView.setOnClickListener(this);

		Intent myintent = getIntent();
		if (myintent.hasExtra("cityname")) {
			String cityname = myintent.getStringExtra("cityname");
			Log.i("mytag", cityname);
			city=cityname+"市";
			updateView(cityname);
		}

		

		mLocationClient = new LocationClient(this); // 声明LocationClient类
		// 注册监听函数
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setOpenGps(true);
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02
		option.setScanSpan(500000);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		mLocationClient.setLocOption(option);

		mLocationClient.registerLocationListener(new BDLocationListener() {

			@Override
			public void onReceiveLocation(BDLocation location) {
				if (location == null)
					return;
				StringBuffer sb = new StringBuffer(256);
				sb.append("time : ");
				sb.append(location.getTime());
				sb.append("\nerror code : ");
				sb.append(location.getLocType());
				sb.append("\nlatitude : ");
				sb.append(location.getLatitude());
				sb.append("\nlontitude : ");
				sb.append(location.getLongitude());
				sb.append("\nradius : ");
				sb.append(location.getRadius());
				if (location.getLocType() == BDLocation.TypeGpsLocation) {
					sb.append("\nspeed : ");
					sb.append(location.getSpeed());
					sb.append("\nsatellite : ");
					sb.append(location.getSatelliteNumber());
				} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
					sb.append("\naddr : ");
					sb.append(location.getAddrStr());
				}
				str = "latitude : " + location.getLatitude() + "  lontitude : "
						+ location.getLongitude();
				Log.i("mytag", "------>"+str);
				cityString = location.getCity();
				Log.i("mytag", "------>city: "+cityString);
				addrStr=location.getAddrStr();
				Log.i("mytag", "------>addrStr: "+addrStr);
				if(!cityString.equals(city)&&cityString != null){
					tempStr=cityString.substring(0, cityString.length()-1);
					if(tempStr!=null){
						updateView(tempStr);
						
						new Thread() {
							public void run() {
								getTodayWeatherInfo();
								Log.i("mytag", "+++++handleMessage  1");
								mHandler.sendEmptyMessage(1);
							}
						}.start();
					}
				}
				
			}

			public void onReceivePoi(BDLocation poiLocation) {
			}

		});
		

		
		new Thread() {
			public void run() {
				getTodayWeatherInfo();
				Log.i("mytag", "+++++handleMessage  1");
				mHandler.sendEmptyMessage(1);
			}
		}.start();
		
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		}
	
	private void updateView(String name) {
		City city = MyApplication.findCity(name);
		url = "http://www.weather.com.cn/data/sk/" + city.getNumber() + ".html";
		Log.i("mytag", "-------------->>>city:"+city+" url:"+url);
	}

	private void initView() {
		titleCity = (TextView) this.findViewById(R.id.title_cityname);
		cityTv = (TextView) this.findViewById(R.id.t_info_address);
		timeTv = (TextView) this.findViewById(R.id.t_info_time);
		humidityTv = (TextView) this.findViewById(R.id.t_info_shidu);
		weekTv = (TextView) this.findViewById(R.id.t_info_jintian);
		temperatureTv = (TextView) this.findViewById(R.id.t_info_wendu);
		windTv = (TextView) this.findViewById(R.id.t_info_weifen);
		climateTv = (TextView) this.findViewById(R.id.t_info_duoyun);

		titleCity.setText("无锡天气");
		cityTv.setText("无锡");
		timeTv.setText("同步中...");
		humidityTv.setText("N/A");
		this.weekTv.setText("N/A");
		this.temperatureTv.setText("N/A");
		this.climateTv.setText("N/A");
		this.windTv.setText("N/A");

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				Log.i("MyWeather", "+++++handleMessage");
				titleCity.setText(mWeatherinfo.getCity() + "天气");
				timeTv.setText("今天" + mWeatherinfo.getTime() + "发布");
				cityTv.setText(mWeatherinfo.getCity());
				humidityTv.setText("湿度" + mWeatherinfo.getSD());
				weekTv.setText(mWeatherinfo.getWS());
				temperatureTv.setText(mWeatherinfo.getTemp() + "℃");
				windTv.setText(mWeatherinfo.getWD());
				climateTv.setText(mWeatherinfo.getWSE());
				break;
			}
		};
	};

	private void getTodayWeatherInfo() {

		String weatherResult = connServerForResult(url);
		parseTodayWeatherInfo(weatherResult);
	}

	private void parseTodayWeatherInfo(String result) {
		if (!TextUtils.isEmpty(result) && !result.contains("页面不存在")) {
			Gson mGson = new Gson();
			TodayWeather mTodayWeather = mGson.fromJson(result,
					TodayWeather.class);
			mWeatherinfo = mTodayWeather.getWeatherinfo();
			Log.i("MyWeather", mTodayWeather.getWeatherinfo().toString());
			Log.i("MyWeather", mTodayWeather.getWeatherinfo().getTemp());
		}
	}

	private String connServerForResult(String url) {
		String strResult = "";
		HttpGet httprequest = new HttpGet(url);
		HttpClient httpclient = new DefaultHttpClient();
		try {
			HttpResponse httpresponse = httpclient.execute(httprequest);
			if (httpresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				strResult = EntityUtils.toString(httpresponse.getEntity());
				Log.i("MyWeather", strResult);
				return strResult;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strResult;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.t_update: {
			Log.i("mytag", "-->test t_update");
			Toast.makeText(this, "正在进行刷新!", Toast.LENGTH_SHORT).show();
			new Thread() {
				public void run() {
					getTodayWeatherInfo();
					Log.i("mytag", "+++++handleMessage  1");
					mHandler.sendEmptyMessage(1);
				}
			}.start();
			break;
		}
		case R.id.t_city: {
			Log.i("mytag", "-->test t_city");
			Intent i = new Intent(this, CityActivity.class);
			startActivity(i);
			break;
		}
		case R.id.t_base: {
			if (mLocationClient == null) {
				return;
			}
			Toast.makeText(this, "正在进行城市定位!", Toast.LENGTH_SHORT).show();
			Log.i("mytag", "-> t_base_test");
			if (mLocationClient.isStarted()) {
				mLocationClient.stop();
			} else {
				mLocationClient.start();
				mLocationClient.requestLocation();
			}
			break;
		}
		case R.id.t_share: {
			Log.i("mytag", "-->test t_share");
			mController.getConfig().removePlatform( SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN);
			mController.openShare(MainActivity.this, false);
		    //agent.startFeedbackActivity();
			break;
		}
		case R.id.t_touxiang: {
		    agent.startFeedbackActivity();
			break;
		}
		default:
			break;
		}
	}

	 @Override
	 protected void onDestroy() {
	 super.onDestroy();
	 if (mLocationClient != null && mLocationClient.isStarted()) {
		 mLocationClient.stop();
		 mLocationClient = null;
	 }
	 }

}
