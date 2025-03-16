package com.example.projectprm392.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectprm392.Adapter.FlightAdapter;
import com.example.projectprm392.Model.Flight;
import com.example.projectprm392.Model.Location;
import com.example.projectprm392.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListFlightActivity extends AppCompatActivity {
    EditText txtAirlineName, txtArriveTime, txtDate, txtFromShort, txtToShort, txtNumberSeat, txtPrice, txtTime;
    Spinner spinnerFrom, spinnerTo;
    Button btnViewFlight, btnInsertFlight, btnUpdateFlight, btnDeleteFlight;
    ImageView btnBack;
    RecyclerView lvFlights;
    DatabaseReference databaseReference;
    List<Flight> flights;
    FlightAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_flight);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtAirlineName = findViewById(R.id.txtAirlineName);
        txtArriveTime = findViewById(R.id.txtArriveTime);
        txtDate = findViewById(R.id.txtDate);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        txtFromShort = findViewById(R.id.txtFromShort);
        spinnerTo = findViewById(R.id.spinnerTo);
        txtToShort = findViewById(R.id.txtToShort);
        txtNumberSeat = findViewById(R.id.txtNumberSeat);
        txtPrice = findViewById(R.id.txtPrice);
        txtTime = findViewById(R.id.txtTime);
        btnViewFlight = findViewById(R.id.btnViewFlight);
        btnInsertFlight = findViewById(R.id.btnInsertFlight);
        btnUpdateFlight = findViewById(R.id.btnUpdateFlight);
        btnDeleteFlight = findViewById(R.id.btnDeleteFlight);
        lvFlights = findViewById(R.id.lvFlights);
        btnBack = findViewById(R.id.btnBack);

        databaseReference = FirebaseDatabase.getInstance().getReference("Flights");
        flights = new ArrayList<>();
        adapter = new FlightAdapter(flights);
        lvFlights.setAdapter(adapter);
        lvFlights.setLayoutManager(new LinearLayoutManager(this));


        loadLocations();
        loadFlights();
        clickHolderItem();
        btnInsertFlight.setOnClickListener(v -> addFlight());
        btnUpdateFlight.setOnClickListener(v -> updateFlight());
        btnDeleteFlight.setOnClickListener(v -> deleteFlight());
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ListFlightActivity.this, AdminActivity.class);
            startActivity(intent);
            finish(); // Optional: closes the current activity
        });
    }

    private void clickHolderItem() {
        Flight flight = (Flight) getIntent().getSerializableExtra("flight");
        if (flight != null) {
            txtAirlineName.setText(flight.getAirlineName());
            txtArriveTime.setText(flight.getArriveTime());
            txtDate.setText(flight.getDate());
            txtFromShort.setText(flight.getFromShort());
            txtToShort.setText(flight.getToShort());
            txtPrice.setText(String.valueOf(flight.getPrice()));
            txtNumberSeat.setText(String.valueOf(flight.getNumberSeat()));
            txtTime.setText(flight.getTime());
            setSpinnerSelection(spinnerFrom, flight.getFrom());
            setSpinnerSelection(spinnerTo, flight.getTo());

            // Set tag for update
            txtAirlineName.setTag(flight.getAirlineId());
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void updateFlight() {
        String airlineId = txtAirlineName.getTag() != null ? txtAirlineName.getTag().toString() : null;

        if (airlineId == null) {
            Toast.makeText(this, "Please select a flight to update", Toast.LENGTH_SHORT).show();
            return;
        }

        Flight flight = new Flight();
        flight.setAirlineId(airlineId);
        flight.setAirlineName(txtAirlineName.getText().toString());
        flight.setFrom(spinnerFrom.getSelectedItem().toString());
        flight.setFromShort(txtFromShort.getText().toString());
        flight.setTo(spinnerTo.getSelectedItem().toString());
        flight.setToShort(txtToShort.getText().toString());
        flight.setArriveTime(txtArriveTime.getText().toString());
        flight.setReservedSeats("");
        flight.setPrice(Double.parseDouble(txtPrice.getText().toString()));
        flight.setNumberSeat(Integer.parseInt(txtNumberSeat.getText().toString()));
        flight.setDate(txtDate.getText().toString());
        flight.setTime(txtTime.getText().toString());

        databaseReference.child(airlineId).setValue(flight)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(ListFlightActivity.this, "Flight Updated", Toast.LENGTH_SHORT).show();
                    clearForm();
                    txtAirlineName.setTag(null); // reset tag after update
                })
                .addOnFailureListener(e -> Toast.makeText(ListFlightActivity.this, "Failed to update flight", Toast.LENGTH_SHORT).show());
    }

    private void loadLocations() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Locations");
        ArrayList<Location> list = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot issue: snapshot.getChildren()){
                        list.add(issue.getValue(Location.class));
                    }
                    ArrayAdapter<Location> adapter = new ArrayAdapter<>(ListFlightActivity.this, R.layout.sp_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerFrom.setAdapter(adapter);
                    spinnerTo.setAdapter(adapter);
                    spinnerFrom.setSelection(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addFlight() {
        databaseReference.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int newId = 1;
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String lastId = child.getKey();
                        newId = Integer.parseInt(lastId) + 1;
                    }
                }

                Flight flight = new Flight();
                flight.setAirlineId(String.valueOf(newId));
                flight.setAirlineName(txtAirlineName.getText().toString());
                flight.setFrom(spinnerFrom.getSelectedItem().toString());
                flight.setFromShort(txtFromShort.getText().toString());
                flight.setTo(spinnerTo.getSelectedItem().toString());
                flight.setToShort(txtToShort.getText().toString());
                flight.setArriveTime(txtArriveTime.getText().toString());
                flight.setReservedSeats("");
                flight.setPrice(Double.parseDouble(txtPrice.getText().toString()));
                flight.setNumberSeat(Integer.parseInt(txtNumberSeat.getText().toString()));
                flight.setDate(txtDate.getText().toString());
                flight.setTime(txtTime.getText().toString());

                databaseReference.child(String.valueOf(newId)).setValue(flight)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(ListFlightActivity.this, "Flight Added", Toast.LENGTH_SHORT).show();
                            clearForm();
                        })
                        .addOnFailureListener(e -> Toast.makeText(ListFlightActivity.this, "Failed to add flight", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListFlightActivity.this, "Error generating ID", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void clearForm(){
        txtAirlineName.setText("");
        txtFromShort.setText("");
        txtToShort.setText("");
        txtArriveTime.setText("");
        txtDate.setText("");
        txtTime.setText("");
        txtPrice.setText("");
        txtNumberSeat.setText("");
        spinnerFrom.setSelection(1);
        spinnerTo.setSelection(0);
    }
    private void loadFlights() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                flights.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Flight f = child.getValue(Flight.class);
                    flights.add(f);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListFlightActivity.this, "Error loading flights", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteFlight(){
        String airlineId = txtAirlineName.getTag() != null ? txtAirlineName.getTag().toString() : null;

        if (airlineId == null) {
            Toast.makeText(this, "Please select a flight to update", Toast.LENGTH_SHORT).show();
            return;
        }

        Flight flight = new Flight();
        flight.setAirlineId(airlineId);
        flight.setAirlineName(txtAirlineName.getText().toString());
        flight.setFrom(spinnerFrom.getSelectedItem().toString());
        flight.setFromShort(txtFromShort.getText().toString());
        flight.setTo(spinnerTo.getSelectedItem().toString());
        flight.setToShort(txtToShort.getText().toString());
        flight.setArriveTime(txtArriveTime.getText().toString());
        flight.setReservedSeats("");
        flight.setPrice(Double.parseDouble(txtPrice.getText().toString()));
        flight.setNumberSeat(Integer.parseInt(txtNumberSeat.getText().toString()));
        flight.setDate(txtDate.getText().toString());
        flight.setTime(txtTime.getText().toString());

        databaseReference.child(airlineId).removeValue()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(ListFlightActivity.this, "Flight Deleted", Toast.LENGTH_SHORT).show();
                    clearForm();
                    txtAirlineName.setTag(null);
                })
                .addOnFailureListener(e -> Toast.makeText(ListFlightActivity.this, "Failed to update flight", Toast.LENGTH_SHORT).show());
    }
}