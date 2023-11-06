package com.example.findaseat_csci310;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotViewHolder>{

    Context context;
    List<TimeSlot> timeSlotList;
    private OnClickListener onClickListener;

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
        TimeSlot item = timeSlotList.get(position);
        holder.timeSlot.setText(item.getTime());
        holder.type.setText(item.getType());
        holder.availableSeats.setText(String.valueOf(item.getAvailableSeats()));

        // set onClickListener for each time slot item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use getAdapterPosition to get the current position
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && onClickListener != null) {
                    onClickListener.onClick(adapterPosition, timeSlotList.get(adapterPosition));
                }
            }
        });
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, TimeSlot item);
    }

    @Override
    public int getItemCount() {
        return timeSlotList.size();
    }
}
