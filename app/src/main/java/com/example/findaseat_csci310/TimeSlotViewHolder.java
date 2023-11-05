package com.example.findaseat_csci310;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TimeSlotViewHolder extends RecyclerView.ViewHolder{
    TextView timeSlot, type, availableSeats;
    public TimeSlotViewHolder(@NonNull View itemView) {
        super(itemView);
        timeSlot = itemView.findViewById(R.id.timeSlot);
        type = itemView.findViewById(R.id.type);
        availableSeats = itemView.findViewById(R.id.availableSeats);
    }
}
