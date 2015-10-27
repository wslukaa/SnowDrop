package com.example.bean;

public class TodayWeather
{
	WeatherInfo weatherinfo;

	public WeatherInfo getWeatherinfo() {
		return weatherinfo;
	}

	public void setWeatherinfo(WeatherInfo weatherinfo) {
		this.weatherinfo = weatherinfo;
	}
	
	@Override
	public String toString()
	{
		return "TodayWeather [weatherinfo = " + weatherinfo + ", toString()="
				+ super.toString() + "]";
	}
}