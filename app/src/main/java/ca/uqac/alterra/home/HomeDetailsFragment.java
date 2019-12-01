package ca.uqac.alterra.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;


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
    private ImageView mThumbnail;
    private TextView mTitle;
    private TextView mTotalUsers;
    private TextView mTotalPhotos;
    private View mHeader;
    private SwipeRefreshLayout mRefresher;

    private float mInitialScrollY;

    public static HomeDetailsFragment newInstance(AlterraPoint alterraPoint) {

        Bundle args = new Bundle();
        args.putSerializable(ARGS_ALTERRA_POINT,alterraPoint);

        HomeDetailsFragment fragment = new HomeDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_home_details,container,false);

        mHeader = v.findViewById(R.id.detailsHeader);

        mRefresher = v.findViewById(R.id.detailsRefresher);

        mPicturesRecyclerView = v.findViewById(R.id.detailsRecyclerView);
        mPicturesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),COLUMN_COUNT));
        /*mPicturesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private static final int HIDE_THRESHOLD = 20;
            private int scrolledDistance = 0;
            private boolean controlsVisible = true;

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int firstVisibleItem = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findFirstVisibleItemPosition();
                //show views if first item is first visible position and views are hidden
                if (firstVisibleItem == 0) {
                    if(!controlsVisible) {
                        onShow();
                        controlsVisible = true;
                    }
                } else {
                    if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                        onHide();
                        controlsVisible = false;
                        scrolledDistance = 0;
                    } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                        onShow();
                        controlsVisible = true;
                        scrolledDistance = 0;
                    }
                }

                if((controlsVisible && dy>0) || (!controlsVisible && dy<0)) {
                    scrolledDistance += dy;
                }
            }

            private void onHide(){
                mHeader.animate().translationY(-mHeader.getHeight()).setInterpolator(new AccelerateInterpolator(2));
            }

            private void onShow(){
                mHeader.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
            }
        });*/

        mThumbnail = v.findViewById(R.id.detailsLocationThumbnail);
        mTitle = v.findViewById(R.id.detailsLocationName);
        mTotalPhotos = v.findViewById(R.id.detailsTotalPhotos);
        mTotalUsers = v.findViewById(R.id.detailsTotalUsers);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        assert(getContext() != null);
        mAlterraPoint = (AlterraPoint) Objects.requireNonNull(getArguments()).getSerializable(ARGS_ALTERRA_POINT);



        Glide.with(getContext())
                .load(mAlterraPoint.getThumbnail())
                .fitCenter()
                .into(mThumbnail);
        mTitle.setText(mAlterraPoint.getTitle());

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
                    mTotalPhotos.setText(String.valueOf(alterraPictures.size()));
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(),R.string.details_loading_failed,Toast.LENGTH_LONG).show();
            }
        });

        mRefresher.setOnRefreshListener(() -> {
            picturesAdapter.clear();
            AlterraCloud.getDatabaseInstance().getAlterraPictures(mAlterraPoint, new AlterraDatabase.OnGetAlterraPicturesListener() {
                @Override
                public void onSuccess(@Nullable List<AlterraPicture> alterraPictures) {
                    if (alterraPictures != null){
                        for (AlterraPicture picture : alterraPictures){
                            picturesAdapter.addPicture(picture.getURL());
                            picturesAdapter.notifyItemInserted(picturesAdapter.getItemCount());
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
}
