package ca.uqac.alterra;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

public class WorkflowActivity extends FragmentActivity implements LogoFragment.LogoListener, LoginFragment.LoginListener, RegisterFragment.RegisterListener {

    private static final int LOGO_TAG = 0;
    private static final int LOGIN_TAG = 1;
    private static final int REGISTER_TAG = 2;
    private static final int HOME_TAG = 3;
    private int current = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workflow);
        updateWorkflow();
    }

    private void updateWorkflow(){
        FragmentTransaction ft;
        switch (current){
            case LOGO_TAG :
                LogoFragment logoFragment = new LogoFragment();
                logoFragment.setLogoListener(this);
                ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.faded_in,
                        R.anim.faded_out);
                ft.replace(R.id.emptyContainer, logoFragment);
                ft.commit();
                break;
            case LOGIN_TAG :
                LoginFragment loginFragment = new LoginFragment();
                loginFragment.setLoginListener(this);
                ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.faded_in,
                        R.anim.faded_out);
                ft.add(R.id.emptyContainer, loginFragment);
                ft.commit();
                break;
            case REGISTER_TAG :
                RegisterFragment registerFragment = new RegisterFragment();
                registerFragment.setRegisterListener(this);
                ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.emptyContainer, registerFragment);
                ft.commit();
                break;
            case HOME_TAG :
                HomeFragment homeFragment = new HomeFragment();
                ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.faded_in,
                        R.anim.faded_out);
                ft.add(R.id.emptyContainer, homeFragment);
                ft.commit();
                break;
        }
    }


    @Override
    public void onLogoAnimationFinished() {
        current++;
        updateWorkflow();
    }

    @Override
    public void onLoginSuccessful() {
        current = HOME_TAG;
        Log.d("zzz", "home");
        updateWorkflow();
    }

    @Override
    public void onRegisterRequested() {
        current = REGISTER_TAG;
        updateWorkflow();
    }

    @Override
    public void onRegisterSuccessful() {
        current = HOME_TAG;
        updateWorkflow();
    }
}
