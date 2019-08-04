package com.udacity.popularMovies;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.preference.PreferenceManager;
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
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;


public class MainActivity extends AppCompatActivity implements MoviesAdapter.ViewHolder.OnMovieListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        OnUpdateMovieListTaskCompleted {
    private FavoriteViewModel mFavoriteViewModel;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Movie> mMovieList;
    private static List<Favorite> mFavoriteMovieList;
    private String sortBy = NetworkUtils.MOST_POPULAR;
    private int moviesPage = 1;
    private static AtomicBoolean showFavorites = new AtomicBoolean();
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
        mMovieList = new ArrayList<>();
        mFavoriteMovieList = new ArrayList<>();
        mAdapter = new MoviesAdapter(mContext, mMovieList, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if(moviesPage <1000)
                        moviesPage++;
                    updateMovieList();
                }
            }
        });
        mFavoriteViewModel = ViewModelProviders.of(this).get(FavoriteViewModel.class);
        mFavoriteViewModel.getAllFavorites().observe(this, new Observer<List<Favorite>>() {
            @Override
            public void onChanged(@Nullable final List<Favorite> favorites) {
                // Update the cached copy of the words in the adapter.
                mFavoriteMovieList = favorites;
            }
        });
        setupSharedPreferences();
        updateMovieList();
       }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.sp_show_favorites))){
            showFavorites.getAndSet(sharedPreferences.getBoolean(key,showFavorites.get()));
            updateMovieList();
        }
    }

    public void updateMovieList(){
        //moviesPage =1;
        if(showFavorites.get()){
             new updateMovieListAsyncTask(this).execute(mMovieList);
        }else{
            loadMovies(sortBy, moviesPage);
        }

    }

    @Override
    public void onUpdateMovieListTaskCompleted(List<Movie> movieList) {
       // mMovieList.clear();
       // mMovieList.addAll(movieList);
        mAdapter.notifyDataSetChanged();
    }

     public class updateMovieListAsyncTask extends AsyncTask<List<Movie>, List<Movie>, List<Movie>> {

        public OnUpdateMovieListTaskCompleted mListener;

        public updateMovieListAsyncTask(OnUpdateMovieListTaskCompleted listener) {
            mListener = listener;
        }

        @Override
        protected List<Movie> doInBackground(List<Movie>... movieList) {
            movieList[0].clear();
           // List<Movie> test = new ArrayList<>();
                for(Favorite favorite : mFavoriteMovieList){
                    Movie m = new Movie();
                    m.setId(favorite.getId());
                    m.setTitle(favorite.getTitle());
                    m.setOverview(favorite.getOverview());
                    m.setVoteAverage(favorite.getVote_average());
                    m.setReleaseDate(favorite.getRelease_date());
                    m.setPosterPath(favorite.getPoster_path());
                    movieList[0].add(m);
                    //test.add(m);
                }

            return movieList[0];
            //return test;
        }

        @Override
        protected void onPostExecute(List<Movie> movieList) {
            super.onPostExecute(movieList);
            mListener.onUpdateMovieListTaskCompleted(movieList);

        }
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        sharedPreferences.edit()
                .putBoolean(getString(R.string.sp_show_favorites),showFavorites.get())
                .apply();

    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == DETAIL_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
           Favorite favorite = new Favorite(
                   data.getIntExtra(Movie.ID,0),
                   data.getStringExtra(Movie.TITLE),
                   data.getStringExtra(Movie.OVERVIEW),
                   data.getFloatExtra(Movie.VOTE_AVERAGE,0),
                   data.getStringExtra(Movie.RELEASE_DATE),
                   data.getStringExtra(Movie.POSTER_PATH));
           boolean favoriteInitValue = data.getBooleanExtra(
                   MovieDetailActivity.FAVORITE_INIT_VALUE, false);
           boolean isFavorite = data.getBooleanExtra(Movie.IS_FAVORITE, false);
           if(favoriteInitValue != isFavorite){
                if(isFavorite) {
                    mFavoriteViewModel.insert(favorite);
                }else{
                    mFavoriteViewModel.delete(favorite);
                    if(showFavorites.get()){
                            IntPredicate index = i -> favorite.getId() == mMovieList.get(i).getId();
                            OptionalInt indexOpt = IntStream.range(0, mMovieList.size())
                                .filter(index)
                                .findFirst();
                            if(indexOpt.isPresent()){
                                mMovieList.remove(indexOpt.getAsInt());
                                mAdapter.notifyItemRemoved(indexOpt.getAsInt());
                            }

                        }
                    }
                }
                //updateMovieList();
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
                        JsonUtils.parseMovieListJson(response, mMovieList, clearMovieList);
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
            case R.id.action_settings:
                Intent intent = new Intent(this, PreferenceActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMovieClick(int position) {
        Movie movie = mMovieList.get(position);
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(Movie.ID,movie.getId());
        intent.putExtra(Movie.TITLE,movie.getTitle());
        intent.putExtra(Movie.OVERVIEW,movie.getOverview());
        intent.putExtra(Movie.POSTER_PATH,movie.getPosterPath());
        intent.putExtra(Movie.VOTE_AVERAGE,String.valueOf(movie.getVoteAverage()));
        intent.putExtra(Movie.RELEASE_DATE,movie.getReleaseDate());
        Optional<Favorite> favorite = mFavoriteMovieList.stream()
                .filter( x -> x.getId() == movie.getId())
                .findAny();
        if(favorite.isPresent())
            intent.putExtra(Movie.IS_FAVORITE,true);
        else
            intent.putExtra(Movie.IS_FAVORITE,false);
        startActivityForResult(intent, DETAIL_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }


}

interface OnUpdateMovieListTaskCompleted{
    void onUpdateMovieListTaskCompleted(List<Movie> movieList);
}
