package ca.uqac.alterra.utility;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.alterra.home.AlterraPoint;

public class AlterraGeolocator {

    private static final int LOCATION_UPDATE_INTERVAL=5000;
    private static Location mCurrentLocation;
    private static List<OnLocationChangedListener> mOnLocationChangedListeners = new ArrayList<>();
    private static List<OnGPSStatusChangedListener> mOnGPSStatusChangedListeners = new ArrayList<>();
    private static boolean mGpsEnabled = false;

    /**
     * Init the application-wide geolocation system with this context
     * @param context Main application context
     */
    public static void initGeolocatorForContext(Context context){
        FusedLocationProviderClient fusedLocationProviderClient;
        //Get the Google Play Services location provider
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        //Init the location request delay and set the callback
        fusedLocationProviderClient.requestLocationUpdates(LocationRequest.create()
                        .setInterval(LOCATION_UPDATE_INTERVAL)
                        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY),
                new AlterraLocationCallback(),null);
    }

    /**
     * Return the current position
     * @return Current position as a LatLng object or null if position cannot be determined
     */
    public static LatLng getCurrentLatLng(){
        if (!mGpsEnabled || mCurrentLocation == null){
            return null;
        } else {
            return new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
        }
    }

    /**
     * Return the current position
     * @return Current position as a Location object or null if position cannot be determined
     */
    public static Location getCurrentLocation(){
        if (!mGpsEnabled || mCurrentLocation == null){
            return null;
        } else {
            return mCurrentLocation;
        }
    }

    /**
     * Return the distance between current user location and the given Alterra Point
     * @param alterraPoint an alterra location descriptor
     * @return distance in meters
     */
    public static double distanceFrom(AlterraPoint alterraPoint){
        Location currentPosition = AlterraGeolocator.getCurrentLocation();
        if (currentPosition == null) {
            return Double.MAX_VALUE;
        } else {
            Location temp = new Location(LocationManager.GPS_PROVIDER);
            temp.setLatitude(alterraPoint.getLatitude());
            temp.setLongitude(alterraPoint.getLongitude());
            return currentPosition.distanceTo(temp);
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

    /**
     * Location Callback for location updates
     */
    private static class AlterraLocationCallback extends LocationCallback {
        @Override
        public void onLocationAvailability (LocationAvailability locationAvailability){
            mGpsEnabled = locationAvailability.isLocationAvailable();

            for (OnGPSStatusChangedListener mOnGPSStatusChangedListener : mOnGPSStatusChangedListeners) {
                mOnGPSStatusChangedListener.onGPSStatusChanged(mGpsEnabled);
            }

            super.onLocationAvailability(locationAvailability);
        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            mCurrentLocation = locationResult.getLastLocation();

            for (OnLocationChangedListener mOnLocationChangedListener : mOnLocationChangedListeners) {
                mOnLocationChangedListener.onLocationChanged(mCurrentLocation);
            }

            super.onLocationResult(locationResult);
        }
    }

    /*
     * Interfaces for callbacks/listeners
     */

    public interface OnLocationChangedListener {
        void onLocationChanged(Location location);
    }

    public interface OnGPSStatusChangedListener {
        void onGPSStatusChanged(boolean enable);
    }
}
