package ah.hathi.snowdrop;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.baidu.mapapi.map.MapView;
import ah.hathi.snowdrop.BaseData;


public class FragmentPage1 extends Fragment { 

	private ScrollView scrollView = null;
	private TextView layout0_city, layout0_time, layout0_shidu, layout0_press, layout0_wendu;
	private ImageView layout0_face;
	private TextView layout1_address, layout1_time, layout1_shidu, layout1_pm, layout1_wuran;
	private ImageView layout1_face;
	private TextView layout2_wendu, layout2_fanwei, layout2_miaoshu, layout2_wind;
	private ImageView layout2_face;
	private TextView week0_time, week0_max, week0_min;
	private ImageView week0_weather, week1_weather, week2_weather, week3_weather, week4_weather;
	private TextView week1_time, week1_max, week1_min;
	private TextView week2_time, week2_max, week2_min;
	private TextView week3_time, week3_max, week3_min;
	private TextView week4_time, week4_max, week4_min;
	private TextView temperture = null, address = null;
	static String tmpNum;
	static String t = "";
	
	private void init(View view) {
		scrollView = (ScrollView)view.findViewById(R.id.focus);
		layout0_city = (TextView) view.findViewById(R.id.focus_layout0_city);
		layout0_time = (TextView) view.findViewById(R.id.focus_layout0_time);
		layout0_shidu = (TextView) view.findViewById(R.id.focus_layout0_shidu);
		layout0_press = (TextView) view.findViewById(R.id.focus_layout0_press);
		layout0_wendu = (TextView) view.findViewById(R.id.focus_layout0_wendu);
		layout0_face = (ImageView) view.findViewById(R.id.focus_layout0_face);
		layout1_address = (TextView) view.findViewById(R.id.focus_layout1_address);
		layout1_time = (TextView) view.findViewById(R.id.focus_layout1_time);
		layout1_shidu = (TextView) view.findViewById(R.id.focus_layout1_shidu);
		layout1_pm = (TextView) view.findViewById(R.id.focus_layout1_pm);
		layout1_wuran = (TextView) view.findViewById(R.id.focus_layout1_wuran);
		layout1_face = (ImageView) view.findViewById(R.id.focus_layout2_face);
		layout2_wendu = (TextView) view.findViewById(R.id.focus_layout2_wendu);
		layout2_fanwei = (TextView) view.findViewById(R.id.focus_layout2_fanwei);
		layout2_miaoshu = (TextView) view.findViewById(R.id.focus_layout2_miaoshu);
		layout2_wind = (TextView) view.findViewById(R.id.focus_layout2_wind);
		layout2_face = (ImageView) view.findViewById(R.id.focus_layout2_face);
		week0_time = (TextView) view.findViewById(R.id.week_0_time);
		week0_max = (TextView) view.findViewById(R.id.week_0_max);
		week0_min = (TextView) view.findViewById(R.id.week_0_min);
		week0_weather = (ImageView) view.findViewById(R.id.week_0_weather);
		week1_time = (TextView) view.findViewById(R.id.week_1_time);
		week1_max = (TextView) view.findViewById(R.id.week_1_max);
		week1_min = (TextView) view.findViewById(R.id.week_1_min);
		week0_weather = (ImageView) view.findViewById(R.id.week_1_weather);
		week2_time = (TextView) view.findViewById(R.id.week_2_time);
		week2_max = (TextView) view.findViewById(R.id.week_2_max);
		week2_min = (TextView) view.findViewById(R.id.week_2_min);
		week0_weather = (ImageView) view.findViewById(R.id.week_2_weather);
		week3_time = (TextView) view.findViewById(R.id.week_3_time);
		week3_max = (TextView) view.findViewById(R.id.week_3_max);
		week3_min = (TextView) view.findViewById(R.id.week_3_min);
		week0_weather = (ImageView) view.findViewById(R.id.week_3_weather);
		week4_time = (TextView) view.findViewById(R.id.week_4_time);
		week4_max = (TextView) view.findViewById(R.id.week_4_max);
		week4_min = (TextView) view.findViewById(R.id.week_4_min);
		week0_weather = (ImageView) view.findViewById(R.id.week_4_weather);
	}
/*	
	private void setData() {
		String climate = "xxx";
		String[] strs = { "晴", "晴" };
		if (climate.contains("转")) {// 天气带转字，取前面那部分
			strs = climate.split("转");
			climate = strs[0];
			if (climate.contains("到")) {// 如果转字前面那部分带到字，则取它的后部分
				strs = climate.split("到");
				climate = strs[1];
			}
		}
		L.i("处理后的天气为：" + climate);
		if (mApplication.getWeatherIconMap().containsKey(climate)) {
			int iconRes = mApplication.getWeatherIconMap().get(climate);
			weatherImg.setImageResource(iconRes);
		} else {
			// do nothing 没有这样的天气图片

		}
	}
*/		
	private static String request(String httpUrl, String httpArg) throws IOException {
	    BufferedReader reader = null;
	    String result = null;
	    StringBuffer sbf = new StringBuffer();
	    httpUrl = httpUrl + "?" + httpArg;	    
	    try {
	        URL url = new URL(httpUrl);
	        HttpURLConnection connection = (HttpURLConnection) url
	                .openConnection();
	        connection.setRequestMethod("GET");
	        // 填入apikey到HTTP header
	    	
	    	String MY_API_KEY = "68c4cf128f66f45e2a64125dd96b6e38";
	        connection.setRequestProperty("apikey",  MY_API_KEY);
	        connection.connect();
	        InputStream is = connection.getInputStream();
	        reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	        String strRead = null;
	        while ((strRead = reader.readLine()) != null) {
	            sbf.append(strRead);
	            sbf.append("\r\n");
	        }	       	      
	        reader.close();
	        result = sbf.toString();
	    } catch (Exception e) {
	        e.printStackTrace();
	        Log.i("request", e.toString());
	    }
	    //System.out.println(data);
	    return result;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) {  
		View view = inflater.inflate(R.layout.fragment_focus, null);
		init(view);
//		setData();
		temperture = (TextView)view.findViewById(R.id.focus_layout2_wendu);
		address = (TextView)view.findViewById(R.id.focus_layout1_address);	
		refresh();
		
		try{
			wait(500);
		}catch(Exception e) {
			
		}
		new Thread() {
			public void run() {
				try{
					String jsonResult;
					String httpUrl = "http://apis.baidu.com/heweather/weather/free";
					String httpArg = "city="+BaseData.getInstance().getLocation();
					jsonResult = request(httpUrl, httpArg);
				
					Log.i("json", jsonResult);
					JSONObject data = new JSONObject(jsonResult);
					JSONObject HeWeather = (JSONObject)data.getJSONArray("HeWeather data service 3.0").get(0);	
					JSONObject nowData= (JSONObject)HeWeather.get("now");
					String tmp = (String)nowData.get("tmp") + "℃";
					t = tmp;
					Log.i("tmp", tmp);			
				} catch (Exception e) {
					e.printStackTrace();
					Log.i("Create", e.toString());
				}
			}
		}.start();
		
		try{
			wait(1000);
		}catch(Exception e) {
			
		}
		temperture.setText(t);
		address.setText(BaseData.getInstance().getCurrentCity());
        return view;       
    }
	
	private void refresh() {		
		switch (BaseData.getInstance().getStyle()) {
			case 1:
				scrollView.setBackgroundColor(Color.parseColor("#FFB6C1"));
				break;
			case 2:
				scrollView.setBackgroundColor(Color.parseColor("#FF0099CC"));
				break;
			case 3:
				scrollView.setBackgroundColor(Color.parseColor("#8A2BE2"));
				break;
			default:
				scrollView.setBackgroundColor(Color.parseColor("#FFB6C1"));
				break;
		}
		temperture.setText(t);
		address.setText(BaseData.getInstance().getCurrentCity());
	}
	
	@Override
	public void onResume() {
		super.onResume();
		refresh();
	}
} 