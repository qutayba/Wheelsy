package site.qutayba.wheelsy.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import site.qutayba.wheelsy.R;
import site.qutayba.wheelsy.databinding.FragmentTripDetailsBinding;
import site.qutayba.wheelsy.models.Trip;
import site.qutayba.wheelsy.models.TripLocation;
import site.qutayba.wheelsy.repositories.TripRepository;


public class TripDetailsFragment extends Fragment implements OnMapReadyCallback {

    private FragmentTripDetailsBinding binding;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final String TAG = "TRIP DETAIL FRAGMENT";
    private TripRepository repository;

    public TripDetailsFragment() {
        repository = new TripRepository();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_trip_details, container, false);
        setHasOptionsMenu(true);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        binding.setHandler(this);
        binding.mapView.onCreate(mapViewBundle);
        binding.mapView.getMapAsync(this);

        Trip trip = TripDetailsFragmentArgs.fromBundle(getArguments()).getTrip();
        binding.setTrip(trip);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles on-ready state for the attached map view
     * @param googleMap the attached map object
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Parsing and setting the custom map style for Google maps
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style_2));

        // Initializing locations array/builder
        List<LatLng> latLngs = new ArrayList<>();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        // Building the locations array/builder
        for (TripLocation location : binding.getTrip().getLocations()) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            latLngs.add(latLng);
            builder.include(latLng);
        }

        // Getting the first and last locations to build the map markers
        LatLng firstLoc = latLngs.get(0);
        LatLng lastLoc = latLngs.get(latLngs.size() - 1);

        // Building the poly line options
        PolylineOptions rectOptions = new PolylineOptions()
                .addAll(latLngs)
                .color(getContext().getResources().getColor(R.color.colorPrimary));

        // Setting the start and end makers in the map (first and last locations)
        googleMap.addMarker(new MarkerOptions().position(firstLoc)
                .title(getString(R.string.start))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));
        googleMap.addMarker(new MarkerOptions().position(lastLoc)
                .title(getString(R.string.end))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));

        // Building the locations bounds
        LatLngBounds bounds = builder.build();

        // Adding poly line (based on the options above)
        googleMap.addPolyline(rectOptions);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);

        // Setting the camera distance in the map
        googleMap.animateCamera(cu, 2000, null);
        binding.mapView.onResume();
    }

    /**
     * Handles the share option for a trip. It triggers the default share popup of Android
     * @param view the attached view
     */
    public void onShareClick(View view) {
        String message = String.format(getString(R.string.share_trip_message), binding.getTrip().getName(), binding.getTrip().getFormattedDistance());
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(share, getString(R.string.share_trip_title)));
    }

    /**
     * Handles the delete option for a trip by triggering a confirmation dialog
     * @param view the attached view
     */
    public void onDeleteClick(View view) {
        new MaterialAlertDialogBuilder(getContext(), R.style.AppTheme_Alert_Delete)
                .setTitle(getString(R.string.delete_trip_title))
                .setMessage(getString(R.string.delete_trip_message))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTrip(binding.getTrip());
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    /**
     * Deletes a trip from the backend
     * @param trip The trip object to be deleted
     */
    private void deleteTrip(Trip trip) {
        binding.setIsLoading(true);
        repository.delete(trip).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if(getActivity() != null)
                    getActivity().onBackPressed();
                binding.setIsLoading(false);
            }
        });

    }
}
