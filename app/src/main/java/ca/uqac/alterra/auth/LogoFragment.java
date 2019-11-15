package ca.uqac.alterra.auth;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.uqac.alterra.R;


public class LogoFragment extends Fragment {

    private LogoListener mListener;

    static LogoFragment newInstance() {
        
        Bundle args = new Bundle();
        
        LogoFragment fragment = new LogoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_logo, container, false);
    }

    void setLogoListener(LogoListener listener) {
        mListener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        Handler handler = new Handler();
        Runnable runnable = () -> mListener.onLogoAnimationFinished();
        handler.postDelayed(runnable, 1000);
    }

    public interface LogoListener {
        void onLogoAnimationFinished();
    }
}
