package com.example.blueserial;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ArchivedActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BluetoothMenuActivity extends AppCompatActivity {

    BluetoothAdapter BlAdapter = BluetoothAdapter.getDefaultAdapter();
    ArrayList<BluetoothDevice> unBluetoothDevicesList= new ArrayList<>();
    ArrayList<BluetoothDevice> beBluetoothDevicesList= new ArrayList<>();
    DevicesRVAdapter devicesRVAdapter;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_menu);
        RecyclerView rvUnbekannteGeräte= findViewById(R.id.rvUnbekannteGeräte);
        RecyclerView rvBekannteGeräte= findViewById(R.id.rvBekannteGeräte);
        rvUnbekannteGeräte.setLayoutManager(new LinearLayoutManager(this));
        rvBekannteGeräte.setLayoutManager(new LinearLayoutManager(this));




        // RV inhalte füllen hier
        devicesRVAdapter = new DevicesRVAdapter(unBluetoothDevicesList);
        rvUnbekannteGeräte.setAdapter(devicesRVAdapter);
        beBluetoothDevicesList= new ArrayList<BluetoothDevice>(BlAdapter.getBondedDevices());

        DevicesRVAdapter devicesRVAdapter1 = new DevicesRVAdapter(beBluetoothDevicesList);
        rvBekannteGeräte.setAdapter(devicesRVAdapter1);
        devicesRVAdapter1.notifyDataSetChanged();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver,filter);

        if(checkBluetoothPermission()){
            startDiscovery();

        }



    }
    @SuppressLint("MissingPermission")
    private void startDiscovery() {
        if (BlAdapter.isDiscovering()) {
            BlAdapter.cancelDiscovery();
        }

            Log.e("Discovery","Bluetooth State ="+ BlAdapter.getState());

        boolean started = BlAdapter.startDiscovery();
        Log.i("Discovery", "startDiscovery() returned: " + started);
    }
    private boolean checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED)  {

                requestPermissions(new String[]{
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, 1);
                return false; // noch nicht erlaubt, Discovery nicht starten
            } else {
                return true; // schon erlaubt
            }
        } else {
            // Für Android < 12 müssen nur alte Permissions vorhanden sein
            return true;
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }

            if (granted) {
                startDiscovery();
            } else {
                Toast.makeText(this, "Bluetooth-Berechtigung verweigert", Toast.LENGTH_SHORT).show();
            }
        }
    }





    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                Log.i("Discovery","Discovery Started");
            }
            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                Log.i("Discovery","Discovery finished");
            }
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                unBluetoothDevicesList.add(intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
                Log.i("Dicovery","Action Found");
                devicesRVAdapter.notifyDataSetChanged();
            }
        }
    };
}