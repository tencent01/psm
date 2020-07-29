package com.dmatek.psm_view.utils;

import android.util.Log;

/**
 * @Author: admin
 * @Description:
 * @Date: 2020/7/28 16:46
 * @Version 1.0
 */
public class ComUtils {
    private static final String TAG= ComUtils.class.getName();

    private static char com_start=0xFF;
    private static char com_end=0xFE;
    public static final char START=0x01;
    public static final char FINISH=0x02;
    public static final char[] START_DATA=new char[]{0x73,0x74,0x61,0x72,0x74};
    public static final char[] FINISH_DATA=new char[]{0x66,0x69,0x6e,0x69,0x73,0x68};



    public static char[] generateCom(char com_start, char type, char com_end, char... data){
        int com_length=data.length+5;
        char[] com=new char[com_length];
        int index=0;
        char checkSum=0;
        com[index++]=com_start;
        checkSum+=com_start;
        com[index++]=(char) (com_length);
        checkSum+=com[index-1];
        com[index++]=type;
        checkSum+=type;
        for(char d:data){
            com[index++]=d;
            checkSum+=d;
        }
        com[index++]=(char)checkSum;
        com[index]=com_end;
        return com;
    }

    public static char[] generateTypeAndDataCom( char type, char... data){
        return generateCom(com_start,type,com_end,data);
    }

    public static char[] generateStart(){
        return generateTypeAndDataCom(START,START_DATA);
    }

    public static char[] generateFinish(){
        //0x66,0x69,0x6e,0x69,0x73,0x68
        return generateTypeAndDataCom(FINISH,FINISH_DATA);
    }

    public static boolean checkStart(char[] com){
        return checkDataCom(com,START,START_DATA);
    }

    public static boolean checkFinish(char[] com){
        //0x66,0x69,0x6e,0x69,0x73,0x68
        return checkDataCom(com,FINISH,FINISH_DATA);
    }

    public static boolean checkTypeAndDataCom(char[] com, char type,char... type_com){
        return checkCom(com,com_start,type,com_end,type_com);
    }

    public static boolean checkCom(char[] com,char com_start, char type, char com_end,char... type_com){
        if(checkCom(com,com_start,type,com_end)&&checkCom(com,type_com)){
            return true;
        }else {
            return false;
        }
    }

    public static boolean checkCom(char[] com,char com_start, char type, char com_end){
        int length=com.length;
        if(length>0&&com_start==com[0]){
            if(length>1&&com.length==com[1]){
                if(length>2&&type==com[2]){
                    if(length>3){
                        int checkSum=0;
                        for(int i=0;i<length-2;i++){
                            checkSum+=com[i];
                        }
                        if(checkSum==com[length-2]){
                            if(length>4&&com_end==com[length-1]){
                                return true;
                            }else {
                                Log.i(TAG, "com end error");
                            }
                        }else{
                            Log.i(TAG, "com check sum error");
                        }
                    }else{
                        Log.i(TAG, "com check sum length error");
                    }
                }else {
                    Log.i(TAG, "com type error");
                }
            }else {
                Log.i(TAG, "com length error");
            }
        }else{
            Log.i(TAG, "com start error");
        }
        return false;
    }

    public static boolean checkCom(char[] com,char com_start, char com_end){
        int length=com.length;
        if(length>0&&com_start==com[0]){
            if(length>1&&com.length==com[1]){
                if(com_end==com[length-1]){
                    return true;
                }else {
                    Log.i(TAG, "com end error");
                }
            }else {
                Log.i(TAG, "com length error");
            }
        }else{
            Log.i(TAG, "com start error");
        }
        return false;
    }
    public static boolean checkDataCom(char[] com,char type,char... data){
        if(com.length==data.length+5){
            if(type==com[2]){
                char checkSum=0;
                for (int i=0;i<com.length-2;i++){
                    checkSum+=com[i];
                    if(i>2&&com[i]!=data[i-3]){
                        Log.i(TAG, "com data error");
                        return false;
                    }
                }
                if(((byte)checkSum)==(byte)com[com.length-2]){
                    Log.i(TAG, "com data ok");
                    return true;
                }else{
                    Log.i(TAG, "com check sum error");
                }
            }else{
                Log.i(TAG, "com type error");
            }
        }else{
            Log.i(TAG, "com type length error");
        }
        return false;
    }

    public static boolean checkCom(char[] com){
        if(checkCom(com,com_start,com_end)){
            return true;
        }else {
            return false;
        }
    }

    public static boolean checkCom(char[] com,char... type_com){
        if(com.length==type_com.length+5){
            for (int i=0;i<type_com.length;i++){
                if(com[i+3]!=type_com[i]){
                    Log.i(TAG, "com data error");
                    return false;
                }
            }
            Log.i(TAG, "com data ok");
            return true;
        }else{
            Log.i(TAG, "com type length error");
        }
        return false;
    }

}
