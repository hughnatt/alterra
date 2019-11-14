package ca.uqac.alterra.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;

import ca.uqac.alterra.R;


public class LoginFragment extends Fragment implements View.OnKeyListener {

    public static final String TAG = LoginFragment.class.getSimpleName();

    private TextInputEditText emailEditText;
    private TextInputLayout emailTextInput;

    private TextInputEditText passwordEditText;
    private TextInputLayout passwordTextInput;

    private MaterialButton loginButton;
    private MaterialButton registerButton;

    private String email;
    private String password;

    private LoginListener mListener;

    private FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;


    public static LoginFragment newInstance() {
        
        Bundle args = new Bundle();
        
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        passwordTextInput = view.findViewById(R.id.passwordTextInput);
        passwordEditText = view.findViewById(R.id.passwordEditText);

        emailTextInput = view.findViewById(R.id.emailTextInput);
        emailEditText = view.findViewById(R.id.emailEditText);
        loginButton = view.findViewById(R.id.loginButton);
        registerButton = view.findViewById(R.id.registerButton);
        Button googleButton = view.findViewById(R.id.googleButton);
        googleButton.setOnClickListener((v) -> signInWithGoogle());

        Button facebookButton = view.findViewById(R.id.facebookButton);
        facebookButton.setOnClickListener((v) -> LoginManager.getInstance().logIn(getActivity(),null));
        //LoginButton facebookButton = (LoginButton) view.findViewById(R.id.facebookButton);
        //facebookButton.setReadPermissions("email");
        // If using in a fragment
        //facebookButton.setFragment(this);
        //

        // Callback registration
        LoginManager.getInstance().registerCallback(((AuthActivity) getActivity()).mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d("DEBUG","onSuccess");
                firebaseAuthWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
                Log.d("DEBUG","onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("DEBUG","onError");
            }
        });

        setEmailTextListener();
        setPasswordTextListener();
        setNextButtonListener();
        setRegisterButtonListener();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //DEBUG: For testing purposes, signOut User on application startup
        //mAuth.signOut();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null){
            mListener.onLoginSuccessful();
            return;
        }

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.alterra_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }

    public void setLoginListener(LoginListener listener) { mListener = listener; }

    private void setNextButtonListener(){
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                verifyFields();
            }
        });
    }

    private void verifyFields(){
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();

        Boolean isValid =true;

        if(email.length() <1){
            emailTextInput.setError("Please enter email");
            isValid = false;
        }

        if(password.length() <1){
            passwordTextInput.setError("Please enter password");
            isValid = false;
        }

        if (isValid)
            firebaseAuthWithEmail();
    }

    private void setRegisterButtonListener(){
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListener.onRegisterRequested();
            }
        });
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

    private void firebaseAuthWithEmail(){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mListener.onLoginSuccessful();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            if(!task.isSuccessful()) {
                                new MaterialAlertDialogBuilder(getContext(), R.style.DialogStyle)
                                        .setTitle("Login Failed")
                                        .setMessage(task.getException().getMessage())
                                        .setPositiveButton("OK", null)
                                        .show();
                            }
                        }
                    }
                });
    }

    protected void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential).addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        mListener.onLoginSuccessful();
                    } else {
                        // If sign in fails, display a message to the user.
                        //Snackbar.make(findViewById(), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void firebaseAuthWithFacebook(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        mListener.onLoginSuccessful();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(getContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }

                    // ...
                });
    }

    public void signInWithGoogle(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        getActivity().startActivityForResult(signInIntent, AuthActivity.RC_SIGN_IN);
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
