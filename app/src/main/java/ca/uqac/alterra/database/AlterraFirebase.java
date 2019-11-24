package ca.uqac.alterra.database;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ca.uqac.alterra.database.exceptions.AlterraAuthException;
import ca.uqac.alterra.database.exceptions.AlterraAuthInvalidCredentialsException;
import ca.uqac.alterra.database.exceptions.AlterraAuthUserCollisionException;
import ca.uqac.alterra.database.exceptions.AlterraAuthWeakPasswordException;
import ca.uqac.alterra.home.AlterraPoint;

public class AlterraFirebase implements AlterraDatabase, AlterraAuth, AlterraStorage {

    private static String COLLECTION_PATH_LOCATIONS = "locations";
    private static String COLLECTION_PATH_USERS = "users";
    private static String STORAGE_BUCKET = "gs://alterra-1569341283377.appspot.com";
    private static final int RC_SIGN_IN = 0x03;

    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private FirebaseAuth mAuth;

    private AlterraUser mCurrentUser;
    private CallbackManager mFacebookCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;

    private AlterraAuthListener mFacebookAuthCallback;
    private AlterraAuthListener mGoogleAuthCallback;

    AlterraFirebase(){
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance(STORAGE_BUCKET);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void getAllAlterraLocations(AlterraUser currentUser, @Nullable OnGetLocationsSuccessListener onGetLocationsSuccessListener) {
        mFirestore.collection(COLLECTION_PATH_LOCATIONS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (onGetLocationsSuccessListener == null) return;
                        ArrayList<AlterraPoint> alterraPoints = new ArrayList<AlterraPoint>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Skip the default document
                            if (document.getId().equals("_default")) continue;

                            //Get and extract the document values
                            Map<String,Object> documentData = document.getData();
                            try {
                                GeoPoint coordinates = (GeoPoint) documentData.get("coordinates");
                                double latitude = coordinates.getLatitude();
                                double longitude = coordinates.getLongitude();
                                Map titles = (Map) documentData.get("name");
                                Map descriptions = (Map) documentData.get("description");
                                String title = (String) titles.get("default");
                                String description = (String) descriptions.get("default");
                                Map users = (Map) documentData.get("users");
                                boolean unlocked = (users != null && users.containsKey(currentUser.getUID()));
                                alterraPoints.add(new AlterraPoint(document.getId(), latitude, longitude, title, description, unlocked));
                            } catch (NullPointerException ex){
                                System.out.println("Invalid Alterra location was skipped : [ID]=" + document.getId());
                            }
                        }
                        onGetLocationsSuccessListener.onSuccess(alterraPoints);
                    } else {
                       System.out.println("Error getting documents: " + task.getException());
                    }
                });
    }

    @Override
    public void unlockAlterraLocation(AlterraUser user, AlterraPoint location) {
        //Add user to location document
        mFirestore.collection(COLLECTION_PATH_LOCATIONS)
                .document(location.getId())
                .update("users."+user.getUID(),true);
        //Add location to user document
        mFirestore.collection(COLLECTION_PATH_USERS)
                .document(user.getUID())
                .update("locations"+location.getId(),true);
    }

    @Override
    public void registerAlterraUser(String userID, String userEmail) {
        //Add user document in database
        HashMap<String, Object> data = new HashMap<>();
        data.put("displayName", userEmail);
        mFirestore.collection(COLLECTION_PATH_USERS).document(userID).set(data);
    }

    @Override
    public AlterraUser getCurrentUser() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null){
            return null;
        } else {
            if (mCurrentUser == null){
                //Time to create the Alterra User object
                mCurrentUser = convert(firebaseUser);
            }
            return mCurrentUser;
        }
    }

    @Override
    public AlterraAuthCallback getCallback() {
        return (requestCode, resultCode, data) -> {
            // Pass the activity result back to the Facebook SDK
            mFacebookCallbackManager.onActivityResult(requestCode,resultCode,data);

            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                } catch (Exception e) {
                    // Google Sign In failed
                    if (mGoogleAuthCallback != null){
                        mGoogleAuthCallback.onFailure(castException(task.getException()));
                    }
                }
            }
        };
    }

    @Override
    public void initGoogleAuth(Context context, String webClientId) {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    @Override
    public void initFacebookAuth() {
        // Facebook Callback registration
        mFacebookCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                firebaseAuthWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
                if (mFacebookAuthCallback != null){
                    mFacebookAuthCallback.onFailure(castException(exception));
                }
            }
        });
    }

    @Override
    public void logInWithPassword(String email, String password, @Nullable AlterraAuthListener alterraAuthListener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        if (alterraAuthListener != null){
                            alterraAuthListener.onSuccess(getCurrentUser());
                        }
                    } else {
                        // Sign in failed
                        if (alterraAuthListener != null){
                            alterraAuthListener.onFailure(castException(task.getException()));
                        }
                    }
                });
    }

    @Override
    public void registerWithPassword(String email, String password, @Nullable AlterraAuthListener alterraAuthListener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (alterraAuthListener != null){
                            alterraAuthListener.onSuccess(getCurrentUser());
                        }
                    } else {
                        if (alterraAuthListener != null){
                            alterraAuthListener.onFailure(castException(task.getException()));
                        }
                    }
                });
    }

    @Override
    public void logInWithFacebook(Activity activity, @Nullable AlterraAuthListener alterraAuthListener) {
        mFacebookAuthCallback = alterraAuthListener;
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()){
            //Current token is valid
            firebaseAuthWithFacebook(accessToken);
        } else {
            LoginManager.getInstance().logIn(activity, Collections.singletonList("email"));
        }
    }

    @Override
    public void logInWithFacebook(Fragment fragment, @Nullable AlterraAuthListener alterraAuthListener) {
        mFacebookAuthCallback = alterraAuthListener;
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()){
            //Current token is valid
            firebaseAuthWithFacebook(accessToken);
        } else {
            LoginManager.getInstance().logIn(fragment, Collections.singletonList("email"));
        }
    }

    @Override
    public void logInWithGoogle(Activity activity, AlterraAuthListener alterraAuthListener) {
        mGoogleAuthCallback = alterraAuthListener;
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void logInWithGoogle(Fragment fragment, AlterraAuthListener alterraAuthListener) {
        mGoogleAuthCallback = alterraAuthListener;
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        fragment.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(@Nullable GoogleSignInAccount acct) {
        if (acct == null) {
            mGoogleAuthCallback.onFailure(new AlterraAuthException());
            return;
        }
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        if (mGoogleAuthCallback != null) {
                            mGoogleAuthCallback.onSuccess(getCurrentUser());
                        }
                    } else {
                        // Sign in failed
                        if (mGoogleAuthCallback != null){
                            mGoogleAuthCallback.onFailure(castException(task.getException()));
                        }
                        //Log out google user
                        mGoogleSignInClient.signOut();
                    }
                });
    }

    private void firebaseAuthWithFacebook(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        //Sign in success
                        if (mFacebookAuthCallback != null){
                            mFacebookAuthCallback.onSuccess(getCurrentUser());
                        }
                    } else {
                        // Sign in failed
                        if (mFacebookAuthCallback != null){
                            mFacebookAuthCallback.onFailure(castException(task.getException()));
                        }
                        // and Log out facebook user
                        LoginManager.getInstance().logOut();
                    }
                });
    }

    @Override
    public void logOut() {
        mCurrentUser = null;
        mAuth.signOut();
        //Facebook logout
        LoginManager.getInstance().logOut();
        //Google logout
        mGoogleSignInClient.signOut();
        //TODO revoke google and facebook access properly
    }


    private static AlterraUser convert(FirebaseUser firebaseUser){
        AuthMethod method;
        switch(firebaseUser.getProviderId()){
            case FacebookAuthProvider.PROVIDER_ID:
                method = AuthMethod.FACEBOOK;
                break;
            case GoogleAuthProvider.PROVIDER_ID:
                method = AuthMethod.GOOGLE;
                break;
            default:
                method = AuthMethod.PASSWORD;
                break;
        }
        return new AlterraUser(firebaseUser.getUid(),firebaseUser.getEmail(),method);
    }

    private static AlterraAuthException castException(Exception e){
        if (e instanceof FirebaseAuthUserCollisionException){
            return new AlterraAuthUserCollisionException();
        } else if (e instanceof FirebaseAuthWeakPasswordException){
            return new AlterraAuthWeakPasswordException();
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            return new AlterraAuthInvalidCredentialsException();
        } else if (e instanceof FacebookException){
            return new AlterraAuthException(e);
        } else {
            return new AlterraAuthException(e);
        }
    }

    @Override
    public void uploadPhoto(String path, AlterraPoint alterraPoint, UploadListener uploadListener) {
        Uri file = Uri.fromFile(new File(path));
        String remotePath = "images/"+file.getLastPathSegment();
        StorageReference imagesRef = mStorage.getReference().child(remotePath);

        UploadTask uploadTask = imagesRef.putFile(file);
        uploadTask.addOnFailureListener(uploadListener::onFailure);
        uploadTask.addOnProgressListener((taskSnapshot) -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            uploadListener.onProgress((int) progress);
        });
        uploadTask.continueWithTask(task -> {
            if (true) {
                return null;
            } else {
                // Continue with the task to get the download URL
                return imagesRef.getDownloadUrl();
            }
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                uploadListener.onSuccess(task.getResult().toString());
            } else {
                uploadListener.onFailure(task.getException());
            }
        });
    }
}
