package ca.uqac.alterra;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AuthActivity extends FragmentActivity implements LogoFragment.LogoListener, LoginFragment.LoginListener, RegisterFragment.RegisterListener {

    private enum FLOW {LOGO,LOGIN,REGISTER,HOME};
    private FLOW mCurrentFlow = FLOW.LOGO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workflow);
        updateWorkflow();
    }

    private void updateWorkflow(){
        FragmentTransaction ft;
        switch (mCurrentFlow){
            case LOGO :
                LogoFragment logoFragment = new LogoFragment();
                logoFragment.setLogoListener(this);
                ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.faded_in,
                        R.anim.faded_out);
                ft.replace(R.id.emptyContainer, logoFragment);
                ft.commit();
                break;
            case LOGIN :
                LoginFragment loginFragment = new LoginFragment();
                loginFragment.setLoginListener(this);
                ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.faded_in,
                        R.anim.faded_out);
                ft.add(R.id.emptyContainer, loginFragment);
                ft.commit();
                break;
            case REGISTER :
                RegisterFragment registerFragment = new RegisterFragment();
                registerFragment.setRegisterListener(this);
                ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.emptyContainer, registerFragment);
                ft.commit();
                break;
            case HOME :
                Log.d("DEBUG", "Passing to HomeActivity");
                Intent startHomeActivityIntent = new Intent(this,HomeActivity.class);
                startActivity(startHomeActivityIntent);
                finish();
                break;
        }
    }


    @Override
    public void onLogoAnimationFinished() {
        mCurrentFlow = FLOW.LOGIN;
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
}
