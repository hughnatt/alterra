package ca.uqac.alterra.home;

import com.google.android.gms.maps.model.LatLng;

public class AlterraPoint {
    private String mId;
    private LatLng mLatLng;
    private String mTitle;
    private String mDescription;

    public AlterraPoint(String id, LatLng latLng, String title, String description){
        mId = id;
        mLatLng = latLng;
        mTitle = title;
        mDescription = description;
    }

    public AlterraPoint(String id, double lat, double lng, String title, String description){
        mId = id;
        mLatLng = new LatLng(lat,lng);
        mTitle = title;
        mDescription = description;
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
}
