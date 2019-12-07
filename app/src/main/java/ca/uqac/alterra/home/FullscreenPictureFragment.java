package ca.uqac.alterra.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.ortiz.touchview.TouchImageView;

import ca.uqac.alterra.R;
import ca.uqac.alterra.types.AlterraPicture;


public class FullscreenPictureFragment extends Fragment{


    private static final String ARGS_PICTURE = "ARGS_PICTURE";

    private AlterraPicture mAlterraPicture;
    private TouchImageView mImageView;

    public static FullscreenPictureFragment newInstance(AlterraPicture alterraPicture){
        Bundle args = new Bundle();
        args.putSerializable(ARGS_PICTURE, alterraPicture);
        FullscreenPictureFragment fullscreenPictureFragment = new FullscreenPictureFragment();
        fullscreenPictureFragment.setArguments(args);
        return fullscreenPictureFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View fragmentView = inflater.inflate(R.layout.fragment_fullscreen_photo,container,false);
        mImageView = fragmentView.findViewById(R.id.imageToShow);
        return fragmentView ;
    }

    @Override
    public void onStart(){
        super.onStart();

        assert getArguments() != null;
        mAlterraPicture = (AlterraPicture) getArguments().getSerializable(ARGS_PICTURE);

        assert(getView() != null);

        assert (getContext() != null);
        Glide.with(getContext())
                .load(mAlterraPicture.getURL())
                .fitCenter()
                .into(mImageView);
    }
}
