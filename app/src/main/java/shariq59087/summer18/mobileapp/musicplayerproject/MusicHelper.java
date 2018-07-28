package shariq59087.summer18.mobileapp.musicplayerproject;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;

import static shariq59087.summer18.mobileapp.musicplayerproject.fragmentSong.songList;

public class MusicHelper implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener
{
    public MediaPlayer mp;
    public  int songPosn ;
    private Context mContext;
    public MusicHelper(Context context)
    {
        this.mContext = context;
    }
    public void setSong(int songIndex) {
        songPosn = songIndex;
    }
    public void playSong() {
        Songs playSong = songList.get(songPosn);
        long currSong = playSong.getId();
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        try {
            mp.setDataSource(mContext, trackUri);
            mp.seekTo(0);
            mp.setLooping(true);
            mp.setVolume(0.5f,0.5f);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        mp.prepareAsync();
    }
    public void initMusicPlayer() {
        mp.setWakeMode(mContext,
                PowerManager.PARTIAL_WAKE_LOCK);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnPreparedListener(this);

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
}
