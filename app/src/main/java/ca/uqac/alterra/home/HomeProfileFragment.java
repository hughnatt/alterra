package ca.uqac.alterra.home;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import ca.uqac.alterra.R;
import ca.uqac.alterra.database.AlterraAuth;
import ca.uqac.alterra.database.AlterraCloud;
import ca.uqac.alterra.database.AlterraUser;

public class HomeProfileFragment extends Fragment {

    AlterraAuth mAuth;
    AlterraUser mCurrentUser;
    FirebaseFirestore mFirestore;
    FirebaseStorage mStorage;

    RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_home_profile,container,false);
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

        mRecyclerView = getView().findViewById(R.id.recyclerview);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        PicturesAdapter myAdapter = new PicturesAdapter(getContext());
        mRecyclerView.setAdapter(myAdapter);

        mFirestore.collection("photos")
                .whereEqualTo("owner", mCurrentUser.getUID())
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String link = (String) document.getData().get("link");
                                myAdapter.addPicture(link);
                                myAdapter.notifyItemInserted(myAdapter.getItemCount());
                            }


                        }
                    }


                });


    }
}