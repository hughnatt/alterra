package ca.uqac.alterra.auth;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import ca.uqac.alterra.R;
import ca.uqac.alterra.database.AlterraAuth;
import ca.uqac.alterra.database.AlterraCloud;
import ca.uqac.alterra.database.AlterraUser;
import ca.uqac.alterra.database.exceptions.AlterraAuthException;
import ca.uqac.alterra.database.exceptions.AlterraAuthInvalidCredentialsException;
import ca.uqac.alterra.database.exceptions.AlterraAuthUserCollisionException;
import ca.uqac.alterra.database.exceptions.AlterraAuthWeakPasswordException;

public class RegisterFragment extends Fragment {

    private TextInputEditText mNameEditText;
    private TextInputLayout mNameTextInput;

    private TextInputEditText mEmailEditText;
    private TextInputLayout mEmailTextInput;

    private TextInputEditText mPasswordEditText;
    private TextInputLayout mPasswordTextInput;

    private TextInputEditText mConfirmPasswordEditText;
    private TextInputLayout mConfirmPasswordTextInput;


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
        mPasswordTextInput = view.findViewById(R.id.passwordTextInput);
        mPasswordEditText = view.findViewById(R.id.passwordEditText);

        mConfirmPasswordTextInput = view.findViewById(R.id.confirmPasswordTextInput);
        mConfirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);

        mNameTextInput = view.findViewById(R.id.nameTextInput);
        mNameEditText = view.findViewById(R.id.nameEditText);

        mEmailTextInput = view.findViewById(R.id.emailTextInput);
        mEmailEditText = view.findViewById(R.id.emailEditText);

        MaterialButton registerButton = view.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> verifyFields());

        MaterialButton backButton = view.findViewById(R.id.registerBackButton);
        backButton.setOnClickListener((v) -> {
            if (mListener != null) { mListener.onBackToLogin(); }
        });

        setTextWatcher(mNameEditText,mNameTextInput);
        setTextWatcher(mEmailEditText,mEmailTextInput);
        setTextWatcher(mPasswordEditText,mPasswordTextInput);
        setTextWatcher(mConfirmPasswordEditText,mConfirmPasswordTextInput);

        mConfirmPasswordEditText.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN &&
                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                verifyFields();
                return true;

            }
            return false; // pass on to other listeners.
        });

        return view;
    }

    private void setTextWatcher(TextInputEditText editText, TextInputLayout inputLayout){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                inputLayout.setErrorEnabled(false);
            }
        });
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
        mNameTextInput.setError(null);
        mEmailTextInput.setError(null);
        mPasswordTextInput.setError(null);
        mConfirmPasswordTextInput.setError(null);

    }

    void setRegisterListener(RegisterFragment.RegisterListener listener) {
        mListener = listener;
    }

    private void verifyFields() {
        String name = Objects.requireNonNull(mNameEditText.getText()).toString();
        String email = Objects.requireNonNull(mEmailEditText.getText()).toString();
        String password = Objects.requireNonNull(mPasswordEditText.getText()).toString();
        String confirmPassword = Objects.requireNonNull(mConfirmPasswordEditText.getText()).toString();

        boolean isValid = true;

        if (name.length() < 1) {
            mNameTextInput.setError(getString(R.string.auth_enter_email));
            isValid = false;
        }

        if (email.length() < 1) {
            mEmailTextInput.setError(getString(R.string.auth_enter_email));
            isValid = false;
        }

        if (password.length() < 1) {
            mPasswordTextInput.setError(getString(R.string.auth_enter_password));

            isValid = false;
        }

        if (confirmPassword.length() < 1) {
            mConfirmPasswordTextInput.setError(getString(R.string.auth_confirm_password));
            isValid = false;
        }

        if (!password.equals(confirmPassword)) {
            mConfirmPasswordTextInput.setError(getString(R.string.auth_password_different));
            isValid = false;
        }

        if (isValid)
            register(email, password);

    }

    private void register(String email, String password) {
        mAuth.registerWithPassword(email, password, new AlterraAuth.AlterraAuthListener() {
            @Override
            public void onSuccess(AlterraUser user) {
                // Register success
                if (user != null){
                    AlterraCloud.getDatabaseInstance().registerAlterraUser(user.getUID(),user.getEmail(),null);
                    mListener.onRegisterSuccessful();
                }
            }

            @Override
            public void onFailure(AlterraAuthException e) {
                // Register failed
                Context context = getContext();
                if (context != null){

                    String errorMessage;

                    if (e instanceof AlterraAuthWeakPasswordException){
                        errorMessage = getString(R.string.auth_exception_weak_password);
                    } else if (e instanceof AlterraAuthInvalidCredentialsException) {
                        errorMessage = getString(R.string.auth_exception_invalid_credentials);
                    } else if (e instanceof AlterraAuthUserCollisionException) {
                        errorMessage = getString(R.string.auth_exception_user_collision);
                    } else {
                        errorMessage = getString(R.string.auth_register_failed);
                    }

                    new MaterialAlertDialogBuilder(context, R.style.DialogStyle)
                            .setTitle(R.string.auth_register_failed)
                            .setMessage(errorMessage)
                            .setPositiveButton(R.string.auth_generic_positive_answer, null)
                            .show();
                }
            }
        });

    }

    public interface RegisterListener {
        void onRegisterSuccessful();
        void onBackToLogin();
    }

}
