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
import net.sf.json.JSONObject;

public class Test1{
    static String MY_API_KEY = "68c4cf128f66f45e2a64125dd96b6e38";   
    static String HTTP_URL = "http://apis.baidu.com/heweather/weather/free";
    static String HTTP_ARG = "city=";
    
    public static void main(String args[]) throws IOException{
        
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
        //System.out.println(tmp);
        
    }
    
    //*****************************
    // @param urlAll
    //            :请求接口
    // @param httpArg
    //            :参数
    // @return 返回结果
    // @throws IOException 
    //*****************************
    public static String request(String city) throws IOException {
        
    	BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        String finalUrl = HTTP_URL + "?" + HTTP_ARG + city;

        try {
            URL url = new URL(finalUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
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