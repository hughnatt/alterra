package ca.uqac.alterra;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.uqac.alterra.R;


public class WorkflowFragment extends Fragment implements LogoFragment.LogoListener, LoginFragment.LoginListener, RegisterFragment.RegisterListener {

    private static final int LOGO_TAG = 0;
    private static final int LOGIN_TAG = 1;
    private static final int REGISTER_TAG = 2;
    private static final int HOME_TAG = 3;
    private int current = 0;


    public WorkflowFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workflow, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWorkflow();
    }

    private void updateWorkflow(){
        FragmentTransaction ft;
        switch (current){
            case LOGO_TAG :
                LogoFragment.setLogoListener(this);
                ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.faded_in,
                        R.anim.faded_out);
                ft.replace(R.id.container, new LogoFragment());
                ft.commit();
                break;
            case LOGIN_TAG :
                LoginFragment.setLoginListener(this);
                ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.faded_in,
                        R.anim.faded_out);
                ft.add(R.id.container, new LoginFragment());
                ft.commit();
                break;
            case REGISTER_TAG :
                RegisterFragment.setRegisterListener(this);
                ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.add(R.id.container, new RegisterFragment());
                ft.commit();
                break;
            case HOME_TAG :
/*
                RegisterFragment.setRegisterListener(this);
*/
                ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.add(R.id.container, new BlankFragment());
                ft.commit();
                break;
        }
    }


    @Override
    public void next() {
        current++;
        updateWorkflow();
    }

    @Override
    public void home() {
        current = HOME_TAG;
        Log.d("zzz", "home");
        updateWorkflow();
    }

    @Override
    public void register() {
        current = REGISTER_TAG;
        updateWorkflow();
    }

}
