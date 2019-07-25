package com.udacity.popularMovies.model;

import java.util.List;

public class Movie {

    private int voteCount;
    private int id;
    private boolean video;
    private float voteAverage;
    private String title;
    private float popularity;
    private String posterPath;
    private String originalLanguage;
    private String originalTitle;
    private List<Integer> genreIds;
    private String backdropPath;
    private boolean adult;
    private String overview;
    private String releaseDate;
    private List<Video> videoList;
    private final String BASE_URL = "http://image.tmdb.org/t/p/";
    private final String[] IMAGE_SIZE = {"original","w92", "w154", "w185", "w342", "w500", "w780"};
    public static final int IMAGE_SIZE_SSMALL = 1;
    public static final int IMAGE_SIZE_SMALL = 2;
    public static final int IMAGE_SIZE_MEDIUM = 3;
    public static final int IMAGE_SIZE_LARGE = 4;
    public static final int IMAGE_SIZE_XLARGE = 5;
    public static final int IMAGE_SIZE_XXLARGE = 6;
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String OVERVIEW = "overview";
    public static final String POSTER_PATH = "poster_path";
    public static final String VOTE_AVERAGE = "vote_average";
    public static final String RELEASE_DATE = "release_date";
    public static final String IS_FAVORITE = "is_favorite";


    public Movie() {
    }

    public Movie(int voteCount, int id, boolean video, float voteAverage, String title,
                 float popularity, String posterPath, String originalLanguage,
                 String originalTitle, List<Integer> genreIds, String backdropPath,
                 boolean adult, String overview, String releaseDate) {
        this.voteCount = voteCount;
        this.id = id;
        this.video = video;
        this.voteAverage = voteAverage;
        this.title = title;
        this.popularity = popularity;
        this.posterPath = posterPath;
        this.originalLanguage = originalLanguage;
        this.originalTitle = originalTitle;
        this.genreIds = genreIds;
        this.backdropPath = backdropPath;
        this.adult = adult;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath(int size) {
        return this.BASE_URL+"/"+this.IMAGE_SIZE[size]+"/"+this.posterPath;
    }

    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<Video> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<Video> videoList) {
        this.videoList = videoList;
    }
}
