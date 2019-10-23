package ca.uqac.alterra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhotoUploader {


    public static class UploadControlReceiver extends BroadcastReceiver {

        private static final String UPLOAD_PAUSE = "UPLOAD_PAUSE";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == UPLOAD_PAUSE){

            }
        }
    }

    private FirebaseStorage mStorage;
    private List<UploadTask> mUploadTasks;
    private Context mContext;

    /**
     * Instantiates a new PhotoUploader
     * @param bucket Firebase Cloud Storage bucket URI e.g gs://app-0123456789.appspot.com
     */
    public PhotoUploader(String bucket, Context context){
        mStorage = FirebaseStorage.getInstance(bucket);
        mUploadTasks = new ArrayList<>();
        mContext = context;
    }

    public void uploadPhoto(String path){
        Uri file = Uri.fromFile(new File(path));
        StorageReference riversRef = mStorage.getReference().child("images/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener((exception) -> {
            //TODO Handle unsuccessful uploads
        }).addOnSuccessListener((taskSnapshot) -> {
            //TODO Display upload successful message
        }).addOnProgressListener((taskSnapshot) -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            System.out.println("Upload is " + progress + "% done");
        });

        mUploadTasks.add(uploadTask);
    }

}
