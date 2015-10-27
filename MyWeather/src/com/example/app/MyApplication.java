package com.example.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.example.bean.City;
import com.example.db.CityDB;

import android.app.Application;
import android.os.Environment;
import android.util.Log;
import android.widget.ListView;

public class MyApplication extends Application {
	private static final String TAG="MyApp";
	private static Application mApplication;
	private CityDB mCityDB;
	private static List<City> mCityList;
	private boolean isCityListComplite;
	private static List<String> list;
	public static String cityLBS="";
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i(TAG, "MyApplication-->onCreate()");
		mApplication=this;
		isCityListComplite=false;
		mCityDB=initCityDB();
		initCityList();
	}
	public static List<String> getList(){
		return list;
	}
	
	public static List<City> getCityList(){
		return mCityList;
	}
	public static Application getInstance(){
		return mApplication;
	}
	private CityDB initCityDB(){
		String path="/data"
				+Environment.getDataDirectory().getAbsolutePath()
				+File.separator+"com.example.helloworld"
				+File.separator
				+CityDB.CITY_DB_NAME;
		File db=new File(path);
		if(!db.exists()){
			Log.i(TAG, "db is not exists");
			try {
				InputStream is=getAssets().open("city.db");
				FileOutputStream fos=new FileOutputStream(db);
				int len=-1;
				byte[] buffer=new byte[1024];
				while((len=is.read(buffer))!=-1){
					fos.write(buffer, 0, len);
					fos.flush();
				}
				fos.close();
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//T.showLong(mApplication,e.getMessage());
				System.exit(0);
			}
		}
		return new CityDB(this,path);
	}
	
	private void initCityList(){
		mCityList=new ArrayList<City>();
		new Thread(new Runnable(){
			public void run(){
				isCityListComplite=false;
				if(prepareCityList())
					isCityListComplite=true;
			}
		}).start();
	}
	private boolean prepareCityList(){
		mCityList=mCityDB.getAllCity();
		list=new ArrayList<String>();
		for(City city : mCityList){
			String cityName=city.getCity();
			list.add(cityName);
			Log.i(TAG, cityName);
		}
		     
		return true;
	}
	
	public static City findCity(String name){
		City city=new City();
		for(City newcity : mCityList){
			if(newcity.getCity().equals(name))
			{city=newcity;}
		}
		return city;
	}

}
