package ca.uqac.alterra;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends FragmentActivity {

    private MapsHandler mMapsHandler;
    private BottomSheetHandler mBottomSheetHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapsHandler = new MapsHandler();
        mBottomSheetHandler = new BottomSheetHandler((FloatingActionButton) findViewById(R.id.cameraButton));

        //Monitoring the bottom panel movements
        BottomSheetBehavior bottomPanelBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomPanel));
        bottomPanelBehavior.setBottomSheetCallback(mBottomSheetHandler);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(mMapsHandler);
    }
}
