package me.khtn.app;

import me.khtn.adapter.TwoTabAdapter;
import me.khtn.uploadimagekhtn.R;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class UploadActivity extends FragmentActivity {	
	private String token;
	private PagerTabStrip pagerTabStrip;
	private ViewPager pager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);		
		getActionBar().setTitle(getString(R.string.actionbar));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Bundle data = getIntent().getExtras();
		token = data.getString("TOKEN");
		
		pager = (ViewPager) findViewById(R.id.viewpager);
		pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_header);
		pagerTabStrip.setDrawFullUnderline(true);
		pagerTabStrip.setTabIndicatorColor(Color.WHITE);
		
		TwoTabAdapter adapter = new TwoTabAdapter(getSupportFragmentManager(), this, token);
		pager.setAdapter(adapter);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return true;
	}
	
}
