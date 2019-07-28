package com.udacity.popularMovies.database;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.udacity.popularMovies.model.Favorite;
import com.udacity.popularMovies.model.FavoriteDao;

@Database(entities = {Favorite.class}, version = 1, exportSchema = false)
public abstract class FavoriteRoomDatabase extends RoomDatabase {

    public abstract FavoriteDao favoriteDao();
    private static volatile FavoriteRoomDatabase INSTANCE;

    public static FavoriteRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FavoriteRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FavoriteRoomDatabase.class, "favorite_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
