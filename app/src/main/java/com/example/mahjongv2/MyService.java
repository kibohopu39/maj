package com.example.mahjongv2;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
    private final Binder mbinder=new LocalBinder();

    public class LocalBinder extends Binder{
        //繫結器
        MyService getService(){
            return MyService.this;
        }
    }
    public MyService() {
    }
    private MediaPlayer mediaPlayer;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer=MediaPlayer.create(this,R.raw.sleep_away);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

    }

    //消音按鈕用
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String act=intent.getStringExtra("ACTION");
        if (act.equals("start")){
            mediaPlayer.start();}
        else if (act.equals("pause")){
            mediaPlayer.pause();
        }
//        mediaPlayer.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if(mediaPlayer!=null) {
            if(mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }
        mediaPlayer.release();
        Log.v("wei","destroy");
        super.onDestroy();

    }

    public void playBackMusic(){
        mediaPlayer.start();
    }
}
