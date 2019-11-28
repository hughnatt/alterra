package ca.uqac.alterra.home;

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
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import ca.uqac.alterra.R;
import ca.uqac.alterra.database.AlterraCloud;
import ca.uqac.alterra.database.AlterraDatabase;
import ca.uqac.alterra.utility.AlterraGeolocator;


public class HomeMapFragment extends Fragment implements AlterraGeolocator.OnLocationChangedListener {

    private MapsHandler mMapsHandler;
    private boolean mEnableLocation;
    private BottomSheetHandler mBottomSheetHandler;
    private static String enableLocationArgument = "enableLocation";
    private float mMapLat,mMapLng,mMapZoom;
    private AlterraPoint mAlterraPoint;

    protected static HomeMapFragment newInstance(boolean enableLocation){
        Bundle args = new Bundle();
        args.putBoolean(enableLocationArgument, enableLocation);
        HomeMapFragment homeMapFragment = new HomeMapFragment();
        homeMapFragment.setArguments(args);
        return homeMapFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_home_map,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
            mMapLat = savedInstanceState.getFloat("LAT");
            mMapLng = savedInstanceState.getFloat("LNG");
            mMapZoom = savedInstanceState.getFloat("ZOOM");
            mAlterraPoint = (AlterraPoint) savedInstanceState.getSerializable("POINT");
        }
    }

    @Override
    public void onStart(){
        assert(getActivity() != null);
        super.onStart();
        if (!mEnableLocation){
            Bundle args = getArguments();
            if (args != null){
                mEnableLocation = args.getBoolean(enableLocationArgument);
            }
        }
        mBottomSheetHandler = new BottomSheetHandler(getActivity(),mAlterraPoint);
        mMapsHandler = new MapsHandler(getContext(),mEnableLocation, mBottomSheetHandler,mMapLat,mMapLng,mMapZoom);


        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(getActivity().findViewById(R.id.bottom_sheet));
        bottomSheetBehavior.addBottomSheetCallback(mBottomSheetHandler);

        /*mCameraButton.setOnClickListener((view ) -> {
            ((HomeActivity) getActivity()).takeAlterraPhoto();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });*/

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert(mapFragment != null);
        mapFragment.getMapAsync(mMapsHandler);

        DrawerLayout navDrawer =getActivity().findViewById(R.id.navDrawer);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ((HomeActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(),navDrawer,toolbar,R.string.app_name,R.string.app_name);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorPrimaryDark));
        navDrawer.addDrawerListener(toggle);
        toggle.syncState();

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
        mMapLat = mMapsHandler.getLatitude();
        mMapLng = mMapsHandler.getLongitude();
        mMapZoom = mMapsHandler.getZoom();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat("LAT",mMapsHandler.getLatitude());
        outState.putFloat("LNG",mMapsHandler.getLongitude());
        outState.putFloat("ZOOM",mMapsHandler.getZoom());
        outState.putSerializable("POINT",mBottomSheetHandler.getAlterraPoint());
    }
}
