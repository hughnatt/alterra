package ca.uqac.alterra;

import android.app.Activity;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class MapsHandler implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private Activity mActivity;
    private BottomSheetBehavior mBottomPanel;

    public MapsHandler(Activity activity){
        mActivity = activity;
        mBottomPanel = BottomSheetBehavior.from(mActivity.findViewById(R.id.bottomPanel));
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
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMarkerClickListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnMapClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        System.out.println(marker.toString());
        mBottomPanel.setState(BottomSheetBehavior.STATE_EXPANDED);
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
}
