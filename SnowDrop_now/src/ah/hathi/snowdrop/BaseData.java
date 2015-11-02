package ah.hathi.snowdrop;

import java.io.Serializable;

public class BaseData implements Serializable {   
	
	private int style;
	private String location, familyCity, bFCity, city2, city3, currentCity;
	
	public int getStyle() {
		return style;
	}
	
	public void setLocation(String location) {
		this.location = location;
		currentCity = location;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setStyle(int style) {
		this.style = style;
	}
	
	public String getFamilyCity() {
		return familyCity;
	}
	
	public void setFamilyCity(String familyCity) {
		this.familyCity = familyCity;
	}
	
	public String getBFCity() {
		return bFCity;
	}
	
	public void setBFCity(String bFCity) {
		this.bFCity = bFCity;
	}
	
	public String getCity2() {
		return city2;
	}
	
	public void setCity2(String city2) {
		this.city2 = city2;
	}
	
	public String getCity3() {
		return city3;
	}
	
	public void setCity3(String city3) {
		this.city3 = city3;
	}
	
	public String getCurrentCity() {
		return currentCity;
	}
	
	public void setCurrentCity(String currentCity) {
		this.currentCity = currentCity;
	}
     
    private static class SingletonHolder {    
        /**  
         * 单例对象实例  
         */    
        static final BaseData INSTANCE = new BaseData();    
    }    
     
    public static BaseData getInstance() {    
        return SingletonHolder.INSTANCE;    
    }    
     
    /**  
     * private的构造函数用于避免外界直接使用new来实例化对象  
     */    
    private BaseData() {
    	style = 1;
    	location = "北京海淀";
    	currentCity = "北京海淀";
    	city2 = "河南南阳";
    	city3 = "哈尔滨";
    	familyCity = "Nanyang";
    	bFCity = "Haerbin";
    }    
     
    /**  
     * readResolve方法应对单例对象被序列化时候  
     */    
    private Object readResolve() {    
        return getInstance();    
    }    
}