package com.example.blueserial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DevicesRVAdapter extends RecyclerView.Adapter<DevicesRVAdapter.DevicesRVHolder>{

    ArrayList<BluetoothDevices>devices;
    static onItemClickListner clicklistener;

    public interface onItemClickListner{
        void onItemClick(String deviceName);
    }

    public DevicesRVAdapter(ArrayList<BluetoothDevices> devices, onItemClickListner itemClickListner){
        this.devices=devices;
        this.clicklistener=itemClickListner;
    }

   public static class DevicesRVHolder extends RecyclerView.ViewHolder{

       private TextView tvDeviceName;

       public DevicesRVHolder(@NonNull View itemView) {
           super(itemView);
           tvDeviceName=itemView.findViewById(R.id.tvDeviceName);
       }

       public void bind(BluetoothDevices device){
            tvDeviceName.setText(device.name);
            tvDeviceName.setOnClickListener(v -> clicklistener.onItemClick(device.name));
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
    public void addDevice(BluetoothDevices device){
        devices.add(device);
        notifyItemInserted(devices.size()-1);
    }
}
