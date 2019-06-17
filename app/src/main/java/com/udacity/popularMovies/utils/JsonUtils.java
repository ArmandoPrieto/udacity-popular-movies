package com.udacity.popularMovies.utils;

import com.udacity.popularMovies.model.Movie;
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
}
