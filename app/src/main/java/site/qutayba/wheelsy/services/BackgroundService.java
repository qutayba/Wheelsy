package site.qutayba.wheelsy.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class BackgroundService extends IntentService {

    private final String LOG_TAG = "BackgroundService";

    public BackgroundService() {
        super("Wheelsy background service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        for (int i = 0; i < 10; i++) {
            Log.v(LOG_TAG, "Background time: " + i);
        }

        try {
            Thread.sleep(500);
        } catch (Exception ex) {

        }
    }
}
