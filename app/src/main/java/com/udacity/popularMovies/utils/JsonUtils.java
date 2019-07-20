package com.udacity.popularMovies.utils;

import com.udacity.popularMovies.model.Movie;
import com.udacity.popularMovies.model.Review;
import com.udacity.popularMovies.model.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class JsonUtils {

    private static final String RESULTS = "results";

    public static void parseMovieListJson(String json, List<Movie> movieList, boolean clearMovieList) {
        if (clearMovieList)
            movieList.clear();
        try {
            JSONObject jo = new JSONObject(json);
            JSONArray results = jo.getJSONArray(RESULTS);
            for(int i = 0; i< results.length(); i++){
                JSONObject movieJsonObject = results.getJSONObject(i);
                Movie movie = new Movie();
                movie.setId(movieJsonObject.getInt(Movie.ID));
                movie.setTitle(movieJsonObject.getString(Movie.TITLE));
                movie.setOverview(movieJsonObject.getString(Movie.OVERVIEW));
                movie.setPosterPath(movieJsonObject.getString(Movie.POSTER_PATH));
                movie.setReleaseDate(movieJsonObject.getString(Movie.RELEASE_DATE));
                movie.setVoteAverage((float) movieJsonObject.getDouble(Movie.VOTE_AVERAGE));
                movieList.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            movieList = null;
        }
    }
    public static void parseMovieVideoListJson(String json, List<Video> videoList) {
       try {
            JSONObject jo = new JSONObject(json);
            JSONArray results = jo.getJSONArray(RESULTS);
            for(int i = 0; i< results.length(); i++){
                JSONObject videoJsonObject = results.getJSONObject(i);
                Video video = new Video();
                video.setId(videoJsonObject.getString(Video.ID));
                video.setIso_639_1(videoJsonObject.getString(Video.ISO_639_1));
                video.setIso_3166_1(videoJsonObject.getString(Video.ISO_3166_1));
                video.setName(videoJsonObject.getString(Video.NAME));
                video.setSite(videoJsonObject.getString(Video.SITE));
                video.setSize(videoJsonObject.getInt(Video.SIZE));
                video.setType(videoJsonObject.getString(Video.TYPE));
                video.setKey(videoJsonObject.getString(Video.KEY));
                videoList.add(video);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            videoList = null;
        }
    }

    public static void parseMovieReviewListJson(String json, List<Review> reviewList, boolean clearReviewList) {
        if (clearReviewList)
            reviewList.clear();
        try {
            JSONObject jo = new JSONObject(json);
            JSONArray results = jo.getJSONArray(RESULTS);
            for(int i = 0; i< results.length(); i++){
                JSONObject reviewJsonObject = results.getJSONObject(i);
                Review review = new Review();
                review.setId(reviewJsonObject.getString(Review.ID));
                review.setAuthor(reviewJsonObject.getString(Review.AUTHOR));
                review.setContent(reviewJsonObject.getString(Review.CONTENT));
                review.setUrl(reviewJsonObject.getString(Review.URL));
                reviewList.add(review);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            reviewList = null;
        }
    }

}
