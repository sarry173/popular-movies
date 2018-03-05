/*
 Copyright (c) 2018 Selvin
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.
 */
package pl.selvin.android.popularmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.Update;
import android.content.Context;

import java.util.List;

import pl.selvin.android.popularmovies.models.Movie;

@Database(entities = {Movie.class}, version = 2)
public abstract class MoviesDatabase extends RoomDatabase {
    public abstract MovieDao movieDao();

    @Dao
    public interface MovieDao {
        @Query("SELECT * FROM movies")
        LiveData<List<Movie>> getAll();

        @Insert(onConflict = OnConflictStrategy.FAIL)
        void insert(Movie movie);

        @Update(onConflict = OnConflictStrategy.FAIL)
        void update(Movie movie);

        @Delete
        void delete(Movie movie);

        @Query("SELECT COUNT(1) FROM movies WHERE id=:id")
        LiveData<Integer> count(long id);

        @Query("SELECT * FROM movies WHERE id=:id")
        LiveData<Movie> getMovie(long id);
    }

    private static MoviesDatabase INSTANCE;

    public synchronized static MoviesDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MoviesDatabase.class, "database-name").build();
        }
        return INSTANCE;
    }
}
