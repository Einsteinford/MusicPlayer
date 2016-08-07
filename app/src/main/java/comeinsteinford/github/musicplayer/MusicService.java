package comeinsteinford.github.musicplayer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class MusicService extends Service {

    private String TAG = this.getClass().getSimpleName();
    private MusicBinder mMusicBinder = new MusicBinder();
    private ArrayList<Parcelable> mMusicList;
    //在OnBind之前创建了实例

    private static final String ACTION_FOO = "comeinsteinford.github.musicplayer.action.FOO";
    public static final String ACTION_BOO = "comeinsteinford.github.musicplayer.action.BOO";

    private static final String EXTRA_PARAM1 = "comeinsteinford.github.musicplayer.extra.PARAM1";


    public static final String UPDATE_MUSIC_NAME = "comeinsteinford.github.musicplayer.extra.UPDATE_MUSIC_NAME";
    public static final String UPDATE_MUSIC_SINGER = "comeinsteinford.github.musicplayer.extra.UPDATE_MUSIC_SINGER";

    private MediaPlayer mMediaPlayer;
    private String mMusicTitleOld;
    private AssetManager am;
    private MusicNotification mMusicNotification;
    private ArrayList<String> mPlaylist;
    private ArrayList<Integer> mPlaylistNum;
    private ArrayList<String> mNamelist;
    private ArrayList<String> mSingerlist;
    private int mPosition = 0;
    private BroadcastReceiver mBroadcastReceiver;
    private String mMusicName;
    private String mMusicArtist;
    private boolean Flag = true;
    private int n = 0;
    boolean CanDo = true;

    public MusicService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        am = getAssets();
        //绑定时实例化资源文件夹管理器

        mMediaPlayer = new MediaPlayer();
        //在绑定时新建一个音乐播放器
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.d(TAG, "onPrepared");
                mMediaPlayer.start();
                //准备好就自动播放
                /////////每一首新歌曲！！都在这里始发！！！！当然进度条从这里开始播放最好
                Log.i(TAG, "onPrepared: CanDo = true");
                CanDo = true;
                sendBroadcast(new Intent(MusicService.ACTION_BOO).putExtra(MainActivity.UPDATE_ICON, 8));
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d(TAG, "onCompletion");
                if (mPosition == (mPlaylistNum.size() - 1)) {
                    mPosition = 0;
                } else {
                    mPosition++;
                }
                startNewMusic(mPlaylist.get(mPosition), mPosition);
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Log.d(TAG, "onError what:" + i + ", extra:" + i1);
                return false;
            }
        });

        Log.i(TAG, "onCreate: ");

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //在点击后实例化AssetManager
        IntentManage(intent);

        //每一个点击事件都会启动服务，所以产生了通知栏
        mMusicNotification = new MusicNotification();
        mMusicNotification.showMusicNotify(this, mMusicName, mMusicArtist, Flag);

        if (Flag) {
            n = 2;
        } else {
            n = 3;
        }

        sendBroadcast(new Intent(MusicService.ACTION_BOO).putExtra(MainActivity.UPDATE_ICON, n));
        //通知你更新暂停按钮

        //在点击后实例化MusicNotification，并启动,每次都新建代替更新吧

        return START_NOT_STICKY;
        //“非粘性的”。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统不会自动重启该服务。
    }

    private void IntentManage(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {        //主fragment内部事件
                final int position = intent.getIntExtra(EXTRA_PARAM1, 0);
                mPosition = position;
                handleActionFoo(position);
            }
        }
    }


    private void handleActionFoo(int position) {

        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                if (mPlaylist.get(position).equals(mMusicTitleOld)) {
                    mMediaPlayer.pause();
                    Flag = false;
                    Log.d(TAG, "pause");
                } else {
                    startNewMusic(mPlaylist.get(position), position);
                }
            } else {
                if (mPlaylist.get(position).equals(mMusicTitleOld)) {
                    mMediaPlayer.start();
                    Log.i(TAG, "handleActionFoo: CanDo = true");
                    CanDo = true;
                    Flag = true;
                } else {
                    startNewMusic(mPlaylist.get(position), position);
                }
            }
        } else {
            Log.i(TAG, "handleActionFoo: new");
            startNewMusic(mPlaylist.get(position), position);
        }
    }

    private void startNewMusic(String musicFileName, int position) {
        Flag = true;
        try {
            mMediaPlayer.reset();
            mMusicTitleOld = musicFileName;
            AssetFileDescriptor afd = am.openFd("music/" + musicFileName);
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMusicName = ((ItemMusicList) mMusicList.get(position)).getMusicTitle();
        mMusicArtist = ((ItemMusicList) mMusicList.get(position)).getSinger();
        //TODO只有播放新音乐的时候，需要设置新名字并更新！

        sendBroadcast(new Intent(MusicService.ACTION_BOO)
                .putExtra(MainActivity.UPDATE_ICON, 1)
                .putExtra(MusicService.UPDATE_MUSIC_NAME,mNamelist.get(mPosition))
                .putExtra(MusicService.UPDATE_MUSIC_SINGER,mSingerlist.get(mPosition))
                );
    }

    public boolean MusicIsPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        mMediaPlayer.release();
        super.onDestroy();
    }

    public static String timeParse(long duration) {
        String time = "" ;

        long minute = duration / 60000 ;
        long seconds = duration % 60000 ;

        long second = Math.round((float)seconds/1000) ;

        if( minute < 10 ){
            time += "0" ;
        }
        time += minute+":" ;

        if( second < 10 ){
            time += "0" ;
        }
        time += second ;

        return time ;
    }

    class MusicBinder extends Binder {
        int progress;


        public void startMusicAction(Context context, int position) {
            Log.i(TAG, "startMusicAction: MusicNameDone");
            Log.i(TAG, "startMusicAction: CanDo =false;");
            CanDo =false;

            Intent intent = new Intent(context, MusicService.class);
            intent.setAction(ACTION_FOO);
            intent.putExtra(EXTRA_PARAM1, position);
            context.startService(intent);

        }
        public boolean CanDo(){
            return CanDo;
        }

        public void next(Context context) {
            next();
            startMusicAction(context, mPosition);
            Log.i(TAG, "next: OK");
        }

        public void previous(Context context) {
            previous();
            startMusicAction(context, mPosition);
            Log.i(TAG, "previous: OK");
        }

        public void pause(Context context) {
            startMusicAction(context, mPosition);
            Log.i(TAG, "pause/start: OK");
        }

        public boolean MusicIsPlaying() {
            return mMediaPlayer.isPlaying();
        }
//        public boolean MusicIs

        public int getProgress() {
            if (mMediaPlayer.getDuration() != 0) {
                progress = (int) (mMediaPlayer.getCurrentPosition() * 1000 / mMediaPlayer.getDuration());
            }
            //听说progressBar最大值10000,又听说不要设置成满
            return progress;
        }
        public int getCurrentPosition() {
            if (mMediaPlayer!=null){
                if (mMediaPlayer.getDuration() != 0) {
                progress = mMediaPlayer.getCurrentPosition();
                }
            }
            return progress;
        }
        public int getDuration() {
            if (mMediaPlayer!=null){
                if (mMediaPlayer.getDuration() != 0) {
                progress = mMediaPlayer.getDuration();
                }
            }
            return progress;
        }
        public void seekTo(int progress){
            if (mMediaPlayer!=null){
                mMediaPlayer.seekTo(progress);
            }
        }

        public String getMusicName() {
            return mMusicName;
        }

        public String getMusicArtist() {
            return mMusicArtist;
        }

        public String getTime(){
            if (mMediaPlayer.isPlaying()){
                return timeParse(mMediaPlayer.getDuration());
            }else {
                return "00:00";
            }
        }

        public String getCurrentTime() {
            return timeParse(mMediaPlayer.getCurrentPosition());
        }

        private void next() {       //转换为position
            if (mPosition == (mPlaylistNum.size() - 1)) {
                mPosition = 0;
            } else {
                mPosition++;
            }
        }

        private void previous() {   //转换为position
            if (mPosition == 0) {
                mPosition = (mPlaylistNum.size() - 1);
            } else {
                mPosition--;
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mMusicList = intent.getParcelableArrayListExtra(MainActivity.MUSIC_LIST);
        //从传递进来的Intent取得ArrayList<ItemMusicList>实例
        mPlaylistNum = new ArrayList<>();
        for (int i = 0; i < mMusicList.size(); i++) {
            mPlaylistNum.add(i);
        }       //给歌曲编号

        Iterator<Parcelable> inter = mMusicList.iterator();
        mPlaylist = new ArrayList<>();
        while (inter.hasNext()) {     //将mMusicList简化为文件名列表
            mPlaylist.add(((ItemMusicList) inter.next()).getMusicFileName());
        }

        Iterator<Parcelable> NameInter = mMusicList.iterator();
        mNamelist = new ArrayList<>();
        while (NameInter.hasNext()) {     //将mMusicList简化为歌曲名列表
            mNamelist.add(((ItemMusicList) NameInter.next()).getMusicTitle());
        }
        Iterator<Parcelable> ArtistInter = mMusicList.iterator();
        mSingerlist = new ArrayList<>();
        while (ArtistInter.hasNext()) {     //将mMusicList简化为歌手名列表
            mSingerlist.add(((ItemMusicList) ArtistInter.next()).getSinger());
        }

        Log.i(TAG, "onBind: " + mPlaylistNum +"  "+ mNamelist +"  "+mSingerlist);
        return mMusicBinder;
    }
}
