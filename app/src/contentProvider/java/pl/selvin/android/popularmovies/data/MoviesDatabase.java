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
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import pl.selvin.android.autocontentprovider.annotation.Column;
import pl.selvin.android.autocontentprovider.annotation.Table;
import pl.selvin.android.autocontentprovider.annotation.TableName;
import pl.selvin.android.autocontentprovider.db.ColumnType;
import pl.selvin.android.popularmovies.api.MoviesServiceResponse;
import pl.selvin.android.popularmovies.models.Movie;
import pl.selvin.android.popularmovies.models.MovieDetails;
import pl.selvin.android.popularmovies.models.MovieDetailsHelper;
import pl.selvin.android.popularmovies.models.MovieHelper;
import pl.selvin.android.popularmovies.models.MovieWithDetails;
import pl.selvin.android.popularmovies.models.Review;
import pl.selvin.android.popularmovies.models.ReviewHelper;
import pl.selvin.android.popularmovies.models.Video;
import pl.selvin.android.popularmovies.models.VideoHelper;
import pl.selvin.android.popularmovies.utils.CursorLoaderLiveData;

//Room => ContentProvider refactoring - this class was most affected - but with help of CursorLoaderLiveData
//we mostly didn't have to change the rest of the app :)
public class MoviesDatabase {
    @Table(primaryKeys = {MoviesDef.ID}, notifyUris = {MoviesDef.MOVIES_WITH_DETAILS_URI})
    public interface MoviesDef {
        @TableName
        String TABLE_NAME = "Movies";

        @Column
        String ID = "id";

        @Column(type = ColumnType.VARCHAR, nullable = true)
        String POSTER_PATH = "posterPath";

        @Column(type = ColumnType.BOOLEAN, nullable = true)
        String ADULT = "adult";

        @Column(type = ColumnType.VARCHAR, nullable = true)
        String OVERVIEW = "overview";

        @Column(type = ColumnType.VARCHAR, nullable = true)
        String RELEASE_DATE = "releaseDate";

        @Column(type = ColumnType.VARCHAR, nullable = true)
        String ORIGINAL_TITLE = "originalTitle";

        @Column(type = ColumnType.VARCHAR, nullable = true)
        String ORIGINAL_LANGUAGE = "originalLanguage";

        @Column(type = ColumnType.VARCHAR, nullable = true)
        String TITLE = "title";

        @Column(type = ColumnType.VARCHAR, nullable = true)
        String BACKDROP_PATH = "backdropPath";

        @Column(type = ColumnType.DECIMAL)
        String POPULARITY = "popularity";

        @Column
        String VOTE_COUNT = "voteCount";

        @Column(type = ColumnType.BOOLEAN, nullable = true)
        String VIDEO = "video";

        @Column(type = ColumnType.DECIMAL)
        String VOTE_AVERAGE = "voteAverage";

        @Column(type = ColumnType.BOOLEAN)
        String TOP_RATED = "topRated";

        @Column(type = ColumnType.BOOLEAN)
        String POPULAR = "popular";

        @Column(type = ColumnType.BOOLEAN)
        String FAVOURITE = "favourite";

        String MOVIES_BULK_INSERT_WITH_RESET_FLAG = "movies_bulk_insert_with_reset_flag";

        String MOVIES_WITH_DETAILS = "movies_with_details";

        String MOVIES_WITH_DETAILS_URI = "content://" + MoviesContentProvider.AUTHORITY + "/" + "movies_with_details";
    }

    @Table(primaryKeys = {MovieDetailsDef.MOVIE_ID}, notifyUris = {MoviesDef.MOVIES_WITH_DETAILS_URI})
    public interface MovieDetailsDef {
        @TableName
        String TABLE_NAME = "moviesDetails";

        @Column
        String MOVIE_ID = "movieId";

        @Column(nullable = true)
        String RUNTIME = "runtime";

        @Column(type = ColumnType.VARCHAR, nullable = true)
        String STATUS = "status";

        @Column(type = ColumnType.VARCHAR, nullable = true)
        String TAGLINE = "tagLine";
    }

    @Table(primaryKeys = {VideosDef.ID})
    public interface VideosDef {
        @TableName
        String TABLE_NAME = "videos";

        @Column(type = ColumnType.VARCHAR)
        String ID = "id";

        @Column(type = ColumnType.VARCHAR)
        String KEY = "key";

        @Column(type = ColumnType.VARCHAR)
        String NAME = "name";

        @Column(type = ColumnType.VARCHAR)
        String SITE = "site";

        @Column(nullable = true)
        String SIZE = "size";

        @Column(type = ColumnType.VARCHAR)
        String TYPE = "type";

        @Column
        String MOVIE_ID = "movieId";
    }

    @Table(primaryKeys = {ReviewsDef.ID})
    public interface ReviewsDef {
        @TableName
        String TABLE_NAME = "reviews";

        @Column(type = ColumnType.VARCHAR)
        String ID = "id";

        @Column(type = ColumnType.VARCHAR)
        String AUTHOR = "author";

        @Column(type = ColumnType.VARCHAR)
        String CONTENT = "content";

        @Column(type = ColumnType.VARCHAR)
        String URL = "url";

        @Column
        String MOVIE_ID = "movieId";
    }

    public static class MovieDao {
        private final Context context;

        MovieDao(Context context) {
            this.context = context;
        }

        public static MovieDao create(Context context) {
            return new MovieDao(context);
        }

        public int updateMovie(Movie movie) {
            return context.getContentResolver().update(MoviesContentProvider.HELPER.getItemUri(MoviesDef.TABLE_NAME, movie.getId()),
                    MovieHelper.toContentValue(movie), null, null);
        }

        public LiveData<MovieWithDetails> loadMovieDetails(long id) {
            return new MovieDetailsCursorLiveData(context, id);
        }

        public LiveData<List<Movie>> loadPopularMovies() {
            return new MoviesCursorLiveData(context, MoviesDef.POPULAR + "=1", MoviesDef.POPULARITY + " DESC");
        }

        public LiveData<List<Video>> loadVideosForMovie(long movieId) {
            return new VideosCursorLiveData(context, movieId);
        }

        public LiveData<List<Review>> loadReviewsForMovie(long movieId) {
            return new ReviewsCursorLiveData(context, movieId);
        }

        public void insertPopular(MoviesServiceResponse<Movie> item) {
            final int size = item.getResults().size();
            final ContentValues[] values = new ContentValues[size];
            int c = -1;
            for (Movie movie : item.getResults()) {
                movie.setPopular(true);
                values[++c] = MovieHelper.toContentValue(movie);
            }
            context.getContentResolver().bulkInsert(
                    MoviesContentProvider.HELPER.getItemUri(MoviesDef.MOVIES_BULK_INSERT_WITH_RESET_FLAG,
                            Integer.toString(MoviesContentProvider.MOVIES_BULK_INSERT_WITH_RESET_FLAG_POPULAR)), values);
        }

        public LiveData<List<Movie>> loadTopRatedMovies() {
            return new MoviesCursorLiveData(context, MoviesDef.TOP_RATED + "=1", MoviesDef.VOTE_AVERAGE + " DESC");
        }

        public void insertTopRated(MoviesServiceResponse<Movie> item) {
            final int size = item.getResults().size();
            final ContentValues[] values = new ContentValues[size];
            int c = -1;
            for (Movie movie : item.getResults()) {
                movie.setTopRated(true);
                values[++c] = MovieHelper.toContentValue(movie);
            }
            context.getContentResolver().bulkInsert(
                    MoviesContentProvider.HELPER.getItemUri(MoviesDef.MOVIES_BULK_INSERT_WITH_RESET_FLAG,
                            Integer.toString(MoviesContentProvider.MOVIES_BULK_INSERT_WITH_RESET_FLAG_TOP_RATED)), values);
        }

        public LiveData<List<Movie>> loadFavourite() {
            return new MoviesCursorLiveData(context, MoviesDef.FAVOURITE + "=1", null);
        }

        public void saveMovieDetails(MovieDetails item) {
            ContentValues values = MovieDetailsHelper.toContentValue(item);
            if (context.getContentResolver().update(MoviesContentProvider.HELPER.getItemUri(MovieDetailsDef.TABLE_NAME, item.getMovieId()),
                    values, null, null) == 0)
                context.getContentResolver().insert(MoviesContentProvider.HELPER.getDirUri(MovieDetailsDef.TABLE_NAME),
                        values);
        }

        public void insertVideos(MoviesServiceResponse<Video> item, long movieId) {
            final List<Video> videos = item.getResults();
            final ContentValues[] values = new ContentValues[videos.size()];
            int c = -1;
            for (Video video : videos) {
                video.setMovieId(movieId);
                values[++c] = VideoHelper.toContentValue(video);
            }
            context.getContentResolver().bulkInsert(
                    MoviesContentProvider.HELPER.getDirUri(VideosDef.TABLE_NAME), values);
        }

        public void insertReviews(MoviesServiceResponse<Review> item, long movieId) {
            final List<Review> reviews = item.getResults();
            final ContentValues[] values = new ContentValues[reviews.size()];
            int c = -1;
            for (Review review : reviews) {
                review.setMovieId(movieId);
                values[++c] = ReviewHelper.toContentValue(review);
            }
            context.getContentResolver().bulkInsert(
                    MoviesContentProvider.HELPER.getDirUri(ReviewsDef.TABLE_NAME), values);
        }

        private static class MoviesCursorLiveData extends CursorLoaderLiveData<List<Movie>> {
            MoviesCursorLiveData(@NonNull Context context, @Nullable String selection, @Nullable String sortOrder) {
                super(context, MoviesContentProvider.HELPER.getDirUri(MoviesDef.TABLE_NAME), null, selection, null, sortOrder);
            }

            @Override
            protected List<Movie> dataFromCursor(Cursor cursor) {
                final ArrayList<Movie> movies = new ArrayList<>();
                if (cursor.moveToFirst()) {
                    do {
                        movies.add(MovieHelper.fromCursor(cursor));
                    } while (cursor.moveToNext());
                }
                return movies;
            }
        }

        private static class MovieDetailsCursorLiveData extends CursorLoaderLiveData<MovieWithDetails> {
            MovieDetailsCursorLiveData(@NonNull Context context, long id) {
                super(context, Uri.parse(MoviesDef.MOVIES_WITH_DETAILS_URI), null, MoviesDef.ID + "=?",
                        new String[]{Long.toString(id)}, null);
            }

            @Override
            protected MovieWithDetails dataFromCursor(Cursor cursor) {
                if (cursor.moveToFirst()) {
                    final MovieWithDetails details = new MovieWithDetails();
                    details.movie = MovieHelper.fromCursor(cursor);
                    if (!cursor.isNull(cursor.getColumnIndex(MovieDetailsDef.MOVIE_ID)))
                        details.details = MovieDetailsHelper.fromCursor(cursor);
                    else
                        details.details = null;
                    return details;
                }
                return null;
            }
        }

        private static class VideosCursorLiveData extends CursorLoaderLiveData<List<Video>> {
            VideosCursorLiveData(@NonNull Context context, long id) {
                super(context, MoviesContentProvider.HELPER.getDirUri(VideosDef.TABLE_NAME), null,
                        VideosDef.MOVIE_ID + "=?", new String[]{Long.toString(id)}, null);
            }

            @Override
            protected List<Video> dataFromCursor(Cursor cursor) {
                final ArrayList<Video> items = new ArrayList<>();
                if (cursor.moveToFirst()) {
                    do {
                        items.add(VideoHelper.fromCursor(cursor));
                    } while (cursor.moveToNext());
                }
                return items;
            }
        }

        private static class ReviewsCursorLiveData extends CursorLoaderLiveData<List<Review>> {
            ReviewsCursorLiveData(@NonNull Context context, long id) {
                super(context, MoviesContentProvider.HELPER.getDirUri(ReviewsDef.TABLE_NAME), null,
                        ReviewsDef.MOVIE_ID + "=?", new String[]{Long.toString(id)}, null);
            }

            @Override
            protected List<Review> dataFromCursor(Cursor cursor) {
                final ArrayList<Review> items = new ArrayList<>();
                if (cursor.moveToFirst()) {
                    do {
                        items.add(ReviewHelper.fromCursor(cursor));
                    } while (cursor.moveToNext());
                }
                return items;
            }
        }
    }
}

