package shariq59087.summer18.mobileapp.musicplayerproject;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static shariq59087.summer18.mobileapp.musicplayerproject.PlayerMain.f1;
import static shariq59087.summer18.mobileapp.musicplayerproject.PlayerMain.tabLayout;
import static shariq59087.summer18.mobileapp.musicplayerproject.PlayerMain.viewPager;

public class fragmentSong extends Fragment implements FragmentChangeListner,MediaPlayer.OnPreparedListener{
    public static ArrayList<Songs> songList;

    private ListView songView;
    private  int songPosn;
    private MyMusicService musicSrv;
    private MediaPlayer player;
    private boolean musicBound = false;
    View view;
    private Context mContext;

    public fragmentSong() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fragment_song, container, false);
        player = new MediaPlayer();
        initMusicPlayer();

        songView = (ListView) view.findViewById(R.id.song_list);
        songList = new ArrayList<Songs>();
        getSongList();
        Collections.sort(songList, new Comparator<Songs>() {
            public int compare(Songs a, Songs b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        SongAdapter songAdt = new SongAdapter(getActivity(), songList);
        songView.setAdapter(songAdt);
        songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Songs ss = songList.get(i);
                String s = view.getTag().toString();
                Player pl = new Player();
                Bundle args = new Bundle();
                args.putString("id", s);
                pl.setArguments(args);
                f1.setVisibility(View.VISIBLE);
                replaceFragment(pl);
         tabLayout.setVisibility(view.GONE);
         viewPager.setVisibility(view.GONE);
////                Intent intent = new Intent(getActivity(),Player.class);
//                intent.putExtra("title",ss.getTitle());
//                intent.putExtra("id",view.getTag().toString());
//             startActivity(intent);
//                setSong(Integer.valueOf(view.getTag().toString()));
//                playSong();

            }
        });
        return view;
    }

    public  void setSong(int songIndex) {
        songPosn = songIndex;
    }


    public  void playSong(){

        //get song
        Songs playSong = songList.get(songPosn);
//get id
        long currSong = playSong.getId();
//set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        try{
            player.setDataSource(getContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }
    public void initMusicPlayer() {
        player.setWakeMode(getContext().getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    public void getSongList() {

        ContentResolver musicResolver = getActivity().getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
  //      Uri MmusicUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songList.add(new Songs(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
    }
//javeriairfan56@outlook.com

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.start();
    }
    @Override
    public void replaceFragment(Fragment fragment) {
        fragmentSong fs = new fragmentSong();
        FragmentManager fragmentManager =getActivity().getSupportFragmentManager();;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.f1, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();

    }
}
