package ca.uqac.alterra.home;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ca.uqac.alterra.R;

/**
 * Handle for the bottom sheet view
 * The main role of this class is to hide/unhide the floating camera action button
 * when the bottom sheet is expanded
 */
public class BottomSheetHandler extends BottomSheetBehavior.BottomSheetCallback {

    private FloatingActionButton mCameraButton;
    private TextView mTitle;
    private TextView mDistance;
    private TextView mAddress;
    private TextView mDescription;

    private LinearLayout bsDescriptionLinLayout;
    private LinearLayout bsAddressLinLayout;

    public BottomSheetHandler(Activity activity){
        mCameraButton = activity.findViewById(R.id.cameraButton);
        mTitle = activity.findViewById(R.id.bottomPanelTitle);
        mDistance =activity.findViewById(R.id.distance);
        mAddress = activity.findViewById(R.id.locationAddress);
        mDescription = activity.findViewById(R.id.locationDescription);
        bsAddressLinLayout = activity.findViewById(R.id.BSAddressLinLayout);
        bsDescriptionLinLayout = activity.findViewById(R.id.BSDescriptionLinLayout);

    }

    @Override
    public void onStateChanged(@NonNull View view, int i) {
            }

    @Override
    public void onSlide(@NonNull View view, float v) {
    }

    public void updateSheet(AlterraPoint alterraPoint){
        mTitle.setText(alterraPoint.getTitle());
        if(alterraPoint.getDescription().isEmpty())
            bsDescriptionLinLayout.setVisibility(View.GONE);
        else{
            mDescription.setText(alterraPoint.getDescription());
            bsDescriptionLinLayout.setVisibility(View.VISIBLE);
        }
        mAddress.setText(alterraPoint.getLatLng().toString());
    }
}
