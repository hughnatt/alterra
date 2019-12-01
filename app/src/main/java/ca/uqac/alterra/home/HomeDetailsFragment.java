package ca.uqac.alterra.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import ca.uqac.alterra.R;
import ca.uqac.alterra.database.AlterraCloud;
import ca.uqac.alterra.database.AlterraDatabase;
import ca.uqac.alterra.database.AlterraPicture;

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
        mAlterraPoint = (AlterraPoint) Objects.requireNonNull(getArguments()).getSerializable(ARGS_ALTERRA_POINT);

        PicturesAdapter picturesAdapter = new PicturesAdapter(getContext(),null,null);
        mPicturesRecyclerView.setAdapter(picturesAdapter);

        AlterraCloud.getDatabaseInstance().getAlterraPictures(mAlterraPoint, new AlterraDatabase.OnGetAlterraPicturesListener() {
            @Override
            public void onSuccess(@Nullable List<AlterraPicture> alterraPictures) {
                if (alterraPictures != null){
                    for (AlterraPicture picture : alterraPictures){
                        picturesAdapter.addPicture(picture.getURL());
                        picturesAdapter.notifyItemInserted(picturesAdapter.getItemCount());
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(),R.string.details_loading_failed,Toast.LENGTH_LONG).show();
            }
        });

    }
}
