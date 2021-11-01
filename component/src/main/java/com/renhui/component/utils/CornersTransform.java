package com.renhui.component.utils;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.util.Util;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

/**
 * glide处理圆角图片
 */
public class CornersTransform extends BitmapTransformation {
    private float radius;
    private final String ID = "com.bumptech.glide.transformations.FillSpace";
    private final byte[] ID_BYTES = ID.getBytes(CHARSET);

    public CornersTransform(int radius) {
        super();
        this.radius = Resources.getSystem().getDisplayMetrics().density * radius;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return cornersCrop(pool, toTransform);
    }

    private Bitmap cornersCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);

        if (result != null && !result.isRecycled()) {
            result.recycle();
            result = null;
        }

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

    /**
     * 必须重写以下三方法，不然刷新会有闪烁
     *
     * @author 孙广超
     * @time 2019/12/9
     **/
    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
        byte[] radiusData = ByteBuffer.allocate(4).putInt((int) radius).array();
        messageDigest.update(radiusData);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof CornersTransform) {
            CornersTransform other = (CornersTransform) obj;
            return radius == other.radius;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Util.hashCode(ID.hashCode(), Util.hashCode(radius));
    }
}
