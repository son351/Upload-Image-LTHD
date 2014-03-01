package me.villani.lorenzo.android.cropimage.example;

import java.io.File;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.android.camera.CropImageIntentBuilder;

public class LauncherActivity extends Activity implements OnClickListener
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        final View button;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        button = findViewById(R.id.button_launch);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(final View view) {
        final Uri dest = Uri.fromFile(new File(getFilesDir(), "test.jpg"));
        final CropImageIntentBuilder builder = new CropImageIntentBuilder(100, 100, dest);

        startActivity(builder.getIntent(this));
    }
}
