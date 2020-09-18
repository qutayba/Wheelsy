package site.qutayba.wheelsy.fragments;


import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import site.qutayba.wheelsy.R;
import site.qutayba.wheelsy.databinding.FragmentHomeBinding;
import site.qutayba.wheelsy.enums.ServiceState;
import site.qutayba.wheelsy.helpers.ServiceHelper;
import site.qutayba.wheelsy.models.Trip;
import site.qutayba.wheelsy.repositories.TripRepository;
import site.qutayba.wheelsy.services.ForegroundService;
import site.qutayba.wheelsy.viewmodels.HomeViewModel;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TripRepository tripRepository;
    private HomeViewModel viewModel;
    private LocalBroadcastManager broadcastManager;

    private ImageView[] images;
    private int selectedImageViewIndex;

    private final String LOG_TAG = "HomeFragment";

    public HomeFragment() {
        // Required empty public constructor
        tripRepository = new TripRepository();
        images = new ImageView[3];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        viewModel = new HomeViewModel(getContext());
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        viewModel.setDistance(0.0f);
        updateState(ServiceHelper.getState(getActivity()));
        binding.setViewModel(viewModel);
        initFabOptions();

        IntentFilter intentFilter = new IntentFilter(ForegroundService.BROADCAST_ACTION);
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_CANCELED)
            return;
        if (resultCode == RESULT_OK && data != null) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            images[selectedImageViewIndex].setImageBitmap(image);
            images[selectedImageViewIndex].setTag("captured");
        }
    }

    /**
     * Initializes the FAB menu
     */
    private void initFabOptions() {

        // Setting the main button color
        ViewGroup btnGroup = (ViewGroup) binding.fabOptions.getChildAt(0);
        FloatingActionButton btn = btnGroup.findViewById(R.id.faboptions_fab);
        btn.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorOnPrimary)));

        // Setting the color of the option buttons
        binding.fabOptions.setButtonColor(R.id.faboptions_pause, R.color.colorOnPrimary);
        binding.fabOptions.setButtonColor(R.id.faboptions_play, R.color.colorOnPrimary);
        binding.fabOptions.setButtonColor(R.id.faboptions_stop, R.color.colorOnPrimary);
        binding.fabOptions.setButtonColor(R.id.faboptions_replay, R.color.colorOnPrimary);

        // Handling the click event of the FAB options
        binding.fabOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.faboptions_play)
                    updateState(ServiceState.RUNNING);
                if (v.getId() == R.id.faboptions_pause)
                    updateState(ServiceState.PAUSED);
                if (v.getId() == R.id.faboptions_replay)
                    updateState(ServiceState.RESET);
                else if (v.getId() == R.id.faboptions_stop)
                    updateState(ServiceState.STOPPED);
            }
        });
    }

    /**
     * Broadcast receives the current location from the running service
     */
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ForegroundService.BROADCAST_ACTION.equals(intent.getAction())) {
                try {
                    // Setting the distance update for the UI
                    viewModel.setDistance(intent.getFloatExtra(getString(R.string.service_distance), 0.0f));

                    // Adding the new location to the trip
                    double latitude = intent.getDoubleExtra(context.getString(R.string.service_latitude), 0);
                    double longitude = intent.getDoubleExtra(context.getString(R.string.service_longitude), 0);
                    viewModel.addLocation(latitude, longitude);
                } catch (Exception ex) {
                    Log.e(LOG_TAG, ex.getLocalizedMessage());
                }
            }
        }
    };

    /**
     * Starts the background service
     */
    private void startService() {
        checkPermissions();
        Intent serviceIntent = new Intent(getActivity(), ForegroundService.class);
        ContextCompat.startForegroundService(getActivity(), serviceIntent);
    }

    /**
     * Stops the running background service
     */
    private void stopService() {
        Intent serviceIntent = new Intent(getActivity(), ForegroundService.class);
        getActivity().stopService(serviceIntent);
        confirmStopping();
    }

    /**
     * Handles the state changes
     *
     * @param state the service's state
     */
    private void updateState(ServiceState state) {
        this.viewModel.setState(state);
        ServiceHelper.setState(getActivity(), state);

        if (state == ServiceState.RUNNING && !isServiceRunning()) {
            startService();
        } else if (state == ServiceState.STOPPED && isServiceRunning()) {
            stopService();
        } else if (state == ServiceState.RESET) {
            stopService();
            viewModel.setDistance(0.0f);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateState(ServiceState.RUNNING);
                }
            }, 500);
        }
    }

    /**
     * Checks if the background service is running
     *
     * @return true if the service is running, otherwise false
     */
    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ForegroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the application have the required permissions to start the background service
     */
    private void checkPermissions() {
        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

    /**
     * Shows a dialog to confirm if the user needs to create a new trip
     */
    private void confirmStopping() {
        if (this.viewModel.requiresConfirmation()) {
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle(getString(R.string.save_trip_title))
                    .setMessage(getString(R.string.save_trip_message))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            createTripDialog();
                        }
                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();
        }
    }

    /**
     * Creates a new trip based on the gathered locations from the service
     */
    private void createTripDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.new_trip));

        // Getting the custom view of create trip dialog
        final View customLayout = getLayoutInflater().inflate(R.layout.trip_create, null);

        // Setting image-views click handlers
        images[0] = customLayout.findViewById(R.id.imageView1);
        images[1] = customLayout.findViewById(R.id.imageView2);
        images[2] = customLayout.findViewById(R.id.imageView3);
        for (int i = 0; i < images.length; i++) {
            final int index = i;
            images[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddPhotoDialog(index);
                }
            });
        }

        // Configuring the dialog builder
        builder.setView(customLayout);
        builder
                .setPositiveButton(getString(R.string.save), null)
                .setCancelable(false)
                .setNegativeButton(getString(R.string.cancel), null);
        AlertDialog dialog = builder.create();

        // Handles the save button click event
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button saveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createTrip(customLayout, dialog);
                    }
                });
            }
        });

        dialog.show();
    }

    /**
     * Creates the current trip from the view-model
     *
     * @param dialogView the custom view of the create trip dialog
     */
    private void createTrip(View dialogView, DialogInterface dialog) {

        // Getting the name input view
        TextInputLayout nameTextInput = dialogView.findViewById(R.id.textFieldTripName);
        String tripName = nameTextInput.getEditText().getText().toString();

        // Checking if the trip is valid (we need only the name at this point)
        boolean isTripValid = tripName.length() != 0;

        // Handles the error for the trip name input
        nameTextInput.setErrorEnabled(!isTripValid);
        nameTextInput.setError(getString(R.string.error_message_required));

        // Getting the trip's description
        TextInputLayout descriptionTextInput = dialogView.findViewById(R.id.textFieldTripDescription);
        String tripDescription = descriptionTextInput.getEditText().getText().toString();

        // If the trip is invalid. Stop everything
        if (!isTripValid)
            return;

        // Getting the current trip and enrich it with the entered name and description
        Trip trip = viewModel.getTrip();
        trip.setName(tripName);
        trip.setDescription(tripDescription);
        dialog.dismiss();

        // Creates a new trip in the backend and handles the task result
        Task<Void> saveTask = tripRepository.create(trip);
        Task<Void> uploadImagesTask = updateSelectedImages(trip);
        Tasks.whenAll(saveTask, uploadImagesTask)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        new MaterialAlertDialogBuilder(getContext())
                                .setTitle(R.string.created_successfully)
                                .setMessage(R.string.trip_created_success_message)
                                .setIcon(R.drawable.outline_check_circle_black_24dp)
                                .setNegativeButton(R.string.close, null)
                                .show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new MaterialAlertDialogBuilder(getContext())
                                .setTitle(R.string.somthing_went_wrong)
                                .setNegativeButton(getString(R.string.close), null)
                                .show();
                    }
                });
    }

    private void showAddPhotoDialog(int imageViewIndex) {
        selectedImageViewIndex = imageViewIndex;
        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 0);
    }

    private Task<Void> updateSelectedImages(Trip trip) {
        Bitmap[] bitmaps = new Bitmap[images.length];

        for (int i = 0; i < images.length; i++) {
            if (images[i].getTag() != "captured")
                continue;

            Bitmap bitmap = ((BitmapDrawable) images[i].getDrawable()).getBitmap();
            if (bitmap != null)
                bitmaps[i] = bitmap;
        }

        if (bitmaps.length > 0)
            return tripRepository.uploadImages(trip, bitmaps);

        return null;
    }
}
