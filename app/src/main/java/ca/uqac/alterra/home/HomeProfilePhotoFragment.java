package ca.uqac.alterra.home;

import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.ortiz.touchview.TouchImageView;

import ca.uqac.alterra.R;

public class HomeProfilePhotoFragment extends Fragment{


    private static String urlArgument = "url";
    private String mUrl;
    private TouchImageView imageToShow;

    public static HomeProfilePhotoFragment newInstance(String imageUrl){
        Bundle args = new Bundle();
        args.putString(urlArgument, imageUrl);
        HomeProfilePhotoFragment homeProfilePhotoFragment = new HomeProfilePhotoFragment();
        homeProfilePhotoFragment.setArguments(args);
        return homeProfilePhotoFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View fragmentView = inflater.inflate(R.layout.fragment_home_profile_photos,container,false);
        return fragmentView ;
    }

    @Override
    public void onStart(){
        super.onStart();
        mUrl = getArguments().getString(urlArgument);

        imageToShow = (TouchImageView) getView().findViewById(R.id.imageToShow);

        Glide.with(getContext())
                .load(mUrl)
                .fitCenter()
                .into(imageToShow);

    }
}
