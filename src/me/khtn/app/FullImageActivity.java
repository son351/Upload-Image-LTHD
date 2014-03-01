package me.khtn.app;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import me.khtn.uploadimagekhtn.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ImageView;

public class FullImageActivity extends Activity {
	private ImageView img;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_image);
		getActionBar().hide();
		Bundle data = getIntent().getExtras();
		String url = data.getString("URL");
		
		img = (ImageView) findViewById(R.id.imageView1);
		UrlImageViewHelper.setUrlDrawable(img, url);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.full_image, menu);
		return true;
	}

}
