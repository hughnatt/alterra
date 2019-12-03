package ca.uqac.alterra.home;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.alterra.R;
import ca.uqac.alterra.database.AlterraCloud;
import ca.uqac.alterra.database.AlterraDatabase;
import ca.uqac.alterra.database.AlterraPicture;
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
    private TextView mDescription;

    private Button mSeeMore;

    private Activity mActivity;

    private ArrayList<String> mImageUrls = new ArrayList<>();
    private RecyclerView mRecyclerView;

    private AlterraPoint mAlterraPoint;

    private FloatingActionButton mCameraButton;
    private AppCompatImageButton mHandleButton;

    private BottomSheetBehavior mBottomSheetBehavior;
    private LinearLayout mBsDescriptionLinearLayout;
    private NestedScrollView mBsHeaderLinLayout;
    private CardView mCardView;

    private ImageView mThumbnail;

    public BottomSheetHandler(Activity activity){
        mActivity = activity;
        mTitle = activity.findViewById(R.id.btmTitleLocation);
        mDistance =activity.findViewById(R.id.btmDistance);
        mDescription = activity.findViewById(R.id.btmDescription);
        mSeeMore =activity.findViewById(R.id.btmButton);
        mCameraButton = activity.findViewById(R.id.cameraButton);
        mBottomSheetBehavior = BottomSheetBehavior.from(mActivity.findViewById(R.id.bottom_sheet));
        mBsHeaderLinLayout = activity.findViewById(R.id.btmScrollView);
        mBsDescriptionLinearLayout = activity.findViewById(R.id.btmLinLayoutDescription);
        mHandleButton = activity.findViewById(R.id.bottomSheetHandle);
        mHandleButton.setOnClickListener((View v) -> mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED));
        mThumbnail = activity.findViewById(R.id.btmThumbnail);
        mCardView = activity.findViewById(R.id.btmCardView);

        //Start new activity
        mSeeMore.setOnClickListener((View v) -> {
            if (mAlterraPoint != null && mAlterraPoint.isUnlocked()){
                ((HomeActivity) mActivity).showPlaceDetails(getAlterraPoint());
            }
        });

        //Start Camera
        mCameraButton.setOnClickListener(v -> {
            if (mAlterraPoint != null){
                ((HomeActivity) mActivity).takeAlterraPhoto(mAlterraPoint);
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView = activity.findViewById(R.id.btmRecyclerView);
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
            mTitle.setText(R.string.maps_first_marker_click);
            mBsHeaderLinLayout.setVisibility(View.GONE);
            mBsDescriptionLinearLayout.setVisibility(View.GONE);
            mSeeMore.setVisibility(View.GONE);
            mDistance.setVisibility(View.GONE);
            mCardView.setVisibility(View.GONE);
        } else {
            mAlterraPoint = alterraPoint;
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            Glide.with(mActivity)
                    .asBitmap()
                    .load(alterraPoint.getThumbnail())
                    .into(mThumbnail);

            mTitle.setText(alterraPoint.getTitle());
            mDistance.setText(PrettyPrinter.formatDistance(AlterraGeolocator.distanceFrom(alterraPoint)));
            mDistance.setVisibility(View.VISIBLE);



            if(alterraPoint.getDescription().isEmpty())
                mDescription.setText("...");
            else{
                mDescription.setText(alterraPoint.getDescription());
            }

            mBsDescriptionLinearLayout.setVisibility(View.VISIBLE);
            mBsHeaderLinLayout.setVisibility(View.VISIBLE);
            mCardView.setVisibility(View.VISIBLE);
            mSeeMore.setVisibility(View.VISIBLE);

            if (alterraPoint.isUnlocked()){
                mSeeMore.setText(mActivity.getString(R.string.alterra_point_unlocked));
                getImages();

            } else if (alterraPoint.isUnlockable()){
                mSeeMore.setText(mActivity.getString(R.string.alterra_point_unlockable));
                mRecyclerView.setVisibility(View.GONE);
            } else {
                mSeeMore.setText(mActivity.getString(R.string.alterra_point_locked));
                mRecyclerView.setVisibility(View.GONE);
            }

        }



    }

    private void getImages(){
        AlterraCloud.getDatabaseInstance().getAlterraPictures(mAlterraPoint, new AlterraDatabase.OnGetAlterraPicturesListener() {
            @Override
            public void onSuccess(@Nullable List<AlterraPicture> alterraPictures) {
                if (alterraPictures != null){
                    mImageUrls.clear();
                    int i =0;
                    while(i<4 && alterraPictures.get(i)!=null) {
                        mImageUrls.add(alterraPictures.get(i).getURL());
                        i++;
                    }
                    BottomSheetAdapter adapter = new BottomSheetAdapter(mActivity, mImageUrls);
                    mRecyclerView.setAdapter(adapter);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(mActivity,"error",Toast.LENGTH_LONG).show();
            }
        });

    }

    protected void unselectSheet(){
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            updateSheet(null);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }else
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
