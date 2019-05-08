package es.jormagar.myBooks;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class SubApplication extends Application {
    private final String TAG = SubApplication.class.getSimpleName();

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     *
     * <p>Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.</p>
     *
     * <p>If you override this method, be sure to call {@code super.onCreate()}.</p>
     *
     * <p class="note">Be aware that direct boot may also affect callback order on
     * Android {@link Build.VERSION_CODES#N} and later devices.
     * Until the user unlocks the device, only direct boot aware components are
     * allowed to run. You should consider that all direct boot unaware
     * components, including such {@link ContentProvider}, are
     * disabled until user unlock happens, especially when component callback
     * order matters.</p>
     */
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Bundle channelOdd = new Bundle();

            channelOdd.putString("id", getString(R.string.notification_channel_id_odd));
            channelOdd.putString("name", getString(R.string.notification_channel_name_odd));
            channelOdd.putString("description", getString(R.string.notification_channel_description_odd));
            channelOdd.putInt("sound", R.raw.notification);
            channelOdd.putInt("color", Color.GREEN);

            Bundle channelEven = new Bundle();

            channelEven.putString("id", getString(R.string.notification_channel_id_even));
            channelEven.putString("name", getString(R.string.notification_channel_name_even));
            channelEven.putString("description", getString(R.string.notification_channel_description_even));
            channelEven.putInt("sound", R.raw.notification2);
            channelEven.putInt("color", Color.BLUE);

            notificationManager.createNotificationChannel(createChannel(channelOdd));
            notificationManager.createNotificationChannel(createChannel(channelEven));
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private NotificationChannel createChannel(Bundle data) {
        long[] VIBRATE_PATTERN = {0, 500};

        Uri defaultSoundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + data.getInt("sound"));
        String channelId = data.getString("id");

        NotificationChannel channel = new NotificationChannel(channelId,
                data.getString("name"),
                NotificationManager.IMPORTANCE_DEFAULT);

        channel.setDescription(data.getString("description"));

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();

        channel.setSound(defaultSoundUri, audioAttributes);

        channel.enableLights(true);
        channel.setLightColor(data.getInt("color"));

        channel.enableVibration(true);
        channel.setVibrationPattern(VIBRATE_PATTERN);

        return channel;
    }
}
