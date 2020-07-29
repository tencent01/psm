package com.dmatek.psm_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.Nullable;

/**
 * @Author: admin
 * @Description:
 * @Date: 2020/7/28 9:38
 * @Version 1.0
 */
public class PlayerView extends androidx.appcompat.widget.AppCompatImageView {
    private static final String TAG = PlayerView.class.getName();
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 1;
    private static final Matrix mShaderMatrix = new Matrix();
    private static final Paint mBitmapPaint = new Paint();
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;
    private int mWidth = 0;
    private Bitmap mBitmap;
    private Boolean DownOnEffectiveArea = false;

    public PlayerView(Context context) {
        super(context);
        init();
    }

    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    public PlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.setClickable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null || getWidth() * getHeight() == 0) {
            return;
        }
        mBitmap = getBitmapFromDrawable(drawable);
        if (mBitmap == null) {
            return;
        }

        setup();
        canvas.drawPath(getHexagonPath(), mBitmapPaint);

    }

    private Path mPath = null;
    public Path getHexagonPath() {
        if (mPath == null) {
            mPath = new Path();
        }
        Log.i(TAG, "getHexagonPath: "+mWidth);
        float d = (float) ((float)mWidth / 4 * (2 - Math.sqrt(3)));//六边形到边到内切圆的距离
        float r = mWidth/15;
        float p0x = r;
        float p0y = 0;


        float p1x = mWidth*14/15;
        float p1y = 0;

        float p2x = mWidth;
        float p2y = mWidth/15;

        float p3x = mWidth;
        float p3y = mWidth*14/15;

        float p4x = p1x;
        float p4y = mWidth;

        float p5x = p0x;
        float p5y = mWidth;

        float p6x = 0;
        float p6y = mWidth*14/15;

        float p7x = 0;
        float p7y = p0x;

        mPath.reset();
        Log.i(TAG, "getHexagonPath: "+"p0:("+p0x+","+p0y+")"+"p1:("+p1x+","+p1y+")"+"p2:("+p2x+","+p2y+")");
        Log.i(TAG, "getHexagonPath: "+"p3:("+p3x+","+p3y+")"+"p4:("+p4x+","+p4y+")"+"p5:("+p5x+","+p5y+")");
        mPath.moveTo(p0x,p0y);
        mPath.lineTo(p1x,p1y);
        mPath.lineTo(p2x,p2y);
        mPath.lineTo(p3x,p3y);
        mPath.lineTo(p4x,p4y);
        mPath.lineTo(p5x,p5y);
        mPath.lineTo(p6x,p6y);
        mPath.lineTo(p7x,p7y);
        mPath.lineTo(p0x,p0y);

        return mPath;
    }



    @Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(l);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                CheckIfDownOnffectiveArea(event);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //if (event.getAction() == MotionEvent.ACTION_DOWN) {
        if (!CheckIfDownOnffectiveArea(event)) { return false;}
        // }
        return super.dispatchTouchEvent(event);
    }

    private Boolean CheckIfDownOnffectiveArea(MotionEvent event) {
        DownOnEffectiveArea = computeRegion(mPath).contains((int)event.getX(),(int)event.getY());
        return DownOnEffectiveArea;
    }

    private Region computeRegion(Path path) {
        Region region = new Region();
        RectF f = new RectF();
        path.computeBounds(f, true);
        region.setPath(path, new Region((int) f.left, (int) f.top, (int) f.right, (int) f.bottom));
        return region;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);//限制为正方形
        mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(mWidth, mWidth);
    }

    public int getmWidth() {
        return mWidth;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        invalidate();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        invalidate();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        invalidate();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;
            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION,
                        COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, mWidth, mWidth);
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    private void setup() {
        if (mBitmap != null) {
            mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mBitmapPaint.setAntiAlias(true);
            mBitmapHeight = mBitmap.getHeight();
            mBitmapWidth = mBitmap.getWidth();
            updateShaderMatrix();
            mBitmapPaint.setShader(mBitmapShader);
        }
    }



    private void updateShaderMatrix() {
        float scale;
        mShaderMatrix.set(null);
        if (mBitmapWidth != mBitmapHeight) {
            scale = Math.max((float) mWidth / mBitmapWidth, (float) mWidth / mBitmapHeight);
        } else {
            scale = (float) mWidth / mBitmapWidth;
        }

        mShaderMatrix.setScale(scale, scale);//放大铺满

        float dx = mWidth - mBitmapWidth * scale;
        float dy = mWidth - mBitmapHeight * scale;
        mShaderMatrix.postTranslate(dx / 2, dy / 2);//平移居中
        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }
}
