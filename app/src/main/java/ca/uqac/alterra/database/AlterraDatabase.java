package ca.uqac.alterra.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import ca.uqac.alterra.home.AlterraPoint;

/**
 * Alterra interface for cloud interactions
 * (e.g database access, retrieving pictures, ...)
 * Will be extended as time goes
 */
public interface AlterraDatabase {

    /*
     * LOCATION SECTION
     */
    void getAllAlterraLocations(@NonNull AlterraUser currentUser, @Nullable OnGetLocationsSuccessListener onGetLocationsSuccessListener);

    interface OnGetLocationsSuccessListener {
        void onSuccess(@Nullable List<AlterraPoint> alterraPoints);
    }

    void unlockAlterraLocation(AlterraUser user, AlterraPoint location);


    /*
     * USER SECTION
     */
    void registerAlterraUser(String UID, String userEmail);

    /*
     * PHOTO SECTION
     */

    /**
     *
     * @param userID Current user ID
     * @param locationID Alterra location ID
     * @param remoteLink The direct download link for this photo
     * @param timestamp Date when the photo was taken
     */
    void addPhoto(String userID, String locationID, String remoteLink, long timestamp);
}
