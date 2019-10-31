package ca.uqac.alterra.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ca.uqac.alterra.R;

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
        mEnableLocation = getArguments().getBoolean(enableLocationArgument);
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
    }

    public void enableGoogleMapsLocation(){
        if(mMapsHandler != null){
            mMapsHandler.enableMyLocation();
        }
    }
}
