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

import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import pl.selvin.android.autocontentprovider.content.AutoContentProvider;
import pl.selvin.android.autocontentprovider.content.ContentHelper;
import pl.selvin.android.autocontentprovider.impl.DefaultDatabaseInfoFactory;
import pl.selvin.android.autocontentprovider.log.Logger;
import pl.selvin.android.autocontentprovider.utils.SupportSQLiteOpenHelperFactoryProvider;
import pl.selvin.android.popularmovies.BuildConfig;
import pl.selvin.android.popularmovies.data.MoviesDatabase.MovieDetailsDef;
import pl.selvin.android.popularmovies.data.MoviesDatabase.MoviesDef;
import pl.selvin.android.popularmovies.data.MoviesDatabase.ReviewsDef;
import pl.selvin.android.popularmovies.data.MoviesDatabase.VideosDef;

//AutoContentProvider and never write CREATE TABLE by yourself again
//this is from https://github.com/SelvinPL/SyncFrameworkAndroid/tree/master/auto-content-provider
//and this is my library
public class MoviesContentProvider extends AutoContentProvider {
    public final static int MOVIES_BULK_INSERT_WITH_RESET_FLAG_TOP_RATED = 0;
    public final static int MOVIES_BULK_INSERT_WITH_RESET_FLAG_POPULAR = 1;
    public final static String AUTHORITY = BuildConfig.APPLICATION_ID;
    private final static int DATABASE_VERSION = 7;
    private final static String DATABASE_NAME = "movies";
    public final static ContentHelper HELPER = new ContentHelper(MoviesDatabase.class, AUTHORITY,
            new DefaultDatabaseInfoFactory(), DATABASE_NAME, DATABASE_VERSION);
    private final Map<String, String> MAP_MOVIES_WITH_DETAILS = new HashMap<>();
    private final UriMatcher matcher;
    private final int MOVIES_BULK_INSERT_WITH_RESET_FLAG = 1;
    private final int MOVIE_DETAILS = 2;
    private final int MOVIES_WITH_DETAILS = 3;
    private final int REVIEWS = 4;
    private final int VIDEOS = 5;

    public MoviesContentProvider() {
        super(HELPER, new Logger("Movies"), ctx -> new FrameworkSQLiteOpenHelperFactory());
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, MoviesDef.MOVIES_BULK_INSERT_WITH_RESET_FLAG + "/#", MOVIES_BULK_INSERT_WITH_RESET_FLAG);
        matcher.addURI(AUTHORITY, MovieDetailsDef.TABLE_NAME, MOVIE_DETAILS);
        matcher.addURI(AUTHORITY, ReviewsDef.TABLE_NAME, REVIEWS);
        matcher.addURI(AUTHORITY, VideosDef.TABLE_NAME, VIDEOS);
        matcher.addURI(AUTHORITY, MoviesDef.MOVIES_WITH_DETAILS, MOVIES_WITH_DETAILS);
        MAP_MOVIES_WITH_DETAILS.putAll(HELPER.getTableFromType(MoviesDef.TABLE_NAME).map);
        MAP_MOVIES_WITH_DETAILS.putAll(HELPER.getTableFromType(MovieDetailsDef.TABLE_NAME).map);
    }

    @Override
    protected SupportSQLiteOpenHelper.Callback getHelperCallback() {
        final SupportSQLiteOpenHelper.Callback defaultCallback = super.getHelperCallback();
        return new SupportSQLiteOpenHelper.Callback(DATABASE_VERSION) {
            @Override
            public void onCreate(SupportSQLiteDatabase db) {
                defaultCallback.onCreate(db);
            }

            @Override
            public void onUpgrade(SupportSQLiteDatabase db, int oldVersion, int newVersion) {
                defaultCallback.onUpgrade(db, oldVersion, newVersion);
            }

            @Override
            public void onDowngrade(SupportSQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
    }

    //overriding bulkInset for some special cases
    @Override
    public synchronized int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        switch (matcher.match(uri)) {
            case MOVIES_BULK_INSERT_WITH_RESET_FLAG: {
                getWritableDatabase().beginTransaction();
                final int flag = Integer.parseInt(uri.getPathSegments().get(1));
                final String RESET_COLUMN = flag == MOVIES_BULK_INSERT_WITH_RESET_FLAG_TOP_RATED ? MoviesDef.TOP_RATED : MoviesDef.POPULAR;
                getWritableDatabase().execSQL("UPDATE " + MoviesDef.TABLE_NAME + " SET " + RESET_COLUMN + "=0 WHERE " + RESET_COLUMN + "=1");
                final Object[] args = new Object[1];
                for (ContentValues value : values) {
                    final long id = value.getAsLong(MoviesDef.ID);
                    args[0] = id;
                    final Cursor temp = getReadableDatabase().query("SELECT " + MoviesDef.TOP_RATED + "," + MoviesDef.POPULAR + "," + MoviesDef.FAVOURITE
                            + " FROM " + MoviesDef.TABLE_NAME + " WHERE " + MoviesDef.ID + "=?", args);
                    if (temp.moveToFirst()) {
                        if (flag == MOVIES_BULK_INSERT_WITH_RESET_FLAG_TOP_RATED)
                            value.put(MoviesDef.POPULAR, temp.getInt(1));
                        else
                            value.put(MoviesDef.TOP_RATED, temp.getInt(0));
                        value.put(MoviesDef.FAVOURITE, temp.getInt(2));
                        getWritableDatabase().update(MoviesDef.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, value, MoviesDef.ID + "=?", args);
                    } else {
                        getWritableDatabase().insert(MoviesDef.TABLE_NAME, SQLiteDatabase.CONFLICT_FAIL, value);
                    }
                    temp.close();
                }
                getWritableDatabase().setTransactionSuccessful();
                getWritableDatabase().endTransaction();
                final ContentResolver cr = getContextOrThrow().getContentResolver();
                cr.notifyChange(HELPER.getDirUri(MoviesDef.TABLE_NAME), null, false);
                return values.length;
            }
            case MOVIE_DETAILS: {
                getWritableDatabase().beginTransaction();
                final Object[] args = new Object[1];
                for (ContentValues value : values) {
                    final long id = value.getAsLong(MovieDetailsDef.MOVIE_ID);
                    args[0] = id;
                    if (getWritableDatabase().update(MovieDetailsDef.TABLE_NAME, SQLiteDatabase.CONFLICT_FAIL, value, MovieDetailsDef.MOVIE_ID + "=?", args) == 0)
                        getWritableDatabase().insert(MovieDetailsDef.TABLE_NAME, SQLiteDatabase.CONFLICT_FAIL, value);
                }
                getWritableDatabase().setTransactionSuccessful();
                getWritableDatabase().endTransaction();
                final ContentResolver cr = getContextOrThrow().getContentResolver();
                cr.notifyChange(uri, null, false);
                cr.notifyChange(Uri.parse(MoviesDef.MOVIES_WITH_DETAILS_URI), null, false);
                return values.length;
            }
            case REVIEWS: {
                getWritableDatabase().beginTransaction();
                final Object[] args = new Object[1];
                for (ContentValues value : values) {
                    final String id = value.getAsString(ReviewsDef.ID);
                    args[0] = id;
                    if (getWritableDatabase().update(ReviewsDef.TABLE_NAME, SQLiteDatabase.CONFLICT_FAIL, value, ReviewsDef.ID + "=?", args) == 0)
                        getWritableDatabase().insert(ReviewsDef.TABLE_NAME, SQLiteDatabase.CONFLICT_FAIL, value);
                }
                getWritableDatabase().setTransactionSuccessful();
                getWritableDatabase().endTransaction();
                final ContentResolver cr = getContextOrThrow().getContentResolver();
                cr.notifyChange(uri, null, false);
                return values.length;
            }
            case VIDEOS: {
                getWritableDatabase().beginTransaction();
                final Object[] args = new Object[1];
                for (ContentValues value : values) {
                    final String id = value.getAsString(VideosDef.ID);
                    args[0] = id;
                    if (getWritableDatabase().update(VideosDef.TABLE_NAME, SQLiteDatabase.CONFLICT_FAIL, value, VideosDef.ID + "=?", args) == 0)
                        getWritableDatabase().insert(VideosDef.TABLE_NAME, SQLiteDatabase.CONFLICT_FAIL, value);
                }
                getWritableDatabase().setTransactionSuccessful();
                getWritableDatabase().endTransaction();
                final ContentResolver cr = getContextOrThrow().getContentResolver();
                cr.notifyChange(uri, null, false);
                return values.length;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    //AutoContentProvider provide query implementation only for single tables ....
    //if you need JOINs then you have to implement by yourself ... I will add VIEWS implementation to AutoContentProvider ... someday
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        final Map<String, String> projectionMap;
        final String groupBy;
        final String having;
        switch (matcher.match(uri)) {
            case MOVIES_WITH_DETAILS:
                builder.setTables(MoviesDef.TABLE_NAME + " LEFT OUTER JOIN " + MovieDetailsDef.TABLE_NAME + " ON " + MoviesDef.ID + "=" + MovieDetailsDef.MOVIE_ID);
                projectionMap = MAP_MOVIES_WITH_DETAILS;
                groupBy = null;
                having = null;
                break;
            default:
                return super.query(uri, projection, selection, selectionArgs, sortOrder);
        }
        builder.setProjectionMap(projectionMap);
        //noinspection ConstantConditions
        logger.LogQuery(getClass(), uri, builder, projection, selection, selectionArgs, groupBy, null, sortOrder, null);
        //noinspection ConstantConditions
        final SupportSQLiteQuery query = new SimpleSQLiteQuery(builder.buildQuery(projection, selection, selectionArgs, groupBy, having, sortOrder, null), selectionArgs);
        final Cursor cursor = getReadableDatabase().query(query);
        if (cursor != null && getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }
}
