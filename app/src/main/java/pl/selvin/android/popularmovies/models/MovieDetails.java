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

import com.google.gson.annotations.SerializedName;

import pl.selvin.android.popularmovies.data.MoviesDatabase.MovieDetailsDef;

@SuppressWarnings("unused,WeakerAccess")
public class MovieDetails {
    @SerializedName("id")
    private long movieId;

    @SerializedName("runtime")
    private Integer runtime;

    @SerializedName("status")
    private String status;

    @SerializedName("tagline")
    private String tagLine;

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTagLine() {
        return tagLine;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MovieDetails item = (MovieDetails) o;
        return movieId == item.movieId;
    }

    @Override
    public int hashCode() {
        return (int)movieId + 7;
    }


    public static MovieDetails fromCursor(Cursor cursor) {
        final MovieDetails ret = new MovieDetails();
        ret.setMovieId(cursor.getLong(cursor.getColumnIndex(MovieDetailsDef.MOVIE_ID)));
        final int runtimeColumnIndex = cursor.getColumnIndex(MovieDetailsDef.RUNTIME);
        ret.setRuntime(cursor.isNull(runtimeColumnIndex) ? null :  cursor.getInt(runtimeColumnIndex));
        ret.setStatus(cursor.getString(cursor.getColumnIndex(MovieDetailsDef.STATUS)));
        ret.setTagLine(cursor.getString(cursor.getColumnIndex(MovieDetailsDef.TAGLINE)));
        return ret;
    }

    public ContentValues toContentValue() {
        final ContentValues ret = new ContentValues();
        ret.put(MovieDetailsDef.MOVIE_ID, movieId);
        ret.put(MovieDetailsDef.RUNTIME, runtime);
        ret.put(MovieDetailsDef.STATUS, status);
        ret.put(MovieDetailsDef.TAGLINE, tagLine);
        return ret;
    }
}
