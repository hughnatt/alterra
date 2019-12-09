package ca.uqac.alterra.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import java.util.Objects;

import ca.uqac.alterra.R;
import ca.uqac.alterra.adapters.LocationAdapter;
import ca.uqac.alterra.adapters.PicturesAdapter;
import ca.uqac.alterra.database.AlterraCloud;
import ca.uqac.alterra.database.AlterraDatabase;
import ca.uqac.alterra.types.AlterraPicture;
import ca.uqac.alterra.types.AlterraPoint;
import ca.uqac.alterra.types.AlterraUser;


public class HomeProfileFragment extends Fragment {

    private static final int SPAN_COUNT_PICTURES = 2;
    private static final int SPAN_COUNT_LOCATIONS = 1;

    private enum Adapter {
        PICTURES,
        LOCATIONS
    }

    private AlterraUser mCurrentUser;
    private RecyclerView mRecyclerView;
    private PicturesAdapter mAdapterPictures;
    private LocationAdapter mAdapterLocation;
    private GridLayoutManager mGridLayoutManager;
    private TextView mTotalLocation;
    private TextView mTotalPhotos;
    private SwipeRefreshLayout mRefresher;
    private Adapter mCurrentAdapter;
    private int mCurrentSpanCount;


    /*
    Get all the references to the layout elements
    Verify if the savedInstanceState is != null to rebuild the state of the fragment before his reconstruction
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        if (savedInstanceState != null) {
            mCurrentAdapter = (Adapter) savedInstanceState.getSerializable("currentAdapter");
            mCurrentSpanCount = savedInstanceState.getInt("currentSpanCount");
        }
        else{
            mCurrentAdapter = Adapter.PICTURES;
            mCurrentSpanCount = SPAN_COUNT_PICTURES;
        }

        View myView = inflater.inflate(R.layout.fragment_home_profile,container,false);

        mRecyclerView = myView.findViewById(R.id.recyclerview);
        mGridLayoutManager = new GridLayoutManager(getContext(),mCurrentSpanCount);
        mRecyclerView.setLayoutManager(mGridLayoutManager);


        mRefresher = myView.findViewById(R.id.profileRefresher);

        mTotalPhotos = myView.findViewById(R.id.profileTotalPhotos);
        mTotalPhotos.setOnClickListener(view -> changeAdapter(Adapter.PICTURES));

        mTotalLocation = myView.findViewById(R.id.profileTotalLocation);
        mTotalLocation.setOnClickListener(view -> changeAdapter(Adapter.LOCATIONS));


        return myView;
    }

    /*
    Apply to the fragment the shared navDrawer & a toolbar
    Instanciation of the both adapter to allow the user switch between the 2 list (pictures or locations)
    Add the behaviour to the refresher depending of the current used adapter
     */
    @Override
    public void onStart() {
        super.onStart();
        mCurrentUser = AlterraCloud.getAuthInstance().getCurrentUser();

        mAdapterPictures = new PicturesAdapter(getContext(), this::switchContext, this::showDeleteAlertDialog);
        mAdapterLocation = new LocationAdapter(getContext(), this::takePicture);

        if(mCurrentAdapter == Adapter.PICTURES){
            mRecyclerView.setAdapter(mAdapterPictures);
        }
        else{
            mRecyclerView.setAdapter(mAdapterLocation);
        }

        AlterraCloud.getDatabaseInstance().getAlterraPictures(mCurrentUser, new AlterraDatabase.OnGetAlterraPicturesListener() {
            @Override
            public void onSuccess(@NonNull List<AlterraPicture> alterraPictures) {
                for(AlterraPicture currentPicture : alterraPictures){
                    mAdapterPictures.addPicture(currentPicture);
                }
                mTotalPhotos.setText(String.valueOf(alterraPictures.size()));
            }
            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(),R.string.profile_loading_fail,Toast.LENGTH_LONG).show();
            }
        });

        AlterraCloud.getDatabaseInstance().getUserUnlockedLocations(mCurrentUser, new AlterraDatabase.OnGetAlterraUserLocation() {
            @Override
            public void onSuccess(@NonNull List<AlterraPoint> userLocations) {
                for(AlterraPoint currentLocation : userLocations){
                    mAdapterLocation.addPoint(currentLocation);
                }
                mTotalLocation.setText(String.valueOf(userLocations.size()));
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(),R.string.details_loading_failed,Toast.LENGTH_LONG).show();
            }
        });

        mRefresher.setOnRefreshListener(() -> {

            if(mCurrentAdapter == Adapter.PICTURES){
                mAdapterPictures.clear();
                AlterraCloud.getDatabaseInstance().getAlterraPictures(mCurrentUser, new AlterraDatabase.OnGetAlterraPicturesListener() {
                    @Override
                    public void onSuccess(@NonNull List<AlterraPicture> alterraPictures) {
                        for(AlterraPicture currentPicture : alterraPictures){
                            mAdapterPictures.addPicture(currentPicture);
                        }
                        mTotalPhotos.setText(String.valueOf(alterraPictures.size()));
                        mRefresher.setRefreshing(false);
                    }
                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getContext(),R.string.details_loading_failed,Toast.LENGTH_LONG).show();
                    }
                });
            }
            else if(mCurrentAdapter == Adapter.LOCATIONS){
                mAdapterLocation.clear();
                AlterraCloud.getDatabaseInstance().getUserUnlockedLocations(mCurrentUser, new AlterraDatabase.OnGetAlterraUserLocation() {
                    @Override
                    public void onSuccess(@NonNull List<AlterraPoint> userLocations) {
                        for(AlterraPoint currentLocation : userLocations){
                            mAdapterLocation.addPoint(currentLocation);
                        }
                        mTotalLocation.setText(String.valueOf(userLocations.size()));
                        mRefresher.setRefreshing(false);
                    }


                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getContext(),R.string.details_loading_failed,Toast.LENGTH_LONG).show();
                    }
                });
            }

        });

    }



    /*
    Change the current adapter to use depending where the user clicked
     */
    public void changeAdapter(Adapter adapter){
        switch (adapter){
            case LOCATIONS:
                if(mCurrentAdapter != Adapter.LOCATIONS) {
                    mRecyclerView.setAdapter(mAdapterLocation);
                    mCurrentSpanCount = SPAN_COUNT_LOCATIONS;
                    mGridLayoutManager.setSpanCount(mCurrentSpanCount);
                    mCurrentAdapter = Adapter.LOCATIONS;
                }
                break;
            case PICTURES:
                if(mCurrentAdapter != Adapter.PICTURES){
                    mRecyclerView.setAdapter(mAdapterPictures);
                    mCurrentSpanCount = SPAN_COUNT_PICTURES;
                    mGridLayoutManager.setSpanCount(mCurrentSpanCount);
                    mCurrentAdapter = Adapter.PICTURES;
                }
                break;
        }
    }


    /*
    Method used to launch the HomeFullPictureFragment via the activity
     */
    private void switchContext(AlterraPicture alterraPicture){
        if(getActivity() instanceof HomeActivity){
            HomeActivity homeActivity =(HomeActivity) getActivity();
            homeActivity.displayPicture(alterraPicture);
        }
    }

    private void takePicture(AlterraPoint point){
        if(getActivity() instanceof HomeActivity){
            HomeActivity homeActivity =(HomeActivity) getActivity();
            homeActivity.takeAlterraPhoto(point);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putSerializable("currentAdapter",mCurrentAdapter);
        saveInstanceState.putInt("currentSpanCount",mCurrentSpanCount);
    }

    private void showDeleteAlertDialog(AlterraPicture picture,int position){
        new MaterialAlertDialogBuilder(Objects.requireNonNull(getActivity()),R.style.DialogStyle)
                       .setTitle(R.string.profile_photos_dialog_box_title)
                       .setMessage(R.string.profile_photos_dialog_box_message)

                       // Specifying a listener allows you to take an action before dismissing the dialog.
                       // The dialog is automatically dismissed when a dialog button is clicked.
                       .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                           // Continue with delete operation
                           AlterraCloud.getDatabaseInstance().deleteAlterraPictureFromFirestore(picture, new AlterraDatabase.AlterraWriteListener() {
                               @Override
                               public void onSuccess() {
                                   mAdapterPictures.deleteItem(position);
                                   mTotalPhotos.setText(String.valueOf(mAdapterPictures.getItemCount()));
                               }

                               @Override
                               public void onError(Exception e) {
                                   String error = getResources().getString(R.string.profile_deletion_fail)+e;
                                   Toast.makeText(getContext(),error,Toast.LENGTH_LONG).show();
                               }
                           });
                       })

                       // A null listener allows the button to dismiss the dialog and take no further action.
                       .setNegativeButton(android.R.string.no, null)
                       .setIcon(android.R.drawable.ic_dialog_alert)
                       .show();
    }

}