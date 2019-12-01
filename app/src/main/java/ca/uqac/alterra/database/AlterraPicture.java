package ca.uqac.alterra.database;

import androidx.annotation.Nullable;

import ca.uqac.alterra.home.AlterraPoint;

public class AlterraPicture {

    private String mId;
    private String mUrl;
    private long mTimestamp;
    private AlterraUser mOwner;
    private AlterraPoint mLocation;

    public AlterraPicture(String id, String url, AlterraUser owner, long timestamp, String locationID){
        mId = id;
        mUrl = url;
        mOwner = owner;
        mTimestamp = timestamp;
        mLocation = null;
        AlterraCloud.getDatabaseInstance().getAlterraPointFromUID(locationID, new AlterraDatabase.OnGetAlterraPointFromUIDListener() {
            @Override
            public void onSuccess(AlterraPoint alterraPoint) {
                mLocation = alterraPoint;
            }

            @Override
            public void onError(Exception e) {
                //TODO make possible to update the location field afterwards
            }
        });
    }

    public AlterraPicture(String id, String url, String ownerID, long timestamp, AlterraPoint location){
        mId = id;
        mUrl = url;
        mOwner = null;
        mTimestamp = timestamp;
        mLocation = location;

        AlterraCloud.getDatabaseInstance().getAlterraUserFromUID(ownerID, new AlterraDatabase.OnGetAlterraUserFromUIDListener() {
            @Override
            public void onSuccess(AlterraUser alterraUser) {
                mOwner = alterraUser;
            }

            @Override
            public void onError(Exception e) {
                //TODO make possible to update the owner field afterwards
            }
        });
    }

    public String getId(){
        return mId;
    }

    public String getURL(){
        return mUrl;
    }

    @Nullable
    public AlterraUser getOwner() {
        return mOwner;
    }

    public long getTimestamp(){
        return mTimestamp;
    }

    @Nullable
    public AlterraPoint getLocation(){
        return mLocation;
    }
}
