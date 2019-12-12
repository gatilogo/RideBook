package com.example.gatilogo_ridebook;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * RideArrayAdapter class
 *
 * Provides a view for each ride on the ride list.
 */
public class RideArrayAdapter extends ArrayAdapter<Ride> {

    private Context context;
    private List<Ride> rideList;

    RideArrayAdapter(Context context, List<Ride> rideList) {
        super(context, R.layout.ride_item, rideList);
        this.context = context;
        this.rideList = rideList;
    }

    // Displays data at a specified position in the data set.
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.ride_item, parent, false);

        // Assign views by their respective ids
        TextView dateView = convertView.findViewById(R.id.rideDate);
        TextView timeView = convertView.findViewById(R.id.rideTime);
        TextView distanceView = convertView.findViewById(R.id.rideDistance);
        TextView avgSpeedView = convertView.findViewById(R.id.rideAvgSpeedNum);
        TextView avgCadenceView = convertView.findViewById(R.id.rideAvgCadenceNum);
        TextView commentView = convertView.findViewById(R.id.rideComment);

        Ride currentRide = rideList.get(position);      // Get ride at the specified position
        Resources resources = context.getResources();   // Get resources of context

        // Set the views details from the current ride
        dateView.setText(currentRide.getDate());
        timeView.setText(currentRide.getTime());
        distanceView.setText(resources.getString(R.string.km_string, Float.toString(currentRide.getDistance())));
        avgSpeedView.setText(resources.getString(R.string.kmh_string, Float.toString(currentRide.getAvgSpeed())));
        avgCadenceView.setText(resources.getString(R.string.rpm_string, Integer.toString(currentRide.getAvgCadence())));
        commentView.setText(currentRide.getComment());

        // Since comment is optional, do not show comment view if empty
        if (currentRide.getComment().equals("")) {
            commentView.setVisibility(View.GONE);
        }

        return convertView;
    }
}
