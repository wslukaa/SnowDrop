package ah.hathi.snowdrop;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.baidu.mapapi.SDKInitializer;

import ah.hathi.snowdrop.SelectCtiyActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class WeatherActivity extends FragmentActivity implements OnClickListener {

	private FragmentPage1 fragmentPage1;  
    private FragmentPage2 fragmentPage2;  
    private FragmentPage3 fragmentPage3;  
    
    private FrameLayout focusFl, mapFl, historyFl;
    private ImageView focusIv, mapIv, historyIv, mCityManagerBtn, mHomeBtn, refreshBtn;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext()); 
		setContentView(R.layout.activity_weather);
		
		initView();
		initData();
		clickFocusBtn();
	}

	private void initView() {
		focusFl = (FrameLayout)findViewById(R.id.layout_focus);
		mapFl = (FrameLayout)findViewById(R.id.layout_map);
		historyFl = (FrameLayout)findViewById(R.id.layout_history);
		
		focusIv = (ImageView)findViewById(R.id.frag_focus);
		mapIv = (ImageView)findViewById(R.id.frag_map);
		historyIv = (ImageView)findViewById(R.id.frag_history);
		mCityManagerBtn = (ImageView) findViewById(R.id.frag_city);
		mHomeBtn = (ImageView) findViewById(R.id.frag_home);
		refreshBtn = (ImageView) findViewById(R.id.frag_refresh);
	}
	
	private void initData() {
		focusFl.setOnClickListener(this);
		mapFl.setOnClickListener(this);
		historyFl.setOnClickListener(this);
		mCityManagerBtn.setOnClickListener(this);
		mHomeBtn.setOnClickListener(this);
		refreshBtn.setOnClickListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.weather, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.layout_focus:
			clickFocusBtn();
			break;
		case R.id.layout_map:
			clickMapBtn();
			break;
		case R.id.layout_history:
			clickHistoryBtn();
			break;
		case R.id.frag_city:
			clickCityBtn();
			break;
		case R.id.frag_home:
			clickHomeBtn();
			break;
		case R.id.frag_refresh:
			clickRefreshBtn();
			break;
		}
	}
	
	private void clickFocusBtn() {
		fragmentPage1 = new FragmentPage1();
		FragmentTransaction fragmentTransaction = 
				this.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.frame_content, fragmentPage1);
		fragmentTransaction.commit();
		focusIv.setImageResource(R.drawable.frag_11);
		mapIv.setImageResource(R.drawable.frag_2);
		historyIv.setImageResource(R.drawable.frag_3);
		focusFl.setSelected(true);
		focusIv.setSelected(true);
		
		mapFl.setSelected(false);
		mapIv.setSelected(false);
		historyFl.setSelected(false);
		historyIv.setSelected(false);
	}
	
	private void clickMapBtn() {
		fragmentPage2 = new FragmentPage2();
		FragmentTransaction fragmentTransaction = 
				this.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.frame_content, fragmentPage2);
		fragmentTransaction.commit();
		focusIv.setImageResource(R.drawable.frag_1);
		mapIv.setImageResource(R.drawable.frag_21);
		historyIv.setImageResource(R.drawable.frag_3);
		mapFl.setSelected(true);
		mapIv.setSelected(true);
		
		focusFl.setSelected(false);
		focusIv.setSelected(false);
		historyFl.setSelected(false);
		historyIv.setSelected(false);
	}
	
	private void clickHistoryBtn() {
		fragmentPage3 = new FragmentPage3();
		FragmentTransaction fragmentTransaction = 
				this.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.frame_content, fragmentPage3);
		fragmentTransaction.commit();
		focusIv.setImageResource(R.drawable.frag_1);
		mapIv.setImageResource(R.drawable.frag_2);
		historyIv.setImageResource(R.drawable.frag_31);
		historyFl.setSelected(true);
		historyIv.setSelected(true);
		
		mapFl.setSelected(false);
		mapIv.setSelected(false);
		focusFl.setSelected(false);
		focusIv.setSelected(false);
	}
	private void clickCityBtn() {
		Intent i = new Intent(this, SelectCtiyActivity.class);
		startActivityForResult(i, 0);
	}
	
	private void clickHomeBtn() {
		Intent i = new Intent(this, HomeActivity.class);
		startActivityForResult(i, 0);
	}
	
	private void clickRefreshBtn() {
		
	}
}

