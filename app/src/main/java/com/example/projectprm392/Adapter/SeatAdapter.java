package com.example.projectprm392.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectprm392.Model.Seat;
import com.example.projectprm392.R;
import com.example.projectprm392.databinding.SeatItemBinding;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatViewholder> {
    private final List<Seat> seatList;
    private final Context context;
    private ArrayList<String> selectedSeatName = new ArrayList<>();
    private SelectedSeat selectedSeat;

    public SeatAdapter(Context context, SelectedSeat selectedSeat, List<Seat> seatList) {
        this.context = context;
        this.selectedSeat = selectedSeat;
        this.seatList = seatList;
    }

    @NonNull
    @Override
    public SeatAdapter.SeatViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SeatItemBinding binding = SeatItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SeatViewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatAdapter.SeatViewholder holder, @SuppressLint("RecyclerView") int position) {
        Seat seat = seatList.get(position);
        holder.binding.SeatImageView.setText(seat.getName());

        switch (seat.getStatus()){
            case AVAILABLE:
                holder.binding.SeatImageView.setBackgroundResource(R.drawable.ic_seat_available);
                holder.binding.SeatImageView.setTextColor(context.getResources().getColor(R.color.white));
                break;
            case SELECTED:
                holder.binding.SeatImageView.setBackgroundResource(R.drawable.ic_seat_selected);
                holder.binding.SeatImageView.setTextColor(context.getResources().getColor(R.color.black));
                break;
            case UNAVAILABLE:
                holder.binding.SeatImageView.setBackgroundResource(R.drawable.ic_seat_unavailable);
                holder.binding.SeatImageView.setTextColor(context.getResources().getColor(R.color.gray));
                break;
            case EMPTY:
                holder.binding.SeatImageView.setBackgroundResource(R.drawable.ic_seat_empty);
                holder.binding.SeatImageView.setTextColor(Color.parseColor("#00000000"));
                break;
        }

        holder.binding.SeatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(seat.getStatus() == Seat.SeatStatus.AVAILABLE){
                    seat.setStatus(Seat.SeatStatus.SELECTED);
                    selectedSeatName.add(seat.getName());
                    notifyItemChanged(position);
                } else if(seat.getStatus() == Seat.SeatStatus.SELECTED){
                    seat.setStatus(Seat.SeatStatus.AVAILABLE);
                    selectedSeatName.remove(seat.getName());
                    notifyItemChanged(position);
                }

                String selected = selectedSeatName.toString()
                        .replace("[", "")
                        .replace("]", "")
                        .replace(" ", "");
                selectedSeat.Return(selected, selectedSeatName.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return seatList.size();
    }

    public class SeatViewholder extends RecyclerView.ViewHolder {
        SeatItemBinding binding;
        public SeatViewholder(@NonNull SeatItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface SelectedSeat{
        void Return(String selectedName, int num);
    }
}
