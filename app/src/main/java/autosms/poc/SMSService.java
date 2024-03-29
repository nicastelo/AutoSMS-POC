package autosms.poc;

import android.telephony.SmsManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class SMSService extends FirebaseMessagingService {

  private static final String TAG = "SMSService";

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {

    Log.d(TAG, "From: " + remoteMessage.getFrom());

    if (remoteMessage.getData().size() > 0) {
      Log.d(TAG, "Message data payload: " + remoteMessage.getData());

      Map message = remoteMessage.getData();

      if (message.containsKey("number")) {
        String phone = message.get("number").toString();
        String text = message.get("body").toString();

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, text, null, null);
      }
    }

    if (remoteMessage.getNotification() != null) {
      Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
    }
  }

  private void sendNotification(String messageBody) {
    Intent intent = new Intent(this, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
      PendingIntent.FLAG_ONE_SHOT);

    Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
      .setContentTitle("FCM Message")
      .setContentText(messageBody)
      .setAutoCancel(true)
      .setSound(defaultSoundUri)
      .setContentIntent(pendingIntent);

    NotificationManager notificationManager =
      (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
  }
}
