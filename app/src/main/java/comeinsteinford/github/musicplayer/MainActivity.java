package comeinsteinford.github.musicplayer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String MUSIC_LIST = "MusicList";
    private String TAG = "MainActivity";
    private MusicService.MusicBinder mMusicBinder;
    private ArrayList<ItemMusicList> mMusicList;
    private ImageButton mPlayNext;
    private ImageButton mPlayPrevious;
    private ImageButton mPlayPause;

    private TextView mMusicPlayingTitle;
    private TextView mCustomSongName;
    private TextView mWidgetSongName;
    private TextView mMusicPlayingArtist;
    private TextView mMusicWidgetArtist;
    private TextView mTimelineAll;

    private SeekBar mTimelineSeekBar;

    private boolean isPlaying = true;

    public static final String UPDATE_ICON = "comeinsteinford.github.musicplayer.UPDATA_NAME_AND_ARTIST";
    public static final String HANDLER = "comeinsteinford.github.musicplayer.HANDLER";


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMusicBinder = (MusicService.MusicBinder) service;
            //TODO
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private Intent mBindIntent;
    private BroadcastReceiver mNameReceiver;
    private AsyncTask<Void, Integer, Void> update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        update = new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Log.i(TAG, "doInBackground: DOINGGGGGGGGGGG");
                try {
                    Thread.sleep(1000);
                    if (mMusicBinder.MusicIsPlaying()){
                        publishProgress(mMusicBinder.getProgress());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "doInBackground: ENDDDDDDDDDDDD");
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                if (isPlaying) {
                    mTimelineSeekBar.setProgress(values[0]);
                }
            }
        };

        FragmentMainListView fragmentMainListView =
                (FragmentMainListView) getFragmentManager().findFragmentById(R.id.fragment_main_list_view);
        //取得FragmentMainListView实例
        mMusicList = fragmentMainListView.getList();
        //取得播放列表

        mBindIntent = new Intent(this, MusicService.class);
        mBindIntent.putExtra(MUSIC_LIST, mMusicList);
        //将播放列表信息通过绑定传递到服务

        bindService(mBindIntent, mServiceConnection, BIND_AUTO_CREATE);
        //创建主活动的时候，即绑定服务，此时，服务执行了onCreate,
        // 然后服务通过onBind传递IBinder对象到上方的onServiceConnected中

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_origin);

        setSupportActionBar(toolbar);
        //将toolbar设置为ActionBar,确保toolbar已经布置在layout中，切不为空

        mPlayNext = (ImageButton) findViewById(R.id.play_next);
        mPlayPrevious = (ImageButton) findViewById(R.id.play_previous);
        mPlayPause = (ImageButton) findViewById(R.id.play_pause);
        mMusicPlayingTitle = (TextView) findViewById(R.id.music_playing_title);
        mCustomSongName = (TextView) findViewById(R.id.tv_custom_song_name);
        mWidgetSongName = (TextView) findViewById(R.id.tv_widget_song_name);
        mMusicPlayingArtist = (TextView) findViewById(R.id.tv_custom_song_singer);
        mMusicWidgetArtist = (TextView) findViewById(R.id.tv_widget_song_singer);
        mTimelineSeekBar = (SeekBar) findViewById(R.id.timeline_seek_bar);
        mTimelineAll = (TextView) findViewById(R.id.timeline_all);

        mPlayNext.setOnClickListener(this);
        mPlayPrevious.setOnClickListener(this);
        mPlayPause.setOnClickListener(this);

        mTimelineSeekBar.setMax(999);
        mTimelineSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mMusicBinder.MusicIsPlaying()) {
                    isPlaying = false;
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mMusicBinder.MusicIsPlaying()) {
                    mMusicBinder.seekTo(seekBar.getProgress());
                    handleAsyncTask();
                }
//                else {
//                    seekBar.setProgress(0);
//                }
                isPlaying = true;
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicService.ACTION_BOO);
        mNameReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getIntExtra(UPDATE_ICON, 0)) {
                    case 1:
                        setMusicName(mMusicBinder.getMusicName());
                        mTimelineAll.setText(mMusicBinder.getTime());
                        isPlaying = true;
                        mTimelineSeekBar.setProgress(0);
                        break;
                    case 2:
                        mPlayPause.setImageResource(R.drawable.uamp_ic_pause_white_48dp);
                        isPlaying = true;
                        Log.i(TAG, "onReceive: 2");
                        handleAsyncTask();  //播放中
                        break;
                    case 3:
                        mPlayPause.setImageResource(R.drawable.uamp_ic_play_arrow_white_48dp);
                        isPlaying = false;
                        handleAsyncTask();  //暂停中、停止
                        Log.i(TAG, "onReceive: 3");
                        break;
                    case 4:
                        setPauseMusic();    //模拟点击主界面上的播放按钮
//                        handleAsyncTask();
                        break;
                    case 8:
                        handleAsyncTask();//产生新歌
                        break;
                    default:
                        break;
                }
            }
        };
        registerReceiver(mNameReceiver, intentFilter);

        Log.i(TAG, "onCreate: ");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //关联菜单项
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void setMusicPosition(int position) {
        //点击ListView时触发此处！开启音乐服务器
        if (update.getStatus() == AsyncTask.Status.PENDING) {
            update.execute();//打开进度条的异步处理线程
        }
        Log.i(TAG, "setMusicPosition: " + update.getStatus());
        mMusicBinder.startMusicAction(MainActivity.this, position);
    }

    public void setNextMusic() { //开启音乐服务器
        mMusicBinder.next(MainActivity.this);
    }

    public void setPreviousMusic() { //开启音乐服务器
        mMusicBinder.previous(MainActivity.this);
    }

    public void setPauseMusic() { //开启音乐服务器
        mMusicBinder.pause(MainActivity.this);
    }

    @Override
    public void onBackPressed() {       //覆写后退按钮，变为Home的功能
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        update.cancel(false);
        unregisterReceiver(mNameReceiver);
        stopService(mBindIntent);
        unbindService(mServiceConnection);

        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }

    public static String getTimeFromInt(int time) {
        if (time <= 0) {
            return "0:00";
        }
        int secondnd = (time / 1000) / 60;
        int million = (time / 1000) % 60;
        String f = String.valueOf(secondnd);
        String m = million >= 10 ? String.valueOf(million) : "0" + String.valueOf(million);
        return f + ":" + m;
    }

    public void setMusicName(String name) {
        mMusicPlayingTitle.setText(name);
    }

    public void handleAsyncTask() {
        Log.i(TAG, "handleAsyncTask: " + update.getStatus());
        if (update.getStatus() == AsyncTask.Status.PENDING) {

            //TODO应该有个回调函数，在音乐开始播放后才运行这条语句
            update.execute();//打开进度条的异步处理线程
        } else if (update.getStatus() == AsyncTask.Status.FINISHED) {
            Log.i(TAG, "handleAsyncTask: NEWWWWWWWWWWWWWWWWWWWWWWWWWW");
            update = new AsyncTask<Void, Integer, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    Log.i(TAG, "doInBackground: DOINGGGGGGGGGGG");
                    while (mMusicBinder.CanDo()) {
                        try {
                            Thread.sleep(1000);
                            if (mMusicBinder.MusicIsPlaying()){
                                publishProgress(mMusicBinder.getProgress());
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.i(TAG, "doInBackground: ENDDDDDDDDDDDD");
                    return null;
                }

                @Override
                protected void onProgressUpdate(Integer... values) {
                    if (isPlaying) {
                        mTimelineSeekBar.setProgress(values[0]);
                    }
                }
            }.execute();
        }
    }

    @Override
    public void onClick(View view) {
        if (update.getStatus() == AsyncTask.Status.PENDING) {
            update.execute();//打开进度条的异步处理线程
        }
//                Log.i(TAG, "onClick: " + mMusicBinder.getProgress()+"‰");
        switch (view.getId()) {
            case R.id.play_next:
                setNextMusic();
                Log.i(TAG, "onClick: play_next");
                break;
            case R.id.play_previous:
                setPreviousMusic();
                Log.i(TAG, "onClick: play_previous");
                break;
            case R.id.play_pause:
                setPauseMusic();
                Log.i(TAG, "onClick: pause_start");
                break;
        }
    }

}
