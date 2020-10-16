package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer.Bean.MusicBean;
import com.example.musicplayer.Service.MusicService;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<MusicBean> musicList = new ArrayList<>();
    private MusicBean musicBean;
    private TextView endTime;
    private TextView nowTime;
    private SeekBar seekBar;
    private TextView musicTitle;
    private TextView musicAuthor;
    private Button play;
    private Button next;
    private Button last;
    private static final int SCAN_OK=1;

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                //扫描完毕,关闭进度dialog
                mProgressDialog.dismiss();
//                MainActivity.this.notify();
                mProgressDialog.hide();
//                adapter.notifyDataSetChanged();
            }
        }
    };
    private ProgressDialog mProgressDialog;
    private MusicService.MyBinder myBinder;
    private MusicService musicService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            musicService = ((MusicService.MyBinder)iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("service ","fail");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        获得读取权限
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                //弹窗解释为何需要该权限，再次请求权限
                Toast.makeText(MainActivity.this, "请授权！", Toast.LENGTH_LONG).show();
                //跳转到应用设置界面
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                initView();
                Log.d("perssion","1");
            }else {
                //不需要解释为何需要授权直接请求授权
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        } else {
            initView();
        }
        
    }

//    这个方法利用ContentProvider扫描内存，获得歌曲信息
    private void getMusic() {
        mProgressDialog = ProgressDialog.show(this ,null,"loading...");
//        开启新线程扫描
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                Cursor cursor = getApplicationContext().getContentResolver().query(musicUri,
                        null,null,null,null);
                if(cursor == null) {
//                    Log.d("1","2");
                    return;
                }
//                cursor.moveToFirst();
                while (cursor.moveToNext()){
                    musicBean = new MusicBean();
                    musicBean.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                    musicBean.setAlbumBip(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
                    musicBean.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                    musicBean.setLength(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                    musicBean.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                    musicList.add(musicBean);
//                    Log.d("1"+"music",cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                }

                handler.sendEmptyMessage(SCAN_OK);
                cursor.close();
                bindServiceConnection();
                Log.d("path",""+musicList.get(1).getPath());
            }
        }).start();

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //授权成功
                    initView();
                } else {
                    //授权失败
                    Toast.makeText(this, "授权失败！", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

//    这个方法用于更新视图
    private void initView() {
//        getMusic();
        endTime=findViewById(R.id.EndTime);
        nowTime=findViewById(R.id.NowTime);
        seekBar=findViewById(R.id.line);
        musicTitle=findViewById(R.id.Music_name);
        musicAuthor=findViewById(R.id.Author);
        
        play=findViewById(R.id.Stop);
        next=findViewById(R.id.Next);
        last=findViewById(R.id.Last);

//        播放、暂停的监听器
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(play.isSelected() == true){
                    play.setSelected(false);
                    if(musicService != null)
                        musicService.PlayOrPause();
                    Log.d("0","10");
                }
                else{
                    play.setSelected(true);
                    if(musicService != null)
                        musicService.PlayOrPause();
                }
            }
        });

//        下一首的监听器
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

//        上一首的监听器
        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        if(musicList.size()==0){
            getMusic();
            Log.d("1","2"+musicList.size());
        }
        else
                    Log.d("music","path"+musicList.get(0).getPath());

    }

    private void bindServiceConnection(){
        Intent intent = new Intent(MainActivity.this,MusicService.class);
        startService(intent);
        bindService(intent,serviceConnection,this.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent  mBindIntent = new Intent(MainActivity.this, MusicService.class);
        MainActivity.this.stopService(mBindIntent);
//        if(mplayer.isPlaying()){
//            mplayer.stop();
//        }
//        mplayer.release();
    }
}
