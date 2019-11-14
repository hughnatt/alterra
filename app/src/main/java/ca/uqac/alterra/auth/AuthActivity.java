package ca.uqac.alterra.auth;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import ca.uqac.alterra.home.HomeActivity;
import ca.uqac.alterra.R;

public class AuthActivity extends FragmentActivity implements LogoFragment.LogoListener, LoginFragment.LoginListener, RegisterFragment.RegisterListener {

    private enum FLOW {LOGO,LOGIN,REGISTER,HOME}
    private FLOW mCurrentFlow;

    private static final String LOGO_FRAGMENT_TAG = "LOGO_FRAGMENT_TAG";
    private static final String LOGIN_FRAGMENT_TAG = "LOGIN_FRAGMENT_TAG";
    private static final String REGISTER_FRAGMENT_TAG = "REGISTER_FRAGMENT_TAG";

    private static final String TAG_CURRENT_FLOW = "CurrentState";

    protected static final int RC_SIGN_IN = 0x03;

    protected CallbackManager mCallbackManager;

    private LoginFragment mLoginFragment;
    private LogoFragment mLogoFragment;
    private RegisterFragment mRegisterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null)
            mCurrentFlow = (FLOW) savedInstanceState.getSerializable(TAG_CURRENT_FLOW);
        else
            mCurrentFlow = FLOW.LOGO;

        setContentView(R.layout.activity_auth);

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();


        updateWorkflow();
    }



    private void updateWorkflow(){
        FragmentTransaction ft;
        switch (mCurrentFlow){
            case LOGO :
                mLogoFragment = (LogoFragment) getSupportFragmentManager().findFragmentByTag(LOGO_FRAGMENT_TAG);
                if(mLogoFragment == null){
                    mLogoFragment = LogoFragment.newInstance();
                }
                mLogoFragment.setLogoListener(this);


                ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.faded_in, R.anim.faded_out);
                ft.replace(R.id.emptyContainer, mLogoFragment, LOGO_FRAGMENT_TAG);
                ft.addToBackStack(null);
                ft.commit();

                break;
            case LOGIN :
                mLoginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag(LOGIN_FRAGMENT_TAG);

                if(mLoginFragment == null){
                    mLoginFragment = LoginFragment.newInstance();
                }
                mLoginFragment.setLoginListener(this);


                ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.faded_in, R.anim.faded_out);
                ft.replace(R.id.emptyContainer, mLoginFragment, LOGIN_FRAGMENT_TAG);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
                break;

            case REGISTER :
                mRegisterFragment = (RegisterFragment) getSupportFragmentManager().findFragmentByTag(REGISTER_FRAGMENT_TAG);


                if(mRegisterFragment == null) {
                    mRegisterFragment = RegisterFragment.newInstance();
                }
                mRegisterFragment.setRegisterListener(this);

                ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.faded_in, R.anim.faded_out);
                ft.replace(R.id.emptyContainer, mRegisterFragment, REGISTER_FRAGMENT_TAG);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("DEBUG", "onActivityResult, requestCode=" + requestCode);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                mLoginFragment.firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("DEBUG", "Google sign in failed", e);
                // [START_EXCLUDE]
                //updateUI(null);
                // [END_EXCLUDE]
            }
        }

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onLogoAnimationFinished() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
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

