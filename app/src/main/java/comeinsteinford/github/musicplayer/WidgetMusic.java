package comeinsteinford.github.musicplayer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.Iterator;

public class WidgetMusic extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            switch (action) {
                case "android.appwidget.action.APPWIDGET_UPDATE":
                    super.onReceive(context, intent);
                    break;
                case "comeinsteinford.github.musicplayer.action.BOO":
                    Log.i("WidgetMusic", "onReceive: BOO");
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    switch (intent.getIntExtra(MainActivity.UPDATE_ICON, 0)) {
                        case 1:
                            appWidgetManager.updateAppWidget(new ComponentName(context, WidgetMusic.class),
                                    getUpdateName(context,
                                            intent.getStringExtra(MusicService.UPDATE_MUSIC_SINGER),
                                            intent.getStringExtra(MusicService.UPDATE_MUSIC_NAME)));
                            Log.i("WidgetMusic", "onReceive: 1:");
                            break;
                        case 2:
                            appWidgetManager.updateAppWidget(new ComponentName(context, WidgetMusic.class),
                                    getUpdateIcon(context, true));
                            Log.i("WidgetMusic", "onReceive: 2:");
                            break;
                        case 3:
                            getUpdateIcon(context, false);
                            appWidgetManager.updateAppWidget(new ComponentName(context, WidgetMusic.class),
                                    getUpdateIcon(context, false));
                            Log.i("WidgetMusic", "onReceive: 3:");
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        appWidgetManager.updateAppWidget(appWidgetIds, getWidgetAttribute(context, true));
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews getWidgetAttribute(Context context, boolean b) {
        RemoteViews mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_music);

        mRemoteViews.setImageViewResource(R.id.widget_song_icon, R.drawable.ic_launcher);

        if (b) {
            mRemoteViews.setImageViewResource(R.id.btn_widget_play, R.drawable.uamp_ic_pause_white_48dp);
        } else {
            mRemoteViews.setImageViewResource(R.id.btn_widget_play, R.drawable.uamp_ic_play_arrow_white_48dp);
        }
        //点击的事件处理
        Intent buttonIntent = new Intent(MusicService.ACTION_BOO);//添加Action
        /* 播放/暂停  按钮 */
        buttonIntent.putExtra(MainActivity.UPDATE_ICON, 4);
        PendingIntent intent_play = PendingIntent.getBroadcast(context, 6, buttonIntent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.btn_widget_play, intent_play);

        return mRemoteViews;
    }

    private RemoteViews getUpdateIcon(Context context, boolean b) {
        RemoteViews mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_music);

        mRemoteViews.setImageViewResource(R.id.widget_song_icon, R.drawable.ic_launcher);

        if (b) {
            mRemoteViews.setImageViewResource(R.id.btn_widget_play, R.drawable.uamp_ic_pause_white_48dp);
        } else {
            mRemoteViews.setImageViewResource(R.id.btn_widget_play, R.drawable.uamp_ic_play_arrow_white_48dp);
        }
        //点击的事件处理
        Intent buttonIntent = new Intent(MusicService.ACTION_BOO);//添加Action
        /* 播放/暂停  按钮 */
        buttonIntent.putExtra(MainActivity.UPDATE_ICON, 4);
        PendingIntent intent_play = PendingIntent.getBroadcast(context, 6, buttonIntent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.btn_widget_play, intent_play);

        return mRemoteViews;
    }

    private RemoteViews getUpdateName(Context context, String name ,String singer) {
        RemoteViews mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_music);
        mRemoteViews.setImageViewResource(R.id.widget_song_icon, R.drawable.ic_launcher);

        mRemoteViews.setTextViewText(R.id.tv_widget_song_singer, name);
        mRemoteViews.setTextViewText(R.id.tv_widget_song_name, singer);

        mRemoteViews.setImageViewResource(R.id.btn_widget_play, R.drawable.uamp_ic_pause_white_48dp);

        //点击的事件处理
        Intent buttonIntent = new Intent(MusicService.ACTION_BOO);//添加Action
        /* 播放/暂停  按钮 */
        buttonIntent.putExtra(MainActivity.UPDATE_ICON, 4);
        PendingIntent intent_play = PendingIntent.getBroadcast(context, 6, buttonIntent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.btn_widget_play, intent_play);

        return mRemoteViews;
    }
}

