package ah.hathi.snowdrop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class HomeActivity extends Activity implements TextWatcher, OnClickListener {
	
	private RadioGroup radioGroup;
	private ScrollView scrollView = null;
	private TextView backBtn = null;
	private Button familyBtn = null, bFBtn = null;
	private TextView familyCity = null, bFCity = null;
	private EditText familyEdit = null, bFEdit = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		initView();
	}
	
	private void initView() {
		radioGroup = (RadioGroup) findViewById(R.id.layout1_choose);
		scrollView = (ScrollView) findViewById(R.id.home);
		backBtn = (TextView) findViewById(R.id.layout0_back);
		familyBtn = (Button) findViewById(R.id.layout2_submit);
		bFBtn = (Button) findViewById(R.id.layout3_submit);
		familyCity = (TextView) findViewById(R.id.layout2_city);
		bFCity = (TextView) findViewById(R.id.layout3_city);
		familyEdit = (EditText) findViewById(R.id.layout2_edit);
		bFEdit = (EditText) findViewById(R.id.layout3_edit);
		backBtn.setOnClickListener(this);
		familyBtn.setOnClickListener(this);
		bFBtn.setOnClickListener(this);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {             
            @Override  
            public void onCheckedChanged(RadioGroup group, int checkedId) {  
//                int radioButtonId = group.getCheckedRadioButtonId();  
//                RadioButton rb = (RadioButton)findViewById(radioButtonId);  
//                String radioButtonLabel = rb.getText().toString();  
                
                switch (checkedId) {
                	case R.id.choose_1:
                		BaseData.getInstance().setStyle(1);
                		break;
                	case R.id.choose_2:
                		BaseData.getInstance().setStyle(2);
                		break;
                	case R.id.choose_3:
                		BaseData.getInstance().setStyle(3);
                		break;
                	default:
                		BaseData.getInstance().setStyle(1);
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
		case R.id.layout2_submit:
			clickFamilyBtn();
			break;
		case R.id.layout3_submit:
			clickBFBtn();
			break;
		default:
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}
	
	private void clickBackBtn() {
		Intent i = new Intent(this, WeatherActivity.class);
		startActivityForResult(i, 0);
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
	
	private void clickFamilyBtn() {
		String family = familyEdit.getText().toString();
		if (family == null) {
			return;
		}
		familyCity.setText("当前城市："+family);
		BaseData.getInstance().setFamilyCity(family);
	}
	
	private void clickBFBtn() {
		String bf = bFEdit.getText().toString();
		if (bf == null) {
			return;
		}
		familyCity.setText("当前城市："+bf);
		BaseData.getInstance().setBFCity(bf);
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