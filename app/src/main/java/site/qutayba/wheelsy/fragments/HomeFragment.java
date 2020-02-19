package site.qutayba.wheelsy.fragments;


import android.Manifest;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.joaquimley.faboptions.FabOptionsButtonContainer;

import site.qutayba.wheelsy.R;
import site.qutayba.wheelsy.databinding.FragmentHomeBinding;
import site.qutayba.wheelsy.enums.ServiceState;
import site.qutayba.wheelsy.helpers.ServiceHelper;
import site.qutayba.wheelsy.models.Trip;
import site.qutayba.wheelsy.services.ForegroundService;
import site.qutayba.wheelsy.viewmodels.HomeViewModel;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private LocalBroadcastManager broadcastManager;

    public HomeFragment() {
        // Required empty public constructor
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

    private void initFabOptions() {
        initFabOptionsColors();

        binding.fabOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.faboptions_play)
                    updateState(ServiceState.RUNNING);
                if(v.getId() == R.id.faboptions_pause)
                    updateState(ServiceState.PAUSED);
                if(v.getId() == R.id.faboptions_replay)
                    updateState(ServiceState.RESET);
                else if(v.getId() == R.id.faboptions_stop)
                    updateState(ServiceState.STOPPED);
            }
        });
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(ForegroundService.BROADCAST_ACTION.equals(intent.getAction()))
            {
                // Setting the distance update for the UI
                viewModel.setDistance(intent.getFloatExtra(getString(R.string.service_distance), 0.0f));

                // Adding the new location to the trip
                double latitude = intent.getDoubleExtra(getString(R.string.service_latitude), 0);
                double longitude = intent.getDoubleExtra(getString(R.string.service_longitude), 0);
                viewModel.addLocation(latitude, longitude);
            }
        }
    };
    private void initFabOptionsColors() {

        // Setting the main button color
        ViewGroup btnGroup = (ViewGroup)binding.fabOptions.getChildAt(0);
        FloatingActionButton btn = btnGroup.findViewById(R.id.faboptions_fab);
        btn.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorOnPrimary)));
        
        // Setting the color of the option buttons
        binding.fabOptions.setButtonColor(R.id.faboptions_pause, R.color.colorOnPrimary);
        binding.fabOptions.setButtonColor(R.id.faboptions_play, R.color.colorOnPrimary);
        binding.fabOptions.setButtonColor(R.id.faboptions_stop, R.color.colorOnPrimary);
        binding.fabOptions.setButtonColor(R.id.faboptions_replay, R.color.colorOnPrimary);

    }

    private void startService() {
        checkPremissions();

        Intent serviceIntent = new Intent(getActivity(), ForegroundService.class);
        ContextCompat.startForegroundService(getActivity(), serviceIntent);
    }
    private void stopService() {
        Intent serviceIntent = new Intent(getActivity(), ForegroundService.class);
        getActivity().stopService(serviceIntent);
    }

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

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ForegroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void checkPremissions() {
        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getActivity().requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }
}
