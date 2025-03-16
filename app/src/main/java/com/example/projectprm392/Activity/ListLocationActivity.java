package com.example.projectprm392.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projectprm392.Adapter.LocationAdapter;
import com.example.projectprm392.Model.Location;
import com.example.projectprm392.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListLocationActivity extends AppCompatActivity {
    EditText etLocation;
    Button btnViewLocation, btnInsertLocation, btnUpdateLocation, btnDeleteLocation;
    ListView lvLocations;
    ImageView btnBack;

    DatabaseReference databaseReference;
    List<Location> locationList;
    LocationAdapter locationAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_location);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etLocation = findViewById(R.id.txtLocation);
        btnViewLocation = findViewById(R.id.btnViewLocation);
        btnInsertLocation = findViewById(R.id.btnInsertLocation);
        btnUpdateLocation = findViewById(R.id.btnUpdateLocation);
        btnDeleteLocation = findViewById(R.id.btnDeleteLocation);
        lvLocations = findViewById(R.id.lvLocations);
        btnBack = findViewById(R.id.btnBack);


        databaseReference = FirebaseDatabase.getInstance().getReference("Locations");
        locationList = new ArrayList<>();
        locationAdapter = new LocationAdapter(this, locationList);
        lvLocations.setAdapter(locationAdapter);

        lvLocations.setOnItemClickListener((parent, view, position, id) -> {
            Location selectedLocation = (Location) parent.getItemAtPosition(position);
            etLocation.setText(selectedLocation.getName());
            etLocation.setTag(String.valueOf(selectedLocation.getId()));
        });
        loadLocations();
        btnInsertLocation.setOnClickListener(v -> addLocation());
        btnUpdateLocation.setOnClickListener(v -> updateLocation());
        btnDeleteLocation.setOnClickListener(v -> deleteLocation());
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ListLocationActivity.this, AdminActivity.class);
            startActivity(intent);
            finish(); // Optional: closes the current activity
        });
    }

    private void deleteLocation() {
        String idStr = etLocation.getTag() != null ? etLocation.getTag().toString() : "";

        if (idStr.isEmpty()) {
            Toast.makeText(this, "Select a location to remove", Toast.LENGTH_SHORT).show();
            return;
        }

        int locationId = Integer.parseInt(idStr);

        databaseReference.child(String.valueOf(locationId)).child("name").removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ListLocationActivity.this, "Location removed!", Toast.LENGTH_SHORT).show();
                    etLocation.setText("");
                    etLocation.setTag(null);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ListLocationActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateLocation() {
        String idStr = etLocation.getTag() != null ? etLocation.getTag().toString() : "";
        String newLocationName = etLocation.getText().toString().trim();

        if (idStr.isEmpty()) {
            Toast.makeText(this, "Select a location to update", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newLocationName.isEmpty()) {
            Toast.makeText(this, "Enter new location name", Toast.LENGTH_SHORT).show();
            return;
        }

        int locationId = Integer.parseInt(idStr);

        databaseReference.child(String.valueOf(locationId)).child("name").setValue(newLocationName)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ListLocationActivity.this, "Location updated!", Toast.LENGTH_SHORT).show();
                    etLocation.setText("");
                    etLocation.setTag(null);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ListLocationActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void addLocation() {
        databaseReference.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int nextId = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    nextId = Integer.parseInt(snapshot.getKey()) + 1;
                }

                String locationName = etLocation.getText().toString().trim();
                if (locationName.isEmpty()) {
                    Toast.makeText(ListLocationActivity.this, "Enter location name", Toast.LENGTH_SHORT).show();
                    return;
                }

                Location newLocation = new Location(nextId, locationName);

                databaseReference.child(String.valueOf(nextId)).setValue(newLocation)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(ListLocationActivity.this, "Location added!", Toast.LENGTH_SHORT).show();
                            etLocation.setText("");
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(ListLocationActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ListLocationActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLocations() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                locationList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Location location = data.getValue(Location.class);
                    locationList.add(location);
                }
                locationAdapter.updateList(locationList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListLocationActivity.this, "Failed to load locations", Toast.LENGTH_SHORT).show();
            }
        });
    }
}