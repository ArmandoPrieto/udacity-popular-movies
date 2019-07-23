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

    public LiveData<List<Favorite>> getAllFavorites() { return mAllFavorites; }

    public void insert(Favorite favorite) { mRepository.insert(favorite); }

    public void delete(Favorite favorite) { mRepository.delete(favorite); }



}
