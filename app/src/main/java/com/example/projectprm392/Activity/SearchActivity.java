package com.example.projectprm392.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.projectprm392.Adapter.FlightAdapter;
import com.example.projectprm392.Model.Flight;
import com.example.projectprm392.databinding.ActivitySearchBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SearchActivity extends BaseActivity {
    private ActivitySearchBinding binding;
    private String from, to, departureDate, returnDate;
    private int numPassenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();
        initList();
        setVariable();
    }

    private void setVariable() {
        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void initList() {
        DatabaseReference myRef = database.getReference("Flights");
        ArrayList<Flight> list = new ArrayList<>();
        Query query = myRef.orderByChild("from").equalTo(from);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    SimpleDateFormat formatter = new SimpleDateFormat("d MMM, yyyy", Locale.ENGLISH);

                    try{
                        Date departureDateObj = formatter.parse(departureDate);
                        Date returnDateObj = formatter.parse(returnDate);

                        for(DataSnapshot issue: snapshot.getChildren()){
                            Flight flight = issue.getValue(Flight.class);
                            Date flightDateObj = formatter.parse(flight.getDate());
                            if(flight.getTo().equals(to) && !flightDateObj.before(departureDateObj) && !flightDateObj.after(returnDateObj)){
                                list.add(flight);
                            }
                            if(!list.isEmpty()){
                                binding.searchView.setLayoutManager(new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false));
                                binding.searchView.setAdapter(new FlightAdapter(list));
                            }
                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    binding.progressBarSearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressBarSearch.setVisibility(View.GONE);
            }
        });
    }

    private void getIntentExtra() {
        from = getIntent().getStringExtra("from");
        to = getIntent().getStringExtra("to");
        departureDate = getIntent().getStringExtra("departureDate");
        returnDate = getIntent().getStringExtra("returnDate");
    }
}