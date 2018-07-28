package shariq59087.summer18.mobileapp.musicplayerproject;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
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


public class Artistfrag extends Fragment {

    public static ArrayList<Songs> songList;

    private ListView songView;
    private  int songPosn;
    private MyMusicService musicSrv;
    private MediaPlayer player;
    private boolean musicBound = false;
    View view;
    private Context mContext;

    public Artistfrag() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_artistfrag, container, false);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    public void getSongList() {

        ContentResolver musicResolver = getActivity().getContentResolver();
        Uri musicUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Playlists.NAME);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Playlists._ID);
            int idCount = musicCursor.getColumnIndex
                    (MediaStore.Audio.Playlists._COUNT);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);



                songList.add(new Songs(thisId, thisTitle, idCount+" tracks"));
            }
            while (musicCursor.moveToNext());
        }
    }



}
