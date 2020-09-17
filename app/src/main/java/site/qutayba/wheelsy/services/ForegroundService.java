package site.qutayba.wheelsy.services;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import site.qutayba.wheelsy.R;
import site.qutayba.wheelsy.activities.MainActivity;
import site.qutayba.wheelsy.enums.ServiceState;
import site.qutayba.wheelsy.helpers.ServiceHelper;

public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static final String BROADCAST_ACTION = "SERVICE_ACTION";
    public static final String PREFERENCES = "SERVICE_PREFERENCES";

    private final int NOTIFICATION_ID = 1;
    private final long RUNNABLE_INTERVAL = TimeUnit.SECONDS.toMillis(1);
    private LocationManager locationManager;
    private Handler handler = new Handler();
    private ServiceState state = ServiceState.STOPPED;
    private List<Location> locations = new ArrayList<>();
    private float distance = 0;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, getServiceNotification());
        handler.post(serviceRunnable);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locations.clear();
        distance = 0;
        handler.removeCallbacks(serviceRunnable);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * The service's runnable to trigger the handle function
     */
    private final Runnable serviceRunnable = new Runnable() {
        @Override
        public void run() {
            handle();
            handler.postDelayed(serviceRunnable, RUNNABLE_INTERVAL);
        }
    };

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
//            Log.i("SERVICE", location.getLatitude() + " - " + location.getLongitude());

            // If the locations list is empty (new trip), then define the current location as a starting point
            if(locations.isEmpty())
                locations.add(location);

            // Calculate the distance in meters
            Location lastLocation = locations.get(locations.size() - 1);
            float newDistance = lastLocation.distanceTo(location);

            // Checking if there is any change in the user's location
            if(newDistance > 0) {

                // Adding the new distance to the total distance in KM
                distance = distance + (newDistance / 1000);
                Log.d("SERVICE", "New distance: " + (newDistance / 1000) + ", Total: " + distance);

                // Adding the new location to the locations list
                locations.add(location);

                // Broadcasting the new distance to the view
                Intent intent = new Intent(BROADCAST_ACTION);
                LocalBroadcastManager manager = LocalBroadcastManager.getInstance(ForegroundService.this);
                intent.putExtra(getString(R.string.state_distance), distance);
                intent.putExtra(getString(R.string.service_latitude), location.getLatitude());
                intent.putExtra(getString(R.string.service_longitude), location.getLongitude());
                manager.sendBroadcast(intent);
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    /**
     * Handles the service job
     */
    private void handle() {
        Log.d("SERVICE","handle()");

        // Get the current state
        ServiceState newState = ServiceHelper.getState(this);

        // Check if the state has been changed
        if (newState != state) {
            state = newState;

            // There is no need to send a notification about resetting or stopping the service
            if (state != ServiceState.STOPPED && state != ServiceState.RESET) {
                Notification notification = getServiceNotification();
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(NOTIFICATION_ID, notification);
            }
        }

        // Check if the service is running
        if (state.equals(ServiceState.RUNNING)) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
        }
    }

    /**
     * Creates the service's notification
     * @return Service's notification
     */
    private Notification getServiceNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        // Getting the notification's text-id based on the service's state
        int textStringId =  state == ServiceState.PAUSED ? R.string.service_notifi_text_paused : R.string.service_notifi_text;

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.service_notifi_title))
                .setContentText(getString(textStringId))
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal)
                .setContentIntent(pendingIntent)
                .build();
    }

    /**
     * Creates the service's notification channel
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.service_notifi_channel),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
