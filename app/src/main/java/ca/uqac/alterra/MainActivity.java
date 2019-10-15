package ca.uqac.alterra;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class MainActivity extends FragmentActivity {

    private MapsHandler mMapsHandler;
    private BottomSheetHandler mBottomSheetHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapsHandler = new MapsHandler(this);
        mBottomSheetHandler = new BottomSheetHandler(this);

        //Monitoring the bottom panel movements
        BottomSheetBehavior bottomPanelBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomPanel));
        bottomPanelBehavior.setBottomSheetCallback(mBottomSheetHandler);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(mMapsHandler);
    }
}
