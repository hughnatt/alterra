package ca.uqac.alterra.home;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Objects;

import ca.uqac.alterra.R;
import ca.uqac.alterra.database.AlterraAuth;
import ca.uqac.alterra.database.AlterraCloud;
import ca.uqac.alterra.database.AlterraDatabase;
import ca.uqac.alterra.database.AlterraStorage;

public class PhotoUploader {

    private static final String CHANNEL_ID = "ca.uqac.alterra.uploadnotifications";


    private Context mContext;
    private NotificationManagerCompat mNotificationManager;
    private static int notificationId;

    private AlterraStorage mStorage;
    private AlterraDatabase mDatabase;
    private AlterraAuth mAuth;

    /**
     * Instantiates a new PhotoUploader
     * @param context Current context of the calling activity
     */
    public PhotoUploader(Context context){
        mContext = context;
        mNotificationManager = NotificationManagerCompat.from(mContext);
        mStorage = AlterraCloud.getStorageInstance();
        mDatabase = AlterraCloud.getDatabaseInstance();
        mAuth = AlterraCloud.getAuthInstance();
        //Notification setup
        createNotificationChannel(context);
    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.notification_channel_upload_title);
            String description = context.getString(R.string.notification_channel_upload_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
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
                updateProgressNotification(id , progressPercentage);
            }

            @Override
            public void onFailure(Exception e) {
                mNotificationManager.cancel(id);
                Log.d("WARNING","Upload failed");
            }
        });
    }

    /**
     * @param progressNotificationId The id of the progress notification related to this success
     * @return the id of the notification
     */
    private int showSuccessNotification(int progressNotificationId){

        //Stop showing the progress bar notification
        mNotificationManager.cancel(progressNotificationId);

        //Create a new notification for success
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alterra_notif)
                .setContentTitle(mContext.getString(R.string.notification_upload_success))
                .setContentText("")
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),AudioManager.STREAM_NOTIFICATION)
                .setPriority(NotificationCompat.PRIORITY_HIGH);


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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alterra_notif)
                .setContentTitle(mContext.getString(R.string.notification_upload_inprogress))
                .setContentText("0%")
                .setOnlyAlertOnce(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),AudioManager.STREAM_NOTIFICATION)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setProgress(100,0,true); //Progress is indeterminate at first

        mNotificationManager.notify(id, builder.build());

        return id;
    }

    private void updateProgressNotification(int notificationId, int progressionPercentage){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alterra_notif)
                .setContentTitle(mContext.getString(R.string.notification_upload_inprogress))
                .setOnlyAlertOnce(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),AudioManager.STREAM_NOTIFICATION)
                .setContentText(progressionPercentage + "%")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setProgress(100,progressionPercentage,false);

        mNotificationManager.notify(notificationId, builder.build());
    }
}
