package site.qutayba.wheelsy.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import site.qutayba.wheelsy.R;
import site.qutayba.wheelsy.enums.ServiceState;
import site.qutayba.wheelsy.services.ForegroundService;

public class ServiceHelper {
    public static ServiceState getState(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(ForegroundService.PREFERENCES, Context.MODE_PRIVATE);
        String stateString = preferences.getString(context.getString(R.string.service_state), "");
        return ServiceState.fromString(stateString);
    }

    public static void setState(Context context, ServiceState state) {
        SharedPreferences preferences = context.getSharedPreferences(ForegroundService.PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getString(R.string.service_state), state.name());
        editor.commit();
    }

    public static float getDistance(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences(ForegroundService.PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getFloat(context.getString(R.string.service_distance), 0.0f);
    }

    public static void setDistance(Context context, float distance) {
        SharedPreferences preferences = context.getSharedPreferences(ForegroundService.PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(context.getString(R.string.service_distance), distance);
        editor.apply();
    }

}
