package ca.uqac.alterra;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.ArrayMap;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotoUploader {

    private static final String ACTION_UPLOAD_PAUSE = "ACTION_UPLOAD_PAUSE";
    private static final String ACTION_UPLOAD_RESUME = "ACTION_UPLOAD_RESUME";
    private static final String EXTRA_NOTIFICATION_ID = "EXTRA_NOTIFICATION_ID";

    public static class UploadControlReceiver extends BroadcastReceiver {


        public UploadControlReceiver(){
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println(context);
           /* int notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID,-1);
            UploadTask task = mUploadTasks.get(notificationId);
            if (intent.getAction() == ACTION_UPLOAD_PAUSE){
                if (task != null){
                    task.pause();
                    updateProgressNotification(notificationId,getTaskProgression(task.getSnapshot()),true);
                }
            } else if (intent.getAction() == ACTION_UPLOAD_RESUME){
                if (task != null){
                    task.resume();
                    updateProgressNotification(notificationId,getTaskProgression(task.getSnapshot()),false);
                }
            }*/
        }
    }

    private FirebaseStorage mStorage;
    private Map<Integer,UploadTask> mUploadTasks;
    private Context mContext;
    private NotificationManagerCompat mNotificationManager;
    private static int notificationId;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    /**
     * Instantiates a new PhotoUploader
     * @param bucket Firebase Cloud Storage bucket URI e.g gs://app-0123456789.appspot.com
     */
    public PhotoUploader(String bucket, Context context){
        mStorage = FirebaseStorage.getInstance(bucket);
        mUploadTasks = new ArrayMap<>();
        mContext = context;
        mNotificationManager = NotificationManagerCompat.from(mContext);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public void uploadPhoto(String path){
        Uri file = Uri.fromFile(new File(path));
        String remotePath = "images/"+file.getLastPathSegment();
        StorageReference riversRef = mStorage.getReference().child(remotePath);
        UploadTask uploadTask = riversRef.putFile(file);


        int id = showProgressNotification();

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener((exception) -> {
            //TODO Handle unsuccessful uploads
        }).addOnSuccessListener((taskSnapshot) -> {
            showSuccessNotification(id);
            updateDatabase(remotePath);
        }).addOnProgressListener((taskSnapshot) -> {
            int progress = getTaskProgression(taskSnapshot);
            System.out.println("Upload is " + progress + "% done");
            updateProgressNotification(id , progress, false);
        });


        //Save the UploadTask and the corresponding notification id
        mUploadTasks.put(id,uploadTask);
    }

    /**
     * Upload photo informations on Firestore
     * @param remotePath The path of the photo in Firebase Storage
     */
    private void updateDatabase(String remotePath){
        String userid = auth.getCurrentUser().getUid();
        HashMap<String, Object> data = new HashMap<>();
        data.put("link", remotePath);
        data.put("owner", userid);

        db.collection("photos").add(data);
    }

    private int getTaskProgression(UploadTask.TaskSnapshot taskSnapshot){
        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
        return (int) progress;
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
        // notificationId is a unique int for each notification
        int id = notificationId++;

        //Create a progress bar notification with 0% progress
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, HomeActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.alterra_logo_round)
                .setContentTitle(mContext.getString(R.string.notification_upload_inprogress))
                .setContentText("0%")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setProgress(100,0,true) //Progress is indeterminate at first
                .addAction(R.drawable.alterra_logo_round,mContext.getString(R.string.notification_upload_button_pause),getPausePendingIntent(id));


        mNotificationManager.notify(id, builder.build());

        return id;
    }

    private void updateProgressNotification(int notificationId, int progressionPercentage, boolean isPaused){
        String pauseStr;
        if (isPaused){
            pauseStr = mContext.getString(R.string.notification_upload_button_resume);
        } else {
            pauseStr = mContext.getString(R.string.notification_upload_button_pause);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, HomeActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.alterra_logo_round)
                .setContentTitle(mContext.getString(R.string.notification_upload_inprogress))
                .setContentText(progressionPercentage + "%")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setProgress(100,progressionPercentage,false)
                .addAction(R.drawable.alterra_logo_round,pauseStr,getPausePendingIntent(notificationId));

        mNotificationManager.notify(notificationId, builder.build());
    }

    private PendingIntent getPausePendingIntent(int notificationId){
        Intent pauseIntent = new Intent(mContext, PhotoUploader.UploadControlReceiver.class);
        pauseIntent.putExtra(EXTRA_NOTIFICATION_ID, notificationId);

        UploadTask task = mUploadTasks.get(notificationId);
        if (task != null){
            if (task.isPaused()){
                pauseIntent.setAction(ACTION_UPLOAD_RESUME);
            } else {
                pauseIntent.setAction(ACTION_UPLOAD_PAUSE);
            }
        }

        PendingIntent pausePendingIntent =
                PendingIntent.getBroadcast(mContext, 0, pauseIntent, 0);
        return pausePendingIntent;
    }
}
