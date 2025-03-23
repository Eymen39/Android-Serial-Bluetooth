package com.example.blueserial;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BLMessageManager {

    BluetoothAdapter adapter;
    BluetoothSocket socket;
    OutputStream outputStream;
    InputStream inputStream;
    BluetoothDevice device;
    byte[] readbuffer;
    int readbufferposition;
    BluetoothListener listener;

    private  BLMessageManager(BluetoothListener listener){
        adapter=BluetoothAdapter.getDefaultAdapter();
        this.listener=listener;


    }




    public void sendDate(String text){
        try {
            outputStream.write(text.getBytes());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public void startListening(){
        readbuffer= new byte[1024];
        readbufferposition=0;
        Thread lsitenerThread= new Thread(()->{

            while(!Thread.currentThread().isInterrupted()){

                try {
                    int availableBytes= inputStream.available();
                    if(availableBytes>0){
                        byte[] readyBytes= new byte[availableBytes];
                        int reallyReadBytes=inputStream.read(readyBytes);
                        byte[] remainingBytes= new byte[availableBytes-reallyReadBytes];

                        if(reallyReadBytes>0){
                            for(int i=0; i<availableBytes; i++){



                            }
                        }


                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            }


        });


    }
    public void openConnection(){

    }
    public void findDevices(){

    }

}
