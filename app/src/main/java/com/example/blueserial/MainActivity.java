package com.example.blueserial;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity
{
    TextView myLabel;
    EditText myTextbox;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    RecyclerView recyclerView;
    RecyclerViewAdapter nachrichtenAdapter;
    ArrayList<Message> nachrichten=new ArrayList<>();
    ArrayList<BluetoothDevices> devicesEasy = new ArrayList<>();
    Set<BluetoothDevice>devices;
    volatile boolean stopWorker;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button openButton = (Button)findViewById(R.id.open);
        Button sendButton = (Button)findViewById(R.id.send);
        Button closeButton = (Button)findViewById(R.id.close);
        myLabel = (TextView)findViewById(R.id.label);
        myTextbox = (EditText)findViewById(R.id.entry);
        recyclerView=(RecyclerView)findViewById(R.id.rvMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        nachrichtenAdapter= new RecyclerViewAdapter(nachrichten);
        recyclerView.setAdapter(nachrichtenAdapter);



        //Open Button
        openButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {

                BLpopUp();

            }
        });

        //Send Button
        sendButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    sendData();
                }
                catch (IOException ex) { }
            }
        });

        //Close button
        closeButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    closeBT();
                }
                catch (IOException ex) { }
            }
        });
    }

    @SuppressLint("MissingPermission")
   private ArrayList<BluetoothDevices> findBT(String deviceName)
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            myLabel.setText("No bluetooth adapter available");
        }

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }
        else{
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){
               if(checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)!= PackageManager.PERMISSION_GRANTED){
                   requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT},1);
               }
            }
        }

        devices = mBluetoothAdapter.getBondedDevices();
        ArrayList<BluetoothDevices> devicesNames= new ArrayList<>();



        if(devices.size() > 0) {

            for (BluetoothDevice device : devices){
                BluetoothDevices devicetoAdd= new BluetoothDevices();
                devicetoAdd.setName(device.getName());
                devicesNames.add(devicetoAdd);

            };

        }
        return devicesNames;
    }

    @SuppressLint("MissingPermission")
    void openBT(BluetoothDevice device) throws IOException
    {
        mmDevice=device;
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();

        myLabel.setText("Bluetooth Opened");
    }

    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10;

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                    String timestamp= new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                                    Message nachricht= new Message(data,timestamp,false);
                                    Log.i("Data",nachricht.getZeitstempel()+nachricht.getMsg());
                                    handler.post(()->
                                    {
                                        nachrichtenAdapter.addNachricht(nachricht);
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    void sendData() throws IOException
    {
        String msg = myTextbox.getText().toString();
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
        String zeitstempel= new SimpleDateFormat("HH:mm:ss",Locale.getDefault()).format(new Date());
        Message nachricht = new Message(msg,zeitstempel,true);
        nachrichtenAdapter.addNachricht(nachricht);
    }

    void closeBT() throws IOException
    {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        myLabel.setText("Bluetooth Closed");
    }

    private void BLpopUp() {
        AlertDialog.Builder blPopUpBuilder= new AlertDialog.Builder(this);
        EditText etDeviceName = new EditText(this);
        blPopUpBuilder.setView(etDeviceName);
        RecyclerView rvBlDevices= new RecyclerView(this);
        rvBlDevices.setLayoutManager(new LinearLayoutManager(this));
        DevicesRVAdapter devicesRVAdapter=new DevicesRVAdapter(devicesEasy,clickListener());
        rvBlDevices.setAdapter(devicesRVAdapter);
        blPopUpBuilder.setView(rvBlDevices);
        devicesEasy = findBT("");
        blPopUpBuilder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (etDeviceName.getText().length() == 0) {
                    Toast.makeText(MainActivity.this, "Geben sie etwas ein", Toast.LENGTH_SHORT).show();
                } else {
                    findBT(etDeviceName.getText().toString());
                }


                dialog.dismiss();


            }
        });
        AlertDialog blPopUp= blPopUpBuilder.create();
        blPopUp.show();




    }
    private BluetoothDevice findDevice(String name){
        for (BluetoothDevice device : devices){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            if(device.getName().equals(name)){
                return device;
            }

        };
        return null;
    }
    private DevicesRVAdapter.onItemClickListner clickListener(){
          DevicesRVAdapter.onItemClickListner listener= new DevicesRVAdapter.onItemClickListner() {
              @Override
              public void onItemClick(String deviceName) {
                  try {
                      BluetoothDevice a = findDevice(deviceName);
                      if( a== null){
                          Toast.makeText(MainActivity.this, "diesesElemente existiert nicht", Toast.LENGTH_SHORT).show();
                      }else{
                          openBT(a);

                      }
                  } catch (IOException e) {
                      Toast.makeText(MainActivity.this,"Etwas ist Schiefgelaufen bei der Verbindung zum gerät",Toast.LENGTH_LONG).show();
                  }
              }


          };



        return listener;
    }


}