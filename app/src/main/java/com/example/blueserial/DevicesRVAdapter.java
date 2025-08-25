package com.example.blueserial;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DevicesRVAdapter extends RecyclerView.Adapter<DevicesRVAdapter.DevicesRVHolder>{

    ArrayList<BluetoothDevice>devices;
    static onItemClickListner clicklistener;

    public interface onItemClickListner{
        void onItemClick(String deviceName);
    }

    public DevicesRVAdapter(ArrayList<BluetoothDevice> devices) {
        this.devices = devices;
    }


   public static class DevicesRVHolder extends RecyclerView.ViewHolder{

       private TextView tvDeviceName;
       private TextView tvDeviceAdresse;

       public DevicesRVHolder(@NonNull View itemView) {
           super(itemView);
           tvDeviceName=itemView.findViewById(R.id.tvDeviceName);
           tvDeviceAdresse=itemView.findViewById(R.id.tvDeviceAdresse);
       }

       @SuppressLint("MissingPermission")
       public void bind(BluetoothDevice device){
            tvDeviceName.setText(device.getName());
            tvDeviceName.setOnClickListener(v -> clicklistener.onItemClick(device.getName()));
            tvDeviceAdresse.setText(device.getAddress());
       }
   }

    @NonNull
    @Override
    public DevicesRVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_device_element,parent,false);
       return new DevicesRVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DevicesRVHolder holder, int position) {
       holder.bind(devices.get(position));

    }


    @Override
    public int getItemCount() {
        return devices.size();
    }
    public void addDevice(BluetoothDevice device){
        devices.add(device);
        notifyItemInserted(devices.size()-1);
    }
}
