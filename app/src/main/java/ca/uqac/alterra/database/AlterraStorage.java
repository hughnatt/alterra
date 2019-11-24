package ca.uqac.alterra.database;

import ca.uqac.alterra.home.AlterraPoint;

public interface AlterraStorage {

    void uploadPhoto(String path, AlterraPoint alterraPoint, UploadListener uploadListener);


    interface UploadListener {
        void onSuccess(String downloadLink);
        void onProgress(int progressPercentage);
        void onFailure(Exception e);
    }
}
