package ca.uqac.alterra.home;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

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

    private Activity mActivity;

    private ArrayList<String> mImageUrls = new ArrayList<>();
    private RecyclerView recyclerView;

    public BottomSheetHandler(Activity activity){
        mActivity = activity;
        mCameraButton = activity.findViewById(R.id.cameraButton);
        mTitle = activity.findViewById(R.id.bottomPanelTitle);
        mDistance =activity.findViewById(R.id.distance);
        mAddress = activity.findViewById(R.id.locationAddress);
        mDescription = activity.findViewById(R.id.locationDescription);
        bsAddressLinLayout = activity.findViewById(R.id.BSAddressLinLayout);
        bsDescriptionLinLayout = activity.findViewById(R.id.BSDescriptionLinLayout);

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        recyclerView = activity.findViewById(R.id.recyclerViewBottomSheet);
        recyclerView.setLayoutManager(layoutManager);



    }

    @Override
    public void onStateChanged(@NonNull View view, int i) {
            }

    @Override
    public void onSlide(@NonNull View view, float v) {
    }

    public void updateSheet(AlterraPoint alterraPoint){
        getImages(alterraPoint);
        mTitle.setText(alterraPoint.getTitle());
        if(alterraPoint.getDescription().isEmpty())
            bsDescriptionLinLayout.setVisibility(View.GONE);
        else{
            mDescription.setText(alterraPoint.getDescription());
            bsDescriptionLinLayout.setVisibility(View.VISIBLE);
        }
        mAddress.setText(alterraPoint.getLatLng().toString());

        BottomSheetAdapter adapter = new BottomSheetAdapter(mActivity, mImageUrls);
        recyclerView.setAdapter(adapter);
    }

    private void getImages(AlterraPoint alterraPoint){
        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/alterra-1569341283377.appspot.com/o/thumbnails%2Feiffel_tower.jpg?alt=media&token=3dcc8619-b9b9-4964-bd78-f42cef4ba303");
        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/alterra-1569341283377.appspot.com/o/thumbnails%2Feiffel_tower.jpg?alt=media&token=3dcc8619-b9b9-4964-bd78-f42cef4ba303");
        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/alterra-1569341283377.appspot.com/o/thumbnails%2Feiffel_tower.jpg?alt=media&token=3dcc8619-b9b9-4964-bd78-f42cef4ba303");
        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/alterra-1569341283377.appspot.com/o/thumbnails%2Feiffel_tower.jpg?alt=media&token=3dcc8619-b9b9-4964-bd78-f42cef4ba303");

    }
}
