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

public class MainActivity extends AppCompatActivity implements UartDataCallBack,UseCameraCallBack {

    private static final String TAG=MainActivity.class.getName();

    private HmiClient hmiClient;
    private ImageView imageView;
    boolean isInitOk=false;
    private Handler mHandler;
    private Bitmap bitmap;
    private int portTag;


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
        boolean isOpen=hmiClient.openUart(portTag,this,2,HmiClient.BAUD_RATE_115200);
        int count=hmiClient.getCameraCount();
        /*if(count>0)
        {
            isInitOk = hmiClient.initUsbCamera(this, count);
            if(!isInitOk)
            {
                Toast.makeText(MainActivity.this,"open usb camera  fail!!!", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(MainActivity.this,"not usb camera device!!!", Toast.LENGTH_LONG).show();
            //count=mClient.addCamDev(0);
            //isInitOk = mClient.initUsbCamera(this, count);
            //if(!isInitOk)
            //{
            //	Toast.makeText(MainActivity.this,"open usb camera  fail!!!", Toast.LENGTH_LONG).show();
            //}


        }*/

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

    private Bitmap update(Bitmap bitmap) {
        int height=bitmap.getHeight();
        int width=bitmap.getWidth();
        for(int i=0;i<100;i++){
            for (int j=0;j<100-i;j++){
                bitmap.setPixel(j,i,Color.argb(0, 0, 0, 0));
            }
            for (int j=100-i;j>0;j--){
                bitmap.setPixel(width-j,i,Color.argb(0, 0, 0, 0));
            }
        }
        for(int i=height-1;i>height-101;i--){
            for (int j=0;j<100+i-height;j++){
                bitmap.setPixel(j,i,Color.argb(0, 0, 0, 0));
            }

            for (int j=101+i-height;j>0;j--){
                bitmap.setPixel(width-j,i,Color.argb(0, 0, 0, 0));
            }
        }
        return bitmap;
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
//                            Toast.makeText(MainActivity.this,"open usb camera  fail!!!", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Log.i(TAG, "UartData not usb camera device!!!");
//                        Toast.makeText(MainActivity.this,"not usb camera device!!!", Toast.LENGTH_LONG).show();
                        //count=mClient.addCamDev(0);
                        //isInitOk = mClient.initUsbCamera(this, count);
                        //if(!isInitOk)
                        //{
                        //	Toast.makeText(MainActivity.this,"open usb camera  fail!!!", Toast.LENGTH_LONG).show();
                        //}
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
