package com.udacity.popularMovies.model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class FavoriteViewModel extends AndroidViewModel {

    private FavoriteRepository mRepository;
    private LiveData<List<Favorite>> mAllFavorites;

    public FavoriteViewModel(Application application) {
        super(application);
        mRepository = new FavoriteRepository(application);
        mAllFavorites = mRepository.getAllFavorites();
    }
    //TODO: If Room is used, database is not re-queried unnecessarily after rotation. Cached LiveData from ViewModel is used instead.
    public LiveData<List<Favorite>> getAllFavorites() {
        return mAllFavorites;
    }

    public void insert(Favorite favorite) { mRepository.insert(favorite); }

    public void delete(Favorite favorite) { mRepository.delete(favorite); }



}
