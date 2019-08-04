package com.udacity.popularMovies;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.udacity.popularMovies.model.Favorite;
import com.udacity.popularMovies.model.FavoriteViewModel;
import com.udacity.popularMovies.model.Movie;
import com.udacity.popularMovies.utils.JsonUtils;
import com.udacity.popularMovies.utils.NetworkUtils;
import org.jetbrains.annotations.NotNull;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

interface OnUpdateFavoriteMovieListTaskCompleted {
    void onUpdateFavoriteMovieListTaskCompleted();
}

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ViewHolder.OnMovieListener,
        OnUpdateFavoriteMovieListTaskCompleted {
    public static final int DETAIL_ACTIVITY_REQUEST_CODE = 1;
    private static List<Favorite> mFavoriteMovieList;
    private static AtomicBoolean showFavorites = new AtomicBoolean();
    private FavoriteViewModel mFavoriteViewModel;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Movie> mMovieList;
    private String sortBy = NetworkUtils.MOST_POPULAR;
    private int moviesPage = 1;
    private final String SHOW_FAVORITE_PREFERENCE = "show_favorite_preference";
    protected ProgressDialog progressDialog;

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
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if (moviesPage < 1000)
                        moviesPage++;
                    updateMovieList();
                }
            }
        });
        mFavoriteViewModel = ViewModelProviders.of(this).get(FavoriteViewModel.class);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        showFavorites.set(sharedPref.getBoolean(SHOW_FAVORITE_PREFERENCE, false));
        mFavoriteViewModel.getAllFavorites().observe(this,
                favorites -> {
                                mFavoriteMovieList = favorites;
                                updateMovieList();
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(SHOW_FAVORITE_PREFERENCE, showFavorites.get());
        editor.apply();
    }

    private void updateMovieList() {
        if (showFavorites.get()) {
            new updateMovieListAsyncTask(this).execute(mMovieList);
        } else {
            loadMovies(sortBy, moviesPage);
        }
    }

    @Override
    public void onUpdateFavoriteMovieListTaskCompleted() {
        mAdapter.notifyDataSetChanged();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DETAIL_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Favorite favorite = new Favorite(
                    data.getIntExtra(Movie.ID, 0),
                    data.getStringExtra(Movie.TITLE),
                    data.getStringExtra(Movie.OVERVIEW),
                    data.getFloatExtra(Movie.VOTE_AVERAGE, 0),
                    data.getStringExtra(Movie.RELEASE_DATE),
                    data.getStringExtra(Movie.POSTER_PATH));
            boolean favoriteInitValue = data.getBooleanExtra(
                    MovieDetailActivity.FAVORITE_INIT_VALUE, false);
            boolean isFavorite = data.getBooleanExtra(Movie.IS_FAVORITE, false);
            if (favoriteInitValue != isFavorite) {
                if (isFavorite) {
                    mFavoriteViewModel.insert(favorite);
                } else {
                    mFavoriteViewModel.delete(favorite);
                    notifyAdapterFavoriteMovieRemoved(favorite);
                }
            }
        }
    }

    private void notifyAdapterFavoriteMovieRemoved(Favorite favorite) {
        if (showFavorites.get()) {
            IntPredicate index = i -> favorite.getId() == mMovieList.get(i).getId();
            OptionalInt indexOpt = IntStream.range(0, mMovieList.size())
                    .filter(index)
                    .findFirst();
            if (indexOpt.isPresent()) {
                mMovieList.remove(indexOpt.getAsInt());
                mAdapter.notifyItemRemoved(indexOpt.getAsInt());
            }
        }
    }

    private void loadMovies(String sortBy, int page) {
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        URL url = NetworkUtils.buildUrl(sortBy, page);
        final boolean clearMovieList = (page == 1);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.toString(),
                response -> {
                    JsonUtils.parseMovieListJson(response, mMovieList, clearMovieList);
                    mAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }, error -> {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), getString(R.string.onError), Toast.LENGTH_SHORT).show();
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
        inflater.inflate(R.menu.activity_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        moviesPage = 1;
        switch (item.getItemId()) {
            case R.id.action_sort_by_highest_rated:
                sortBy = NetworkUtils.TOP_RATED;
                showFavorites.set(false);
                updateMovieList();
                Toast.makeText(this, getString(R.string.highest_rated), Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_sort_by_most_popular:
                sortBy = NetworkUtils.MOST_POPULAR;
                showFavorites.set(false);
                updateMovieList();
                Toast.makeText(this, getString(R.string.most_popular), Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_show_favorites:
                showFavorites.set(true);
                updateMovieList();
                Toast.makeText(this, getString(R.string.favorites), Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMovieClick(int position) {
        Movie movie = mMovieList.get(position);
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(Movie.ID, movie.getId());
        intent.putExtra(Movie.TITLE, movie.getTitle());
        intent.putExtra(Movie.OVERVIEW, movie.getOverview());
        intent.putExtra(Movie.POSTER_PATH, movie.getPosterPath());
        intent.putExtra(Movie.VOTE_AVERAGE, String.valueOf(movie.getVoteAverage()));
        intent.putExtra(Movie.RELEASE_DATE, movie.getReleaseDate());
        Optional<Favorite> favorite = mFavoriteMovieList.stream()
                .filter(x -> x.getId() == movie.getId())
                .findAny();
        if (favorite.isPresent())
            intent.putExtra(Movie.IS_FAVORITE, true);
        else
            intent.putExtra(Movie.IS_FAVORITE, false);
        startActivityForResult(intent, DETAIL_ACTIVITY_REQUEST_CODE);
    }

    static class updateMovieListAsyncTask extends AsyncTask<List<Movie>, Void, Void> {

        OnUpdateFavoriteMovieListTaskCompleted mListener;

        updateMovieListAsyncTask(OnUpdateFavoriteMovieListTaskCompleted listener) {
            mListener = listener;
        }

        @Override
        protected Void doInBackground(List<Movie>... movieList) {
            movieList[0].clear();
            for (Favorite favorite : mFavoriteMovieList) {
                Movie m = new Movie();
                m.setId(favorite.getId());
                m.setTitle(favorite.getTitle());
                m.setOverview(favorite.getOverview());
                m.setVoteAverage(favorite.getVote_average());
                m.setReleaseDate(favorite.getRelease_date());
                m.setPosterPath(favorite.getPoster_path());
                movieList[0].add(m);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mListener.onUpdateFavoriteMovieListTaskCompleted();
        }
    }
}
