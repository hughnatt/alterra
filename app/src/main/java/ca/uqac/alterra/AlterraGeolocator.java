package ca.uqac.alterra;

import android.content.Context;
import android.location.Location;

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

public class AlterraGeolocator extends LocationCallback {

    //Try to update location every 5 seconds
    private static final int LOCATION_UPDATE_INTERVAL=5000;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mCurrentLocation;
    private List<OnLocationChangedListener> mOnLocationChangedListeners;

    public AlterraGeolocator(Context context){
        super();
        mOnLocationChangedListeners = new ArrayList<>();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        mFusedLocationProviderClient.requestLocationUpdates(LocationRequest.create()
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

    public LatLng getCurrentLatLng(){
        return new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
    }

    /**
     * Add a new listener that will receive an update when location changes
     * @param myListener
     */
    public void addOnLocationChangedListener(OnLocationChangedListener myListener){
        mOnLocationChangedListeners.add(myListener);
    }

    private class AlterraLocationCallback extends LocationCallback {
        @Override
        public void onLocationAvailability (LocationAvailability locationAvailability){
            super.onLocationAvailability(locationAvailability);
            if (locationAvailability.isLocationAvailable()){
                System.out.println("Location on");
            } else {
                System.out.println("Location off");
            }
        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            System.out.println(locationResult.getLastLocation());
            mCurrentLocation = locationResult.getLastLocation();
            Iterator<OnLocationChangedListener> listenerIterator = mOnLocationChangedListeners.iterator();
            while (listenerIterator.hasNext()){
                listenerIterator.next().onLocationChanged(mCurrentLocation);
            }
        }
    }

    public interface OnLocationChangedListener {
        void onLocationChanged(Location location);
    }
}
