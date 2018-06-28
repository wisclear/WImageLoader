package com.wisclear.image.wimageloader.image;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by JohnsonFan on 2018/3/7.
 */

public interface BitmapCallBack {

	void onBitmapLoaded(Bitmap bitmap);

	void onBitmapFailed(Exception e);

	void onBitmapFailed(Drawable drawable);
	public static class EmptyCallback implements BitmapCallBack {


		@Override
		public void onBitmapLoaded(Bitmap bitmap) {

		}

		@Override
		public void onBitmapFailed(Exception e) {

		}

		@Override
		public void onBitmapFailed(Drawable drawable) {

		}
	}
}
