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
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.selvin.android.popularmovies.adapters.MoviesAdapter;
import pl.selvin.android.popularmovies.viewmodels.MoviesListViewModel;

public class MoviesListActivity extends AppCompatActivity implements MoviesAdapter.ViewHolderListener {
    private static final int SHOW_DETAILS = 666;
    private final View.OnClickListener dismissOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
    private MoviesListViewModel model;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.movies_list_activity_list)
    RecyclerView recyclerView;
    @BindView(R.id.movies_list_activity_progress)
    View progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_list_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        model = ViewModelProviders.of(this).get(MoviesListViewModel.class);
        model.getMovies().observe(this, new Observer<MoviesListViewModel.MoviesData>() {
            @Override
            public void onChanged(@Nullable MoviesListViewModel.MoviesData moviesData) {
                if (moviesData != null) {
                    progress.setVisibility(View.GONE);
                    if (moviesData.successful) {
                        recyclerView.setVisibility(View.VISIBLE);
                        final int size = moviesData.movies.size();
                        recyclerView.setLayoutManager(new GridLayoutManager(MoviesListActivity.this,
                                Math.min(getResources().getInteger(R.integer.movies_span_count),  size < 1 ? 1 :size)));
                        recyclerView.setAdapter(new MoviesAdapter(MoviesListActivity.this, moviesData.movies, MoviesListActivity.this));
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        final Snackbar snackBar;
                        if (moviesData.errorRes == -1)
                            snackBar = Snackbar.make(findViewById(android.R.id.content), moviesData.errorString, Snackbar.LENGTH_INDEFINITE);
                        else
                            snackBar = Snackbar.make(findViewById(android.R.id.content), moviesData.errorRes, Snackbar.LENGTH_INDEFINITE);
                        snackBar.setAction(R.string.snackbar_dismiss, dismissOnClick).show();
                    }
                } else {
                    progress.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies_list_activity, menu);
        switch (model.getMoviesToShow()) {
            case MoviesListViewModel.MOVIES_TO_SHOW_POPULAR:
                setTitle(R.string.movies_list_menu_popular);
                menu.findItem(R.id.movies_list_menu_popular).setChecked(true);
                break;
            case MoviesListViewModel.MOVIES_TO_SHOW_TOP_RATED:
                setTitle(R.string.movies_list_menu_top_rated);
                menu.findItem(R.id.movies_list_menu_top_rated).setChecked(true);
                break;
            case MoviesListViewModel.MOVIES_TO_SHOW_FAVOURITE:
                setTitle(R.string.movies_list_menu_favourite);
                menu.findItem(R.id.movies_list_menu_favourite).setChecked(true);
                break;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.movies_list_menu_popular:
                if (!item.isChecked())
                    item.setChecked(true);
                setTitle(R.string.movies_list_menu_popular);
                model.setMoviesToShow(MoviesListViewModel.MOVIES_TO_SHOW_POPULAR);
                return true;
            case R.id.movies_list_menu_top_rated:
                if (!item.isChecked())
                    item.setChecked(true);
                setTitle(R.string.movies_list_menu_top_rated);
                model.setMoviesToShow(MoviesListViewModel.MOVIES_TO_SHOW_TOP_RATED);
                return true;
            case R.id.movies_list_menu_favourite:
                if (!item.isChecked())
                    item.setChecked(true);
                setTitle(R.string.movies_list_menu_favourite);
                model.setMoviesToShow(MoviesListViewModel.MOVIES_TO_SHOW_FAVOURITE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SHOW_DETAILS) {
            progress.setVisibility(View.GONE);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(MoviesAdapter.Holder holder) {
        progress.setVisibility(View.VISIBLE);
        MovieDetailsActivity.startDetailsActivityForResult(this, model.getMoviesToShow() == MoviesListViewModel.MOVIES_TO_SHOW_FAVOURITE,
                holder, progress, SHOW_DETAILS);
    }

    @Override
    public void onLoadCompleted(int position) {

    }
}
