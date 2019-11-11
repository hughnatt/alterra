package ca.uqac.alterra.home;

import com.google.android.gms.maps.model.LatLng;

public class AlterraPoint {
    private LatLng mLatLng;
    private String mTitle;
    private String mDescription;


    AlterraPoint(LatLng latLng, String title, String description){
        mLatLng = latLng;
        mTitle = title;
        mDescription = description;
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
}
