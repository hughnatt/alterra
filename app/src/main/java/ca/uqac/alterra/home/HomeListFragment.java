package ca.uqac.alterra.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import ca.uqac.alterra.R;

public class HomeListFragment extends Fragment {

    FloatingActionButton mCameraButton;

    private RecyclerView mRecyclerView;

    private FirebaseFirestore mDatabase;
    private FirebaseStorage mStorage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_list,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();

        mDatabase = FirebaseFirestore.getInstance();

        DrawerLayout navDrawer = getActivity().findViewById(R.id.navDrawer);
        Toolbar toolbar = getView().findViewById(R.id.toolbar);
        ((HomeActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(),navDrawer,toolbar,R.string.app_name,R.string.app_name);
        navDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mCameraButton = getView().findViewById(R.id.cameraButton);
        mCameraButton.setOnClickListener((view) -> ((HomeActivity) getActivity()).dispatchTakePictureIntent());

        mRecyclerView = getView().findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        HomeListAdapter recyclerAdapter =  new HomeListAdapter();
        mRecyclerView.setAdapter(recyclerAdapter);

        mDatabase.collection("locations").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    if(doc.get("name") != null ){

                        HashMap<String, String> descriptionArray;
                        descriptionArray = (HashMap<String, String>) doc.get("name");

                        recyclerAdapter.addData(new HomeListDataModel(descriptionArray.get("default"),R.drawable.about));
                        recyclerAdapter.notifyItemInserted(recyclerAdapter.getItemCount());
                    }
                }
            }
        });
    }
}
