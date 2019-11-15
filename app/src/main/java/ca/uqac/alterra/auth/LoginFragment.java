package ca.uqac.alterra.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import ca.uqac.alterra.R;
import ca.uqac.alterra.database.AlterraAuth;
import ca.uqac.alterra.database.AlterraCloud;
import ca.uqac.alterra.database.AlterraUser;
import ca.uqac.alterra.database.exceptions.AlterraAuthException;
import ca.uqac.alterra.database.exceptions.AlterraAuthUserCollisionException;


public class LoginFragment extends Fragment implements View.OnKeyListener {

    private static final String TAG = LoginFragment.class.getSimpleName();


    private TextInputEditText emailEditText;
    private TextInputLayout emailTextInput;
    private TextInputEditText passwordEditText;
    private TextInputLayout passwordTextInput;

    private LoginListener mListener;

    private AlterraAuth mAuth;

    static LoginFragment newInstance() {
        
        Bundle args = new Bundle();
        
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = AlterraCloud.getAuthInstance();
        mAuth.initFacebookAuth();
        mAuth.initGoogleAuth(getContext(), getString(R.string.alterra_web_client_id));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        passwordTextInput = view.findViewById(R.id.passwordTextInput);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        emailTextInput = view.findViewById(R.id.emailTextInput);
        emailEditText = view.findViewById(R.id.emailEditText);

        MaterialButton loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> verifyFields());

        MaterialButton registerButton = view.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> mListener.onRegisterRequested());



        Button googleButton = view.findViewById(R.id.googleButton);
        googleButton.setOnClickListener((v) -> {
            mAuth.logInWithGoogle(this, new AlterraAuth.AlterraAuthListener() {
                @Override
                public void onSuccess(AlterraUser user) {
                    AlterraCloud.getDatabaseInstance().registerAlterraUser(user.getUID(),user.getEmail());
                    mListener.onLoginSuccessful();
                }

                @Override
                public void onFailure(AlterraAuthException e) {
                    if (e instanceof AlterraAuthUserCollisionException){
                        Toast.makeText(getContext(), "Authentication failed. An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Authentication failed.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        });

        Button facebookButton = view.findViewById(R.id.facebookButton);
        facebookButton.setOnClickListener((v) -> {
            mAuth.logInWithFacebook(this, new AlterraAuth.AlterraAuthListener() {
                @Override
                public void onSuccess(AlterraUser user) {
                    AlterraCloud.getDatabaseInstance().registerAlterraUser(user.getUID(),user.getEmail());
                    mListener.onLoginSuccessful();
                }

                @Override
                public void onFailure(AlterraAuthException e) {
                    if (e instanceof AlterraAuthUserCollisionException){
                        Toast.makeText(getContext(), "Authentication failed. An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Authentication failed.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        });

        setEmailTextListener();
        setPasswordTextListener();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //DEBUG: For testing purposes, signOut User on application startup
        //mAuth.signOut();
        //LoginManager.getInstance().logOut();
        //mGoogleSignInClient.revokeAccess();

        // Check if user is signed in (non-null) and update UI accordingly.
        if(mAuth.getCurrentUser()!=null){
            mListener.onLoginSuccessful();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("DEBUG","requestCode = " + requestCode);
        mAuth.getCallback().onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);

    }

    protected void setLoginListener(LoginListener listener) { mListener = listener; }


    private void verifyFields(){
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        boolean isValid = true;

        if(email.length() <1){
            emailTextInput.setError("Please enter email");
            isValid = false;
        }

        if(password.length() <1){
            passwordTextInput.setError("Please enter password");
            isValid = false;
        }

        if (isValid)
            mAuth.logInWithPassword(email, password, new AlterraAuth.AlterraAuthListener() {
                @Override
                public void onSuccess(AlterraUser user) {
                    mListener.onLoginSuccessful();
                }

                @Override
                public void onFailure(AlterraAuthException e) {
                    new MaterialAlertDialogBuilder(getContext(), R.style.DialogStyle)
                            .setTitle("Login Failed")
                            .setMessage(e.getMessage())
                            .setPositiveButton("OK", null)
                            .show();
                }
            });



        mListener.onLoginSuccessful();
    }


    private void setEmailTextListener(){
        emailEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(emailTextInput.isErrorEnabled()){
                    emailTextInput.setErrorEnabled(false);
                }
            }
        });
    }

    private void setPasswordTextListener(){
        passwordEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(passwordTextInput.isErrorEnabled()){
                    passwordTextInput.setErrorEnabled(false);
                }
            }
        });

        passwordEditText.setOnKeyListener(this);

    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN &&
                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            verifyFields();
            return true;

        }
        return false; // pass on to other listeners.
    }

    @Override
    public void onStop() {
        super.onStop();
        emailTextInput.setError(null);
        passwordTextInput.setError(null);
    }

    public interface LoginListener {
        void onLoginSuccessful();
        void onRegisterRequested();
    }

}
