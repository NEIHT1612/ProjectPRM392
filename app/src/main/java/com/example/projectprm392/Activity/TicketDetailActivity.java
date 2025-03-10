package com.example.projectprm392.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectprm392.Model.Flight;
import com.example.projectprm392.databinding.ActivityTicketDetailBinding;

public class TicketDetailActivity extends AppCompatActivity {
    private ActivityTicketDetailBinding binding;
    private Flight flight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTicketDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setVariable();
        getIntentExtra();
    }

    private void getIntentExtra() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.txtFromShort.setText(flight.getFrom());
        binding.txtFromSmall.setText(flight.getFromShort());
        binding.txtToShort.setText(flight.getTo());
        binding.txtToSmall.setText(flight.getToShort());
        binding.txtDate.setText(flight.getDate());
        binding.txtTime.setText(flight.getTime());
        binding.txtArrival.setText(flight.getArriveTime());
        binding.txtAirlineName.setText(flight.getAirlineName());
        binding.txtPlaneName.setText(flight.getAirlineName());
        binding.txtTotalPrice.setText("$" + flight.getPrice());
        binding.txtSeats.setText(flight.getPassenger());
    }

    private void setVariable() {
        flight = (Flight) getIntent().getSerializableExtra("flight");
    }
}