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

import site.qutayba.wheelsy.R;
import site.qutayba.wheelsy.databinding.FragmentTripDetailsBinding;
import site.qutayba.wheelsy.models.Trip;
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


    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style_2));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }


        LatLng purmerend = new LatLng(52.509930, 4.945020);
        LatLng amsterdam = new LatLng(52.373169, 4.890660);
        LatLng l1 = new LatLng(52.494800, 5.071470);
        PolylineOptions rectOptions = new PolylineOptions()
                .add(purmerend,l1, amsterdam)
                .color(getContext().getResources().getColor(R.color.colorPrimary));
        googleMap.addMarker(new MarkerOptions().position(purmerend)
                .title("Purmerend").icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));
        googleMap.addMarker(new MarkerOptions().position(amsterdam)
                .title("Amsterdam").icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

//the include method will calculate the min and max bound.
        builder.include(purmerend);
        builder.include(l1);
        builder.include(amsterdam);
        LatLngBounds bounds = builder.build();

        googleMap.addPolyline(rectOptions);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);

        googleMap.animateCamera(cu, 2000, null);
        binding.mapView.onResume();
    }

    public void onShareClick(View view) {
        String message = String.format(getString(R.string.share_trip_message), binding.getTrip().getName(), binding.getTrip().getFormattedDistance());
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);

        startActivity(Intent.createChooser(share, getString(R.string.share_trip_title)));
    }

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
