package ca.uqac.alterra.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ca.uqac.alterra.R;

public class HomeListFragment extends Fragment {

    FloatingActionButton mCameraButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState){
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
    }
}
