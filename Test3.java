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
import java.util.Vector;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Test3{
	
	static class PMData{
		String name,code,pm,pm24,aqi;
		String lat,lng;
		PMData(JSONObject json){
			name = json.getString("name"); code = json.getString("code");
			pm = json.getString("pm"); pm24 = json.getString("pm24");
			aqi = json.getString("aqi");
			lat = json.getString("lat");lng = json.getString("lng");
		}
		
	}

    static String HTTP_URL = "http://166.111.206.75/lookup.php";
    static String HTTP_ARG = "city=";
	static Vector<PMData> pmData = new Vector<PMData>();
    
    public static void main(String args[]) throws IOException{
			        
        String jsonResult = request("上海");
        System.out.println(jsonResult);
        
        pmData.removeAllElements();
        JSONObject spots = JSONObject.fromString(jsonResult);
        JSONArray data = spots.getJSONArray("data");
        
        for(int i=0;i<data.length();i++){
        	JSONObject json = data.getJSONObject(i);
        	pmData.addElement(new PMData(json));
        }       
        for(int i=0;i<pmData.size();i++){
        	System.out.println(pmData.elementAt(i).lng);
        }
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
        String finalUrl = HTTP_URL + "?" + HTTP_ARG + city ;
	   
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