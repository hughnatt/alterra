package ca.uqac.alterra.home;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import ca.uqac.alterra.database.AlterraCloud;

public class AlterraPoint {
    private String mId;
    private LatLng mLatLng;
    private String mTitle;
    private String mDescription;
    private boolean mUnlocked;
    private String mThumbnail;

    public AlterraPoint(String id, LatLng latLng, String title, String description){
        mId = id;
        mLatLng = latLng;
        mTitle = title;
        mDescription = description;
    }

    public AlterraPoint(String id, double lat, double lng, String title, String description, boolean unlocked, String thumbnail){
        mId = id;
        mLatLng = new LatLng(lat,lng);
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
        return mLatLng;
    }

    public double getLatitude(){
        return mLatLng.latitude;
    }

    public double getLongitude(){
        return mLatLng.longitude;
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
        AlterraCloud.getDatabaseInstance().unlockAlterraLocation(AlterraCloud.getAuthInstance().getCurrentUser(),this);
    }
}
