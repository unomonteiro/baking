package io.monteirodev.baking.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.monteirodev.baking.R;

public class RecipeActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_RECIPE_ID = "intent_extra_recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        int recipeId = getIntent().getIntExtra(INTENT_EXTRA_RECIPE_ID, 0);
    }
}
