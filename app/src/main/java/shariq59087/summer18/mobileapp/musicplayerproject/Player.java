package shariq59087.summer18.mobileapp.musicplayerproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import static shariq59087.summer18.mobileapp.musicplayerproject.fragmentSong.songList;

public class Player extends Fragment implements  MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener
    ,MediaPlayer.OnCompletionListener{

    private ImageView songImage;
    private TextView songTitle,elapsedtime,remainingtime;
    public static SeekBar seekvol;
    private        SeekBar seekposition;
    private Button playBtn, forward,backward;

   private static MediaPlayer mp;

    int totalTime;
    private int songPosn;
    private String  title;
    private View view;
    public  int val;
    private AudioManager audioManager = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_player, container, false);
        mp = getInstance();
        mp.seekTo(0);
        mp.reset();
        initMusicPlayer();
        songPosn = 0;
        final int value = Integer.valueOf(getArguments().getString("id"));
        val = value;
        songImage = (ImageView) view.findViewById(R.id.songthumbnail);
        songTitle = (TextView) view.findViewById(R.id.songname);
        audioManager = (AudioManager)getActivity().getSystemService(getContext().AUDIO_SERVICE);
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        remainingtime = (TextView) view.findViewById(R.id.remainingtime);
        elapsedtime  = (TextView) view.findViewById(R.id.elapsedtime);
        seekposition = (SeekBar) view.findViewById(R.id.positionbar);
        seekposition.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mp.seekTo(progress);
                    seekposition.setProgress(progress/1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekvol = (SeekBar) view.findViewById(R.id.volumebar);
        audioManager = (AudioManager) getActivity().getSystemService(getContext().AUDIO_SERVICE);
        seekvol.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekvol.setProgress(audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));
        seekvol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        i, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        playBtn = (Button)view.findViewById(R.id.plyBtn);
        forward = (Button)view.findViewById(R.id.forward);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songList.size()-1 > val) {
                    val = val+1;
                    setSong(val);
                    playSong();

                }
            }
        });
        backward = (Button)view.findViewById(R.id.backward);
        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (val >= 0)
                {
                    val = val-1;
                    setSong(val);
                    playSong();
                }
            }
        });
        if(mp!=null){
            if(mp.isPlaying()){
                mp.stop();
                playBtn.setBackgroundResource(R.drawable.pauseline);
            }
        }
        setSong(value);
            playSong();
        playBtn.setBackgroundResource(R.drawable.pauseline);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mp.isPlaying())
                {
                    mp.start();
                    playBtn.setBackgroundResource(R.drawable.pauseline);
                }
                else {
                    mp.pause();
                    playBtn.setBackgroundResource(R.drawable.play);
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mp != null)
                {
                    try {
                        Message msg = new Message();
                        msg.what = mp.getCurrentPosition();
                        handler.sendMessage(msg );

                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e)
                    {

                    }
                }
            }
        }).start();

return  view;
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
           int currentposition = msg.what;
           seekposition.setProgress(currentposition);
           String elapsedTime = createTimeLabel(currentposition);
           elapsedtime.setText(elapsedTime);
           String remainingTime = createTimeLabel(totalTime - currentposition);
           remainingtime.setText(remainingTime);

        }
    };




    public String createTimeLabel(int currentpostion) {
        String timelabel = "";
        int min = currentpostion/1000/60;
        int sec = currentpostion /1000 % 60;
        timelabel = min + " :";
        if (    sec <10 )timelabel += "0";
        timelabel +=sec;
    return timelabel;
    }

    public void setSong(int songIndex) {
        songPosn = songIndex;
  }
    public void playSong() {
            mp.reset();
        Songs playSong = songList.get(songPosn);
//get id
        long currSong = playSong.getId();
//set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        try{
            mp.setDataSource(getContext(), trackUri);

        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
            mp.prepareAsync();
        totalTime = mp.getDuration();
        seekposition.setProgress(1);
        seekposition.setMax(totalTime);
    }

    public void initMusicPlayer() {
        mp.setWakeMode(getActivity().getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnPreparedListener(this);

    }



    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mp.setOnCompletionListener(this);
        mp.start();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mp.setOnErrorListener(this);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mp.setOnPreparedListener(this);
        mp.start();
    }

    public static   MediaPlayer getInstance()
    {
        if (mp == null  )
        {
            mp = new MediaPlayer();
        }
        return mp;
    }
}
