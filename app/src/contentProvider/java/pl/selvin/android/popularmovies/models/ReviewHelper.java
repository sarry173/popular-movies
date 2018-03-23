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

import pl.selvin.android.popularmovies.data.MoviesDatabase.ReviewsDef;

public class ReviewHelper {

    public static Review fromCursor(Cursor cursor) {
        final Review ret = new Review();
        ret.setMovieId(cursor.getLong(cursor.getColumnIndex(ReviewsDef.MOVIE_ID)));
        ret.setId(cursor.getString(cursor.getColumnIndex(ReviewsDef.ID)));
        ret.setAuthor(cursor.getString(cursor.getColumnIndex(ReviewsDef.AUTHOR)));
        ret.setContent(cursor.getString(cursor.getColumnIndex(ReviewsDef.CONTENT)));
        ret.setUrl(cursor.getString(cursor.getColumnIndex(ReviewsDef.URL)));
        return ret;
    }

    public static ContentValues toContentValue(Review review) {
        final ContentValues ret = new ContentValues();
        ret.put(ReviewsDef.MOVIE_ID, review.movieId);
        ret.put(ReviewsDef.ID, review.id);
        ret.put(ReviewsDef.AUTHOR, review.author);
        ret.put(ReviewsDef.CONTENT, review.content);
        ret.put(ReviewsDef.URL, review.url);
        return ret;
    }
}
