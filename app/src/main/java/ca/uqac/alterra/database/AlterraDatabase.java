package ca.uqac.alterra.database;

import java.util.List;

import javax.annotation.Nullable;

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
    void getAllAlterraLocations(@Nullable OnGetLocationsSuccessListener onGetLocationsSuccessListener);

    interface OnGetLocationsSuccessListener {
        void onSuccess(@Nullable List<AlterraPoint> alterraPoints);
    }


    /*
     * USER SECTION
     */
    void registerAlterraUser(String UID, String userEmail);




}
