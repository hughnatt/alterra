package ca.uqac.alterra.auth;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import ca.uqac.alterra.database.AlterraAuth;
import ca.uqac.alterra.database.AlterraCloud;
import ca.uqac.alterra.home.HomeActivity;
import ca.uqac.alterra.R;

public class AuthActivity extends FragmentActivity implements LogoFragment.LogoListener, LoginFragment.LoginListener, RegisterFragment.RegisterListener {

    private enum FLOW {LOGO,LOGIN,REGISTER,HOME}
    private FLOW mCurrentFlow;

    private static final String LOGO_FRAGMENT_TAG = "LOGO_FRAGMENT_TAG";
    private static final String LOGIN_FRAGMENT_TAG = "LOGIN_FRAGMENT_TAG";
    private static final String REGISTER_FRAGMENT_TAG = "REGISTER_FRAGMENT_TAG";

    private static final String TAG_CURRENT_FLOW = "CurrentState";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null)
            mCurrentFlow = (FLOW) savedInstanceState.getSerializable(TAG_CURRENT_FLOW);
        else
            mCurrentFlow = FLOW.LOGO;

        setContentView(R.layout.activity_auth);

        AlterraAuth auth = AlterraCloud.getAuthInstance();
        auth.initFacebookAuth();
        auth.initGoogleAuth(AuthActivity.this, getString(R.string.alterra_web_client_id));

        updateWorkflow();
    }



    private void updateWorkflow(){
        FragmentTransaction ft;
        switch (mCurrentFlow){
            case LOGO :
                LogoFragment logoFragment = (LogoFragment) getSupportFragmentManager().findFragmentByTag(LOGO_FRAGMENT_TAG);
                if(logoFragment == null){
                    logoFragment = LogoFragment.newInstance();
                }
                logoFragment.setLogoListener(this);


                ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.faded_in, R.anim.faded_out);
                ft.replace(R.id.emptyContainer, logoFragment, LOGO_FRAGMENT_TAG);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case LOGIN :
                LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag(LOGIN_FRAGMENT_TAG);

                if(loginFragment == null){
                    loginFragment = LoginFragment.newInstance();
                }
                loginFragment.setLoginListener(this);

                ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.faded_in, R.anim.faded_out);
                ft.replace(R.id.emptyContainer, loginFragment, LOGIN_FRAGMENT_TAG);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
                break;

            case REGISTER :
                RegisterFragment registerFragment = (RegisterFragment) getSupportFragmentManager().findFragmentByTag(REGISTER_FRAGMENT_TAG);

                if(registerFragment == null) {
                    registerFragment = RegisterFragment.newInstance();
                }
                registerFragment.setRegisterListener(this);

                ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.faded_in, R.anim.faded_out);
                ft.replace(R.id.emptyContainer, registerFragment, REGISTER_FRAGMENT_TAG);
                ft.addToBackStack(null);
                ft.commit();
                break;

            case HOME :
                Log.d("DEBUG", "Passing to HomeActivity");
                Intent startHomeActivityIntent = new Intent(this, HomeActivity.class);
                startActivity(startHomeActivityIntent);
                finish();
                break;
        }
    }

    @Override
    public void onLogoAnimationFinished() {
        if (AlterraCloud.getAuthInstance().getCurrentUser() == null) {
            mCurrentFlow = FLOW.LOGIN;
        } else {
            mCurrentFlow = FLOW.HOME;
        }
        updateWorkflow();
    }

    @Override
    public void onLoginSuccessful() {
        mCurrentFlow = FLOW.HOME;
        updateWorkflow();
    }

    @Override
    public void onRegisterRequested() {
        mCurrentFlow = FLOW.REGISTER;
        updateWorkflow();
    }

    @Override
    public void onRegisterSuccessful() {
        mCurrentFlow = FLOW.HOME;
        updateWorkflow();
    }

    @Override
    public void onBackToLogin() {
        onBackPressed();
    }

    @Override
    public void onSaveInstanceState(@NonNull  Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(TAG_CURRENT_FLOW, mCurrentFlow);
    }

    @Override
    public void onBackPressed() {
        switch (mCurrentFlow){
            case REGISTER:
                mCurrentFlow = FLOW.LOGIN;
                updateWorkflow();
                break;
            case LOGO:
            case LOGIN:
            case HOME:
                finish();
                break;
        }
    }
}

