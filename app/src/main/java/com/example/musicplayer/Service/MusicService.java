package com.example.musicplayer.Service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.musicplayer.Bean.MusicBean;
import com.example.musicplayer.MainActivity;
import com.example.musicplayer.R;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MusicService extends Service {
    private NotificationManager notifyManager;

    private int position = 1;
    private MediaPlayer mplayer;
    private MusicBean musicBean;
    public MyBinder binder = new MyBinder();
    private ArrayList<MusicBean> musicList;

    public MusicService() {
        mplayer=new MediaPlayer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

        // Service 创建方法
    @Override
    public void onCreate() {
        super.onCreate();
        this.musicList= MainActivity.musicList;
        Log.i(TAG, "----onCreate----");
        try {
//            Log.d("music","path"+musicList.get(0).getPath());
            Log.d("1","2");
            mplayer.setDataSource(musicList.get(position).getPath());
            mplayer.prepare();

        } catch (IOException e) {
            e.printStackTrace();
//            Log.d("music","path"+String.valueOf(musicBean));
        }
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
        if(mplayer.isPlaying()){
            mplayer.stop();
        }
        mplayer.release();
    }

    public class MyBinder extends Binder {
        public int getPosition() {
            return position;
        }

        public MusicService getService(){
            return MusicService.this;
        }
        public void PlayOrPause(){
            if(mplayer.isPlaying()){
                mplayer.pause();
            }
            else mplayer.start();
        }
        public void playNext() throws IOException {
            mplayer.pause();
            mplayer=new MediaPlayer();
            position++;
            mplayer.setDataSource(musicList.get(position).getPath());
            Log.d("music","posotion "+position+musicList.get(position).getTitle());
            mplayer.prepare();
            mplayer.start();
        }
        public void playPrev() throws IOException {
            if(position == 1) {
                Toast.makeText(getApplicationContext(),"这是第一首歌了！",Toast.LENGTH_SHORT).show();
                return;
            }
            mplayer.pause();
            mplayer=new MediaPlayer();
            position--;
            mplayer.setDataSource(musicList.get(position).getPath());
            Log.d("music","posotion "+position+musicList.get(position).getTitle());
            mplayer.prepare();
            mplayer.start();
        }
    }
}
