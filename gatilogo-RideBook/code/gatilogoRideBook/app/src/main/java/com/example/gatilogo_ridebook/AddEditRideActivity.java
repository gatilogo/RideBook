package com.example.gatilogo_ridebook;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * AddEditRideActivity class
 *
 * Contains logic for handling inputs from the user.
 * This class is used for when adding or editing a ride. If the user is editing a ride, they are
 * also given an option to delete the ride.
 * Before adding a new ride or saving any changes, this class also validates the user input.
 */
public class AddEditRideActivity extends AppCompatActivity {

    // Declare views for later use.
    private EditText rideDate;
    private EditText rideTime;
    private EditText rideDistance;
    private EditText rideAvgSpeed;
    private EditText rideAvgCadence;
    private EditText rideComment;
    private TextView deleteRide;
    private Button saveButton;

    private ArrayList<EditText> emptyFields;    // Keep an array of empty editText views
    private Boolean edit;                       // True if user is editing a ride, otherwise false
    private int position;                       // Keeps track of the ride to delete from list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ride);

        // Assign views by their respective ids
        rideDate = findViewById(R.id.dateField);
        rideTime =  findViewById(R.id.timeField);
        rideDistance = findViewById(R.id.distanceField);
        rideAvgSpeed = findViewById(R.id.avgSpeedField);
        rideAvgCadence = findViewById(R.id.avgCadenceField);
        rideComment = findViewById(R.id.commentField);
        deleteRide = findViewById(R.id.deleteRide);
        saveButton = findViewById(R.id.saveButton);

        Bundle extras = getIntent().getExtras();
        // If data is passed from main activity, user is editing a ride.
        if (extras != null) {
            Ride currentRide = (Ride) extras.getSerializable("selectedRide");
            position = extras.getInt("position");

            rideDate.setText(currentRide.getDate());
            rideTime.setText(currentRide.getTime());
            rideDistance.setText(Float.toString(currentRide.getDistance()));
            rideAvgSpeed.setText(Float.toString(currentRide.getAvgSpeed()));
            rideAvgCadence.setText(Integer.toString(currentRide.getAvgCadence()));
            rideComment.setText(currentRide.getComment());

            setTitle(R.string.edit_ride_activity_title);    // Change title to "Edit Ride"
            deleteRide.setVisibility(View.VISIBLE);         // Show the "delete ride" view
            edit = true;                                    // "Edit ride" mode
        }
        // Otherwise, user is adding a ride.
        else {
            deleteRide.setVisibility(View.GONE);            // Hide the "delete ride" view
            edit = false;                                   // "Add ride" mode
        }

        // Shows a date picker when the Date field is clicked.
        // It also displays the date in yyyy-MM-dd format in the field.
        rideDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year;
                int month;
                int day;

                // If date field is not empty, set the year, month and day to specified date.
                if (rideDate.length() != 0) {
                    String[] myDate = rideDate.getText().toString().trim().split("-");
                    year = Integer.parseInt(myDate[0]);
                    month = Integer.parseInt(myDate[1]) - 1;
                    day = Integer.parseInt(myDate[2]);
                }
                // Otherwise, set date to today's date.
                else {
                    Calendar cal = Calendar.getInstance();
                    year = cal.get(Calendar.YEAR);
                    month = cal.get(Calendar.MONTH);
                    day = cal.get(Calendar.DAY_OF_MONTH);
                }

                DatePickerDialog dialog = new DatePickerDialog(
                        AddEditRideActivity.this,
                        DatePickerDialog.THEME_HOLO_LIGHT,
                        new DatePickerDialog.OnDateSetListener() {
                            // Sets the date field to selected date.
                            // It also removes the error icon (if needed) if date field is populated.
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                rideDate.setText(String.format("%4d-%02d-%02d", year, (month + 1), day));
                                if (rideDate.length() != 0) {
                                    rideDate.setError(null);
                                }
                            }
                        }, year, month, day
                );

                dialog.setTitle(getString(R.string.select_date));
                dialog.show();
            }
        });

        // Shows a time picker when the Time field is clicked.
        // It also displays the time in hh:mm in a 24 hour format.
        rideTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour;
                int minute;

                // If time field is not empty, set the hour and minute to specified time.
                if (rideTime.length() != 0) {
                    String[] myTime = rideTime.getText().toString().trim().split(":");
                    hour = Integer.parseInt(myTime[0]);
                    minute = Integer.parseInt(myTime[1]);
                }
                // Otherwise, set time to current time.
                else {
                    Calendar cal = Calendar.getInstance();
                    hour = cal.get(Calendar.HOUR_OF_DAY);
                    minute = cal.get(Calendar.MINUTE);
                }

                TimePickerDialog dialog = new TimePickerDialog(
                        AddEditRideActivity.this,
                        TimePickerDialog.THEME_HOLO_LIGHT,
                        new TimePickerDialog.OnTimeSetListener() {
                            // Sets the time field to selected time.
                            // It also removes the error icon (if needed) if time field is populated.
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                rideTime.setText(String.format("%02d:%02d", hour, minute));
                                if (rideTime.length() != 0) {
                                    rideTime.setError(null);
                                }
                            }
                        }, hour, minute, true
                );

                dialog.setTitle(getString(R.string.select_time));
                dialog.show();
            }
        });

        // Validates the entered input before adding/editing a ride
        // If valid, it sends the ride data back to MainActivity
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If ride is valid
                if (validateRide()) {
                    // Create a new instance of the ride
                    Ride ride = new Ride(
                            rideDate.getText().toString(),
                            rideTime.getText().toString(),
                            Float.parseFloat(rideDistance.getText().toString()),
                            Float.parseFloat(rideAvgSpeed.getText().toString()),
                            Integer.parseInt(rideAvgCadence.getText().toString()),
                            rideComment.getText().toString()
                    );
                    Intent intent = new Intent(AddEditRideActivity.this, MainActivity.class);

                    // If user is editing, pass the ride data and position of item to edit back
                    // into MainActivity
                    if (edit) {
                        intent.putExtra("editRide", ride);
                        intent.putExtra("position", position);
                    }
                    // If user is adding a new ride, pass the ride data back into MainActivity
                    else {
                        intent.putExtra("addRide", ride);
                    }

                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        // Deletes the selected ride if user is in edit mode.
        // It also shows an alert dialog before deleting a ride
        deleteRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddEditRideActivity.this);
                builder.setMessage(getString(R.string.delete_message));
                builder.setCancelable(true);

                // If user confirms deletion, pass the position of the item to delete back
                // in MainActivity
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(AddEditRideActivity.this, MainActivity.class);
                        intent.putExtra("deleteRidePosition", position);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });

                // If user does not confirm deletion, cancel the dialog and go back to editing mode.
                builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    // Checks if an editText field is empty, and adds the empty editText field in an array
    // for later use.
    private void checkIfEmpty(EditText editText) {
        if (editText.length() == 0) {
            emptyFields.add(editText);
        }
    }

    // Validates the inputs by checking if fields are empty.
    // For each empty field, it also sets an error to show to users.
    // This method also checks if the comment field is more than 20 characters.
    private Boolean validateRide() {
        emptyFields = new ArrayList<>();

        // Check if fields are empty
        checkIfEmpty(rideDate);
        checkIfEmpty(rideTime);
        checkIfEmpty(rideDistance);
        checkIfEmpty(rideAvgSpeed);
        checkIfEmpty(rideAvgCadence);

        // If a field is empty, set an error message
        if (!emptyFields.isEmpty()) {
            for (EditText editText: emptyFields) {
                editText.setError(getString(R.string.empty_field));
            }

            return false;
        }

        // If the comment field contains more than 20 characters, set an error message
        if (rideComment.getText().toString().length() > 20) {
            rideComment.setError(getString(R.string.comment_error));
            return false;
        }

        return true;
    }
}
