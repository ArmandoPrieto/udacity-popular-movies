package com.udacity.popularMovies;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.udacity.popularMovies.model.Favorite;
import com.udacity.popularMovies.model.FavoriteViewModel;
import com.udacity.popularMovies.model.Movie;
import com.udacity.popularMovies.utils.JsonUtils;
import com.udacity.popularMovies.utils.NetworkUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ViewHolder.OnMovieListener {
    private FavoriteViewModel mFavoriteViewModel;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Movie> movieList;
    private List<Favorite> favoriteMovieList;
    private String sortBy = NetworkUtils.MOST_POPULAR;
    private int moviesPage = 1;
    public static final int DETAIL_ACTIVITY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        mRecyclerView = findViewById(R.id.rv_movie_list);
        int numberOfColumns = calculateNoOfColumns(160);
        mLayoutManager = new GridLayoutManager(mContext, numberOfColumns);
        mRecyclerView.setLayoutManager(mLayoutManager);
        movieList = new ArrayList<>();
        favoriteMovieList = new ArrayList<>();
        mAdapter = new MoviesAdapter(mContext,movieList,this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if(moviesPage <1000)
                        moviesPage++;
                    loadMovies(sortBy,moviesPage);
                }
            }
        });
        mFavoriteViewModel = ViewModelProviders.of(this).get(FavoriteViewModel.class);
        mFavoriteViewModel.getAllFavorites().observe(this, new Observer<List<Favorite>>() {
            @Override
            public void onChanged(@Nullable final List<Favorite> favorites) {
                // Update the cached copy of the words in the adapter.
                favoriteMovieList = favorites;
            }
        });
        loadMovies(sortBy, moviesPage);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == DETAIL_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            int id = data.getIntExtra(MovieDetailActivity.EXTRA_REPLY_ID,0);
            String title = data.getStringExtra(MovieDetailActivity.EXTRA_REPLY_TITLE);
            Favorite favorite = new Favorite(id,title);
            if(data.getBooleanExtra(MovieDetailActivity.EXTRA_REPLY_IS_FAVORITE, false) == true)
                mFavoriteViewModel.insert(favorite);
            else
                mFavoriteViewModel.delete(favorite);
        }
    }

    private void loadMovies(String sortBy, int page){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        URL url = NetworkUtils.buildUrl(sortBy, page);
        final boolean clearMovieList = (page == 1);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonUtils.parseMovieListJson(response, movieList, clearMovieList);
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
     }

     private int calculateNoOfColumns(float columnWidthDp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
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
                sortBy = NetworkUtils.TOP_RATED;
                moviesPage = 1;
                loadMovies(sortBy,moviesPage);
                Toast.makeText(this,getString(R.string.highest_rated),Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_sort_by_most_popular:
                sortBy = NetworkUtils.MOST_POPULAR;
                moviesPage = 1;
                loadMovies(sortBy,moviesPage);
                Toast.makeText(this,getString(R.string.most_popular),Toast.LENGTH_LONG).show();
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
        //TODO: add extra isFavorite
        //startActivity(intent);
        startActivityForResult(intent, DETAIL_ACTIVITY_REQUEST_CODE);
    }
}
