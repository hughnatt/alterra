package ca.uqac.alterra.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ca.uqac.alterra.R;
import ca.uqac.alterra.adapters.LocationAdapter;
import ca.uqac.alterra.database.AlterraAuth;
import ca.uqac.alterra.database.AlterraCloud;
import ca.uqac.alterra.database.AlterraDatabase;
import ca.uqac.alterra.types.AlterraPoint;

public class HomeListFragment extends Fragment {

    private HomeActivity mActivity;
    private RecyclerView mRecyclerView;
    private AlterraAuth mAuth;
    private SwipeRefreshLayout mRefresher;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View myview = inflater.inflate(R.layout.fragment_home_list,container,false);
        mRefresher = myview.findViewById(R.id.homeListRefresher);
        mRecyclerView = myview.findViewById(R.id.recyclerview);
        return myview;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (HomeActivity) getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();

        mAuth = AlterraCloud.getAuthInstance();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        LocationAdapter recyclerAdapter =  new LocationAdapter(this.getContext(), point -> mActivity.takeAlterraPhoto(point));

        mRecyclerView.setAdapter(recyclerAdapter);
        mRefresher.setOnRefreshListener(() -> {
            recyclerAdapter.clear();

            AlterraDatabase alterraDatabase = AlterraCloud.getDatabaseInstance();
            alterraDatabase.getAllAlterraLocations(mAuth.getCurrentUser(),(list) -> {

                if(list != null) {
                    for (AlterraPoint p : list) {
                        recyclerAdapter.addPoint(p);
                    }
                }
            });

            mRefresher.setRefreshing(false);

        });


        //Get Alterra locations
        AlterraDatabase alterraDatabase = AlterraCloud.getDatabaseInstance();
        alterraDatabase.getAllAlterraLocations(mAuth.getCurrentUser(),(list) -> {

            if(list != null) {
                for (AlterraPoint p : list) {
                    recyclerAdapter.addPoint(p);
                }
            }
        });
    }
}
