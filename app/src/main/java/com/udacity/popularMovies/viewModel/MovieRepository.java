package com.udacity.popularMovies.viewModel;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.udacity.popularMovies.R;
import com.udacity.popularMovies.database.FavoriteDao;
import com.udacity.popularMovies.database.FavoriteRoomDatabase;
import com.udacity.popularMovies.model.Favorite;
import com.udacity.popularMovies.model.Movie;
import com.udacity.popularMovies.utils.JsonUtils;
import com.udacity.popularMovies.utils.NetworkUtils;

import java.net.URL;
import java.util.List;

public class MovieRepository {
   //TODo: add json
    // private FavoriteDao mFavoriteDao;
    private List<Movie> mAllMovies;

    MovieRepository(Application application, List<Movie> mAllMovies) {
        mAllMovies = mAllMovies;
    }

    List<Movie> getmAllMovies(String sortBy, int page, List<Movie> movieList) {
        RequestQueue queue = Volley.newRequestQueue(this);
        URL url = NetworkUtils.buildUrl(sortBy, page);
        final boolean clearMovieList = (page == 1);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonUtils.parseMovieListJson(response, mAllMovies, clearMovieList);
                        mAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.onError), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
        return mAllMovies;
    }
}
