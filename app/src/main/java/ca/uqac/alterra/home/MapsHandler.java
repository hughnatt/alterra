package ca.uqac.alterra.home;

import android.content.Context;
import android.location.Location;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ca.uqac.alterra.R;
import ca.uqac.alterra.database.AlterraPoint;
import ca.uqac.alterra.utility.AlterraGeolocator;
import ca.uqac.alterra.utility.JsonReader;

public class MapsHandler implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnMapClickListener {

    private Context mContext;
    private GoogleMap mMap;
    private BottomSheetHandler mBottomSheetHandler;
    private LatLng mUserLocation;
    private boolean mEnableLocation;
    private BitmapDescriptor mMarkerUnlockedBitmap;
    private BitmapDescriptor mMarkerUnlockableBitmap;
    private BitmapDescriptor mMarkerLockedBitmap;
    private List<AlterraPoint> mAlterraPoints;
    private List<Marker> mMarkers;
    private CameraPosition mCameraPosition;
    //private Marker mUserMarker;

    public MapsHandler(Context context, boolean enableLocation, BottomSheetHandler bottomSheetHandler){
        mContext = context;
        mBottomSheetHandler = bottomSheetHandler;
        mEnableLocation = enableLocation;
        mMarkerLockedBitmap = BitmapDescriptorFactory.fromAsset(mContext.getString(R.string.map_marker_locked_icon));
        mMarkerUnlockableBitmap = BitmapDescriptorFactory.fromAsset(mContext.getString(R.string.map_marker_unlockable_icon));
        mMarkerUnlockedBitmap = BitmapDescriptorFactory.fromAsset(mContext.getString(R.string.map_marker_unlocked_icon));
        mAlterraPoints = new ArrayList<>();
        mMarkers = new ArrayList<>();
    }

    public MapsHandler(Context context, boolean enableLocation, BottomSheetHandler bottomSheetHandler, float lat, float lng, float zoom){
        this(context,enableLocation,bottomSheetHandler);
        mCameraPosition = CameraPosition.fromLatLngZoom(new LatLng(lat,lng),zoom);
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
        mMap.setOnMarkerClickListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnMapClickListener(this);

        String jsonStylesheet = JsonReader.loadJSONFromAsset(mContext,mContext.getString(R.string.maps_stylesheet));
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

        //Restore previous camera position
        if (mCameraPosition != null){
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        }

        populateMap(mAlterraPoints);
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
        AlterraPoint alterraPoint = (AlterraPoint) Objects.requireNonNull(marker.getTag());
        mBottomSheetHandler.updateSheet(alterraPoint);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),(getZoom() > 14 ? getZoom() : 14)), new GoogleMap.CancelableCallback(){
            @Override
            public void onFinish() {

            }
            @Override
            public void onCancel() {
                //Do Nothing
            }
        });

        //FOR TESTING ONLY, unlock position for current user
        //alterraPoint.unlock();

        return true; //Consume event to prevent the default Google Maps behavior
    }

    @Override
    public void onCameraMove() {
       /* if(mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED )
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);*/
    }


    @Override
    public void onMapClick(LatLng latLng) {
        mBottomSheetHandler.unselectSheet();
    }

    /**
     * Callback for location changes
     * @param location Current user location
     */
    void onLocationChanged(@Nullable Location location) {
        //Update user location and marker position
        mUserLocation = new LatLng(location.getLatitude(),location.getLongitude());

        for (Marker marker : mMarkers) {
            AlterraPoint alterraPoint = (AlterraPoint) marker.getTag();
            assert alterraPoint != null;
            if (!alterraPoint.isUnlocked()){
                if (AlterraGeolocator.distanceFrom(alterraPoint) < AlterraPoint.MINIMUM_UNLOCK_DISTANCE) {
                    marker.setIcon(mMarkerUnlockableBitmap);
                } else {
                    marker.setIcon(mMarkerLockedBitmap);
                }
            }
        }


/*        if (mUserMarker != null){
            mUserMarker.remove();
        }
        mUserMarker = mMap.addMarker(new MarkerOptions()
                .position(mUserLocation)
                .title("Current user location")
                .icon(BitmapDescriptorFactory.fromAsset(mActivity.getString(R.string.asset_icon))));*/
    }

    public void addAlterraPoint(AlterraPoint alterraPoint){
        mAlterraPoints.add(alterraPoint);
        if (mMap != null){
            addMarker(alterraPoint);
        }
    }

    public void populateMap(List<AlterraPoint> alterraLocations){
        for (AlterraPoint alterraPoint : alterraLocations) {
            addMarker(alterraPoint);
        }
    }

    /**
     *
     * @param alterraPoint
     * @return Reference to the new marker or null if marker hasn't been created (maps is not ready)
     */
    @Nullable
    private Marker addMarker(AlterraPoint alterraPoint){
        if (mMap != null) {
            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(alterraPoint.getLatLng())
                    .title(alterraPoint.getTitle())
                    .zIndex(Float.MAX_VALUE));
            m.setTag(alterraPoint);
            if (alterraPoint.isUnlocked()) {
                m.setIcon(mMarkerUnlockedBitmap);
            } else {
                m.setIcon(mMarkerLockedBitmap);
            }
            mMarkers.add(m);
            return m;
        } else {
            return null;
        }
    }

    protected float getLatitude(){
        if (mMap != null){
            return (float) mMap.getCameraPosition().target.latitude;
        } else {
            return 0.0F;
        }
    }
    protected float getLongitude(){
        if (mMap != null){
            return (float) mMap.getCameraPosition().target.longitude;
        } else {
            return 0.0F;
        }
    }

    protected float getZoom() {
        if (mMap != null) {
            return mMap.getCameraPosition().zoom;
        } else {
            return 0.0F;
        }
    }
}
