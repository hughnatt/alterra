package ca.uqac.alterra.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.InputStream;
import java.util.ArrayList;

import ca.uqac.alterra.R;
import ca.uqac.alterra.details.DetailsActivity;
import ca.uqac.alterra.utility.AlterraGeolocator;
import ca.uqac.alterra.utility.PrettyPrinter;

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

    private Activity mActivity;

    private ArrayList<String> mImageUrls = new ArrayList<>();
    private RecyclerView mRecyclerView;

    private AlterraPoint mAlterraPoint;

    private FloatingActionButton mCameraButton;
    private AppCompatImageButton mHandleButton;

    private BottomSheetBehavior mBottomSheetBehavior;
    private LinearLayout mBsParentLinLayout;
    private NestedScrollView mBsHeaderLinLayout;

    private ImageView mThumbnail;

    public BottomSheetHandler(Activity activity){
        mActivity = activity;
        mTitle = activity.findViewById(R.id.bottomPanelTitle);
        mDistance =activity.findViewById(R.id.distance);
        mAddress = activity.findViewById(R.id.locationAddress);
        mDescription = activity.findViewById(R.id.locationDescription);
        mSeeMore =activity.findViewById(R.id.SeeMore);
        mCameraButton = activity.findViewById(R.id.cameraButton);
        mBottomSheetBehavior = BottomSheetBehavior.from(mActivity.findViewById(R.id.bottom_sheet));
        mBsHeaderLinLayout = activity.findViewById(R.id.headerBottomSheet);
        mBsParentLinLayout = mActivity.findViewById(R.id.BSLocationInfoParentLayout);
        mHandleButton = activity.findViewById(R.id.bottomSheetHandle);
        mHandleButton.setOnClickListener((View v) -> mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED));
        mThumbnail = activity.findViewById(R.id.thumbnailBottomSheet);

        //Start new activity
        mSeeMore.setOnClickListener((View v) -> {
            if (mAlterraPoint != null && mAlterraPoint.isUnlocked()){
                Intent startActivityIntent = new Intent(activity, DetailsActivity.class);
                startActivityIntent.putExtra("AlterraPoint", mAlterraPoint);
                mActivity.startActivity(startActivityIntent);
            }
        });

        //Start Camera
        mCameraButton.setOnClickListener(v -> {
            if (mAlterraPoint != null){
                ((HomeActivity) mActivity).takeAlterraPhoto(mAlterraPoint);
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView = activity.findViewById(R.id.recyclerViewBottomSheet);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    protected BottomSheetHandler(Activity activity, @Nullable AlterraPoint alterraPoint){
        this(activity);
        updateSheet(alterraPoint);
    }

    @Override
    public void onStateChanged(@NonNull View view, int newState) {
    }

    @Override
    public void onSlide(@NonNull View view, float slideOffset) {
        float scale;
        scale = (1+slideOffset < 1) ? (1 + slideOffset) : 1;
        mCameraButton.animate().scaleX(scale).scaleY(scale).setDuration(0).start();
        scale = ((slideOffset > -0.8) ? 0 : ((slideOffset < -1) ? 1 : -5*slideOffset-4));
        mHandleButton.animate().scaleX(scale).scaleY(scale).setDuration(0).start();
    }

    public void updateSheet(@Nullable AlterraPoint alterraPoint){
        if (alterraPoint == null){
            mBsParentLinLayout.setVisibility(View.GONE);
            mBsHeaderLinLayout.setVisibility(View.GONE);
            mTitle.setText(R.string.maps_first_marker_click);
            mDistance.setText("");
            mBsHeaderLinLayout.setBackground(null);
        } else {
            mAlterraPoint = alterraPoint;
            Glide.with(mActivity)
                    .asBitmap()
                    .load(alterraPoint.getThumbnail())
                    .into(mThumbnail);

/*            Picasso.get()
                    .load(mAlterraPoint.getThumbnail())
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            mBsHeaderLinLayout.setBackground(new BitmapDrawable(bitmap));
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });*/

            mTitle.setText(alterraPoint.getTitle());
            mDistance.setText(PrettyPrinter.formatDistance(AlterraGeolocator.distanceFrom(alterraPoint)));
            mImageUrls.clear();
            getImages();
            BottomSheetAdapter adapter = new BottomSheetAdapter(mActivity, mImageUrls);
            mRecyclerView.setAdapter(adapter);



            if(alterraPoint.getDescription().isEmpty())
                mDescription.setText("...");
            else{
                mDescription.setText(alterraPoint.getDescription());
            }
            mAddress.setText(alterraPoint.getLatLng().toString());

            mBsParentLinLayout.setVisibility(View.VISIBLE);
            mBsHeaderLinLayout.setVisibility(View.VISIBLE);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            if (alterraPoint.isUnlocked()){
                mSeeMore.setText(mActivity.getString(R.string.alterra_point_unlocked));
            } else if (alterraPoint.isUnlockable()){
                mSeeMore.setText(mActivity.getString(R.string.alterra_point_unlockable));
            } else {
                mSeeMore.setText(mActivity.getString(R.string.alterra_point_locked));
            }

        }

    }

    private void getImages(){
        mImageUrls.add(mAlterraPoint.getThumbnail());
        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/alterra-1569341283377.appspot.com/o/thumbnails%2Feiffel_tower.jpg?alt=media&token=3dcc8619-b9b9-4964-bd78-f42cef4ba303");
        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/alterra-1569341283377.appspot.com/o/thumbnails%2Feiffel_tower.jpg?alt=media&token=3dcc8619-b9b9-4964-bd78-f42cef4ba303");
        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/alterra-1569341283377.appspot.com/o/thumbnails%2Feiffel_tower.jpg?alt=media&token=3dcc8619-b9b9-4964-bd78-f42cef4ba303");
        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/alterra-1569341283377.appspot.com/o/thumbnails%2Feiffel_tower.jpg?alt=media&token=3dcc8619-b9b9-4964-bd78-f42cef4ba303");
        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/alterra-1569341283377.appspot.com/o/thumbnails%2Feiffel_tower.jpg?alt=media&token=3dcc8619-b9b9-4964-bd78-f42cef4ba303");
        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/alterra-1569341283377.appspot.com/o/thumbnails%2Feiffel_tower.jpg?alt=media&token=3dcc8619-b9b9-4964-bd78-f42cef4ba303");
        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/alterra-1569341283377.appspot.com/o/thumbnails%2Feiffel_tower.jpg?alt=media&token=3dcc8619-b9b9-4964-bd78-f42cef4ba303");
        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/alterra-1569341283377.appspot.com/o/thumbnails%2Feiffel_tower.jpg?alt=media&token=3dcc8619-b9b9-4964-bd78-f42cef4ba303");
        mImageUrls.add("https://firebasestorage.googleapis.com/v0/b/alterra-1569341283377.appspot.com/o/thumbnails%2Feiffel_tower.jpg?alt=media&token=3dcc8619-b9b9-4964-bd78-f42cef4ba303");


    }

    protected void unselectSheet(){
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        updateSheet(null);
    }

    /**
     * @return Currently displayed Alterra Point
     */
    @Nullable
    protected AlterraPoint getAlterraPoint(){
        return mAlterraPoint;
    }



}
