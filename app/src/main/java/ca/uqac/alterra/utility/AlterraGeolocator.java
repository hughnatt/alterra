package ca.uqac.alterra.utility;

import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AlterraGeolocator {

    //Try to update location every 5 seconds
    private static final int LOCATION_UPDATE_INTERVAL=5000;
    private static Location mCurrentLocation;
    private static List<OnLocationChangedListener> mOnLocationChangedListeners = new ArrayList<>();
    private static List<OnGPSStatusChangedListener> mOnGPSStatusChangedListeners = new ArrayList<>();
    private static boolean mGpsEnabled = false;

    public static void initGeolocatorForContext(Context context){
        FusedLocationProviderClient fusedLocationProviderClient;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        fusedLocationProviderClient.requestLocationUpdates(LocationRequest.create()
                        .setInterval(LOCATION_UPDATE_INTERVAL)
                        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY),
                new AlterraLocationCallback(),null);
                /*Task task = mFusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener((OnSuccessListener<Location>) location -> {

            if (location != null){
                System.out.println("Position:" + location.getLatitude() + "," + location.getLongitude());
            } else {
                System.out.println("null location");
            }
        });*/
    }

    public static LatLng getCurrentLatLng(){
        if (!mGpsEnabled || mCurrentLocation == null){
            return null;
        } else {
            return new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
        }
    }

    public static Location getCurrentLocation(){
        if (!mGpsEnabled || mCurrentLocation == null){
            return null;
        } else {
            return mCurrentLocation;
        }
    }

    /**
     * Add a new listener that will receive an update when location changes
     * @param onLocationChangedListener callback for location updates
     */
    public static void addOnLocationChangedListener(@NonNull OnLocationChangedListener onLocationChangedListener){
        mOnLocationChangedListeners.add(onLocationChangedListener);
    }

    /**
     * Add a new listener that will receive update when GPS status changes
     * @param onGPSStatusChangedListener callback for GPS status changes
     */
    public static void addOnGPSStatusChangedListener(@NonNull OnGPSStatusChangedListener onGPSStatusChangedListener){
        mOnGPSStatusChangedListeners.add(onGPSStatusChangedListener);
    }

    private static class AlterraLocationCallback extends LocationCallback {
        @Override
        public void onLocationAvailability (LocationAvailability locationAvailability){
            super.onLocationAvailability(locationAvailability);
            if (locationAvailability.isLocationAvailable()){
                System.out.println("Location on");
                mGpsEnabled = true;
            } else {
                System.out.println("Location off");
                mGpsEnabled = false;
            }
            for (OnGPSStatusChangedListener mOnGPSStatusChangedListener : mOnGPSStatusChangedListeners) {
                mOnGPSStatusChangedListener.onGPSStatusChanged(mGpsEnabled);
            }
        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            System.out.println(locationResult.getLastLocation());
            mCurrentLocation = locationResult.getLastLocation();
            for (OnLocationChangedListener mOnLocationChangedListener : mOnLocationChangedListeners) {
                mOnLocationChangedListener.onLocationChanged(mCurrentLocation);
            }
        }
    }

    public interface OnLocationChangedListener {
        void onLocationChanged(Location location);
    }

    public interface OnGPSStatusChangedListener {
        void onGPSStatusChanged(boolean enable);
    }
}
