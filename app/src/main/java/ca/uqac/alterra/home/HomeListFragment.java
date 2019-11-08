package ca.uqac.alterra.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import ca.uqac.alterra.R;

public class HomeListFragment extends Fragment {

    FloatingActionButton mCameraButton;

    private ArrayList<HomeListDataModel> mDataList;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_list,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();
        DrawerLayout navDrawer =getActivity().findViewById(R.id.navDrawer);
        Toolbar toolbar = getView().findViewById(R.id.toolbar);
        ((HomeActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(),navDrawer,toolbar,R.string.app_name,R.string.app_name);
        navDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mCameraButton = getView().findViewById(R.id.cameraButton);
        mCameraButton.setOnClickListener((view) -> ((HomeActivity) getActivity()).dispatchTakePictureIntent());

        mDataList = new ArrayList<>();

        mRecyclerView = getView().findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mDataList.add(new HomeListDataModel("Item 1",R.drawable.about));
        mDataList.add(new HomeListDataModel("Item 2",R.drawable.about));
        mDataList.add(new HomeListDataModel("Item 3",R.drawable.about));
        mDataList.add(new HomeListDataModel("Item 4",R.drawable.about));

        HomeListAdapter recyclerAdapter =  new HomeListAdapter(mDataList);
        mRecyclerView.setAdapter(recyclerAdapter);

    }
}
