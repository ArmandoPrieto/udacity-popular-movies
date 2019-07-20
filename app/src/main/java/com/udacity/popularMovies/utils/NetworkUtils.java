package com.udacity.popularMovies.utils;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {
    private final static String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3/movie";
    private final static String PARAM_API_KEY = "api_key";
    private final static String PARAM_PAGE = "page";
    //TODO Please add API Key here
    private final static String api_key = "";
    public final static String MOST_POPULAR = "popular";
    public final static String TOP_RATED = "top_rated";
    private final static String VIDEOS = "videos";
    private final static String REVIEWS = "reviews";

    private NetworkUtils() {
    }

    public static URL buildUrl(String sortBy, int page) {
        Uri builtUri;
        URL url = null;
        switch(sortBy)
        {
            case MOST_POPULAR:
                builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendPath(MOST_POPULAR)
                        .appendQueryParameter(PARAM_API_KEY, api_key)
                        .appendQueryParameter(PARAM_PAGE, String.valueOf(page))
                        .build();
                break;
            case TOP_RATED:
                builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendPath(TOP_RATED)
                        .appendQueryParameter(PARAM_API_KEY, api_key)
                        .appendQueryParameter(PARAM_PAGE, String.valueOf(page))
                        .build();
                break;
            default:
                builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendPath(MOST_POPULAR)
                        .appendQueryParameter(PARAM_API_KEY, api_key)
                        .appendQueryParameter(PARAM_PAGE, String.valueOf(page))
                        .build();
        }
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
    public static URL buildVideosUrl(int movieId) {
        Uri builtUri;
        URL url = null;
        builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendPath(VIDEOS)
                .appendQueryParameter(PARAM_API_KEY, api_key)
                .build();

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildReviewsUrl(int movieId, int page) {
        Uri builtUri;
        URL url = null;
        builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendPath(REVIEWS)
                .appendQueryParameter(PARAM_API_KEY, api_key)
                .appendQueryParameter(PARAM_PAGE, String.valueOf(page))
                .build();

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

}
