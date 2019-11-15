package ca.uqac.alterra.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import ca.uqac.alterra.R;
import ca.uqac.alterra.database.AlterraAuth;
import ca.uqac.alterra.database.AlterraCloud;
import ca.uqac.alterra.database.AlterraUser;
import ca.uqac.alterra.database.exceptions.AlterraAuthException;
import ca.uqac.alterra.database.exceptions.AlterraAuthInvalidCredentialsException;
import ca.uqac.alterra.database.exceptions.AlterraAuthUserCollisionException;
import ca.uqac.alterra.database.exceptions.AlterraAuthWeakPasswordException;

public class RegisterFragment extends Fragment implements View.OnKeyListener {

    private static final String TAG = RegisterFragment.class.getSimpleName();

    private TextInputEditText nameEditText;
    private TextInputLayout nameTextInput;

    private TextInputEditText emailEditText;
    private TextInputLayout emailTextInput;

    private TextInputEditText passwordEditText;
    private TextInputLayout passwordTextInput;

    private TextInputEditText confirmPasswordEditText;
    private TextInputLayout confirmPasswordTextInput;

    private MaterialButton registerButton;
    private MaterialButton backButton;

    private String name =null;
    private String email=null;
    private String password=null;
    private String confirmPassword=null;

    private RegisterListener mListener;

    private AlterraAuth mAuth;

    static RegisterFragment newInstance() {
        
        Bundle args = new Bundle();
        
        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = AlterraCloud.getAuthInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        passwordTextInput = view.findViewById(R.id.passwordTextInput);
        passwordEditText = view.findViewById(R.id.passwordEditText);

        confirmPasswordTextInput = view.findViewById(R.id.confirmPasswordTextInput);
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);

        nameTextInput = view.findViewById(R.id.nameTextInput);
        nameEditText = view.findViewById(R.id.nameEditText);

        emailTextInput = view.findViewById(R.id.emailTextInput);
        emailEditText = view.findViewById(R.id.emailEditText);

        registerButton = view.findViewById(R.id.registerButton);
        backButton = view.findViewById(R.id.registerBackButton);

        backButton.setOnClickListener((v) -> {
            if (mListener != null) { mListener.onBackToLogin(); }
        });


        setNameTextListener();
        setEmailTextListener();
        setPasswordTextListener();
        setRegisterButtonListener();


        return view;
    }

    public void setRegisterListener(RegisterFragment.RegisterListener listener) {
        mListener = listener;
    }


    public boolean isSamePassword(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }


    private void setRegisterButtonListener() {
        registerButton.setOnClickListener(v -> verifyFields());
    }

    private void verifyFields() {
        name = nameEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        confirmPassword = confirmPasswordEditText.getText().toString();

        boolean isValid = true;

        if (name.length() < 1) {
            nameTextInput.setError("Enter your name");
            isValid = false;
        }

        if (email.length() < 1) {
            emailTextInput.setError("Enter your email");
            isValid = false;
        }

        if (password.length() < 1) {
            passwordTextInput.setError("Enter your password");

            isValid = false;
        }

        if (confirmPassword.length() < 1) {
            confirmPasswordTextInput.setError("Confirm your password");
            isValid = false;
        }

        if (!isSamePassword(password, confirmPassword)) {
            confirmPasswordTextInput.setError("Password are different");
            isValid = false;
        }

        if (isValid)
            register();

    }

    private void setNameTextListener() {
        nameEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                nameTextInput.setError(null);
                name = s.toString();
            }
        });
    }

    private void setEmailTextListener() {
        emailEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                emailTextInput.setError(null);
                email = s.toString();
            }
        });
    }

    private void setPasswordTextListener() {
        passwordEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                passwordTextInput.setError(null);
                password=s.toString();
            }
        });


        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                confirmPasswordTextInput.setError(null);
                confirmPassword= s.toString();
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
        AlterraUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
            mListener.onRegisterSuccessful();
    }

    @Override
    public void onStop() {
        super.onStop();
        nameTextInput.setError(null);
        emailTextInput.setError(null);
        passwordTextInput.setError(null);
        confirmPasswordTextInput.setError(null);

    }

    private void register() {
        Log.d(TAG, "register");
        mAuth.registerWithPassword(email, password, new AlterraAuth.AlterraAuthListener() {
            @Override
            public void onSuccess(AlterraUser user) {
                // Sign in success
                if (user != null){
                    AlterraCloud.getDatabaseInstance().registerAlterraUser(user.getUID(),user.getEmail());
                    mListener.onRegisterSuccessful();
                }
            }

            @Override
            public void onFailure(AlterraAuthException e) {
                // Sign in failed
                if (e instanceof AlterraAuthWeakPasswordException){
                    passwordEditText.setError("Password must be at least 6 characters long");
                    passwordTextInput.requestFocus();
                } else if (e instanceof AlterraAuthInvalidCredentialsException) {
                    emailEditText.setError("Email is incorrect");
                    emailTextInput.requestFocus();
                } else if (e instanceof AlterraAuthUserCollisionException) {
                    emailEditText.setError("Email already exists");
                    emailTextInput.requestFocus();
                } else {
                    Log.e(TAG, e.getMessage());
                }

                new MaterialAlertDialogBuilder(getContext(), R.style.DialogStyle)
                        .setTitle("Register Failed")
                        .setMessage(e.getMessage())
                        .setPositiveButton("OK", null)
                        .show();
            }
        });

    }

    public interface RegisterListener {
        void onRegisterSuccessful();
        void onBackToLogin();
    }

}
