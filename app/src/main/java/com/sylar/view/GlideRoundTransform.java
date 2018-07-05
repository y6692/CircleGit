package com.sylar.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * 圆角图片
 * @author djy
 * @time 2017/3/17 14:10
 */
public class GlideRoundTransform extends BitmapTransformation {
    private static final int DEFAULT_BORDER_WIDTH = 4;
    private static final int DEFAULT_BORDER_COLOR = Color.WHITE;
    private static final int DEFAULT_ROUND_PX = 24;

    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;
    private float roundPx = DEFAULT_ROUND_PX;

    public GlideRoundTransform(Context context, float roundPx) {
        super(context);
        this.roundPx = roundPx;
    }

    public GlideRoundTransform(Context context, float roundPx, int mBorderColor, int mBorderWidth) {
        super(context);
        this.roundPx = roundPx;
        this.mBorderColor = mBorderColor;
        this.mBorderWidth = mBorderWidth;
    }


    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return drawImg(pool, toTransform);
    }

    private Bitmap drawImg(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        //绘制圆角矩形
        Bitmap roundBitmap = Bitmap.createBitmap(source.getWidth(),
                source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, source.getWidth(), source.getHeight());
        RectF rectF = new RectF(rect);
        //绘制
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, rect, rect, paint);


        if(mBorderWidth > 0){
            Paint mBorderPaint = new Paint();
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setColor(mBorderColor);
            mBorderPaint.setStrokeWidth(mBorderWidth);
            mBorderPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN)); // 不加边缘就有模糊
            canvas.drawRoundRect(rectF, roundPx, roundPx, mBorderPaint);
        }
        return roundBitmap;
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
