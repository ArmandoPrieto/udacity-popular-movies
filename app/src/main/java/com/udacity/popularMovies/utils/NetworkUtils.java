package com.udacity.popularMovies.utils;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {

    final static String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3/movie/popular";
    final static String PARAM_API_KEY = "api_key";
    final static String PARAM_SORT = "sort";
    final static String api_key = "da1a7249c02c35267ec14af7c8c1d0a8";
    final static String sortBy = "asc";

    private NetworkUtils() {
    }

    public static URL buildUrl() {
        Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, api_key)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}
