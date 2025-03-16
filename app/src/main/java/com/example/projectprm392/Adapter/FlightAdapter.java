package com.example.projectprm392.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectprm392.Activity.ListFlightActivity;
import com.example.projectprm392.Activity.SeatListActivity;
import com.example.projectprm392.Model.Flight;
import com.example.projectprm392.databinding.ViewholderFlightaBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.ViewHolder> {
    private final List<Flight> flights;
    private Context context;

    public FlightAdapter(List<Flight> flights) {
        this.flights = flights;
    }

    @NonNull
    @Override
    public FlightAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderFlightaBinding binding = ViewholderFlightaBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FlightAdapter.ViewHolder holder, int position) {
        Flight flight = flights.get(position);
        holder.binding.txtAirlineName.setText(flight.getAirlineName());
        holder.binding.txtFrom.setText(flight.getFrom());
        holder.binding.txtFromShort.setText(flight.getFromShort());
        holder.binding.txtTo.setText(flight.getTo());
        holder.binding.txtToShort.setText(flight.getToShort());
        holder.binding.txtArrival.setText(flight.getArriveTime());
        holder.binding.txtPrice.setText("$" + flight.getPrice());

        holder.itemView.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && user.getEmail() != null) {
                String email = user.getEmail();
                if (email.equals("thien@gmail.com")) {
                    Intent intent = new Intent(context, ListFlightActivity.class);
                    intent.putExtra("flight", flight);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, SeatListActivity.class);
                    intent.putExtra("flight", flight);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return flights.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final ViewholderFlightaBinding binding;
        public ViewHolder(ViewholderFlightaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
