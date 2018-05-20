package io.monteirodev.baking.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class BakingSyncIntentService extends IntentService {

    public BakingSyncIntentService() {
        super("BakingSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SyncTask.syncRecipes(this);
    }
}
