package es.jormagar.myBooks.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import es.jormagar.myBooks.BookDetailActivity;
import es.jormagar.myBooks.BookHelper;
import es.jormagar.myBooks.BookListActivity;
import es.jormagar.myBooks.R;

public class MessagingService extends FirebaseMessagingService {

    private static String TAG = MessagingService.class.getSimpleName();
    private final String NOTIFICATION_ID_KEY = "notification_id";
    private final String BOOK_TITLE_KEY = "book_title";

    private long[] VIBRATE_PATTERN = {0, 500};

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage notification) {

        String title = "";
        String body = "";
        String book_title = null;

        int notificationId = NotificationID.getID();

        if (notification.getNotification() != null) {
            title = notification.getNotification().getTitle();
            body = notification.getNotification().getBody();
        }

        if (notification.getData() != null) {
            book_title = notification.getData().get(BOOK_TITLE_KEY);
        }

        Intent deleteIntent = new Intent(this, BookListActivity.class);
        deleteIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        deleteIntent.setAction(Intent.ACTION_DELETE);

        deleteIntent.putExtra(NOTIFICATION_ID_KEY, notificationId);
        deleteIntent.putExtra(BOOK_TITLE_KEY, book_title);

        PendingIntent pendingDeleteIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), deleteIntent, PendingIntent.FLAG_ONE_SHOT);

        Intent detailIntent;

        //Si es una tablet
        if (getResources().getBoolean(R.bool.isLarge)) {
            detailIntent = new Intent(this, BookListActivity.class);
        } else {
            detailIntent = new Intent(this, BookDetailActivity.class);
        }

        detailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        detailIntent.setAction(Intent.ACTION_VIEW);

        detailIntent.putExtra(NOTIFICATION_ID_KEY, notificationId);
        detailIntent.putExtra(BOOK_TITLE_KEY, book_title);

        PendingIntent pendingDetailIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), detailIntent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.notification);

        Bundle channel = getChannelConfig(book_title);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channel.getString("id"))
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setSmallIcon(R.drawable.outline_book_24)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                        .addAction(new NotificationCompat.Action(R.drawable.outline_delete_24, getString(R.string.push_delete_action), pendingDeleteIntent))
                        .addAction(new NotificationCompat.Action(R.drawable.outline_book_24, getString(R.string.push_view_action), pendingDetailIntent));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationBuilder.setVibrate(VIBRATE_PATTERN);
        notificationBuilder.setLights(channel.getInt("color"), 2000, 2000);
        notificationBuilder.setSound(defaultSoundUri);


        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    private Bundle getChannelConfig(String title) {
        Bundle channel = new Bundle();

        if (BookHelper.isEven(title)) {
            Log.d(TAG, "BOOK EVEN");
            channel.putString("id", getString(R.string.notification_channel_id_even));
            channel.putInt("color", Color.BLUE);
        } else {
            Log.d(TAG, "BOOK ODD");
            channel.putString("id", getString(R.string.notification_channel_id_odd));
            channel.putInt("color", Color.GREEN);
        }

        return channel;
    }
}
