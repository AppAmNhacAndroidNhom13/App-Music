package com.example.model;

public class Song {
    private String songName;
    private String artistName;
    private String pathSong;

    public Song() {}

    public Song(String songName, String artistName, String pathSong) {
        this.songName = songName;
        this.artistName = artistName;
        this.pathSong = pathSong;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getPathSong() {
        return pathSong;
    }

    public void setPathSong(String pathSong) {
        this.pathSong = pathSong;
    }
}
