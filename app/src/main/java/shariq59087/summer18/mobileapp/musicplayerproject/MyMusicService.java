package shariq59087.summer18.mobileapp.musicplayerproject;

import android.app.Service;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

public class MyMusicService extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener,MediaPlayer.OnPreparedListener{

    private MediaPlayer player;
    private ArrayList<Songs> songs;
    private int songPosn;
    private final IBinder musicBind = new MusicBinder();
    public MyMusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        songPosn=0;
        player = new MediaPlayer();
        initMusicPlayer();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);

    }
    public void setSong(int songIndex){
        songPosn=songIndex;
    }
    public void setList(ArrayList<Songs> theSongs){
        songs=theSongs;
    }
        public void playSong(){
        player.reset();
        Songs playSong = songs.get(songPosn);
        long currSong = playSong.getId();
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return  musicBind;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.start();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mediaPlayer.setOnErrorListener(this);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.start();
    }
    public class MusicBinder extends Binder {
        MyMusicService getService() {
            return MyMusicService.this;
        }
    }
}
