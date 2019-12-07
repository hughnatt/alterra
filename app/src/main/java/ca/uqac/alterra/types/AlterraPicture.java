package ca.uqac.alterra.types;

import androidx.annotation.Nullable;

public class AlterraPicture {

    private String mId;
    private String mUrl;
    private long mTimestamp;
    private String mOwnerID;
    private String mLocationID;

    public AlterraPicture(String id, String url, String ownerID, long timestamp, String locationID){
        mId = id;
        mUrl = url;
        mOwnerID = ownerID;
        mTimestamp = timestamp;
        mLocationID = locationID;
    }

    public String getId(){
        return mId;
    }

    public String getURL(){
        return mUrl;
    }

    public String getOwnerID() {
        return mOwnerID;
    }

    public long getTimestamp(){
        return mTimestamp;
    }

    public String getLocationID(){
        return mLocationID;
    }
}
