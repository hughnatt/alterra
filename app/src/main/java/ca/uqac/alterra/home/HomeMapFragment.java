package ca.uqac.alterra.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

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
import ca.uqac.alterra.database.AlterraFirebase;

public class HomeMapFragment extends Fragment {

    private MapsHandler mMapsHandler;
    private boolean mEnableLocation;
    private BottomSheetHandler mBottomSheetHandler;
    private FloatingActionButton mCameraButton;
    private static String enableLocationArgument = "enableLocation";

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
        mMapsHandler = new MapsHandler(getActivity(),mEnableLocation);
        mBottomSheetHandler = new BottomSheetHandler(getActivity());
        mCameraButton = getView().findViewById(R.id.cameraButton);
        mCameraButton.setOnClickListener((view) -> ((HomeActivity) getActivity()).dispatchTakePictureIntent());

        //Monitoring the bottom panel movements
        BottomSheetBehavior bottomPanelBehavior = BottomSheetBehavior.from(getView().findViewById(R.id.bottomPanel));
        bottomPanelBehavior.addBottomSheetCallback(mBottomSheetHandler);

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
            alterraDatabase.getAllAlterraLocations((list) -> {
            if (list == null) return;
            Iterator<AlterraPoint> iter = list.iterator();
            while (iter.hasNext()){
                AlterraPoint p = iter.next();
                System.out.println(p.getTitle());
                mMapsHandler.addAlterraPoint(p);
            }
        });
    }

    public void enableGoogleMapsLocation(){
        mEnableLocation = true;
        if(mMapsHandler != null){
            mMapsHandler.enableMyLocation();
        }
    }
}
