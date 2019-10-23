package ca.uqac.alterra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.ArrayMap;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private Map<UploadTask,Integer> mUploadTasks;
    private Context mContext;
    private NotificationManagerCompat mNotificationManager;
    private static int notificationId;

    /**
     * Instantiates a new PhotoUploader
     * @param bucket Firebase Cloud Storage bucket URI e.g gs://app-0123456789.appspot.com
     */
    public PhotoUploader(String bucket, Context context){
        mStorage = FirebaseStorage.getInstance(bucket);
        mUploadTasks = new ArrayMap<>();
        mContext = context;
        mNotificationManager = NotificationManagerCompat.from(mContext);
    }

    public void uploadPhoto(String path){
        Uri file = Uri.fromFile(new File(path));
        StorageReference riversRef = mStorage.getReference().child("images/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);


        int id = showProgressNotification();

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener((exception) -> {
            //TODO Handle unsuccessful uploads
        }).addOnSuccessListener((taskSnapshot) -> {
            showSuccessNotification(id);
        }).addOnProgressListener((taskSnapshot) -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            System.out.println("Upload is " + progress + "% done");
            updateProgressNotification(id ,(int) progress);
        });


        //Save the UploadTask and the corresponding notification id
        mUploadTasks.put(uploadTask,id);
    }

    /**
     * @param progressNotificationId The id of the progress notification related to this success
     * @return the id of the notification
     */
    private int showSuccessNotification(int progressNotificationId){

        //Stop showing the progress bar notification
        mNotificationManager.cancel(progressNotificationId);

        //Create a new notification for success
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, HomeActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.alterra_logo_round)
                .setContentTitle(mContext.getString(R.string.notification_upload_success))
                .setContentText("")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        // notificationId is a unique int for each notification
        int id = notificationId++;
        mNotificationManager.notify(id, builder.build());

        return id;
    }

    /**

     * @return the id of the notification
     */
    private int showProgressNotification(){
        //Create a progress bar notification with 0% progress
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, HomeActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.alterra_logo_round)
                .setContentTitle(mContext.getString(R.string.notification_upload_inprogress))
                .setContentText("0%")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setProgress(100,0,true); //Progress is indeterminate at first


        // notificationId is a unique int for each notification
        int id = notificationId++;
        mNotificationManager.notify(id, builder.build());

        return id;
    }

    private void updateProgressNotification(int notificationId, int progressionPercentage){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, HomeActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.alterra_logo_round)
                .setContentTitle(mContext.getString(R.string.notification_upload_inprogress))
                .setContentText(progressionPercentage + "%")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setProgress(100,progressionPercentage,false);

        mNotificationManager.notify(notificationId, builder.build());
    }
}
