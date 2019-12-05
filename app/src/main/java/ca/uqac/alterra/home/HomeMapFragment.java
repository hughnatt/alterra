package ca.uqac.alterra.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.SupportMapFragment;

import ca.uqac.alterra.R;
import ca.uqac.alterra.database.AlterraCloud;
import ca.uqac.alterra.database.AlterraDatabase;
import ca.uqac.alterra.utility.AlterraGeolocator;


public class HomeMapFragment extends Fragment implements AlterraGeolocator.OnLocationChangedListener {

    private MapsHandler mMapsHandler;
    private boolean mEnableLocation;
    private BottomSheetHandler mBottomSheetHandler;
    private float mMapLat,mMapLng,mMapZoom;
    private AlterraPoint mAlterraPoint;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_home_map,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
            mAlterraPoint = (AlterraPoint) savedInstanceState.getSerializable("POINT");
        }

        assert(getActivity() != null);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        mMapLat = sharedPref.getFloat("LAT",0.0F);
        mMapLng = sharedPref.getFloat("LNG",0.0F);
        mMapZoom = sharedPref.getFloat("ZOOM",0.0F);
    }

    @Override
    public void onStart(){
        assert(getActivity() != null);
        super.onStart();

        //Never enable location subsystem at start, it can crash the app if location permission are not granted, wait for geolocator to provide location updates to enable button
        mEnableLocation = false;
        AlterraGeolocator.addOnLocationChangedListener(this);

        mBottomSheetHandler = new BottomSheetHandler(getActivity(),mAlterraPoint);
        mMapsHandler = new MapsHandler(getContext(),mEnableLocation, mBottomSheetHandler,mMapLat,mMapLng,mMapZoom);


        //TODO, see if there is any way to simplify this block
        DrawerLayout navDrawer =getActivity().findViewById(R.id.navDrawer);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ((HomeActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(),navDrawer,toolbar,R.string.app_name,R.string.app_name);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorPrimaryDark));
        navDrawer.addDrawerListener(toggle);
        toggle.syncState();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert(mapFragment != null);
        mapFragment.getMapAsync(mMapsHandler);

        AlterraDatabase alterraDatabase = AlterraCloud.getDatabaseInstance();
            alterraDatabase.getAllAlterraLocations(AlterraCloud.getAuthInstance().getCurrentUser(),(list) -> {
            if (list == null) return;
                for (AlterraPoint p : list) {
                    System.out.println(p.getTitle());
                    mMapsHandler.addAlterraPoint(p);
                }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        //Give the location change event to the maps handler
        if (mMapsHandler != null){
            mMapsHandler.onLocationChanged(location);
        }
        mEnableLocation = true;
        if(mMapsHandler != null){
            mMapsHandler.enableMyLocation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        assert(getActivity() != null);
        AlterraGeolocator.removeOnLocationChangedListener(this);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat("LAT",mMapsHandler.getLatitude());
        editor.putFloat("LNG",mMapsHandler.getLongitude());
        editor.putFloat("ZOOM",mMapsHandler.getZoom());
        editor.apply();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mBottomSheetHandler != null){
            outState.putSerializable("POINT",mBottomSheetHandler.getAlterraPoint());
        }
    }
}
