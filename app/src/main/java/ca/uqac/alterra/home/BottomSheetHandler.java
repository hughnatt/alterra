package ca.uqac.alterra.home;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;

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

    public BottomSheetHandler(Activity activity){
        mCameraButton = activity.findViewById(R.id.cameraButton);
    }

    @Override
    public void onStateChanged(@NonNull View view, int i) {
        switch(i){
            case BottomSheetBehavior.STATE_COLLAPSED:
                mCameraButton.show();
                break;
            default:
                mCameraButton.hide();
                break;
        }
    }

    @Override
    public void onSlide(@NonNull View view, float v) {
    }
}
