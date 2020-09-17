package site.qutayba.wheelsy.models;

import java.io.Serializable;

public class TripLocation implements IModel, Serializable {
    private String id;
    private double latitude;
    private double longitude;

    public TripLocation() {

    }

    public TripLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
