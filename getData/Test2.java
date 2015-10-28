import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Test2{
    static String MY_API_KEY = "token=5j1znBVAsnSf5xQyNQyq";
    static String HTTP_URL = "http://www.pm25.in/api/querys/pm2_5.json";
    static String HTTP_ARG = "city=";
	
    public static void main(String args[]) throws IOException{
			        
        String jsonResult = request("zhuhai");
        System.out.println(jsonResult);
        JSONArray spots = JSONArray.fromString(jsonResult);
        JSONObject last_spot = (JSONObject)spots.get(spots.length() - 1);
        Integer aqi = last_spot.getInt("aqi");
        Integer pm2_5 = last_spot.getInt("pm2_5");
        Integer pm2_5_24h = last_spot.getInt("pm2_5_24h");
        String station_code = last_spot.getString("station_code");
        String time_point = last_spot.getString("time_point");
        
        //System.out.println(last_spot.getInt("pm2_5"));
    }
    
    //************************************
    // @param urlAll
    //            :请求接口
    // @param httpArg
    //            :参数
    // @return 返回结果
    // @throws IOException 
    //************************************
    public static String request(String city) throws IOException {
        
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
}