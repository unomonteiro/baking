package io.monteirodev.baking.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.monteirodev.baking.IdlingResource.SimpleIdlingResource;
import io.monteirodev.baking.R;
import io.monteirodev.baking.database.BakingProvider;
import io.monteirodev.baking.database.RecipeColumns;
import io.monteirodev.baking.models.Recipe;
import io.monteirodev.baking.sync.SyncUtils;
import io.monteirodev.baking.utils.NetworkUtils;
import io.monteirodev.baking.widget.WidgetIntentService;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,RecipeAdapter.RecipeClickListener {

    private static final int ID_RECIPE_LOADER = 1;
    private static final String RECIPE_LIST_KEY = "recipe_list_key";
    public static final String RECIPE_ID_KEY = "recipe_id_key";
    public static final int INVALID_RECIPE_ID = -1;

    @BindView(R.id.recipes_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.loading_view)
    View mLoadingView;

    private RecipeAdapter mRecipeAdapter;

    private Snackbar mSnackbar;
    private ArrayList<Recipe> mRecipes;

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    private void setIdle(boolean idle) {
        if (mIdlingResource!= null){
            mIdlingResource.setIdleState(idle);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("OnCreate()");
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        boolean isTablet = getResources().getBoolean(R.bool.is_tablet);

        mRecyclerView.setLayoutManager(getDeviceLayoutManager(isTablet));

        mRecipeAdapter = new RecipeAdapter(this);
        mRecyclerView.setAdapter(mRecipeAdapter);
        mRecyclerView.setHasFixedSize(true);

        mIdlingResource = (SimpleIdlingResource) getIdlingResource();
        mIdlingResource.setIdleState(false);

        if (savedInstanceState == null || mRecipes == null || mRecipes.size() == 0) {
            showLoading();
            checkInternet();
        } else {
            mRecipes = savedInstanceState.getParcelable(RECIPE_LIST_KEY);
            mRecipeAdapter.setRecipes(mRecipes);
            showRecipeList();
        }

        SyncUtils.initialise(this);
    }

    private LinearLayoutManager getDeviceLayoutManager(boolean isTablet) {
        if (isTablet) {
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
            int numColumns = (int) (dpWidth / 200);
            return new GridLayoutManager(this, numColumns);
        } else {
            return new LinearLayoutManager(this);
        }
    }

    @Override
    @NonNull
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle args) {
        switch (loaderId) {
            case ID_RECIPE_LOADER:
                Timber.d("onCreateLoader " + loaderId);
                setIdle(false);
                return new CursorLoader(MainActivity.this, BakingProvider.Recipes.CONTENT_URI,
                        null,
                        null,
                        null,
                        RecipeColumns.NAME);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        int loaderId = loader.getId();
        switch (loaderId) {
            case ID_RECIPE_LOADER:
                Timber.d("onLoadFinished " + loaderId);
                if (data == null || data.getCount() == 0) {
                    checkInternet();
                } else {
                    ArrayList<Recipe> recipes = new ArrayList<>();
                    data.moveToPosition(-1);
                    while (data.moveToNext()) {
                        recipes.add(new Recipe(data));
                    }
                    mRecipes = recipes;
                    mRecipeAdapter.setRecipes(mRecipes);
                    showRecipeList();
                }
                setIdle(true);
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        switch (loader.getId()) {
            case ID_RECIPE_LOADER:
                mRecipeAdapter.setRecipes(null);
                showLoading();
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + loader.getId());
        }
    }

    private void checkInternet() {
        if (NetworkUtils.isOnline(this)) {
            getSupportLoaderManager().restartLoader(ID_RECIPE_LOADER, null, this);
            SyncUtils.startImmediateSync(MainActivity.this);
            if (mSnackbar != null) {
                mSnackbar.dismiss();
                mSnackbar = null;
            }
        } else {
            showOfflineSnack();
        }
    }

    private void showOfflineSnack() {
        mSnackbar = Snackbar.make(findViewById(android.R.id.content),
                R.string.oops_check_internet, Snackbar.LENGTH_INDEFINITE);
        mSnackbar.setAction(R.string.retry, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternet();
            }
        });
        mSnackbar.show();
    }

    private void showRecipeList() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra(RecipeActivity.INTENT_EXTRA_RECIPE, recipe);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RECIPE_LIST_KEY, mRecipes);
    }
}
