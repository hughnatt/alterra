package ca.uqac.alterra.home;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
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
import ca.uqac.alterra.types.AlterraPicture;
import ca.uqac.alterra.types.AlterraPoint;
import ca.uqac.alterra.utility.AlterraGeolocator;
import ca.uqac.alterra.utility.PrettyPrinter;

/**
 * Handle for the bottom sheet view
 * The main role of this class is to hide/unhide the floating camera action button
 * when the bottom sheet is expanded
 */
public class BottomSheetHandler  {

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

    private BottomSheetBehavior mInfoBottomSheet;
    private BottomSheetBehavior mUnselectedBottomSheet;

    private ImageView mThumbnail;

    public BottomSheetHandler(Activity activity){
        mActivity = activity;
        mTitle = activity.findViewById(R.id.btmTitleLocation);
        mDistance =activity.findViewById(R.id.btmDistance);
        mDescription = activity.findViewById(R.id.btmDescription);
        mSeeMore =activity.findViewById(R.id.btmButton);
        mCameraButton = activity.findViewById(R.id.cameraButton);
        mThumbnail = activity.findViewById(R.id.btmThumbnail);

        mInfoBottomSheet = BottomSheetBehavior.from(mActivity.findViewById(R.id.bottom_sheet));
        mInfoBottomSheet.addBottomSheetCallback(new InfoSheetCallback());

        mUnselectedBottomSheet = BottomSheetBehavior.from(mActivity.findViewById(R.id.bottom_sheet_nopoint));
        mUnselectedBottomSheet.addBottomSheetCallback(new UnselectedSheetCallback());


        mHandleButton = activity.findViewById(R.id.bottomSheetHandle);

        mHandleButton.setOnClickListener((View v) -> {
            if (mAlterraPoint == null){
                mUnselectedBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                mInfoBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            mHandleButton.animate().scaleX(0F).scaleY(0F).setDuration(1).start();
        });


        //Start new activity
        mSeeMore.setOnClickListener((View v) -> {
            if (mAlterraPoint != null) {
                if (mAlterraPoint.isUnlocked()) {
                    ((HomeActivity) mActivity).showPlaceDetails(getAlterraPoint());
                } else if (mAlterraPoint.isUnlockable()){
                    mAlterraPoint.unlock(); //TODO use the to-be created OnUnlockSuccessfullListener
                    updateSheet(null); //TODO Better sheet content update
                } else {
                    Toast.makeText(mActivity,R.string.alterra_point_locked,Toast.LENGTH_LONG).show();
                }
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

    protected void updateSheet(@Nullable AlterraPoint alterraPoint){
        if (mAlterraPoint == alterraPoint && alterraPoint != null && mInfoBottomSheet.getState()==BottomSheetBehavior.STATE_COLLAPSED){
            mInfoBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            mAlterraPoint = alterraPoint;
            mHandleButton.animate().scaleX(0F).scaleY(0F).setDuration(1).start();
            if (alterraPoint == null){
                mInfoBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                mUnselectedBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                mCameraButton.animate().scaleY(0F).scaleX(0F).setDuration(1).start();
            } else {
                mUnselectedBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                mInfoBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);

                Glide.with(mActivity)
                        .asBitmap()
                        .load(alterraPoint.getThumbnail())
                        .into(mThumbnail);

                mTitle.setText(alterraPoint.getTitle());
                mDistance.setText(PrettyPrinter.formatDistance(AlterraGeolocator.distanceFrom(alterraPoint)));

                if(alterraPoint.getDescription().isEmpty())
                    mDescription.setText("...");
                else{
                    mDescription.setText(alterraPoint.getDescription());
                }

                if (alterraPoint.isUnlocked()){
                    mSeeMore.setText(mActivity.getString(R.string.alterra_point_unlocked));
                    mSeeMore.setTextColor(mActivity.getResources().getColor(R.color.colorPrimary));
                    mCameraButton.animate().scaleX(1).scaleY(1).setDuration(0).start();
                    getImages();
                } else if (alterraPoint.isUnlockable()){
                    mSeeMore.setText(mActivity.getString(R.string.alterra_point_unlockable));
                    mSeeMore.setTextColor(mActivity.getResources().getColor(R.color.colorPrimaryDark));
                    mRecyclerView.setAdapter(null);
                } else {
                    mSeeMore.setText(mActivity.getString(R.string.alterra_point_locked));
                    mSeeMore.setTextColor(mActivity.getResources().getColor(R.color.colorPrimaryDark));
                    mRecyclerView.setAdapter(null);
                }
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
        if (mAlterraPoint != null){
            updateSheet(null);
        }
    }

    /**
     * @return Currently displayed Alterra Point
     */
    @Nullable
    protected AlterraPoint getAlterraPoint(){
        return mAlterraPoint;
    }


    private class UnselectedSheetCallback extends BottomSheetBehavior.BottomSheetCallback {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN && mAlterraPoint == null){
                mHandleButton.animate().scaleX(1F).scaleY(1F).setDuration(1).start();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            /*float scale = ((slideOffset > -0.8) ? 0 : ((slideOffset < -1) ? 1 : -5*slideOffset-4));
            mHandleButton.animate().scaleX(scale).scaleY(scale).setDuration(0).start();*/
        }
    }

    private class InfoSheetCallback extends BottomSheetBehavior.BottomSheetCallback {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            /*if(mInfoBottomSheet.getState() == BottomSheetBehavior.STATE_DRAGGING && !isSelected){
                mInfoBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }*/
            if (newState == BottomSheetBehavior.STATE_HIDDEN && mAlterraPoint != null){
                mHandleButton.animate().scaleX(1F).scaleY(1F).setDuration(1).start();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            if (mAlterraPoint != null && mAlterraPoint.isUnlocked()){
                float scale;
                scale = (1+slideOffset < 1) ? (1 + slideOffset) : 1;
                mCameraButton.animate().scaleX(scale).scaleY(scale).setDuration(0).start();
            }
            /*scale = ((slideOffset > -0.8) ? 0 : ((slideOffset < -1) ? 1 : -5*slideOffset-4));
            mHandleButton.animate().scaleX(scale).scaleY(scale).setDuration(0).start();*/
        }
    }
}
