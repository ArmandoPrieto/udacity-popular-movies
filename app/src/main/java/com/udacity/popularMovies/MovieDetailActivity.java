package com.udacity.popularMovies;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.udacity.popularMovies.model.Movie;
import com.udacity.popularMovies.model.Review;
import com.udacity.popularMovies.model.Video;
import com.udacity.popularMovies.utils.JsonUtils;
import com.udacity.popularMovies.utils.NetworkUtils;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class MovieDetailActivity extends AppCompatActivity implements VideosAdapter.ViewHolder.OnVideoListener,
 ReviewsAdapter.ViewHolder.OnReviewListener{

    private static final String YOUTUBE_URL = "http://www.youtube.com/watch?v=";
    private TextView mMovieTitle;
    private TextView mMovieOverview;
    private TextView mVoteAverage;
    private TextView mReleaseDate;
    private ImageView mMoviePoster;
    private ToggleButton mFavoriteButton;
    private ArrayList<Video> videoList = new ArrayList<>();
    private ArrayList<Review> reviewList = new ArrayList<>();
    private RecyclerView mVideosRecyclerView;
    private RecyclerView.Adapter mVideosAdapter;
    private LinearLayoutManager mVideosLayoutManager;
    private RecyclerView mReviewsRecyclerView;
    private RecyclerView.Adapter mReviewsAdapter;
    private LinearLayoutManager mReviewsLayoutManager;
    private int reviewsPage = 1;
    private boolean isFavorite;
    private boolean favoriteInitValue;
    public static final String FAVORITE_INIT_VALUE = "favorite_init_value";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        mMovieTitle = findViewById(R.id.tv_movie_title);
        mMovieOverview = findViewById(R.id.tv_movie_overview);
        mVoteAverage = findViewById(R.id.tv_movie_vote_average);
        mReleaseDate = findViewById(R.id.tv_movie_release_date);
        mMoviePoster = findViewById(R.id.iv_movie_poster_detail);
        mVideosRecyclerView = findViewById(R.id.rv_movie_video_list);
        mFavoriteButton = findViewById(R.id.bt_favorite);
        mVideosLayoutManager = new LinearLayoutManager(this);
        mVideosRecyclerView.setLayoutManager(mVideosLayoutManager);
        mReviewsRecyclerView = findViewById(R.id.rv_movie_review_list);
        mReviewsLayoutManager = new LinearLayoutManager(this);
        mReviewsRecyclerView.setLayoutManager(mReviewsLayoutManager);

        final Intent currentIntent = getIntent();
        if(currentIntent.hasExtra(Movie.TITLE))
            mMovieTitle.setText(currentIntent.getStringExtra(Movie.TITLE));
        if(currentIntent.hasExtra(Movie.OVERVIEW))
            mMovieOverview.setText(currentIntent.getStringExtra(Movie.OVERVIEW));
        if(currentIntent.hasExtra(Movie.VOTE_AVERAGE))
            mVoteAverage.setText(currentIntent.getStringExtra(Movie.VOTE_AVERAGE));
        if(currentIntent.hasExtra(Movie.RELEASE_DATE)){
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date date = formatter1.parse(currentIntent.getStringExtra(Movie.RELEASE_DATE));
                mReleaseDate.setText(formatter2.format(date));
            } catch (ParseException e) {
                mReleaseDate.setText(currentIntent.getStringExtra(Movie.RELEASE_DATE));
            }
        }
        if(currentIntent.hasExtra(Movie.POSTER_PATH))
            Picasso.get().load(
                    Movie.buildPosterPath(currentIntent.getStringExtra(Movie.POSTER_PATH),
                            Movie.IMAGE_SIZE_LARGE))
                            .into(mMoviePoster);
        if(currentIntent.hasExtra(Movie.IS_FAVORITE)) {
            isFavorite = currentIntent.getBooleanExtra(Movie.IS_FAVORITE, false);
            favoriteInitValue = isFavorite;
            mFavoriteButton.setChecked(isFavorite);
        }
        mVideosAdapter = new VideosAdapter(this,videoList,this);
        mVideosRecyclerView.setAdapter(mVideosAdapter);
        mReviewsAdapter = new ReviewsAdapter(this,reviewList,this);
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);

        mVideosRecyclerView.setNestedScrollingEnabled(false);
        mReviewsRecyclerView.setNestedScrollingEnabled(false);
        if(currentIntent.hasExtra(Movie.ID))
            loadComponents(currentIntent.getIntExtra(Movie.ID, 0));

        mFavoriteButton.setOnClickListener(view -> {
            isFavorite = !isFavorite;
            Intent replyIntent = new Intent();
            setResult(RESULT_OK, replyIntent);
            replyIntent.putExtra(Movie.ID, currentIntent.getIntExtra(Movie.ID, 0));
            replyIntent.putExtra(Movie.TITLE, currentIntent.getStringExtra(Movie.TITLE));
            replyIntent.putExtra(Movie.OVERVIEW, currentIntent.getStringExtra(Movie.OVERVIEW));
            replyIntent.putExtra(Movie.VOTE_AVERAGE, Float.parseFloat(currentIntent.getStringExtra(Movie.VOTE_AVERAGE)));
            replyIntent.putExtra(Movie.POSTER_PATH, currentIntent.getStringExtra(Movie.POSTER_PATH));
            replyIntent.putExtra(Movie.RELEASE_DATE, currentIntent.getStringExtra(Movie.RELEASE_DATE));
            replyIntent.putExtra(Movie.IS_FAVORITE, isFavorite);
            replyIntent.putExtra(FAVORITE_INIT_VALUE, favoriteInitValue);
        });

    }

    private void loadComponents(int movieID) {
        loadMovieVideos(movieID);
        loadMovieReviews(movieID,reviewsPage);
    }

    private void loadMovieVideos(int movieId){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        URL url = NetworkUtils.buildVideosUrl(movieId);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.toString(),
                response -> {
                    JsonUtils.parseMovieVideoListJson(response, videoList);
                    mVideosAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }, error -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), getString(R.string.onVideoLoadingError), Toast.LENGTH_SHORT).show();
                });
        queue.add(stringRequest);
    }

    private void loadMovieReviews(int movieId, int page){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        URL url = NetworkUtils.buildReviewsUrl(movieId, page);
        final boolean clearReviewList = (page == 1);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.toString(),
                response -> {
                    JsonUtils.parseMovieReviewListJson(response, reviewList, clearReviewList);
                    mReviewsAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }, error -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), getString(R.string.onError), Toast.LENGTH_SHORT).show();
                });
        queue.add(stringRequest);
    }

    @Override
    public void onVideoClick(int position) {
        Video video = videoList.get(position);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_URL +video.getKey()));
        startActivity(intent);

    }

    @Override
    public void onReviewClick(int position) {
        Toast.makeText(this,"Review clicked: #"+position,Toast.LENGTH_SHORT).show();
    }


}
