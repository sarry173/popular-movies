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

package pl.selvin.android.popularmovies;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import pl.selvin.android.popularmovies.adapters.MoviesAdapter;
import pl.selvin.android.popularmovies.viewmodels.MoviesListViewModel;

public class MoviesListActivity extends AppCompatActivity implements MoviesAdapter.ViewHolderListener {
    private final View.OnClickListener dismissOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_list_activity);
        setSupportActionBar(this.<Toolbar>findViewById(R.id.toolbar));
        recyclerView = findViewById(R.id.movies_list_activity_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.movies_span_count)));
        MoviesListViewModel model = ViewModelProviders.of(this).get(MoviesListViewModel.class);
        model.getMovies().observe(this, new Observer<MoviesListViewModel.MoviesData>() {
            @Override
            public void onChanged(@Nullable MoviesListViewModel.MoviesData moviesData) {
                if (moviesData != null)
                    if (moviesData.successful) {
                        recyclerView.setAdapter(new MoviesAdapter(MoviesListActivity.this, moviesData.movies, MoviesListActivity.this));
                    }
                    else {
                        final Snackbar snackBar;
                        if (moviesData.errorRes == -1)
                            snackBar = Snackbar.make(findViewById(android.R.id.content), moviesData.errorString, Snackbar.LENGTH_INDEFINITE);
                        else
                            snackBar = Snackbar.make(findViewById(android.R.id.content), moviesData.errorRes, Snackbar.LENGTH_INDEFINITE);
                        snackBar.setAction(R.string.snackbar_dismiss, dismissOnClick).show();
                    }
            }
        });

    }

    @Override
    public void onItemClick(MoviesAdapter.Holder holder) {
        MovieDetailsActivity.startDetailsActivity(this, holder);
    }

    @Override
    public void onLoadCompleted(int position) {

    }
}
