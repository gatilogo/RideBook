package com.example.gatilogo_ridebook;

import java.io.Serializable;

/**
 * Ride class (data structure)
 *
 * Keeps track of the date, time, distance, average speed, average cadence,
 * and comment of each ride.
 */
class Ride implements Serializable {

    private String date;
    private String time;
    private float distance;
    private float avgSpeed;
    private int avgCadence;
    private String comment;

    Ride(String date, String time, float distance, float avgSpeed, int avgCadence, String comment) {
        this.date = date;
        this.time = time;
        this.distance = distance;
        this.avgSpeed = avgSpeed;
        this.avgCadence = avgCadence;
        this.comment = comment;
    }

    String getDate() {
        return this.date;
    }

    String getTime() {
        return this.time;
    }

    float getDistance() {
        return this.distance;
    }

    float getAvgSpeed() {
        return this.avgSpeed;
    }

    int getAvgCadence() {
        return this.avgCadence;
    }

    String getComment() {
        return this.comment;
    }
}
