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
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import pl.selvin.android.popularmovies.data.MoviesDatabase.VideosDef;

import static pl.selvin.android.popularmovies.utils.Constants.YOUTUBE_BASE_URL;

@SuppressWarnings("unused,WeakerAccess")
public class Video {
    @SuppressWarnings("NullableProblems")
    @SerializedName("id")
    @NonNull
    private String id;

    @SerializedName("key")
    private String key;

    @SerializedName("name")
    private String name;

    @SerializedName("site")
    private String site;

    @SerializedName("size")
    private Integer size;

    @SerializedName("type")
    private String type;

    private long movieId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Video item = (Video) o;
        return id.equals(item.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode() + 7;
    }


    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Uri getVideoUri() {
        return Uri.parse(YOUTUBE_BASE_URL + key);
    }

    public ContentValues toContentValue() {
        final ContentValues ret = new ContentValues();
        ret.put(VideosDef.ID, id);
        ret.put(VideosDef.KEY, key);
        ret.put(VideosDef.NAME, name);
        ret.put(VideosDef.SITE, site);
        ret.put(VideosDef.MOVIE_ID, movieId);
        ret.put(VideosDef.SIZE, size);
        ret.put(VideosDef.TYPE, type);
        return ret;
    }

    public static Video fromCursor(Cursor cursor) {
        final Video ret = new Video();
        ret.setId(cursor.getString(cursor.getColumnIndex(VideosDef.ID)));
        ret.setKey(cursor.getString(cursor.getColumnIndex(VideosDef.KEY)));
        ret.setName(cursor.getString(cursor.getColumnIndex(VideosDef.NAME)));
        ret.setSite(cursor.getString(cursor.getColumnIndex(VideosDef.SITE)));
        ret.setMovieId(cursor.getLong(cursor.getColumnIndex(VideosDef.MOVIE_ID)));
        final int sizeColumnIndex = cursor.getColumnIndex(VideosDef.SIZE);
        ret.setSize(cursor.isNull(sizeColumnIndex) ? null :  cursor.getInt(sizeColumnIndex));
        ret.setType(cursor.getString(cursor.getColumnIndex(VideosDef.TYPE)));
        return ret;
    }
}
