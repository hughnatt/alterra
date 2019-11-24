package ca.uqac.alterra.home;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ca.uqac.alterra.R;
import ca.uqac.alterra.database.AlterraAuth;
import ca.uqac.alterra.database.AlterraCloud;
import ca.uqac.alterra.database.AlterraDatabase;
import ca.uqac.alterra.database.AlterraStorage;

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

    private Map<Integer,UploadTask> mUploadTasks;
    private Context mContext;
    private NotificationManagerCompat mNotificationManager;
    private static int notificationId;

    private AlterraStorage mStorage;
    private AlterraDatabase mDatabase;
    private AlterraAuth mAuth;

    /**
     * Instantiates a new PhotoUploader
     * @param bucket Firebase Cloud Storage bucket URI e.g gs://app-0123456789.appspot.com
     */
    public PhotoUploader(String bucket, Context context){
        mUploadTasks = new ArrayMap<>();
        mContext = context;
        mNotificationManager = NotificationManagerCompat.from(mContext);
        mStorage = AlterraCloud.getStorageInstance();
        mDatabase = AlterraCloud.getDatabaseInstance();
        mAuth = AlterraCloud.getAuthInstance();
    }

    public void uploadPhoto(String path, AlterraPoint alterraPoint){

        int id = showProgressNotification();

        mStorage.uploadPhoto(path, alterraPoint, new AlterraStorage.UploadListener() {
            @Override
            public void onSuccess(String downloadLink) {
                showSuccessNotification(id);
                mDatabase.addPhoto(mAuth.getCurrentUser().getUID(),alterraPoint.getId(),downloadLink,System.currentTimeMillis(),null);
            }

            @Override
            public void onProgress(int progressPercentage) {
                updateProgressNotification(id , progressPercentage, false);
            }

            @Override
            public void onFailure(Exception e) {
                mNotificationManager.cancel(id);
                Log.d("WARNING","Upload failed");
            }
        });

        //Save the UploadTask and the corresponding notification id
        //mUploadTasks.put(id,uploadTask);
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
                .setSmallIcon(R.drawable.ic_alterra_notif)
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
                .setSmallIcon(R.drawable.ic_alterra_notif)
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
                .setSmallIcon(R.drawable.ic_alterra_notif)
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
