package com.example.trollsmarter.HelperClasses;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.annotation.NonNull;
import org.greenrobot.eventbus.EventBus;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;

public class GPSSpeedProvider implements LocationListener {
    private Location lastLocation;
    private double lastTime;
    public double speed;

    @Override
    public String toString(){
        return String.valueOf(speed);
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        double distance = lastLocation.distanceTo(location);
        double elapsedTime = System.currentTimeMillis() / 1000L;
        speed = 2.23694 * (distance/(elapsedTime - lastTime));
        lastLocation = location;
        lastTime = System.currentTimeMillis() / 1000L;
        EventBus.getDefault().post(new SpeedUpdateEvent(speed));
    }

    public GPSSpeedProvider(){
        lastLocation = new Location(GPS_PROVIDER);
        lastTime = System.currentTimeMillis() / 1000L;
    }
}
