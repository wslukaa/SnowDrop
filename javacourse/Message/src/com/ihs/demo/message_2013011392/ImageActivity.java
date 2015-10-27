package com.ihs.demo.message_2013011392;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ihs.app.framework.activity.HSActivity;
import com.ihs.message_2013011392.R;

public class ImageActivity extends HSActivity {
    private ImageView imageView;
    private String path;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        path = getIntent().getStringExtra("path");
        setContentView(R.layout.activity_image);
        imageView = (ImageView) findViewById(R.id.originImage);
        if (!path.equals(null))
        	imageView.setImageBitmap(BitmapFactory.decodeFile(path));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}