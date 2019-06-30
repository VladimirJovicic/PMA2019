package com.example.donesiklon;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static RemoteMessage staticRemoteMessage;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        staticRemoteMessage = remoteMessage;

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run()
            {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                .setContentTitle(staticRemoteMessage.getNotification().getTitle())
                                .setContentText(staticRemoteMessage.getNotification().getBody());


                NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                nm.notify(1, mBuilder.build());

                //Toast.makeText(getApplicationContext(), staticRemoteMessage.getNotification().getTitle() + " \n" + staticRemoteMessage.getNotification().getBody(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.i("onNewToken", s);
        FirebaseMessaging.getInstance().subscribeToTopic("newRestaurants")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "";
                        if (!task.isSuccessful()) {
                            msg = "Token failed to subscribe to a topic";
                        }else{
                            msg = "Token succesfully subscribed to a topic";
                        }
                        Log.i("subscription to a topic", msg);
                    }
                });
    }
}
