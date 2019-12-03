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

    void getAlterraPointFromUID(String UID, @Nullable OnGetAlterraPointFromUIDListener onGetAlterraPointFromUIDListener);
    interface OnGetAlterraPointFromUIDListener {
        void onSuccess(AlterraPoint alterraPoint);
        void onError(Exception e);
    }

    void unlockAlterraLocation(AlterraUser user, AlterraPoint location, @Nullable AlterraWriteListener alterraWriteListener);

    void getUnlockedUsers(AlterraPoint location, @Nullable OnGetUsersListener onGetUsersListener);
    interface OnGetUsersListener {
        void onSuccess(List<AlterraUser> users);
        void onError(Exception e);
    }


    /*
     * USER SECTION
     */
    void registerAlterraUser(String UID, String userEmail, @Nullable AlterraWriteListener alterraWriteListener);
    void getAlterraUserFromUID(String UID, @Nullable OnGetAlterraUserFromUIDListener onGetAlterraUserFromUIDListener);
    interface OnGetAlterraUserFromUIDListener{
        void onSuccess(AlterraUser alterraUser);
        void onError(Exception e);
    }

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
    void addPhoto(String userID, String locationID, String remoteLink, long timestamp, @Nullable AlterraWriteListener alterraWriteListener);

    void getAlterraPictures(@NonNull AlterraPoint location, @Nullable OnGetAlterraPicturesListener onGetAlterraPicturesListener);

    void getAlterraPictures(@NonNull AlterraUser owner, @Nullable OnGetAlterraPicturesListener onGetAlterraPicturesListener);

    void deleteAlterraPictureFromFirestore(@NonNull AlterraPicture picture, @Nullable AlterraWriteListener alterraWriteListener);

    void deleteAlterraPictureFromStorage(@NonNull AlterraPicture picture, @Nullable AlterraWriteListener alterraWriteListener);

    interface OnGetAlterraPicturesListener {
        void onSuccess(@NonNull List<AlterraPicture> alterraPictures);
        void onError(Exception e);
    }



    interface AlterraWriteListener {
        void onSuccess();
        void onError(Exception e);
    }
}
