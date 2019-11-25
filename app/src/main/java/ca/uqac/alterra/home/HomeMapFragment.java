package ca.uqac.alterra.home;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Iterator;

import ca.uqac.alterra.R;
import ca.uqac.alterra.database.AlterraCloud;
import ca.uqac.alterra.database.AlterraDatabase;
import ca.uqac.alterra.utility.AlterraGeolocator;


public class HomeMapFragment extends Fragment implements AlterraGeolocator.OnLocationChangedListener {

    private MapsHandler mMapsHandler;
    private boolean mEnableLocation;
    private BottomSheetHandler mBottomSheetHandler;
    private FloatingActionButton mCameraButton;
    private static String enableLocationArgument = "enableLocation";
    private BottomSheetBehavior bottomSheetBehavior;

    public static HomeMapFragment newInstance(boolean enableLocation){
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
    public void onStart(){
        super.onStart();
        if (!mEnableLocation){
            mEnableLocation = getArguments().getBoolean(enableLocationArgument);
        }
        mBottomSheetHandler = new BottomSheetHandler(getActivity());
        mMapsHandler = new MapsHandler(getActivity(),mEnableLocation, mBottomSheetHandler);
        mCameraButton = getView().findViewById(R.id.cameraButton);

        initBottomSheet();

        mCameraButton.setOnClickListener((view ) -> {
            ((HomeActivity) getActivity()).takeAlterraPhoto();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mMapsHandler);

        DrawerLayout navDrawer =getActivity().findViewById(R.id.navDrawer);
        Toolbar toolbar = getView().findViewById(R.id.toolbar);
        ((HomeActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(),navDrawer,toolbar,R.string.app_name,R.string.app_name);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorPrimaryDark));
        navDrawer.addDrawerListener(toggle);
        toggle.syncState();

        AlterraDatabase alterraDatabase = AlterraCloud.getDatabaseInstance();
            alterraDatabase.getAllAlterraLocations(AlterraCloud.getAuthInstance().getCurrentUser(),(list) -> {
            if (list == null) return;
            Iterator<AlterraPoint> iter = list.iterator();
            while (iter.hasNext()){
                AlterraPoint p = iter.next();
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

    private void initBottomSheet() {
        LinearLayout bottomSheet = getActivity().findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch(newState){
                    case BottomSheetBehavior.STATE_HIDDEN:
                        mCameraButton.setVisibility(View.GONE);
                        break;
                    default:
                        mCameraButton.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) { }
        });
    }
}
