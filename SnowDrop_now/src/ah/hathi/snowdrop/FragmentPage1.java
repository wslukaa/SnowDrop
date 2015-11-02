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
	private TextView temperture = null, address = null;
	static String tmpNum;
	static String t = "";
	
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
		scrollView = (ScrollView)view.findViewById(R.id.focus);
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