package com.udacity.popularMovies.viewModel;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import com.udacity.popularMovies.database.FavoriteRoomDatabase;
import com.udacity.popularMovies.model.Favorite;
import com.udacity.popularMovies.database.FavoriteDao;

import java.util.List;

public class FavoriteRepository {
    private FavoriteDao mFavoriteDao;
    private LiveData<List<Favorite>> mAllFavorites;

    FavoriteRepository(Application application) {
        FavoriteRoomDatabase db = FavoriteRoomDatabase.getDatabase(application);
        mFavoriteDao = db.favoriteDao();
        mAllFavorites = mFavoriteDao.getAllFavorites();
    }

    LiveData<List<Favorite>> getAllFavorites() {
        return mAllFavorites;
    }

    void insert(Favorite favorite) {
        new insertAsyncTask(mFavoriteDao).execute(favorite);
    }

    void delete(Favorite favorite) {
        new deleteAsyncTask(mFavoriteDao).execute(favorite);
    }

    private static class insertAsyncTask extends AsyncTask<Favorite, Void, Void> {

        private FavoriteDao mAsyncTaskDao;

        insertAsyncTask(FavoriteDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Favorite... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Favorite, Void, Void> {

        private FavoriteDao mAsyncTaskDao;

        deleteAsyncTask(FavoriteDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Favorite... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }
}
