package com.example.helloworld;

import java.util.ArrayList;
import java.util.List;

import com.example.fragment.FirstWeatherFragment;
import com.example.fragment.SecondWeatherFragment;

import android.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class WeatherPagerAdapter extends FragmentPagerAdapter {
	
	
	private List<Fragment> fragments;
	
	
	public WeatherPagerAdapter(android.support.v4.app.FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
		
		
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fragments.size();
	}

}
