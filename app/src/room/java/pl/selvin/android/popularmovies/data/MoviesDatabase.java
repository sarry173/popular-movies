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
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;
import android.content.Context;

import java.util.List;

import pl.selvin.android.popularmovies.api.MoviesServiceResponse;
import pl.selvin.android.popularmovies.models.Movie;
import pl.selvin.android.popularmovies.models.MovieDetails;
import pl.selvin.android.popularmovies.models.MovieWithDetails;
import pl.selvin.android.popularmovies.models.Review;
import pl.selvin.android.popularmovies.models.Video;

@Database(entities = {Movie.class, MovieDetails.class, Video.class, Review.class}, version = 7, exportSchema = false)
public abstract class MoviesDatabase extends RoomDatabase {
    public abstract MovieDao movieDao();

    @Dao
    public abstract static class MovieDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        public abstract void insertAllMovies(List<Movie> movies);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        public abstract void insertAllVideos(List<Video> movies);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        public abstract void insertAllReviews(List<Review> movies);

        @Update(onConflict = OnConflictStrategy.FAIL)
        public abstract int updateMovie(Movie movie);

        @Query("SELECT m.*, d.* FROM movies m LEFT OUTER JOIN moviesDetails d ON d.movieId = m.id WHERE id=:id")
        public abstract LiveData<MovieWithDetails> loadMovieDetails(long id);

        @Query("SELECT * FROM movies WHERE id=:id")
        protected abstract Movie loadMovieS(long id);

        @Query("SELECT * FROM movies WHERE popular=1")
        public abstract LiveData<List<Movie>> loadPopularMovies();

        @Query("SELECT * FROM videos WHERE movieId=:movieId")
        public abstract LiveData<List<Video>> loadVideosForMovie(long movieId);

        @Query("SELECT * FROM reviews WHERE movieId=:movieId")
        public abstract LiveData<List<Review>> loadReviewsForMovie(long movieId);

        @SuppressWarnings("UnusedReturnValue")
        @Query("UPDATE movies SET popular=0 WHERE popular=1")
        public abstract int unsetPopular();

        @Transaction
        public void insertPopular(MoviesServiceResponse<Movie> item) {
            unsetPopular();
            final List<Movie> movies = item.getResults();
            for (final Movie movie : movies) {
                final Movie existing = loadMovieS(movie.getId());
                movie.setPopular(true);
                if (existing != null) {
                    movie.setTopRated(existing.isTopRated());
                    movie.setFavourite(existing.isFavourite());
                }
            }
            insertAllMovies(movies);
        }

        @Query("SELECT * FROM movies WHERE topRated=1")
        public abstract LiveData<List<Movie>> loadTopRatedMovies();

        @SuppressWarnings("UnusedReturnValue")
        @Query("UPDATE movies SET topRated=0 WHERE topRated=1")
        public abstract int unsetTopRated();

        public void insertTopRated(MoviesServiceResponse<Movie> item) {
            unsetTopRated();
            final List<Movie> movies = item.getResults();
            for (final Movie movie : movies) {
                final Movie existing = loadMovieS(movie.getId());
                movie.setTopRated(true);
                if (existing != null) {
                    movie.setPopular(existing.isPopular());
                    movie.setFavourite(existing.isFavourite());
                }
            }
            insertAllMovies(movies);
        }

        @Query("SELECT * FROM movies WHERE favourite=1")
        public abstract LiveData<List<Movie>> loadFavourite();

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        public abstract void saveMovieDetails(MovieDetails item);

        public void insertVideos(MoviesServiceResponse<Video> item, long movieId) {
            final List<Video> videos = item.getResults();
            for (Video video : videos) {
                video.setMovieId(movieId);
            }
            insertAllVideos(videos);
        }

        public void insertReviews(MoviesServiceResponse<Review> item, long movieId) {
            final List<Review> reviews = item.getResults();
            for (Review review : reviews) {
                review.setMovieId(movieId);
            }
            insertAllReviews(reviews);
        }

        public static MovieDao create(Context context) {
            final MoviesDatabase database = Room.databaseBuilder(context.getApplicationContext(), MoviesDatabase.class, "movies")
                    .fallbackToDestructiveMigration().build();
            return database.movieDao();
        }
    }
}
