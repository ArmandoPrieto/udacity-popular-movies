package com.udacity.popularMovies.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.udacity.popularMovies.model.Favorite;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Insert
    void insert(Favorite favorite);
    @Delete
    void delete(Favorite favorite);
    @Update
    void update(Favorite favorite);
    @Query("DELETE FROM favorite")
    void deleteAll();
    @Query("SELECT * from favorite ORDER BY title ASC")
    LiveData<List<Favorite>> getAllFavorites();

}
