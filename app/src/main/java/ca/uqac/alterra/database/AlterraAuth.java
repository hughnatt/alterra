package ca.uqac.alterra.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ca.uqac.alterra.database.exceptions.AlterraAuthException;


/**
 * Interface providing authentification abstract methods
 */
public interface AlterraAuth {

    AlterraUser getCurrentUser();

    AlterraAuthCallback getCallback();

    void initGoogleAuth(Context context, String webClientId);
    void initFacebookAuth();


    void logInWithPassword(String email, String password, @Nullable AlterraAuthListener alterraAuthListener);
    void registerWithPassword(String email, String password, @Nullable AlterraAuthListener alterraAuthListener);


    void logInWithFacebook(Activity activity, @Nullable AlterraAuthListener alterraAuthListener);
    void logInWithFacebook(Fragment fragment, @Nullable AlterraAuthListener alterraAuthListener);
    void logInWithGoogle(Activity activity,@Nullable AlterraAuthListener alterraAuthListener);
    void logInWithGoogle(Fragment fragment, @Nullable AlterraAuthListener alterraAuthListener);

    void logOut();

    interface AlterraAuthListener {
        void onSuccess(AlterraUser user);
        void onFailure(AlterraAuthException e);
    }

    interface AlterraAuthCallback {
        void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
    }
}