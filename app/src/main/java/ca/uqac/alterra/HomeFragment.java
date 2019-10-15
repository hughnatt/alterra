package ca.uqac.alterra;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class HomeFragment extends Fragment {

    private MapsHandler mMapsHandler;
    private BottomSheetHandler mBottomSheetHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapsHandler = new MapsHandler(getActivity());
        mBottomSheetHandler = new BottomSheetHandler(getActivity());

        //Monitoring the bottom panel movements
        BottomSheetBehavior bottomPanelBehavior = BottomSheetBehavior.from(getActivity().findViewById(R.id.bottomPanel));
        bottomPanelBehavior.addBottomSheetCallback(mBottomSheetHandler);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mMapsHandler);
    }
}
