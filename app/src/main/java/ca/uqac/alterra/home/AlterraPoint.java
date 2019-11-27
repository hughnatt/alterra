package ca.uqac.alterra.home;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

import ca.uqac.alterra.database.AlterraCloud;

public class AlterraPoint implements Serializable {
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

    public LatLng getLatLng(){
        return new LatLng(getLatitude(),getLongitude());
    }

    public double getLatitude(){
        return mLatitude;
    }

    public double getLongitude(){
        return mLongitude;
    }

    public boolean isUnlocked() {
        return mUnlocked;
    }

    public String getThumbnail(){
        return mThumbnail;
    }

    @NonNull
    @Override
    public String toString() {
        return getTitle();
    }

    public void unlock(){
        mUnlocked = true;
        AlterraCloud.getDatabaseInstance().unlockAlterraLocation(AlterraCloud.getAuthInstance().getCurrentUser(),this, null);
    }
}
