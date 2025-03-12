package com.example.projectprm392.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectprm392.Model.Flight;
import com.example.projectprm392.R;
import com.example.projectprm392.databinding.ActivityTicketDetailBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TicketDetailActivity extends AppCompatActivity {
    private ActivityTicketDetailBinding binding;
    private Flight flight;
    private Button btnDownloadTicket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTicketDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setVariable();
        getIntentExtra();
        buyTicket();
    }

    private void buyTicket() {
        btnDownloadTicket = findViewById(R.id.downloadTicketBtn);
        btnDownloadTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Flights");
                if(flight != null && flight.getAirlineId() != null){
                    String newSeats = binding.txtSeats.getText().toString();

                    databaseReference.child(flight.getAirlineId())
                            .child("reservedSeats")
                            .get()
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful() && task.getResult().exists()){
                                    String currentReservedSeats = task.getResult().getValue(String.class);
                                    String updatedReservedSeats = currentReservedSeats + "," + newSeats;

                                    databaseReference.child(flight.getAirlineId()).child("reservedSeats").setValue(updatedReservedSeats)
                                            .addOnSuccessListener(s -> {
                                                Toast.makeText(TicketDetailActivity.this, "Book success", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(TicketDetailActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(TicketDetailActivity.this, "Failed booking", Toast.LENGTH_SHORT).show();
                                            });
                                }
                                else{
                                    Toast.makeText(TicketDetailActivity.this, "Failed to fetch existing seats", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else{
                    Toast.makeText(TicketDetailActivity.this, "Flight data is missing", Toast.LENGTH_SHORT).show();
                }
            }
        });
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