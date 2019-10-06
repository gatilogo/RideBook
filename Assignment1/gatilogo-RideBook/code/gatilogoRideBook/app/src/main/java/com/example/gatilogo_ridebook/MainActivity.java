package com.example.gatilogo_ridebook;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * MainActivity class
 *
 * Contains logic for handling ride data.
 * This class directs users to AddEditRideActivity in "add ride mode" when the add button is clicked.
 * It also directs users to AddEditRideActivity in "edit ride mode" when an item from the ride list
 * is clicked.
 * This class receives data from AddEditRideActivity and determines what to do with the data.
 */
public class MainActivity extends AppCompatActivity {

    // Declare views for later use.
    private ListView rideListView;
    private TextView emptyListView;
    private TextView totalDistance;
    private FloatingActionButton addRideButton;

    private static final int ADD_EDIT_RIDE_REQUEST_CODE = 0;        // Request code to identify result from AddEditActivity
    private static ArrayList<Ride> rideList = new ArrayList<>();    // Contains list of rides
    private ArrayAdapter<Ride> rideAdapter;                         // Adapter for rideListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign views by their respective ids
        rideListView = findViewById(R.id.ridesList);
        emptyListView = findViewById(R.id.emptyRidesList);
        totalDistance = findViewById(R.id.totalDistanceNum);
        addRideButton = findViewById(R.id.addRideButton);

        // Initialize adapter for the list view
        rideAdapter = new RideArrayAdapter(this, rideList);
        rideListView.setAdapter(rideAdapter);
        // Display emptyListView if ride list is empty
        rideListView.setEmptyView(emptyListView);
        // When a user single-clicks on an item, it directs users to AddEditActivity in
        // "edit ride mode" and passes the data of the selected ride
        rideListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Ride selectedRide = rideList.get(i);
                Intent intent = new Intent(MainActivity.this, AddEditRideActivity.class);
                intent.putExtra("selectedRide", selectedRide);
                intent.putExtra("position", i);
                startActivityForResult(intent, ADD_EDIT_RIDE_REQUEST_CODE);
            }
        });
        // When a user long-clicks on an item, an alert dialog will display a "Delete ride?" message
        // and prompts the user to confirm deletion.
        rideListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(getString(R.string.delete_message_2));
                builder.setCancelable(true);

                // Delete ride if user clicks on "Yes" button
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        rideList.remove(position);
                        rideAdapter.notifyDataSetChanged();
                        updateTotalDistance();
                    }
                });

                // Close dialog if user clicks on "No" button
                builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });

        // If user clicks on the add button, direct user to AddEditRideActivity in "add ride mode"
        addRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(
                        MainActivity.this,
                        AddEditRideActivity.class), ADD_EDIT_RIDE_REQUEST_CODE);
            }
        });
    }

    // Updates the total distance if user clicks on the back button from the add/edit page.
    @Override
    protected void onResume() {
        super.onResume();

        updateTotalDistance();
    }

    // Handles the data received from AddEditRideActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_EDIT_RIDE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                // If user was editing a ride
                if (extras.containsKey("editRide")) {
                    Ride ride = (Ride) extras.getSerializable("editRide");
                    int position = extras.getInt("position");
                    rideList.set(position, ride);   // Update the ride
                }
                // If user was adding a ride
                else if (extras.containsKey("addRide")) {
                    Ride ride = (Ride) extras.getSerializable("addRide");
                    rideList.add(ride);             // Add a new ride
                }
                // If user was removing a ride from the edit page
                else if (extras.containsKey("deleteRidePosition")) {
                    int deleteRide = extras.getInt("deleteRidePosition");
                    rideList.remove(deleteRide);    // Delete the specified ride
                }
            }
        }

        rideAdapter.notifyDataSetChanged(); // Update the ride list view
        updateTotalDistance();              // Update the total distance
    }

    // Adds the total distance of all rides and displays the number at the bottom of the page.
    private void updateTotalDistance() {
        float totalDistanceValue = 0;

        for (Ride ride: rideList) {
            totalDistanceValue += ride.getDistance();
        }

        totalDistance.setText(String.format("%.1f", totalDistanceValue));
    }
}