package ca.uqac.alterra.home;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Iterator;
import java.util.List;

import ca.uqac.alterra.R;
import ca.uqac.alterra.database.AlterraAuth;
import ca.uqac.alterra.database.AlterraCloud;
import ca.uqac.alterra.database.AlterraDatabase;

public class HomeListFragment extends Fragment {

    FloatingActionButton mCameraButton;

    private RecyclerView mRecyclerView;
    private FirebaseFirestore mDatabase;
    private FirebaseStorage mStorage;

    private AlterraAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_list,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();

        mDatabase = FirebaseFirestore.getInstance();
        mAuth = AlterraCloud.getAuthInstance();

        DrawerLayout navDrawer = getActivity().findViewById(R.id.navDrawer);
        Toolbar toolbar = getView().findViewById(R.id.toolbar);
        ((HomeActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(),navDrawer,toolbar,R.string.app_name,R.string.app_name);
        navDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mCameraButton = getView().findViewById(R.id.cameraButton);
        mCameraButton.setOnClickListener((view) -> ((HomeActivity) getActivity()).takeAlterraPhoto());
        mRecyclerView = getView().findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        HomeListAdapter recyclerAdapter =  new HomeListAdapter(this.getContext());
        mRecyclerView.setAdapter(recyclerAdapter);


        //Get Alterra locations
        AlterraDatabase alterraDatabase = AlterraCloud.getDatabaseInstance();
        alterraDatabase.getAllAlterraLocations(mAuth.getCurrentUser(),(list) -> {

            if(list != null) {
                for (AlterraPoint p : list) {

                    recyclerAdapter.addData(new HomeListDataModel(p.getTitle(), p.getThumbnail()));
                    recyclerAdapter.notifyItemInserted(recyclerAdapter.getItemCount());

                }
            }
        });
    }
}
