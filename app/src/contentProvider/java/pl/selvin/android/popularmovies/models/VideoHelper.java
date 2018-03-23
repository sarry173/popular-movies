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

import pl.selvin.android.popularmovies.data.MoviesDatabase.VideosDef;

public class VideoHelper {

    public static Video fromCursor(Cursor cursor) {
        final Video ret = new Video();
        ret.setId(cursor.getString(cursor.getColumnIndex(VideosDef.ID)));
        ret.setKey(cursor.getString(cursor.getColumnIndex(VideosDef.KEY)));
        ret.setName(cursor.getString(cursor.getColumnIndex(VideosDef.NAME)));
        ret.setSite(cursor.getString(cursor.getColumnIndex(VideosDef.SITE)));
        ret.setMovieId(cursor.getLong(cursor.getColumnIndex(VideosDef.MOVIE_ID)));
        final int sizeColumnIndex = cursor.getColumnIndex(VideosDef.SIZE);
        ret.setSize(cursor.isNull(sizeColumnIndex) ? null : cursor.getInt(sizeColumnIndex));
        ret.setType(cursor.getString(cursor.getColumnIndex(VideosDef.TYPE)));
        return ret;
    }

    public static ContentValues toContentValue(Video video) {
        final ContentValues ret = new ContentValues();
        ret.put(VideosDef.ID, video.id);
        ret.put(VideosDef.KEY, video.key);
        ret.put(VideosDef.NAME, video.name);
        ret.put(VideosDef.SITE, video.site);
        ret.put(VideosDef.MOVIE_ID, video.movieId);
        ret.put(VideosDef.SIZE, video.size);
        ret.put(VideosDef.TYPE, video.type);
        return ret;
    }
}
