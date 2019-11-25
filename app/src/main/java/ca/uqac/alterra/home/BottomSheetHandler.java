package ca.uqac.alterra.home;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;

import java.util.ArrayList;

import ca.uqac.alterra.R;

/**
 * Handle for the bottom sheet view
 * The main role of this class is to hide/unhide the floating camera action button
 * when the bottom sheet is expanded
 */
public class BottomSheetHandler extends BottomSheetBehavior.BottomSheetCallback {

    private TextView mTitle;
    private TextView mDistance;
    private TextView mAddress;
    private TextView mDescription;

    private Button mSeeMore;

    private LinearLayout bsDescriptionLinLayout;
    private LinearLayout bsAddressLinLayout;

    private Activity mActivity;

    private ArrayList<String> mImageUrls = new ArrayList<>();
    private RecyclerView recyclerView;

    private AlterraPoint mAlterraPoint;

    public BottomSheetHandler(Activity activity){
        mActivity = activity;
        mTitle = activity.findViewById(R.id.bottomPanelTitle);
        mDistance =activity.findViewById(R.id.distance);
        mAddress = activity.findViewById(R.id.locationAddress);
        mDescription = activity.findViewById(R.id.locationDescription);
        bsAddressLinLayout = activity.findViewById(R.id.BSAddressLinLayout);
        bsDescriptionLinLayout = activity.findViewById(R.id.BSDescriptionLinLayout);
        mSeeMore =activity.findViewById(R.id.SeeMore);

        //Start new activity
        mSeeMore.setOnClickListener((View v) -> {
            Intent startActivityIntent = new Intent(activity, AlterraDetailsActivity.class);
            startActivityIntent.putExtra("AlterraPoint", new Gson().toJson(mAlterraPoint));
            mActivity.startActivity(startActivityIntent);
        });


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
        mImageUrls.clear();
        getImages();
        BottomSheetAdapter adapter = new BottomSheetAdapter(mActivity, mImageUrls);
        recyclerView.setAdapter(adapter);

        mTitle.setText(alterraPoint.getTitle());
        if(alterraPoint.getDescription().isEmpty())
            bsDescriptionLinLayout.setVisibility(View.GONE);
        else{
            mDescription.setText(alterraPoint.getDescription());
            bsDescriptionLinLayout.setVisibility(View.VISIBLE);
        }
        mAddress.setText(alterraPoint.getLatLng().toString());
    }

    private void getImages(){
        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/alterra-1569341283377.appspot.com/o/thumbnails%2Feiffel_tower.jpg?alt=media&token=3dcc8619-b9b9-4964-bd78-f42cef4ba303");
        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/alterra-1569341283377.appspot.com/o/thumbnails%2Feiffel_tower.jpg?alt=media&token=3dcc8619-b9b9-4964-bd78-f42cef4ba303");
        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/alterra-1569341283377.appspot.com/o/thumbnails%2Feiffel_tower.jpg?alt=media&token=3dcc8619-b9b9-4964-bd78-f42cef4ba303");
        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/alterra-1569341283377.appspot.com/o/thumbnails%2Feiffel_tower.jpg?alt=media&token=3dcc8619-b9b9-4964-bd78-f42cef4ba303");
    }
}
