package com.udacity.popularMovies.utils;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {

    final static String MOVIE_DB_BASE_URL_MOST_POPULAR = "https://api.themoviedb.org/3/movie/popular";
    final static String MOVIE_DB_BASE_URL_TOP_RATED = "https://api.themoviedb.org/3/movie/top_rated";
    final static String PARAM_API_KEY = "api_key";
    final static String PARAM_PAGE = "page";
    //TODO Please add API Key here
    final static String api_key = "";
    public final static String MOST_POPULAR = "popular";
    public final static String TOP_RATED = "top_rated";

    private NetworkUtils() {
    }

    public static URL buildUrl(String sortBy, int page) {
        Uri builtUri;
        URL url = null;
        switch(sortBy)
        {
            case MOST_POPULAR:
                builtUri = Uri.parse(MOVIE_DB_BASE_URL_MOST_POPULAR).buildUpon()
                        .appendQueryParameter(PARAM_API_KEY, api_key)
                        .appendQueryParameter(PARAM_PAGE, String.valueOf(page))
                        .build();
                break;
            case TOP_RATED:
                builtUri = Uri.parse(MOVIE_DB_BASE_URL_TOP_RATED).buildUpon()
                        .appendQueryParameter(PARAM_API_KEY, api_key)
                        .appendQueryParameter(PARAM_PAGE, String.valueOf(page))
                        .build();
                break;
            default:
                builtUri = Uri.parse(MOVIE_DB_BASE_URL_MOST_POPULAR).buildUpon()
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
}
