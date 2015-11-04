package ah.hathi.snowdrop;

import java.util.ArrayList;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class FragmentPage3 extends Fragment{  
	
	private ScrollView scrollView = null;
	private TextView layout1_position, layout1_refresh_time;
	private TextView layout2_pm2_value, layout2_pm2_color, layout2_pm10_value, layout2_pm10_color; 
	private ImageView layout2_face;
	private TextView layout3;
	private LineChart lineChart24, lineChart6;
	private String pm2_value = null, pm10_value = null, pm2_color = null, pm10_color = null;
	
	private void init(View view) {
    	scrollView = (ScrollView)view.findViewById(R.id.history);
    	layout1_position = (TextView) view.findViewById(R.id.pm_layout1_position);
    	layout1_refresh_time = (TextView) view.findViewById(R.id.pm_layout1_refresh_time);
    	layout2_pm2_value = (TextView) view.findViewById(R.id.pm_layout2_pm2_5_value);
    	layout2_pm2_color = (TextView) view.findViewById(R.id.pm_layout2_pm2_5_color);
    	layout2_pm10_value = (TextView) view.findViewById(R.id.pm_layout2_pm10_value);
    	layout2_pm10_color = (TextView) view.findViewById(R.id.pm_layout2_pm10_color);
    	layout2_face = (ImageView) view.findViewById(R.id.pm_layout2_face);
    	layout3 = (TextView) view.findViewById(R.id.pm_layout3);
		lineChart24 = (LineChart) view.findViewById(R.id.pm_layout4_24hours);
    	lineChart6 = (LineChart) view.findViewById(R.id.pm_layout4_6hours);
	}
//MainActivity.java	
	private void setData() {
/*		if (!pm2_value.isEmpty()) {
			int pm2 = Integer.valueOf(pm2_value).intValue();
			int imageId = 0;
			if (pm2 <= 35) {
				pm2_color = "优";
				imageId = R.drawable.biz_plugin_weather_0_50;
			} else if (pm2 <= 100) {
				pm2_color = "良";
				imageId = R.drawable.biz_plugin_weather_51_100;
			} else if (pm2 <= 150) {
				pm2_color = "轻污";
				imageId = R.drawable.biz_plugin_weather_101_150;
			} else if (pm2 <= 200) {
				pm2_color = "中污";
				imageId = R.drawable.biz_plugin_weather_151_200;
			} else if (pm2 <= 300) {
				pm2_color = "重污";
				imageId = R.drawable.biz_plugin_weather_201_300;
			} else {
				pm2_color = "严重";
				imageId = R.drawable.biz_plugin_weather_greater_300;
			}
			layout2_pm2_color.setText(pm2_color);
			layout2_pm2_value.setText(pm2_value);
			layout2_face.setImageResource(imageId);
		}
*/		
		LineData lineData24 = getLineData(24, 100); 
    	LineData lineData6 = getLineData(6, 100);
        showChart(lineChart24, lineData24, Color.rgb(114, 188, 223));
        showChart(lineChart6, lineData6, Color.rgb(114, 188, 223));
	}
	
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) {  
    	View view = inflater.inflate(R.layout.fragment_history, null);
    	init(view);
    	refresh();
    	setData();
    	return view;       
    }    
    
    /**  
     * 生成一个数据  
     * @param count 表示图表中有多少个坐标点  
     * @param range 用来生成range以内的随机数  
     * @return  
     */ 
    private LineData getLineData(int count, float range) {    
        ArrayList<String> xValues = new ArrayList<String>();    
        for (int i = 0; i < count; i++) {    
            // x轴显示的数据，这里默认使用数字下标显示    
            xValues.add("" + i);    
        }    
    
        // y轴的数据    
        ArrayList<Entry> yValues = new ArrayList<Entry>();    
        for (int i = 0; i < count; i++) {    
            float value = (float) (Math.random() * range) + 3;    
            yValues.add(new Entry(value, i));    
        }    
    
        // create a dataset and give it a type    
        // y轴的数据集合    
        LineDataSet lineDataSet = new LineDataSet(yValues, "PM2.5指数" /*显示在比例图上*/);    
        // mLineDataSet.setFillAlpha(110);    
        // mLineDataSet.setFillColor(Color.RED);    
    
        //用y轴的集合来设置参数    
        lineDataSet.setLineWidth(1.75f); // 线宽    
        lineDataSet.setCircleSize(3f);// 显示的圆形大小    
        lineDataSet.setColor(Color.WHITE);// 显示颜色    
        lineDataSet.setCircleColor(Color.WHITE);// 圆形的颜色    
        lineDataSet.setHighLightColor(Color.WHITE); // 高亮的线的颜色    
    
        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();    
        lineDataSets.add(lineDataSet); // add the datasets    
    
        // create a data object with the datasets    
        LineData lineData = new LineData(xValues, lineDataSets);    
    
        return lineData;    
    }
    
    private void showChart(LineChart lineChart, LineData lineData, int color) {    
        lineChart.setDrawBorders(false);  //是否在折线图上添加边框    
    
        // no description text    
        lineChart.setDescription("");// 数据描述    
        // 如果没有数据的时候，会显示这个，类似listview的emtpyview    
        lineChart.setNoDataTextDescription("You need to provide data for the chart.");    
            
        // enable / disable grid background    
        lineChart.setDrawGridBackground(false); // 是否显示表格颜色    
        lineChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF); // 表格的的颜色，在这里是是给颜色设置一个透明度    
    
        // enable touch gestures    
        lineChart.setTouchEnabled(true); // 设置是否可以触摸    
    
        // enable scaling and dragging    
        lineChart.setDragEnabled(true);// 是否可以拖拽    
        lineChart.setScaleEnabled(true);// 是否可以缩放    
    
        // if disabled, scaling can be done on x- and y-axis separately    
        lineChart.setPinchZoom(false);//     
    
//        lineChart.setBackgroundColor(color);// 设置背景    
    
        // add data    
        lineChart.setData(lineData); // 设置数据    
    
        // get the legend (only possible after setting data)    
        Legend mLegend = lineChart.getLegend(); // 设置比例图标示，就是那个一组y的value的    
    
        // modify the legend ...    
        // mLegend.setPosition(LegendPosition.LEFT_OF_CHART);    
        mLegend.setForm(LegendForm.CIRCLE);// 样式    
        mLegend.setFormSize(6f);// 字体    
        mLegend.setTextColor(Color.WHITE);// 颜色    
//      mLegend.setTypeface(mTf);// 字体    
    
        lineChart.animateX(2500); // 立即执行的动画,x轴    
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