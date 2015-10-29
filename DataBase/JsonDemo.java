
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class JsonDemo {
	
	static String MY_API_KEY = "68c4cf128f66f45e2a64125dd96b6e38";   
    static String HTTP_URL = "http://apis.baidu.com/heweather/weather/free";
    static String HTTP_ARG = "city=";
	
	public static void main(String args[]) throws Exception {
		/*
		JSONObject data = JSONObject.fromObject(jsonString);
		JSONObject HeWeather = (JSONObject)data.getJSONArray("HeWeather data service 3.0").get(0);	
		JSONObject nowData= (JSONObject)HeWeather.get("now");
		String tmp = (String)nowData.get("tmp");
		System.out.println(tmp);
		JSONArray spots = JSONArray.fromString(jsonString);
		JSONObject last_spot = (JSONObject)spots.get(spots.length() - 1);
		System.out.println((String)last_spot.get("pm2_5"));
		
		*/
		String jsonResult = request("beijing");		
        System.out.println(jsonResult);
        JSONObject data = JSONObject.fromObject(jsonResult);
        JSONObject HeWeather = (JSONObject)data.getJSONArray("HeWeather data service 3.0").get(0);	
        JSONObject nowData= (JSONObject)HeWeather.get("now");
        String fl = (String)nowData.get("fl");
        String hum = (String)nowData.get("hum");
        String pcpn = (String)nowData.get("pcpn");
        String pres = (String)nowData.get("pres");
        String tmp = (String)nowData.get("tmp");
        String vis = (String)nowData.get("vis");
        JSONObject wind = (JSONObject)nowData.get("wind");
        String deg = (String)wind.get("deg");
        String spd = (String)wind.get("spd");
		
		
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection("jdbc:sqlite:myDB.db");
		Statement stat = conn.createStatement();
//		stat.executeUpdate("create table airweather(tmp text, hum text)");
		stat.executeUpdate("insert into airweather values ("+tmp+", "+hum+")");
		conn.close();
	}
	
public static String request1(String city) throws IOException {
        
    	BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        String finalUrl = HTTP_URL + "?" + HTTP_ARG + city + "&" + MY_API_KEY;
	   
        try {
            URL url = new URL(finalUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
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
        }
        return result;
    }

	public static String request(String city) throws IOException {
	    
		BufferedReader reader = null;
	    String result = null;
	    StringBuffer sbf = new StringBuffer();
	    String finalUrl = HTTP_URL + "?" + HTTP_ARG + city;
	
	    try {
	        URL url = new URL(finalUrl);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("GET");
	        // ÃÓ»ÎapikeyµΩHTTP header
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
	    }
	    return result;
	}
}
