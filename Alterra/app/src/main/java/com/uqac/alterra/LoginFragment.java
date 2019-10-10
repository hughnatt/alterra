package com.uqac.alterra;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginFragment extends Fragment {

    public static final String TAG = LoginFragment.class.getSimpleName();


    TextInputEditText emailEditText;
    TextInputLayout emailTextInput;

    TextInputEditText passwordEditText;
    TextInputLayout passwordTextInput;

    MaterialButton loginButton;
    MaterialButton registerButton;

    private String email;
    private String password;

    private static LoginListener mListener;

    private FirebaseAuth mAuth;


    public LoginFragment() {
        // Required empty public constructor
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

        setEmailTextListener();
        setPasswordTextListener();
        setNextButtonListener();
        setRegisterButtonListener();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
       if(currentUser!=null){
           mListener.home();
       }
    }

    public static void setLoginListener(LoginListener listener) { LoginFragment.mListener = listener; }

    private boolean isValidEmail(String email){
        if(email.length() == 0){
            return false;
        }else if(!email.contains("@")){
            return false;
        }else{
            return true;
        }
    }

    private boolean isPasswordValid(String password){
        if(password.length() == 0){
            return false;
        }else{
            return true;
        }
    }

    private void setNextButtonListener(){
       loginButton.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
               email = emailEditText.getText().toString();
               password = passwordEditText.getText().toString();
               if(!isValidEmail(email) && !isPasswordValid(password)){
                   Log.d(TAG, "Error email" );
                   emailTextInput.setError("Please enter a valid address");
               }else{
                   login();
               }

               Log.d(TAG, email + ":" + password );
           }
       });
    }

    private void setRegisterButtonListener(){
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListener.register();
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
    }

    private void login(){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mListener.home();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            emailTextInput.setErrorEnabled(true);
                            passwordTextInput.setErrorEnabled(true);
                        }
                    }
                });
    }




    public interface LoginListener {
        // TODO: Update argument type and name
        void home();
        void register();
    }

}
