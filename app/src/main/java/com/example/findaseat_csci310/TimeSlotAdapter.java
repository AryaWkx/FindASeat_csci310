package com.example.findaseat_csci310;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotViewHolder>{

    Context context;
    List<TimeSlot> timeSlotList;

    public TimeSlotAdapter(Context context, List<TimeSlot> timeSlotList) {
        this.context = context;
        this.timeSlotList = timeSlotList;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TimeSlotViewHolder(LayoutInflater.from(context).inflate(R.layout.time_slot_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        holder.timeSlot.setText(timeSlotList.get(position).getTime());
        holder.type.setText(timeSlotList.get(position).getType());
        holder.availableSeats.setText(timeSlotList.get(position).getAvailableSeats());
    }

    @Override
    public int getItemCount() {
        return timeSlotList.size();
    }
}
