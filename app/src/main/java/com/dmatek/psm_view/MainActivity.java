package com.dmatek.psm_view;

import android.graphics.*;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.dmatek.psm_view.utils.ComUtils;
import dma.xch.hmi.api.HmiClient;
import dma.xch.hmi.api.callback.UartDataCallBack;
import dma.xch.hmi.api.callback.UseCameraCallBack;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity implements UartDataCallBack,UseCameraCallBack {

    private static final String TAG=MainActivity.class.getName();

    private HmiClient hmiClient;
    private ImageView imageView;
    boolean isInitOk=false;
    private Handler mHandler;
    private Bitmap bitmap;
    private int portTag;
    private int port=2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideVirtualKey();
        init();
    }

    private void init(){
        initPara();
        initView();
    }

    private void initPara(){
        mHandler = new Handler();
        hmiClient=HmiClient.getInstance();
        hmiClient.initSDK();
        boolean isOpen=hmiClient.openUart(portTag,this,port,HmiClient.BAUD_RATE_115200);
        int count=hmiClient.getCameraCount();

    }
    private void initView(){
        imageView=findViewById(R.id.imageView);

    }
    private void hideVirtualKey(){
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        window.setAttributes(params);
    }

    private ColorMatrix rotateMatrix;
    private ColorMatrix saturationMatrix;
    private ColorMatrix scaleMatrix;
    private ColorMatrix colorMatrix;
    //亮度 0-360 超出此范围，呈周期性变化
    float rotate=360f;
    //饱和度 0 为灰度图，纯黑白， 1 为与原图一样，但是取值可以更大
    float saturation=1f;
    //色调
    float scale=2f;

    private Bitmap update(Bitmap bitmap,float rotate,float saturation,float scale) {
        Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        Log.i(TAG, "update: "+rotate+""+saturation+""+saturation);
        rotateMatrix = new ColorMatrix();
        rotateMatrix.setRotate(0,rotate);
        rotateMatrix.setRotate(1,rotate);
        rotateMatrix.setRotate(2,rotate);

        saturationMatrix = new ColorMatrix();
        saturationMatrix.setSaturation(saturation);

        scaleMatrix = new ColorMatrix();
        scaleMatrix.setScale(scale,scale,scale,1);

        colorMatrix=new ColorMatrix();
        colorMatrix.postConcat(rotateMatrix);
        colorMatrix.postConcat(saturationMatrix);
        colorMatrix.postConcat(scaleMatrix);

        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap,0,0,paint);

        return bmp;
    }
    @Override
    public void preview(Bitmap bitmap) {
        this.bitmap=bitmap;
        mHandler.post(mUpdateUI);
    }

    final Runnable mUpdateUI = new Runnable()
    {
        @Override
        public void run()
        {
            // TODO Auto-generated method stub
            imageView.setImageBitmap(bitmap);
//            imageView.setImageBitmap(update(bitmap,rotate,saturation,scale));
        }
    };



    @Override
    public void UartData(int i, char[] chars) {
        Log.i(TAG, "UartData: "+i);
        Log.i(TAG, "UartData: "+chars.length);
        Log.i(TAG, "UartData: "+String.valueOf(chars));
        if(ComUtils.checkCom(chars)){
            if(ComUtils.checkStart(chars)){
                if(!isInitOk){
                    int count=hmiClient.getCameraCount();
                    if(count>0){
                        isInitOk = hmiClient.initUsbCamera(this, count);
                        if(!isInitOk)
                        {
                            Log.i(TAG, "UartData open usb camera  fail!!!");
                        }
                    }else{
                        Log.i(TAG, "UartData not usb camera device!!!");
                    }
                }
            }else if(ComUtils.checkFinish(chars)){
                if(isInitOk){
                    hmiClient.disConnectUsbCamera();
                    isInitOk=false;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageResource(R.color.colorImgBack);
                    }
                });
            }
        }

    }
}
