package io.monteirodev.baking.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class BakingFirebaseJobService extends JobService {

    private AsyncTask<Void, Void, Void> mFetchRecipesTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        mFetchRecipesTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                SyncTask.syncRecipes(context);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(job, false);
            }
        };
        mFetchRecipesTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mFetchRecipesTask != null) {
            mFetchRecipesTask.cancel(true);
        }
        return true;
    }
}
