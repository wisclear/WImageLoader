package com.wisclear.image.wimageloader;

import android.app.Application;

import com.wisclear.image.wimageloader.image.ImageLoader;


/**
 * Created by JohnsonFan on 2017/12/9.
 */

public class App extends Application {

	public static App gApp;

	@Override
	public void onCreate() {
		super.onCreate();
		gApp = this;
		//初始化图片库
		ImageLoader.getInstance().setImageLoader(new GlideLoader());
	}
}
