package ca.uqac.alterra.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import ca.uqac.alterra.R;

public class HomeProfilePhotoFragment extends Fragment implements IOnBackPressed {


    private static String urlArgument = "url";
    private String mUrl;
    private ImageView imageToShow;

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
        return inflater.inflate(R.layout.fragment_home_profile_photos,container,false);
    }

    @Override
    public void onStart(){
        super.onStart();
        mUrl = getArguments().getString(urlArgument);

        imageToShow = (ImageView) getView().findViewById(R.id.imageToShow);

        Glide.with(getContext())
                .load(mUrl)
                .fitCenter()
                .into(imageToShow);

    }

    @Override
    public boolean onBackPressed()
    {

        return (getContext() instanceof HomeActivity);

    }
}
