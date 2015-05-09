package edu.teco.guerillaSensing.data;

import com.google.gson.Gson;

import java.util.List;

/**
 * This class represents the JSON data returned on a query to the the InfluxDB.
 */
public class SensorDataPoint {
    public String name;
    public List<String> columns;
    public List<List<String>> points;

    public static SensorDataPoint fromJson(String s) {
        return new Gson().fromJson(s, SensorDataPoint.class);
    }
    public String toString() {
        return new Gson().toJson(this);
    }
}

