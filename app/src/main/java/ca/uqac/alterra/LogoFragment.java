package ca.uqac.alterra;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.uqac.alterra.R;


public class LogoFragment extends Fragment {

    private LogoListener mListener;

    public LogoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_logo, container, false);

        // Inflate the layout for this fragment
        return v ;
    }

    public void setLogoListener(LogoListener listener) {
        mListener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mListener.onLogoAnimationFinished();
            }
        };

        handler.postDelayed(runnable, 1000);
    }

    public interface LogoListener {
        void onLogoAnimationFinished();
    }
}
