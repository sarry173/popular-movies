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
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import pl.selvin.android.popularmovies.data.MoviesDatabase.ReviewsDef;

@SuppressWarnings("unused,WeakerAccess")
public class Review {
    @SuppressWarnings("NullableProblems")
    @SerializedName("id")
    @NonNull
    private String id;

    @SerializedName("author")
    private String author;

    @SerializedName("content")
    private String content;

    @SerializedName("url")
    private String url;

    private long movieId;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Review item = (Review) o;
        return id.equals(item.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode() + 7;
    }

    public ContentValues toContentValue() {
        final ContentValues ret = new ContentValues();
        ret.put(ReviewsDef.MOVIE_ID, movieId);
        ret.put(ReviewsDef.ID, id);
        ret.put(ReviewsDef.AUTHOR, author);
        ret.put(ReviewsDef.CONTENT, content);
        ret.put(ReviewsDef.URL, url);
        return ret;
    }

    public static Review fromCursor(Cursor cursor) {
        final Review ret = new Review();
        ret.setMovieId(cursor.getLong(cursor.getColumnIndex(ReviewsDef.MOVIE_ID)));
        ret.setId(cursor.getString(cursor.getColumnIndex(ReviewsDef.ID)));
        ret.setAuthor(cursor.getString(cursor.getColumnIndex(ReviewsDef.AUTHOR)));
        ret.setContent(cursor.getString(cursor.getColumnIndex(ReviewsDef.CONTENT)));
        ret.setUrl(cursor.getString(cursor.getColumnIndex(ReviewsDef.URL)));
        return ret;
    }
}
