package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.musicplayer.Bean.MusicBean;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<MusicBean> musicList = new ArrayList<>();
    private MusicBean musicBean;

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
                getMusic();
                Log.d("perssion","1");
            }else {
                //不需要解释为何需要授权直接请求授权
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        } else {
            initView();
            getMusic();
        }
    }

//    这个方法利用ContentProvider扫描内存，获得歌曲信息
    private void getMusic() {
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
//                    Log.d("1"+"music",cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                }
                cursor.close();
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
    }
}