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


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.selvin.android.popularmovies.adapters.MoviesAdapter;
import pl.selvin.android.popularmovies.viewmodels.MoviesListViewModel;
import pl.selvin.android.popularmovies.viewmodels.MoviesListViewModel.MoviesToShow;

@SuppressWarnings("WeakerAccess")
public class MoviesListActivity extends AppCompatActivity implements MoviesAdapter.ViewHolderListener {

    private static final int SHOW_DETAILS = 666;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.movies_list_activity_list)
    RecyclerView recyclerView;
    @BindView(R.id.movies_list_activity_progress)
    View progress;
    @BindView(R.id.movies_list_activity_bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.movies_list_activity_coordinator_layout)
    View coordinatorLayout;
    private MoviesListViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_list_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        model = ViewModelProviders.of(this).get(MoviesListViewModel.class);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onOptionsItemSelected);
        recyclerView.setLayoutManager(new GridLayoutManager(MoviesListActivity.this, getResources().getInteger(R.integer.movies_span_count)));
        final MoviesAdapter adapter = new MoviesAdapter(MoviesListActivity.this, new ArrayList<>(), MoviesListActivity.this);
        recyclerView.setAdapter(adapter);
        model.movies.observe(this, moviesData -> {
            if (moviesData != null) {
                if (moviesData.data != null) {
                    adapter.setMovies(moviesData.data);
                }
                switch (moviesData.status) {
                    case ERROR:
                        Snackbar.make(coordinatorLayout, moviesData.message != null ? moviesData.message : "An error appear", Snackbar.LENGTH_SHORT)
                                .show();
                    case SUCCESS:
                        progress.setVisibility(View.GONE);
                        break;
                    default:
                        if (moviesData.data == null) {
                            progress.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            } else {
                progress.setVisibility(View.VISIBLE);
            }
        });
    }

    public int setupTitleAndGetSelectedMenu() {
        switch (model.getMoviesToShow()) {
            case POPULAR:
                setTitle(R.string.movies_list_menu_popular);
                return R.id.movies_list_menu_popular;
            case TOP_RATED:
                setTitle(R.string.movies_list_menu_top_rated);
                return R.id.movies_list_menu_top_rated;
            case FAVOURITE:
                setTitle(R.string.movies_list_menu_favourite);
                return R.id.movies_list_menu_favourite;
        }
        return -1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        final MenuInflater inflater = getMenuInflater();
        final int selectedItemId = setupTitleAndGetSelectedMenu();
        if (model.getShowBottomNavigation()) {
            bottomNavigationView.setVisibility(View.VISIBLE);
            bottomNavigationView.setSelectedItemId(selectedItemId);
        } else {
            inflater.inflate(R.menu.movies_list_activity_views, menu);
            menu.findItem(selectedItemId).setChecked(true);
            bottomNavigationView.setVisibility(View.GONE);
        }
        inflater.inflate(R.menu.movies_list_activity, menu);
        menu.findItem(R.id.movies_list_menu_show_bottom_navigation).setChecked(model.getShowBottomNavigation());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.movies_list_menu_popular:
                if (!item.isChecked())
                    item.setChecked(true);
                setTitle(R.string.movies_list_menu_popular);
                model.setMoviesToShow(MoviesToShow.POPULAR);
                return true;
            case R.id.movies_list_menu_top_rated:
                if (!item.isChecked())
                    item.setChecked(true);
                setTitle(R.string.movies_list_menu_top_rated);
                model.setMoviesToShow(MoviesToShow.TOP_RATED);
                return true;
            case R.id.movies_list_menu_favourite:
                if (!item.isChecked())
                    item.setChecked(true);
                setTitle(R.string.movies_list_menu_favourite);
                model.setMoviesToShow(MoviesToShow.FAVOURITE);
                return true;
            case R.id.movies_list_menu_show_bottom_navigation:
                boolean isChecked = !item.isChecked();
                item.setChecked(isChecked);
                model.setShowBottomNavigation(isChecked);
                invalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(MoviesAdapter.Holder holder) {
        MovieDetailsActivity.startDetailsActivityForResult(this, holder, SHOW_DETAILS);
    }
}
