package ca.uqac.alterra.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import ca.uqac.alterra.R;
import ca.uqac.alterra.auth.AuthActivity;

import ca.uqac.alterra.database.AlterraCloud;
import ca.uqac.alterra.database.AlterraDatabase;
import ca.uqac.alterra.database.AlterraAuth;
import ca.uqac.alterra.database.AlterraUser;
import ca.uqac.alterra.utility.AlterraGeolocator;


public class HomeActivity extends AppCompatActivity {




    private enum FRAGMENT_ID {FRAGMENT_MAP, FRAGMENT_LIST, FRAGMENT_PROFILE}
    private FRAGMENT_ID mCurrentFragment;

    private PhotoUploader mPhotoUploader;
    private String mCurrentImagePath;
    private AlterraPoint mCurrentImagePoint;
    private AlterraAuth mAuth;
    private NavigationView mNavigationView;

    private boolean mGpsEnabled = false;
    private boolean mLocationEnabled = false;

    /**
     * True if we already requested runtime permissions
     * but we are still waiting user response.
     * If the user changes orientation, we need to make
     * sure not to recreate the request
     */
    private boolean mPendingPermissionRequest = false;

    private List<AlterraPoint> mAlterraLocations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (savedInstanceState == null){
            //Not restoring from previous state, use default fragment
            updateWorkflow(FRAGMENT_ID.FRAGMENT_MAP);
        } else {
            mCurrentImagePath = savedInstanceState.getString("mCurrentImagePath");
            mPendingPermissionRequest = savedInstanceState.getBoolean("mPendingPermissionRequest",false);
        }

        setNavigationViewListener();
        mAuth = AlterraCloud.getAuthInstance();


        //Get all the alterra location from database
        AlterraDatabase alterraDatabase = AlterraCloud.getDatabaseInstance();
        alterraDatabase.getAllAlterraLocations(mAuth.getCurrentUser(),(list) -> mAlterraLocations = list);

        //Trigger an alert message when GPS is disabled
        AlterraGeolocator.addOnGPSStatusChangedListener(enabled -> {
            mGpsEnabled = enabled;
            /*if (!enabled) {
                requestGPSActivation();
            }*/
        });

        mPhotoUploader = new PhotoUploader(this);
    }


    @Override
    public void onStart() {
        super.onStart();
        AlterraUser currentUser = mAuth.getCurrentUser();

        //Make sure a user is logged in. If it's not the case, go back to login screen
        if (currentUser != null) {
            View headerView = mNavigationView.getHeaderView(0);
            TextView navUsername = (TextView) headerView.findViewById(R.id.navUsername);
            navUsername.setText(currentUser.getEmail());
        } else {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        }

        if (!checkLocationPermissions() ){
            if (!mPendingPermissionRequest){
                requestLocationPermissions(false);
            }
        } else {
            locationPermissionGranted();
        }
    }

    private void updateWorkflow(FRAGMENT_ID nextFragment){
        if(nextFragment == mCurrentFragment){
            return;
        }
        mCurrentFragment = nextFragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (mCurrentFragment){
            case FRAGMENT_MAP:
                HomeMapFragment homeMapFragment = (HomeMapFragment) fragmentManager.findFragmentByTag("HomeMapFragment");
                if(homeMapFragment == null){
                    homeMapFragment = new HomeMapFragment();
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_home, homeMapFragment,"HomeMapFragment")
                        .setCustomAnimations(R.anim.faded_in,R.anim.faded_out)
                        .addToBackStack(null)
                        .commit();
                break;

            case FRAGMENT_LIST:
                HomeListFragment homeListFragment = (HomeListFragment) fragmentManager.findFragmentByTag("HomeListFragment");
                if(homeListFragment == null){
                    homeListFragment = new HomeListFragment();
                }

                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_home, homeListFragment,"HomeListFragment")
                        .setCustomAnimations(R.anim.faded_in,R.anim.faded_out)
                        .addToBackStack(null)
                        .commit();
                break;

            case FRAGMENT_PROFILE:
                HomeProfileFragment homeProfileFragment = (HomeProfileFragment) fragmentManager.findFragmentByTag("HomeProfileFragment");
                if(homeProfileFragment == null){
                    homeProfileFragment = new HomeProfileFragment();
                }

                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_home, homeProfileFragment,"HomeProfileFragment")
                        .setCustomAnimations(R.anim.faded_in,R.anim.faded_out)
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }

    static final int REQUEST_TAKE_PHOTO = 1;



    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // TODO Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "ca.uqac.alterra.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                System.out.println("Photo saved as" + mCurrentImagePath);
            }
        }
    }

    public void takeAlterraPhoto(AlterraPoint alterraPoint){
        assert(alterraPoint != null);
        if (AlterraGeolocator.distanceFrom(alterraPoint) < AlterraPoint.MINIMUM_UNLOCK_DISTANCE) {
            mCurrentImagePoint = alterraPoint;
            dispatchTakePictureIntent();
        } else {
            Toast.makeText(this,getString(R.string.alterra_point_locked),Toast.LENGTH_LONG).show();
        }
    }

    public void takeAlterraPhoto(){
        //Make sure GPS is enabled
        if (!mGpsEnabled) {
            requestGPSActivation();
            return;
        }

        //Make sure location list is setup
        if (mAlterraLocations == null){
            showNoAlterraPointFoundAlert();
            return;
        }

        //Look for valid locations
        List<AlterraPoint> validLocations = new ArrayList<>();
        for (AlterraPoint p : mAlterraLocations) {
            System.out.println("Distance = " + AlterraGeolocator.distanceFrom(p));
            if (AlterraGeolocator.distanceFrom(p) < AlterraPoint.MINIMUM_UNLOCK_DISTANCE) {
                validLocations.add(p);
            }
        }
        if (validLocations.size() == 0){
            showNoAlterraPointFoundAlert();
            return;
        }

        final ArrayAdapter<AlterraPoint> arrayAdapter = new ArrayAdapter<AlterraPoint>(this,android.R.layout.select_dialog_item){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                text1.setTextColor(ContextCompat.getColor(HomeActivity.this,R.color.colorPrimaryDark));
                text1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                return view;
            }
        };
        arrayAdapter.addAll(validLocations);

        new MaterialAlertDialogBuilder(this, R.style.DialogStyle)
                .setAdapter(arrayAdapter, (dialog, which) -> {
                        mCurrentImagePoint = arrayAdapter.getItem(which);
                        dispatchTakePictureIntent();
                })
                .setTitle(R.string.alterrapointchoice_alert_title)
                .setCancelable(true)
                .show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentImagePath = image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK){
                    //Check if user is still located near the selected point
                    if (AlterraGeolocator.distanceFrom(mCurrentImagePoint) < AlterraPoint.MINIMUM_UNLOCK_DISTANCE){
                        mPhotoUploader.uploadPhoto(mCurrentImagePath,mCurrentImagePoint);
                    } else {
                        //TODO tell user he moved too far away from its initial position
                    }
                }
                break;
            case REQUEST_PERMISSIONS_LOCATION:
                //Do nothing, if we are back from the settings screen, the onStart method will be called
                //and the permission check will be done there
                break;
        }

    }

    private void setNavigationViewListener() {
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        DrawerLayout mDrawer = (DrawerLayout) findViewById(R.id.navDrawer);
        mNavigationView.setNavigationItemSelectedListener((item) -> {
            switch (item.getItemId()){
                case R.id.nav_item_profile :
                    Toast toastProfile = Toast.makeText(getApplicationContext(), "Profile", Toast.LENGTH_LONG);
                    toastProfile.show();
                    updateWorkflow(FRAGMENT_ID.FRAGMENT_PROFILE);
                    break;
                case R.id.nav_item_list :
                    Toast toastPictures = Toast.makeText(getApplicationContext(), "List", Toast.LENGTH_LONG);
                    toastPictures.show();
                    updateWorkflow(FRAGMENT_ID.FRAGMENT_LIST);
                    break;
                case R.id.nav_item_map :
                    Toast toastPlaces = Toast.makeText(getApplicationContext(), "Map", Toast.LENGTH_LONG);
                    toastPlaces.show();
                    updateWorkflow(FRAGMENT_ID.FRAGMENT_MAP);
                    break;
                case R.id.nav_item_settings :
                    Toast toastSettings = Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_LONG);
                    toastSettings.show();
                    break;
                case R.id.nav_item_about :
                    Toast toastAbout = Toast.makeText(getApplicationContext(), "About", Toast.LENGTH_LONG);
                    toastAbout.show();
                    break;
                case R.id.nav_item_logout :
                    mAuth.logOut();
                    startActivity(new Intent(this, AuthActivity.class));
                    finish();
                    break;
                default:
                    return super.onOptionsItemSelected(item);
            }

            mDrawer.closeDrawers();
            return true;
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mCurrentImagePath",mCurrentImagePath);
        outState.putBoolean("mPendingPermissionRequest",mPendingPermissionRequest);
    }

    private static final int REQUEST_PERMISSIONS_LOCATION = 0x10;

    private boolean checkLocationPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Requesting Location permissions");
            return false;
        } else {
            return true;
        }
    }

    private static final int REQUEST_PERMISSION_SETTING = 0x0a;

    /**
     * Ask for location permission
     * By default, open an in-app permission request message
     * @param openSettings Open the app settings so the user can
     *                     enable the permission from there
     */
    private void requestLocationPermissions(boolean openSettings){
        if (!openSettings){ //in-app permission request message

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_LOCATION);
            mPendingPermissionRequest = true;
        } else { //request permission from settings
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS_LOCATION: {
                mPendingPermissionRequest = false;
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted, yay!
                    // Instantiates the geolocator
                    locationPermissionGranted();
                } else {
                    boolean showRationale = true;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
                        // if showRationale = false
                        // user also CHECKED "never ask again"
                        // We need to open settings screen
                        // Permission denied,
                        // Display a message and request permission again
                        showLocationPermissionDeniedAlert(!showRationale);
                    }

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions Alterra might request.
        }
    }

    /**
     *
     * @param neverAskAgain true if user checked the never ask again popup (API > M)
     *                      in that case, we redirect to the app settings rather than
     *                      showing the popup again
     */
    private void showLocationPermissionDeniedAlert(boolean neverAskAgain){
        new MaterialAlertDialogBuilder(this, R.style.DialogStyle)
                .setTitle(R.string.permission_alert_title)
                .setMessage(R.string.permission_alert_body_location)
                .setPositiveButton(R.string.permission_alert_button_positive, (dialog, which) -> {
                    //User wants to retry, request permission again
                    requestLocationPermissions(neverAskAgain);
                })
                .setNegativeButton(R.string.permission_alert_button_negative, (dialog, which) -> {
                    finish(); //User doesn't want to give location permission, exit application
                })
                .setCancelable(false)
                .show();
    }

    /**
     * Callback when location permission is acquired
     */
    private void locationPermissionGranted(){
        mLocationEnabled = true;
        AlterraGeolocator.initGeolocatorForContext(this);
    }

    /**
     * Show a cancellable alert dialog to ask the user to enable its GPS system
     */
    private void requestGPSActivation(){
        new MaterialAlertDialogBuilder(this, R.style.DialogStyle)
                .setTitle(R.string.gps_alert_title)
                .setMessage(R.string.gps_alert_body)
                .setPositiveButton(R.string.gps_alert_button_positive, null)
                .setCancelable(true)
                .show();
    }

    /**
     * Show an alert fragment indicating that no alterra points were found nearby user location
     */
    private void showNoAlterraPointFoundAlert(){
        new MaterialAlertDialogBuilder(this, R.style.DialogStyle)
                .setTitle(R.string.pointnotfound_alert_title)
                .setMessage(R.string.pointnotfound_alert_body)
                .setPositiveButton(R.string.pointnotfound_alert_button_positive,null)
                .setCancelable(true)
                .show();
    }
    
    public void displayPicture(String url){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_home, HomeProfilePhotoFragment.newInstance(url))
                .setCustomAnimations(R.anim.faded_in,R.anim.faded_out)
                .addToBackStack(null)
                .commit();
    }

    public void showPlaceDetails(AlterraPoint alterraPoint){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_home,HomeDetailsFragment.newInstance(alterraPoint))
                .setCustomAnimations(R.anim.faded_in,R.anim.faded_out)
                .addToBackStack(null)
                .commit();
    }

    @Override public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_home);
        if ((fragment instanceof HomeProfilePhotoFragment)){
            fragmentManager.popBackStack();
        } else if (fragment instanceof  HomeDetailsFragment){
            fragmentManager.popBackStack();
        }
    }
}
