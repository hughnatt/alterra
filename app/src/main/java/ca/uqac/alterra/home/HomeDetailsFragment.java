package ca.uqac.alterra.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ca.uqac.alterra.R;

public class HomeDetailsFragment extends Fragment {

    private static final int COLUMN_COUNT = 3;
    private static final String ARGS_ALTERRA_POINT = "ARGS_ALTERRA_POINT";

    private AlterraPoint mAlterraPoint;
    private RecyclerView mPicturesRecyclerView;

    public static HomeDetailsFragment newInstance(AlterraPoint alterraPoint) {

        Bundle args = new Bundle();
        args.putSerializable(ARGS_ALTERRA_POINT,alterraPoint);

        HomeDetailsFragment fragment = new HomeDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_home_details,container,false);

        mPicturesRecyclerView = v.findViewById(R.id.detailsRecyclerView);
        mPicturesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),COLUMN_COUNT));
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        PicturesAdapter picturesAdapter = new PicturesAdapter(getContext(),null,null);
        mPicturesRecyclerView.setAdapter(picturesAdapter);

    }
}
