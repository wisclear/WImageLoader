package com.wisclear.image.wimageloader;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.wisclear.image.wimageloader.image.ImageLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //使用方式
        ImageView imageView = findViewById(R.id.imageview);
        String url = "http://img2.imgtn.bdimg.com/it/u=2850936076,2080165544&fm=206&gp=0.jpg";
        ImageLoader.getInstance()
                .load(url)
                .with(this)
                .angle(80)
                .resize(600, 600)
                .centerCrop()
                .config(Bitmap.Config.RGB_565)
                .placeholder(R.mipmap.test)
                .error(R.mipmap.test)
                .skipLocalCache(true)
                .into(imageView);
    }
}
