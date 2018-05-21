package io.monteirodev.baking;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.facebook.stetho.Stetho;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.monteirodev.baking.database.BakingProvider;
import io.monteirodev.baking.database.RecipeColumns;
import io.monteirodev.baking.sync.SyncUtils;
import io.monteirodev.baking.utils.NetworkUtils;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,RecipeAdapter.RecipeClickListener {

    private static final int ID_RECIPE_LOADER = 1;

    @BindView(R.id.recipes_recycler_view)
    RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;
    @BindView(R.id.loading_view)
    View mLoadingView;

    LinearLayoutManager mLayoutManager;
    private RecipeAdapter mRecipeAdapter;


    static final String[] RECIPES_PROJECTION = {
            RecipeColumns.ID,
            RecipeColumns.NAME,
            RecipeColumns.IMAGE
    };

    private Snackbar mSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecipeAdapter = new RecipeAdapter(this);
        mRecyclerView.setAdapter(mRecipeAdapter);
        mRecyclerView.setHasFixedSize(true);

        showLoading();

        getSupportLoaderManager().initLoader(ID_RECIPE_LOADER, null, this);
        SyncUtils.initialise(this);
    }

    @Override
    @NonNull
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle args) {
        switch (loaderId) {
            case ID_RECIPE_LOADER:
                return new CursorLoader(MainActivity.this, BakingProvider.Recipes.CONTENT_URI,
                        RECIPES_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case ID_RECIPE_LOADER:
                if (data == null || data.getCount() == 0) {
                    checkInternet();
                } else {
                    mRecipeAdapter.swapCursor(data);
                    if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
                    mRecyclerView.smoothScrollToPosition(mPosition);
                    showRecipeList();
                }
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        switch (loader.getId()) {
            case ID_RECIPE_LOADER:
                mRecipeAdapter.swapCursor(null);
                showLoading();
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + loader.getId());
        }
    }

    private void checkInternet() {
        if (!NetworkUtils.isOnline(this)) {
            showOfflineSnack();
        }
    }

    private void showOfflineSnack() {
        mSnackbar = Snackbar.make(findViewById(android.R.id.content),
                R.string.oops_check_internet, Snackbar.LENGTH_INDEFINITE);
        mSnackbar.setAction(R.string.retry, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SyncUtils.startImmediateSync(MainActivity.this);
                mSnackbar.dismiss();
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
    public void onRecipeClick(int recipeId) {
        Timber.d( "onRecipeClick: " + recipeId);
    }
}
