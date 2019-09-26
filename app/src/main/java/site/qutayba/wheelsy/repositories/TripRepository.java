package site.qutayba.wheelsy.repositories;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import site.qutayba.wheelsy.models.Trip;

public class TripRepository extends BaseRepository<Trip> {

    public TripRepository() {
        super("trips");
    }

}
