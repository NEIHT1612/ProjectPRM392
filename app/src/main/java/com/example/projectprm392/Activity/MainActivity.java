package com.example.projectprm392.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.projectprm392.Model.Location;
import com.example.projectprm392.R;
import com.example.projectprm392.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    private int passenger = 1;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM, yyyy", Locale.ENGLISH);
    private Calendar calendar = Calendar.getInstance();
    private TextView txtAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getCurrentUser();
        initLocations();
        initPassengers();
        initDatePickup();
        setVariable();
    }

    private void getCurrentUser() {
        txtAccount = findViewById(R.id.txtAccount);
        txtAccount.setText(user.getEmail());
    }

    private void setVariable() {
        binding.btnSearch.setOnClickListener(v -> {
            String departureDateStr = binding.txtDepartureDate.getText().toString();
            String returnDateStr = binding.txtReturnDate.getText().toString();
            try {
                Date departureDate = dateFormat.parse(departureDateStr);
                Date returnDate = dateFormat.parse(returnDateStr);
                if (returnDate != null && departureDate != null && returnDate.before(departureDate)) {
                    Toast.makeText(MainActivity.this, "Return date cannot be before departure date!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("from", ((Location)binding.spinnerFrom.getSelectedItem()).getName());
                intent.putExtra("to", ((Location)binding.spinnerTo.getSelectedItem()).getName());
                intent.putExtra("departureDate", departureDateStr);
                intent.putExtra("returnDate", returnDateStr);
                intent.putExtra("numPassenger", passenger);
                startActivity(intent);
            } catch (ParseException e) {
                Toast.makeText(MainActivity.this, "Invalid date format!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initDatePickup() {
        Calendar calendarToday = Calendar.getInstance();
        String currentDate = dateFormat.format(calendarToday.getTime());
        binding.txtDepartureDate.setText(currentDate);

        Calendar calendarTomorrow = Calendar.getInstance();
        calendarTomorrow.add(Calendar.DAY_OF_YEAR, 1);
        String tomorrowDate = dateFormat.format(calendarTomorrow.getTime());
        binding.txtReturnDate.setText(tomorrowDate);

        binding.txtDepartureDate.setOnClickListener(v -> showDatePickerDialog(binding.txtDepartureDate));
        binding.txtReturnDate.setOnClickListener(v -> showDatePickerDialog(binding.txtReturnDate));
    }

    private void initPassengers() {
        binding.btnPlusPassenger.setOnClickListener(v -> {
            passenger++;
            binding.txtPassenger.setText(String.valueOf(passenger));
        });

        binding.btnMinusPassenger.setOnClickListener(v -> {
            if(passenger > 1){
                passenger--;
                binding.txtPassenger.setText(String.valueOf(passenger));
            }
        });
    }

    private void initLocations() {
        binding.progressBarFrom.setVisibility(View.VISIBLE);
        binding.progressBarTo.setVisibility(View.VISIBLE);

        DatabaseReference myRef = database.getReference("Locations");
        ArrayList<Location> list = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot issue: snapshot.getChildren()){
                        list.add(issue.getValue(Location.class));
                    }
                    ArrayAdapter<Location> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerFrom.setAdapter(adapter);
                    binding.spinnerTo.setAdapter(adapter);
                    binding.spinnerFrom.setSelection(1);
                    binding.progressBarFrom.setVisibility(View.GONE);
                    binding.progressBarTo.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDatePickerDialog(TextView textView){
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            calendar.set(selectedYear, selectedMonth, selectedDay);
            String formattedDate = dateFormat.format(calendar.getTime());
            textView.setText(formattedDate);
        }, year, month, day);
        datePickerDialog.show();
    }
}