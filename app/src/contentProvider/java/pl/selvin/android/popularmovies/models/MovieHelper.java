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
package pl.selvin.android.popularmovies.models;

import android.content.ContentValues;
import android.database.Cursor;

import pl.selvin.android.popularmovies.data.MoviesDatabase.MoviesDef;

public class MovieHelper {

    public static Movie fromCursor(Cursor cursor) {
        final Movie ret = new Movie();
        ret.setId(cursor.getLong(cursor.getColumnIndex(MoviesDef.ID)));
        ret.setTitle(cursor.getString(cursor.getColumnIndex(MoviesDef.TITLE)));
        ret.setOverview(cursor.getString(cursor.getColumnIndex(MoviesDef.OVERVIEW)));
        ret.setReleaseDate(cursor.getString(cursor.getColumnIndex(MoviesDef.RELEASE_DATE)));
        ret.setBackdropPath(cursor.getString(cursor.getColumnIndex(MoviesDef.BACKDROP_PATH)));
        ret.setTopRated(cursor.getInt(cursor.getColumnIndex(MoviesDef.TOP_RATED)) > 0);
        ret.setFavourite(cursor.getInt(cursor.getColumnIndex(MoviesDef.FAVOURITE)) > 0);
        ret.setPopular(cursor.getInt(cursor.getColumnIndex(MoviesDef.POPULAR)) > 0);
        ret.setVoteCount(cursor.getInt(cursor.getColumnIndex(MoviesDef.VOTE_COUNT)));
        ret.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(MoviesDef.VOTE_AVERAGE)));
        ret.setPopularity(cursor.getDouble(cursor.getColumnIndex(MoviesDef.POPULARITY)));
        ret.setPosterPath(cursor.getString(cursor.getColumnIndex(MoviesDef.POSTER_PATH)));
        final int adultColumnIndex = cursor.getColumnIndex(MoviesDef.ADULT);
        ret.setAdult(cursor.isNull(adultColumnIndex) ? null : (cursor.getInt(adultColumnIndex) > 0));
        ret.setOriginalTitle(cursor.getString(cursor.getColumnIndex(MoviesDef.ORIGINAL_TITLE)));
        ret.setOriginalLanguage(cursor.getString(cursor.getColumnIndex(MoviesDef.ORIGINAL_LANGUAGE)));
        final int videoColumnIndex = cursor.getColumnIndex(MoviesDef.VIDEO);
        ret.setVideo(cursor.isNull(videoColumnIndex) ? null : (cursor.getInt(videoColumnIndex) > 0));
        return ret;
    }

    public static ContentValues toContentValue(Movie movie) {
        final ContentValues ret = new ContentValues();
        ret.put(MoviesDef.ID, movie.id);
        ret.put(MoviesDef.TITLE, movie.title);
        ret.put(MoviesDef.OVERVIEW, movie.overview);
        ret.put(MoviesDef.RELEASE_DATE, movie.releaseDate);
        ret.put(MoviesDef.BACKDROP_PATH, movie.backdropPath);
        ret.put(MoviesDef.TOP_RATED, movie.topRated ? 1 : 0);
        ret.put(MoviesDef.FAVOURITE, movie.favourite ? 1 : 0);
        ret.put(MoviesDef.POPULAR, movie.popular ? 1 : 0);
        ret.put(MoviesDef.VOTE_COUNT, movie.voteCount);
        ret.put(MoviesDef.VOTE_AVERAGE, movie.voteAverage);
        ret.put(MoviesDef.POPULARITY, movie.popularity);
        ret.put(MoviesDef.POSTER_PATH, movie.posterPath);
        ret.put(MoviesDef.ADULT, movie.adult == null ? null : movie.adult ? 1 : 0);
        ret.put(MoviesDef.ORIGINAL_TITLE, movie.originalTitle);
        ret.put(MoviesDef.ORIGINAL_LANGUAGE, movie.originalLanguage);
        ret.put(MoviesDef.VIDEO, movie.video == null ? null : movie.video ? 1 : 0);
        return ret;
    }
}
