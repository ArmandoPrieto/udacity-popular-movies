package com.udacity.popularMovies;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.udacity.popularMovies.model.Movie;
import com.udacity.popularMovies.utils.JsonUtils;
import com.udacity.popularMovies.utils.NetworkUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements MoviesAdapter.ViewHolder.OnMovieListener {

    private static final String TAG = MainActivity.class.getName();
    private Context mContext;
    RelativeLayout mRelativeLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Movie> movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_list);
        int numberOfColumns = calculateNoOfColumns(160);
        mLayoutManager = new GridLayoutManager(mContext, numberOfColumns);
        mRecyclerView.setLayoutManager(mLayoutManager);
        movieList = new ArrayList<>();
        mAdapter = new MoviesAdapter(mContext,movieList,this);
        mRecyclerView.setAdapter(mAdapter);
        //TODO: Add readme file
        loadMovies();
    }

    public void loadMovies(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        URL url = NetworkUtils.buildUrl();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        JsonUtils.parseMovieListJson(response, movieList);
                        mAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: Something went wrong");
                progressDialog.dismiss();
                // also supports Toast.LENGTH_LONG
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
     }

    public int calculateNoOfColumns(float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
        return noOfColumns;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_sort_by_highest_rated:
                Toast.makeText(this,"Highest rated",Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_sort_by_most_popular:
                Toast.makeText(this,"Most popular",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMovieClick(int position) {
        Movie movie = movieList.get(position);
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(Movie.ID,movie.getId());
        intent.putExtra(Movie.TITLE,movie.getTitle());
        intent.putExtra(Movie.OVERVIEW,movie.getOverview());
        intent.putExtra(Movie.POSTER_PATH,movie.getPosterPath(Movie.IMAGE_SIZE_LARGE));
        intent.putExtra(Movie.VOTE_AVERAGE,String.valueOf(movie.getVoteAverage()));
        intent.putExtra(Movie.RELEASE_DATE,movie.getReleaseDate());
        startActivity(intent);
    }
}
