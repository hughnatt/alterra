package ca.uqac.alterra;

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
import ca.uqac.alterra.R;


public class RegisterFragment extends Fragment {

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

    private boolean isValidEmail(String email){
        if(email.length() == 0){
            return false;
        }else if(!email.contains("@")){
            return false;
        }else{
            return true;
        }
    }

    private boolean isPasswordValid(String password, String confirmPassword) {
        if (password.length() == 0 && confirmPassword.length() == 0) {
            return false;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordTextInput.setErrorEnabled(true);
            return false;
        } else
            return true;
    }


    private void setRegisterButtonListener(){
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();
                confirmPassword = confirmPasswordEditText.getText().toString();
                if(isPasswordValid(password, confirmPassword) && isValidEmail(email))
                    register();
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
                if(confirmPasswordTextInput.isErrorEnabled()){
                    confirmPasswordTextInput.setErrorEnabled(false);
                }
            }
        });
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
                            mListener.onRegisterSuccessful();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "createUserWithEmail:failure", task.getException());
                        }

                        // ...
                    }
                });
    }

    public interface RegisterListener {
        // TODO: Update argument type and name
        void onRegisterSuccessful();
    }

}
