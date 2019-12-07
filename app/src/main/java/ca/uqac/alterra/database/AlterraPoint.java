package ca.uqac.alterra.database;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

import ca.uqac.alterra.database.AlterraCloud;
import ca.uqac.alterra.utility.AlterraGeolocator;

public class AlterraPoint implements Serializable {

    public static final double MINIMUM_UNLOCK_DISTANCE = 1000; //in meters, obviously too big, will be reduced later

    private String mId;
    private double mLatitude;
    private double mLongitude;
    private String mTitle;
    private String mDescription;
    private boolean mUnlocked;
    private String mThumbnail;


    public AlterraPoint(String id, LatLng latLng, String title, String description){
        mId = id;
        mLatitude = latLng.latitude;
        mLongitude = latLng.longitude;
        mTitle = title;
        mDescription = description;
    }

    public AlterraPoint(String id, double lat, double lng, String title, String description, boolean unlocked, String thumbnail){
        mId = id;
        mLatitude = lat;
        mLongitude = lng;
        mTitle = title;
        mDescription = description;
        mUnlocked = unlocked;
        mThumbnail = thumbnail;
    }

    public String getId(){
        return mId;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getDescription(){
        return mDescription;
    }

    public String getThumbnail(){
        return mThumbnail;
    }

    public LatLng getLatLng(){
        return new LatLng(getLatitude(),getLongitude());
    }

    public double getLatitude(){
        return mLatitude;
    }

    public double getLongitude(){
        return mLongitude;
    }

    @NonNull
    @Override
    public String toString() {
        return getTitle();
    }

    public boolean isUnlocked() {
        return mUnlocked;
    }


    public boolean isUnlockable() {
        return AlterraGeolocator.distanceFrom(this) < MINIMUM_UNLOCK_DISTANCE;
    }


    public boolean unlock(){
        if (isUnlockable()){
            mUnlocked = true;
            AlterraCloud.getDatabaseInstance().unlockAlterraLocation(AlterraCloud.getAuthInstance().getCurrentUser(),this, null);
        }
        return mUnlocked;
    }
}
