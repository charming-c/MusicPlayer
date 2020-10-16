package com.example.musicplayer.Service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.musicplayer.R;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class MusicService extends Service {
    private NotificationManager notifyManager;
    private MediaPlayer mplayer;
    private String[] musicBean;
    public MyBinder binder = new MyBinder();

    public MusicService() {
        mplayer=new MediaPlayer();
        try {
//            Log.d("music","path"+musicList.get(0).getPath());
            Log.d("1","2");
            mplayer.setDataSource("/storage/emulated/0/qqmusic/song/陈韵若陈每文 - 爱的回归线 [mqms2].mp3");
            mplayer.prepare();

        } catch (IOException e) {
            e.printStackTrace();
//            Log.d("music","path"+String.valueOf(musicBean));
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void PlayOrPause(){
        if(mplayer.isPlaying()){
            mplayer.pause();
        }
        else mplayer.start();
    }
        // Service 创建方法
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "----onCreate----");
    }


    // Service  启动方法
    //这里进行service启动后所进行的操作
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        musicBean = intent.getStringArrayExtra("songs");
        return super.onStartCommand(intent, flags, startId);
    }


//    public class MyBinder extends Binder {
//
//        public MusicService getService() {
//
//            return MusicService.this;
//
//        }
//    }

    // Service  销毁方法
    @Override
    public void onDestroy() {
        Log.i(TAG, "----onDestroy----");
        notifyManager.cancelAll();
        super.onDestroy();
    }

    public class MyBinder {
        public MusicService getService(){
            return MusicService.this;
        }
    }
}
