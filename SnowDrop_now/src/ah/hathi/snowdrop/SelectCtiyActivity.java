package ah.hathi.snowdrop;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class SelectCtiyActivity extends Activity implements TextWatcher, OnClickListener {
	
	private TextView backBtn = null, city1 = null, city2 = null, city3 = null;
	private EditText edit2 = null, edit3 = null;
	private ScrollView scrollView = null;
	private RadioGroup radioGroup = null;
	private Button city2Btn = null, city3Btn = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_city);
		initView();
		refresh();
	}
	
	private void initView() {
		radioGroup = (RadioGroup) findViewById(R.id.layout1_choose);
		scrollView = (ScrollView) findViewById(R.id.select_city);
		backBtn = (TextView) findViewById(R.id.layout0_back);
		city1 = (TextView) findViewById(R.id.layout2_city1);
		city2 = (TextView) findViewById(R.id.layout2_city2);
		city3 = (TextView) findViewById(R.id.layout2_city3);
		edit2 = (EditText) findViewById(R.id.layout3_edit);
		edit3 = (EditText) findViewById(R.id.layout4_edit);
		city2Btn = (Button) findViewById(R.id.layout3_submit);
		city3Btn = (Button) findViewById(R.id.layout4_submit);
		backBtn.setOnClickListener(this);
		city2Btn.setOnClickListener(this);
		city3Btn.setOnClickListener(this);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {             
            @Override  
            public void onCheckedChanged(RadioGroup group, int checkedId) {  
//                int radioButtonId = group.getCheckedRadioButtonId();  
//                RadioButton rb = (RadioButton)findViewById(radioButtonId);  
//                String radioButtonLabel = rb.getText().toString();  
                
                switch (checkedId) {
                	case R.id.choose_1:
                		BaseData.getInstance().setCurrentCity(BaseData.getInstance().getLocation());
                		break;
                	case R.id.choose_2:
                		BaseData.getInstance().setCurrentCity(BaseData.getInstance().getCity2());
                		break;
                	case R.id.choose_3:
                		BaseData.getInstance().setCurrentCity(BaseData.getInstance().getCity3());;
                		break;
                	default:
                		BaseData.getInstance().setCurrentCity(BaseData.getInstance().getLocation());
                		break;
                }
                refresh();
            }  
        }); 
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.layout0_back:
			clickBackBtn();
			break;
		case R.id.layout3_submit:
			clickCity2Btn();
			break;
		case R.id.layout4_submit:
			clickCity3Btn();
			break;
		}
	}
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	private void clickBackBtn() {
		Intent i = new Intent(this, WeatherActivity.class);
		startActivityForResult(i, 0);
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
	
	private void clickCity2Btn() {
		String city = edit2.getText().toString();
		if (city == null) {
			return;
		}
		city2.setText("城市二："+city);
		BaseData.getInstance().setCity2(city);
	}
	
	private void clickCity3Btn() {
		String city = edit3.getText().toString();
		if (city == null) {
			return;
		}
		city3.setText("城市三："+city);
		BaseData.getInstance().setCity3(city);
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
	}
	
	@Override
	public void onResume() {
		super.onResume();
		refresh();
	}
}