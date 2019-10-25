package ca.uqac.alterra;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;


public class RegisterFragment extends Fragment implements View.OnKeyListener {

    public static final String TAG = RegisterFragment.class.getSimpleName();

    TextInputEditText emailEditText;
    TextInputLayout emailTextInput;

    TextInputEditText passwordEditText;
    TextInputLayout passwordTextInput;

    TextInputEditText confirmPasswordEditText;
    TextInputLayout confirmPasswordTextInput;

    MaterialButton registerButton;

    private String email;
    private String password;
    private String confirmPassword;

    private RegisterListener mListener;


    private FirebaseAuth mAuth;

    public RegisterFragment() {
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
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        passwordTextInput = view.findViewById(R.id.passwordTextInput);
        passwordEditText = view.findViewById(R.id.passwordEditText);

        confirmPasswordTextInput = view.findViewById(R.id.confirmPasswordTextInput);
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);

        emailTextInput = view.findViewById(R.id.emailTextInput);
        emailEditText = view.findViewById(R.id.emailEditText);
        registerButton = view.findViewById(R.id.registerButton);


        setEmailTextListener();
        setPasswordTextListener();
        setRegisterButtonListener();

        return view;
    }

    public void setRegisterListener(RegisterFragment.RegisterListener listener) {mListener = listener; }


    public boolean isSamePassword(String password, String confirmPassword){
        return password.equals(confirmPassword);
    }


    private void setRegisterButtonListener(){
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                verifyFields(); }});
    }

    private void verifyFields(){
            email = emailEditText.getText().toString();
            password = passwordEditText.getText().toString();
            confirmPassword = confirmPasswordEditText.getText().toString();

            boolean isValid = true;

            if(email.length() <1){
                emailTextInput.setError("Please enter email");
                isValid = false;
            }

            if(password.length() <1){
                passwordTextInput.setError("Please enter password");
                isValid = false;
            }

            if(confirmPassword.length() < 1){
                confirmPasswordTextInput.setError("Please confirm password");
                isValid = false;
            }

            if (!isSamePassword(password, confirmPassword)){
                confirmPasswordTextInput.setError("Password are different");
                isValid = false;
            }

            if(isValid)
                register();

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
                emailTextInput.setError(null);
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
                passwordTextInput.setError(null);
            }
        });


        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                confirmPasswordTextInput.setError(null);
            }
        });

        confirmPasswordEditText.setOnKeyListener(this);
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
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
            mListener.onRegisterSuccessful();
    }

    private void register(){
        Log.d(TAG, "register");
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user!=null)
                                mListener.onRegisterSuccessful();
                        } else {
                            if(!task.isSuccessful()) {
                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthWeakPasswordException e) {
                                    passwordTextInput.setError("Password must be at least 6 characters long");
                                    passwordTextInput.requestFocus();
                                } catch(FirebaseAuthInvalidCredentialsException e) {
                                    emailTextInput.setError("Email is incorrect");
                                    emailTextInput.requestFocus();
                                } catch(FirebaseAuthUserCollisionException e) {
                                    emailTextInput.setError("Email already exists");
                                    emailTextInput.requestFocus();
                                } catch(Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }

                                new MaterialAlertDialogBuilder(getContext(), R.style.DialogStyle)
                                        .setTitle("Register Failed")
                                        .setMessage(task.getException().getMessage())
                                        .setPositiveButton("OK", null)
                                        .show();

                            }
                        }
                    }
                });
    }

    public interface RegisterListener {
        void onRegisterSuccessful();
    }

}
