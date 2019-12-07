package ca.uqac.alterra.database;

public interface AlterraStorage {

    void uploadPhoto(String path, AlterraPoint alterraPoint, UploadListener uploadListener);


    interface UploadListener {
        void onSuccess(String downloadLink);
        void onProgress(int progressPercentage);
        void onFailure(Exception e);
    }
}
