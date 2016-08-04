package comeinsteinford.github.musicplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

/**
 * Helper class for showing and canceling music
 * notifications.
 * <p/>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class MusicNotification {
    /**
     * The unique identifier for this type of notification.
     */
    public static final int NOTIFICATION_ID = 1;


    public void showMusicNotify(MusicService musicService,String MusicName,String Artist,boolean b) {
        NotificationManager mNotificationManager = (NotificationManager) musicService.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder mBuilder = new Notification.Builder(musicService);
        RemoteViews mRemoteViews = new RemoteViews(musicService.getPackageName(), R.layout.notification_music);

        mRemoteViews.setImageViewResource(R.id.custom_song_icon, R.drawable.ic_launcher);
        mRemoteViews.setTextViewText(R.id.tv_custom_song_singer, Artist);
        mRemoteViews.setTextViewText(R.id.tv_custom_song_name,MusicName);

        if (b) {
            mRemoteViews.setImageViewResource(R.id.btn_custom_play, R.drawable.uamp_ic_pause_white_48dp);
        } else {
            mRemoteViews.setImageViewResource(R.id.btn_custom_play, R.drawable.uamp_ic_play_arrow_white_48dp);
        }
        //点击的事件处理
        Intent buttonIntent = new Intent(MusicService.ACTION_BOO);
//        /* 上一首按钮 */
//        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PREV_ID);
//        //这里加了广播，所及INTENT的必须用getBroadcast方法
//        PendingIntent intent_prev = PendingIntent.getBroadcast(this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_prev, intent_prev);
        /* 播放/暂停  按钮 */
        buttonIntent.putExtra(MainActivity.UPDATE_ICON,4);
        PendingIntent intent_play = PendingIntent.getBroadcast(musicService, 5, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_play, intent_play);
//        /* 下一首 按钮  */
//        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_NEXT_ID);
//        PendingIntent intent_next = PendingIntent.getBroadcast(this, 3, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_next, intent_next);

        Intent notificationIntent = new Intent(musicService, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(musicService, 0, notificationIntent, 0);
        mBuilder.setContent(mRemoteViews)
                .setContentTitle("Music Title")
                .setContentText("Singer")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification)
                .setTicker("Music Start")
                .setContentIntent(pendingIntent)
                .setOngoing(true);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        musicService.startForeground(NOTIFICATION_ID, notification);

        //创建前台服务，在下拉菜单显
    }
}
