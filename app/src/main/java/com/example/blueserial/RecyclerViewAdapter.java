package com.example.blueserial;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclewViewHolder> {

    ArrayList<Message> nachrichten;


    public RecyclerViewAdapter(ArrayList<Message> nachrichten){
        this.nachrichten=nachrichten;
    }

    public static class RecyclewViewHolder extends RecyclerView.ViewHolder{

        Color sentColor;
        Color receivedColor;
        TextView tvZeitStempel, tvMsg;

        public RecyclewViewHolder(@NonNull View itemView) {
            super(itemView);
            sentColor=Color.valueOf(Color.CYAN);
            receivedColor=Color.valueOf(Color.YELLOW);
            tvZeitStempel= itemView.findViewById(R.id.tvTimestamp);
            tvMsg= itemView.findViewById(R.id.tvMessage);
        }
        public void bind(Message nachricht){

            tvZeitStempel.setText(nachricht.getZeitstempel());
            tvMsg.setText(nachricht.getMsg());

            if(nachricht.sent) {

                tvZeitStempel.setTextColor(sentColor.toArgb());
                tvMsg.setTextColor(sentColor.toArgb());

            }
            else{
                tvZeitStempel.setTextColor(receivedColor.toArgb());
                tvMsg.setTextColor(receivedColor.toArgb());
            }
        }


    }
    @NonNull
    @Override
    public RecyclewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_element,parent,false);
        return new RecyclewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclewViewHolder holder, int position) {
        holder.bind(nachrichten.get(position));
    }

    @Override
    public int getItemCount() {

        return nachrichten.size();
    }

    public void addNachricht(Message nachricht){
        nachrichten.add(nachricht);
        notifyItemInserted(nachrichten.size()-1);
    }



}
