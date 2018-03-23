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

import pl.selvin.android.popularmovies.data.MoviesDatabase.MovieDetailsDef;

public class MovieDetailsHelper {
    public static MovieDetails fromCursor(Cursor cursor) {
        final MovieDetails ret = new MovieDetails();
        ret.setMovieId(cursor.getLong(cursor.getColumnIndex(MovieDetailsDef.MOVIE_ID)));
        final int runtimeColumnIndex = cursor.getColumnIndex(MovieDetailsDef.RUNTIME);
        ret.setRuntime(cursor.isNull(runtimeColumnIndex) ? null : cursor.getInt(runtimeColumnIndex));
        ret.setStatus(cursor.getString(cursor.getColumnIndex(MovieDetailsDef.STATUS)));
        ret.setTagLine(cursor.getString(cursor.getColumnIndex(MovieDetailsDef.TAGLINE)));
        return ret;
    }

    public static ContentValues toContentValue(MovieDetails movieDetails) {
        final ContentValues ret = new ContentValues();
        ret.put(MovieDetailsDef.MOVIE_ID, movieDetails.movieId);
        ret.put(MovieDetailsDef.RUNTIME, movieDetails.runtime);
        ret.put(MovieDetailsDef.STATUS, movieDetails.status);
        ret.put(MovieDetailsDef.TAGLINE, movieDetails.tagLine);
        return ret;
    }
}
