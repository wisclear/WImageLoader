package com.wisclear.image.wimageloader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import com.wisclear.image.wimageloader.image.BitmapCallBack;
import com.wisclear.image.wimageloader.image.ILoaderStrategy;
import com.wisclear.image.wimageloader.image.LoaderOptions;

import java.security.MessageDigest;

public class GlideLoader implements ILoaderStrategy {
    @Override
    public void loadImage(LoaderOptions options) {
        Log.d("ImageLoader","--------------load image with Glide!----------");

        RequestManager requestManager = Glide.with(options.context);
        RequestBuilder requestBuilder = null;
        RequestOptions requestOptions = new RequestOptions();

        if (options.url != null) {
            requestBuilder = requestManager.load(options.url);
        } else if (options.file != null) {
            requestBuilder = requestManager.load(options.file);
        }else if (options.drawableResId != 0) {
            requestBuilder = requestManager.load(options.drawableResId);
        } else if (options.uri != null){
            requestBuilder = requestManager.load(options.uri);
        }

        if (requestBuilder == null) {
            throw new NullPointerException("requestCreator must not be null");
        }
        if (options.targetHeight > 0 && options.targetWidth > 0) {
            requestOptions.override(options.targetWidth, options.targetHeight);
        }
        if (options.isCenterInside) {
            requestOptions.centerInside();
        } else if (options.isCenterCrop) {
            requestOptions.centerCrop();
        }
        if (options.format != null) {
            requestOptions.format(options.format);
        }
        if (options.errorResId != 0) {
            requestOptions.error(options.errorResId);
        }
        if (options.placeholderResId != 0) {
            requestOptions.placeholder(options.placeholderResId);
        }
        if (options.bitmapAngle != 0) {
            requestOptions.bitmapTransform((new GlideRoundedCornersTransformation(options.context, (int) options.bitmapAngle)));
        }
//        if (options.skipLocalCache) {
//            requestCreator.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE);
//        }
//        if (options.skipNetCache) {
//            requestCreator.networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE);
//        }
//        if (options.degrees != 0) {
//            requestOptions.rotate(options.degrees);
//        }

        if (options.targetView instanceof ImageView) {
            requestBuilder.apply(requestOptions).into(((ImageView)options.targetView));
        } else if (options.callBack != null){
            requestBuilder.apply(requestOptions).into(new GlideTarget(options.callBack));
        }
    }

    @Override
    public void clearMemoryCache() {

    }

    @Override
    public void clearDiskCache() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                Glide.get(App.gApp).clearDiskCache();
            }
        }.start();
    }


    class GlideTarget extends SimpleTarget<Bitmap> {
        BitmapCallBack callBack;

        protected GlideTarget(BitmapCallBack callBack) {
            this.callBack = callBack;
        }
        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            if (this.callBack != null) {
                this.callBack.onBitmapLoaded(resource);
            }
        }

        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {
            super.onLoadFailed(errorDrawable);
            if (this.callBack != null) {
                this.callBack.onBitmapFailed(errorDrawable);
            }
        }
    }

    /**
     * 圆角转换
     *
     * @author Jack
     */
    class GlideRoundedCornersTransformation extends BitmapTransformation {

        private float radius = 0f;

        GlideRoundedCornersTransformation(Context context, int radius) {
            if (radius < 0) {
                radius = 0;
            }
            this.radius = Resources.getSystem().getDisplayMetrics().density * radius;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return roundCrop(pool, toTransform);
        }

        private Bitmap roundCrop(BitmapPool pool, Bitmap source) {
            Log.d("ImageLoader","--------------load image with Glide!----------roundCrop:"+radius);

            if (source == null) return null;
            if (radius == 0)
                return source;

            Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
            canvas.drawRoundRect(rectF, radius, radius, paint);
            return result;
        }


        public String getId() {
            return getClass().getName() + Math.round(radius);
        }

        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

        }
    }

//    class GlideTransformation implements Transformation {
//        private float bitmapAngle;
//
//        protected GlideTransformation(float corner){
//            this.bitmapAngle = corner;
//        }
//
//        @Override
//        public Bitmap transform(Bitmap source) {
//            float roundPx = bitmapAngle;//圆角的横向半径和纵向半径
//            Bitmap output = Bitmap.createBitmap(source.getWidth(),
//                    source.getHeight(), Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(output);
//            final int color = 0xff424242;
//            final Paint paint = new Paint();
//            final Rect rect = new Rect(0, 0, source.getWidth(),source.getHeight());
//            final RectF rectF = new RectF(rect);
//            paint.setAntiAlias(true);
//            canvas.drawARGB(0, 0, 0, 0);
//            paint.setColor(color);
//            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
//            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//            canvas.drawBitmap(source, rect, rect, paint);
//            source.recycle();
//            return output;
//        }
//
//        @Override
//        public String key() {
//            return "bitmapAngle()";
//        }
//    }
}
