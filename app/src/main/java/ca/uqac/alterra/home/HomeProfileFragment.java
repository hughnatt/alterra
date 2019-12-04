package ca.uqac.alterra.home;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import ca.uqac.alterra.R;
import ca.uqac.alterra.database.AlterraAuth;
import ca.uqac.alterra.database.AlterraCloud;
import ca.uqac.alterra.database.AlterraDatabase;
import ca.uqac.alterra.database.AlterraPicture;
import ca.uqac.alterra.database.AlterraUser;

public class HomeProfileFragment extends Fragment {

    AlterraAuth mAuth;
    AlterraUser mCurrentUser;
    FirebaseFirestore mFirestore;
    FirebaseStorage mStorage;

    RecyclerView mRecyclerView;
    PicturesAdapter mAdapterPictures;
    HomeListAdapter mAdapterLocation;


    private TextView mTotalLocation;
    private TextView mTotalPhotos;
    private View mHeader;
    private SwipeRefreshLayout mRefresher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View myView = inflater.inflate(R.layout.fragment_home_profile,container,false);

        mRecyclerView = myView.findViewById(R.id.recyclerview);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);


        mHeader = myView.findViewById(R.id.profileHeader);
        mRefresher = myView.findViewById(R.id.profileRefresher);

        mTotalPhotos = myView.findViewById(R.id.profileTotalPhotos);
        mTotalLocation = myView.findViewById(R.id.profileTotalLocation);


        return myView;
    }

    @Override
    public void onStart() {
        super.onStart();
        DrawerLayout navDrawer = getActivity().findViewById(R.id.navDrawer);
        Toolbar toolbar = getView().findViewById(R.id.toolbar);
        ((HomeActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(),navDrawer,toolbar,R.string.app_name,R.string.app_name);
        navDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mAuth = AlterraCloud.getAuthInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        mAdapterPictures = new PicturesAdapter(getContext(), url -> switchContext(url), (alterraPicture, position) -> showDeleteAlertDialog(alterraPicture,position));
        mRecyclerView.setAdapter(mAdapterPictures);

        mAdapterLocation = new HomeListAdapter(getContext());

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
                Toast.makeText(getContext(),R.string.details_loading_failed,Toast.LENGTH_LONG).show();
            }
        });

        AlterraCloud.getDatabaseInstance().getUserLocation(mCurrentUser, new AlterraDatabase.OnGetAlterraUserLocation() {
            @Override
            public void onSuccess(@NonNull List<HomeListDataModel> userLocations) {
                for(HomeListDataModel currentLocation : userLocations){
                    mAdapterLocation.addData(currentLocation);
                }
                mTotalLocation.setText(String.valueOf(userLocations.size()));
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(),R.string.details_loading_failed,Toast.LENGTH_LONG).show();
            }
        });

        mRefresher.setOnRefreshListener(() -> {
            mAdapterPictures.clear();
            AlterraCloud.getDatabaseInstance().getAlterraPictures(mCurrentUser, new AlterraDatabase.OnGetAlterraPicturesListener() {
                @Override
                public void onSuccess(@NonNull List<AlterraPicture> alterraPictures) {
                    if(alterraPictures != null){
                        for(AlterraPicture currentPicture : alterraPictures){
                            mAdapterPictures.addPicture(currentPicture);
                        }
                        mTotalPhotos.setText(String.valueOf(alterraPictures.size()));
                    }
                    mRefresher.setRefreshing(false);
                }
                @Override
                public void onError(Exception e) {
                    Toast.makeText(getContext(),R.string.details_loading_failed,Toast.LENGTH_LONG).show();
                }
            });
        });

    }


    public void switchContext(String url){
        if(getActivity() instanceof HomeActivity){
            HomeActivity homeActivity =(HomeActivity) getActivity();
            homeActivity.displayPicture(url);
        }
    }

    public void showDeleteAlertDialog(AlterraPicture picture,int position){
        new MaterialAlertDialogBuilder(getActivity(),R.style.DialogStyle)
                       .setTitle(R.string.profile_photos_dialog_box_title)
                       .setMessage(R.string.profile_photos_dialog_box_message)

                       // Specifying a listener allows you to take an action before dismissing the dialog.
                       // The dialog is automatically dismissed when a dialog button is clicked.
                       .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int which) {
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
                           }
                       })

                       // A null listener allows the button to dismiss the dialog and take no further action.
                       .setNegativeButton(android.R.string.no, null)
                       .setIcon(android.R.drawable.ic_dialog_alert)
                       .show();
    }
}