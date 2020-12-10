package it.unibo.oop.lab.lambda.ex02;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return songs.stream().map(t -> t.getSongName()).sorted();
    }

    @Override
    public Stream<String> albumNames() {
        return albums.keySet().stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {

        return albums.entrySet().stream()
                .filter((n) -> n.getValue().equals(year))
                .map(n -> n.getKey());
    }

    @Override
    public int countSongs(final String albumName) {
       int count = 0;
        for (Song s : songs) {
           if (s.getAlbumName().equals(Optional.of(albumName))) {
               count++;
           }
       }
       return count;
    }
    @Override
    public int countSongsInNoAlbum() {
        return (int) songs.stream().filter(t -> t.getAlbumName().isEmpty()).count();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        List<Song> list = songs.stream().filter(t -> t.getAlbumName().isPresent())
        .filter(t -> t.getAlbumName().get().equals(albumName))
        .collect(Collectors.toList());
        double totalDuration = 0;
        int numberOfSong = 0;
        for (Song s : list) {
            totalDuration += s.getDuration();
            numberOfSong++;
        }
        return OptionalDouble.of(totalDuration / numberOfSong);
    }

    @Override
    public Optional<String> longestSong() {
        double max = 0;
        String songName = null;
        for (Song s : songs) {
            if (s.getDuration() > max) {
                max = s.getDuration();
                songName = s.getSongName();
            }
        }
        return Optional.of(songName);
    }

    @Override
    public Optional<String> longestAlbum() {
        Set<String> nameAlbum = albums.keySet();
        double maxDuration = 0;
        String albmax = null;
        for (String s : nameAlbum) {
            final double currentDuration = songs.stream()
                    .filter(t -> t.getAlbumName().isPresent())
                    .filter(t -> t.getAlbumName().get().equals(s))
                    .map(t -> t.getDuration())
                    .reduce((a, b) -> a + b)
                    .get();
            if (currentDuration > maxDuration) {
                albmax = s;
                maxDuration = currentDuration;
            }
        }
        return Optional.of(albmax);
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
