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


import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.os.CancellationSignal;
import android.support.v4.os.OperationCanceledException;

//Since I can't use Room and I did all the stuff already this class will load data from ContentProvider in LiveData manner
public abstract class CursorLoaderLiveData<T> extends LiveData<T> {

    @NonNull
    private final ForceLoadContentObserver observer;
    @Nullable
    private CancellationSignal cancellationSignal;
    @NonNull
    private final Context context;
    @NonNull
    private final Uri uri;
    @Nullable
    private final String[] projection;
    @Nullable
    private final String selection;
    @Nullable
    private final String[] selectionArgs;
    @Nullable
    private final String sortOrder;
    private Cursor oldCursor = null;

    public CursorLoaderLiveData(@NonNull Context context, @NonNull Uri uri, @SuppressWarnings("SameParameterValue") @Nullable String[] projection,
                                @Nullable String selection, @Nullable String[] selectionArgs,
                                @Nullable String sortOrder) {
        this.context = context.getApplicationContext();
        observer = new ForceLoadContentObserver();
        this.uri = uri;
        this.projection = projection;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
        this.sortOrder = sortOrder;
    }

    private void loadData() {
        loadData(false);
    }

    @SuppressLint("StaticFieldLeak")
    private void loadData(boolean forceQuery) {
        if (!forceQuery) {
            if (oldCursor != null
                    && !oldCursor.isClosed()) {
                return;
            }
        }

        new AsyncTask<Void, Void, T>() {

            @Override
            protected T doInBackground(Void... params) {
                try {
                    synchronized (CursorLoaderLiveData.this) {
                        cancellationSignal = new CancellationSignal();
                    }
                    try {
                        Cursor cursor = ContentResolverCompat.query(
                                context.getContentResolver(),
                                uri,
                                projection,
                                selection,
                                selectionArgs,
                                sortOrder,
                                cancellationSignal
                        );
                        if (cursor != null) {
                            try {
                                // Ensure the cursor window is filled.
                                cursor.getCount();
                                cursor.registerContentObserver(observer);
                            } catch (RuntimeException ex) {
                                cursor.close();
                                throw ex;
                            }
                        }
                        setNewCursor(cursor);

                        return dataFromCursor(cursor);
                    } finally {
                        synchronized (CursorLoaderLiveData.this) {
                            cancellationSignal = null;
                        }
                    }
                } catch (OperationCanceledException e) {
                    if (hasActiveObservers()) {
                        throw e;
                    }
                    setNewCursor(null);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(T data) {
                setValue(data);
            }

        }.execute();
    }

    protected abstract T dataFromCursor(Cursor cursor);

    public final class ForceLoadContentObserver
            extends ContentObserver {

        ForceLoadContentObserver() {
            super(new Handler());
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            loadData(true);
        }
    }


    private void setNewCursor(Cursor newCursor) {
        if (oldCursor != null) {
            oldCursor.close();
        }
        oldCursor = newCursor;
    }

    @Override
    protected void onActive() {
        loadData();
    }

    @Override
    protected void onInactive() {
        synchronized (CursorLoaderLiveData.this) {
            if (cancellationSignal != null) {
                cancellationSignal.cancel();
            }
        }
    }
}
