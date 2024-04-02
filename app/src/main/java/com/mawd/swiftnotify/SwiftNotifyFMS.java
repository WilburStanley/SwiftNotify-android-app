package com.mawd.swiftnotify;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mawd.swiftnotify.models.NotificationInfo;
import com.mawd.swiftnotify.models.NotificationInfoExtractor;

import java.util.Map;
import java.util.Objects;

public class SwiftNotifyFMS extends FirebaseMessagingService {

    private FirebaseAuth auth;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            sendNotification(message.getNotification().getBody(), message.getData());
            NotificationInfo notificationInfo = NotificationInfoExtractor.extractInfoFromQR(Objects.requireNonNull(message.getNotification().getBody()));
            addNotificationToDatabase(notificationInfo);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

    }

    private void sendNotification(String messageBody, Map<String, String> data) {
        int notificationId = (int) System.currentTimeMillis();
        Intent intent = new Intent(this, MainPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_IMMUTABLE);

        String channelId = "fcm_default_channel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("You got beeped (Check credentials):")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        if (data.containsKey("vibrate") && Boolean.parseBoolean(data.get("vibrate"))) {
            vibratePhone();
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());
    }

    private void vibratePhone() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                    VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE)
            );
        } else {
            if (vibrator != null) {
                long[] pattern = {0, 5000, 10, 5000};
                vibrator.vibrate(pattern, -1);
            }
        }
    }

    private void addNotificationToDatabase(NotificationInfo notification) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("notifications");
        String notificationKey = ref.push().getKey();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && notificationKey != null) {
            String uid = currentUser.getUid();
            ref.child(uid).child(notificationKey).setValue(notification);
        }
    }
}
