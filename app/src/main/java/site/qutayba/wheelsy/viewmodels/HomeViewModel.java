package site.qutayba.wheelsy.viewmodels;


import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import site.qutayba.wheelsy.enums.ServiceState;
import site.qutayba.wheelsy.models.Trip;

public class HomeViewModel extends BaseObservable {
    private ServiceState state;
    private Context context;
    private Trip trip;

    public HomeViewModel(Context context) {
        this.context = context;
        trip = new Trip();
    }

    @Bindable
    public String getStateName() {
        return context.getString(state.getStringId());
    }

    @Bindable
    public int getStateColor() {
        return context.getColor(state.getColorId());
    }

    @Bindable
    public String getDistance() {
        return String.format("%.1f", trip.getDistance());
    }

    public Trip getTrip() {
        return trip;
    }

    public ServiceState getState() {
        return state;
    }

    public void setState(ServiceState state) {
        this.state = state;
        notifyChange();
    }

    public void setDistance(float distance) {
        trip.setDistance(distance);
        notifyChange();
    }

    public void addLocation(double latitude, double longitude) {
        trip.addLocation(latitude, longitude);
    }

    public boolean requiresConfirmation() {
        return this.trip.getDistance() > 0 || this.trip.getLocations().size() > 0;
    }
}
