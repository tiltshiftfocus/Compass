package com.jerry.compass;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {
	
	private ImageView compassArrow;
	private ImageView compassBase;
	private float currentDegree = 0f;
	private TextView degreeText;
	private TextView dateText;
	private TextView timeText;
	private Date todaysDate;
	
	//private Animation fadeZoomIn;
	
	private SensorManager mSensorManager;
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd/MM/yyyy");
	private SimpleDateFormat timeFormat24h = new SimpleDateFormat("HH:mm:ss");
	private SimpleDateFormat timeFormat12h = new SimpleDateFormat("hh:mm:ss aa");
	
	private boolean mActive;
	private final Handler mHandler;
	
	private final Runnable mRunnable = new Runnable(){
		public void run(){
			if(mActive){
				if(timeText!=null){
					timeText.setText(getTime());
				}
				mHandler.postDelayed(mRunnable, 1000);
			}
		}
	};
	
	public MainActivity(){
		mHandler = new Handler();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ActionBar aB = getActionBar();
		aB.hide();
		
		Typeface font1 = Typeface.createFromAsset(getAssets(), "TrajanPro-Regular.otf");
		
		compassArrow = (ImageView)findViewById(R.id.compass_arrow);
		compassBase = (ImageView)findViewById(R.id.compass_base);
		//fadeZoomIn = AnimationUtils.loadAnimation(this, R.anim.fadezoom_in);
		
		degreeText = (TextView)findViewById(R.id.degree_text);
		degreeText.setTypeface(font1);
		dateText = (TextView)findViewById(R.id.today_date);
		timeText = (TextView)findViewById(R.id.time_now);
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		
		//compassArrow.setVisibility(View.VISIBLE);
		//compassArrow.startAnimation(fadeZoomIn);
		
		//compassBase.setVisibility(View.VISIBLE);
		//compassBase.startAnimation(fadeZoomIn);
		
		todaysDate = new Date();
		dateText.setText(dateFormat.format(todaysDate));
		startClock();
	}
	
	private void startClock(){
		mActive = true;
		mHandler.post(mRunnable);
	}
	
	private String getTime(){
		if(android.text.format.DateFormat.is24HourFormat(this)){
			return timeFormat24h.format(new Date(System.currentTimeMillis()));
		}
		return timeFormat12h.format(new Date(System.currentTimeMillis()));
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), 
				SensorManager.SENSOR_DELAY_GAME);
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		int degree = Math.round(event.values[0]);
		String direction = "N";
		
		if(degree==0||degree==360){
			direction = "N";
		}else if(degree>=45&&degree<90){
			direction = "NE";
		}else if (degree>=90&&degree<135){
			direction = "E";
		}else if (degree>=135&&degree<180){
			direction = "SE";
		}else if (degree>=180&&degree<225){
			direction = "S";
		}else if (degree>=225&&degree<270){
			direction = "SW";
		}else if (degree>=270&&degree<315){
			direction = "W";
		}else if (degree>=315&&degree<360){
			direction = "NW";
		}
		
		degreeText.setText(Integer.toString(degree) + "° " + direction);
		
		RotateAnimation ra = new RotateAnimation(
				currentDegree,
				degree,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		
		ra.setDuration(210);
		ra.setFillAfter(true);
		
		compassArrow.startAnimation(ra);
		currentDegree = degree;
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
}
