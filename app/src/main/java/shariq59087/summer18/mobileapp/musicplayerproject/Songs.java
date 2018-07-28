package shariq59087.summer18.mobileapp.musicplayerproject;

public class Songs {
    private long id;
    private String title;
    private String Artist;

    public Songs(long id, String title, String Artist) {
        this.id = id;
        this.title = title;
        this.Artist = Artist;
    }

    public long getId() {
        return id;
    }

    public String getArtist() {
        return Artist;
    }

    public String getTitle() {
        return title;
    }
}