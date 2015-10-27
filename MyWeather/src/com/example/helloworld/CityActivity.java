package com.example.helloworld;

import java.util.ArrayList;
import java.util.List;
import com.example.app.MyApplication;
import com.example.bean.City;
import com.example.pinnedHeaderListView.BladeView;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CityActivity extends Activity implements View.OnClickListener {

	private ImageView mBackBtn;
	private ListView listView;
	private TextView textView;
	private ArrayAdapter<String> adapter;
	private List<City> data = new ArrayList<City>();
	private List<String> newlist = new ArrayList<String>();
	private List<String> citynameList = new ArrayList<String>();
	private EditText edit_search;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city);

		mBackBtn = (ImageView) this.findViewById(R.id.title_back);
		mBackBtn.setOnClickListener(this);

		edit_search = (EditText) this.findViewById(R.id.searchbox);
		listView = (ListView) this.findViewById(R.id.listview);
		textView = (TextView) this.findViewById(R.id.title_name);

		data = MyApplication.getCityList();
		citynameList = MyApplication.getList();
		adapter = new ArrayAdapter<String>(CityActivity.this,
				android.R.layout.simple_list_item_1, citynameList);
		listView.setAdapter(adapter);

		// 监听搜索框
		edit_search.addTextChangedListener(new TextWatcher_Enum());
		// 监听选择城市
		selectCityListener();
		// 监听字母下拉框
		findViewListener();

	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.title_back:
			finish();
			break;
		default:
			break;
		}

	}

	class TextWatcher_Enum implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			newlist.clear();
			if (edit_search.getText() != null) {
				String input_info = edit_search.getText().toString();
				newlist = getNewData(input_info);
				adapter = new ArrayAdapter<String>(CityActivity.this,
						android.R.layout.simple_list_item_1, newlist);
				listView.setAdapter(adapter);
			}
		}
	}

	private List<String> getNewData(String input_info) {
		// 遍历list
		for (int i = 0; i < data.size(); i++) {
			String city = data.get(i).getCity();
			String allpyString = data.get(i).getAllPY();
			// 如果遍历到的名字包含所输入字符串
			if (city.contains(input_info)
					|| (allpyString.toLowerCase()).contains(input_info
							.toLowerCase())) {
				// 将遍历到的元素重新组成一个list
				newlist.add(city);
			}
		}
		return newlist;
	}

	private void findViewListener() {

		BladeView mLetterListView = (BladeView) findViewById(R.id.mLetterListView);

		mLetterListView
				.setOnItemClickListener(new com.example.pinnedHeaderListView.BladeView.OnItemClickListener() {

					@Override
					public void onItemClick(String s) {
						if (s != null) {
							int count = 0;
							Log.i("mytag", s);
							for (City city : data) {
								if (!city.getFirstPY().equals(s)) {
									count++;
								} else {
									break;
								}
							}
							listView.setSelection(count);
						}

					}
				});
	}

	private void selectCityListener() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String city = listView.getItemAtPosition(arg2).toString();
				Log.i("mytag", city);
				// 存入城市名称
				SharedPreferences sp = getSharedPreferences("city_name",
						MODE_PRIVATE);
				boolean flag = false;
				flag = sp.edit().putString("cityname", city).commit();
				Log.i("mytag", "-->" + flag);
				textView.setText(city + "天气");

				Intent intent = new Intent(CityActivity.this,
						MainActivity.class);
				intent.putExtra("cityname", city);
				startActivity(intent);
				CityActivity.this.finish();
				// 取出城市名称
				// SharedPreferences sp=getSharedPreferences("city_name",
				// MODE_PRIVATE);
				// String cityname=sp.getString("cityname", "");
				// Log.i("matag", "-->"+cityname);

			}
		});
	}

}
