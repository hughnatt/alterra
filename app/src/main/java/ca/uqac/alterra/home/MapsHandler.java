package ca.uqac.alterra.home;

import android.app.Activity;
import android.location.Location;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.Iterator;
import java.util.List;

import ca.uqac.alterra.R;
import ca.uqac.alterra.utility.JsonReader;

public class MapsHandler implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnMapClickListener, AlterraGeolocator.OnLocationChangedListener {

    private GoogleMap mMap;
    private Activity mActivity;
    private BottomSheetBehavior mBottomPanel;
    private LatLng mUserLocation;
    private boolean mEnableLocation;
    private BitmapDescriptor mAlterraMarkerBitmap;
    //private Marker mUserMarker;

    public MapsHandler(Activity activity, boolean enableLocation){
        mActivity = activity;
        mBottomPanel = BottomSheetBehavior.from(mActivity.findViewById(R.id.bottomPanel));
        mEnableLocation = enableLocation;
        mAlterraMarkerBitmap = BitmapDescriptorFactory.fromAsset(mActivity.getString(R.string.asset_icon));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMarkerClickListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnMapClickListener(this);

        String jsonStylesheet = JsonReader.loadJSONFromAsset(mActivity,mActivity.getString(R.string.maps_stylesheet));
        mMap.setMapStyle(new MapStyleOptions(jsonStylesheet));
        /*mMap.setOnMyLocationButtonClickListener(() -> {
            System.out.println("Click on geolocate");
            mMap.animateCamera(CameraUpdateFactory.newLatLng(mUserLocation), 200, null);
            return true;
        });*/
        mMap.setPadding(0,100,0,0);

        if (mEnableLocation){
            mMap.setMyLocationEnabled(true);
        }
    }

    /**
     * Should be called only when we are sure to have the ACCESS_FINE_LOCATION permission
     */
    public void enableMyLocation(){

        mEnableLocation = true;
        if (mMap != null){
            mMap.setMyLocationEnabled(true);
        }
        /* else:
         * We have the location permission but the maps is not ready
         * the location service will be enabled when the map becomes ready
         * thanks to the mEnableLocation boolean value
         */

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        System.out.println(marker.toString());

        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()),200, new GoogleMap.CancelableCallback(){
            @Override
            public void onFinish() {
                mBottomPanel.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            @Override
            public void onCancel() {
                //Do Nothing
            }
        });


        return true; //Consume event to prevent the default Google Maps behavior
    }

    @Override
    public void onCameraMove() {
        mBottomPanel.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }


    @Override
    public void onMapClick(LatLng latLng) {
        mBottomPanel.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onLocationChanged(Location location) {
        //Update user location and marker position
        mUserLocation = new LatLng(location.getLatitude(),location.getLongitude());

/*        if (mUserMarker != null){
            mUserMarker.remove();
        }
        mUserMarker = mMap.addMarker(new MarkerOptions()
                .position(mUserLocation)
                .title("Current user location")
                .icon(BitmapDescriptorFactory.fromAsset(mActivity.getString(R.string.asset_icon))));*/
    }

    public void addAlterraMarker(AlterraPoint alterraPoint){
        mMap.addMarker(new MarkerOptions()
                .position(alterraPoint.getLatLng())
                .title(alterraPoint.getTitle())
                .icon(mAlterraMarkerBitmap));
    }

    public void populateMap(List<AlterraPoint> alterraLocations){
        Iterator<AlterraPoint> iter = alterraLocations.iterator();
        while (iter.hasNext()){
            AlterraPoint alterraPoint = iter.next();
            mMap.addMarker(new MarkerOptions()
                    .position(alterraPoint.getLatLng())
                    .title(alterraPoint.getTitle())
                    .icon(mAlterraMarkerBitmap));
        }
    }
}
