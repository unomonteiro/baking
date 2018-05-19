package io.monteirodev.baking.api;

import java.util.List;

import io.monteirodev.baking.models.Recipe;
import retrofit2.Call;
import retrofit2.http.GET;

public interface BakingInterface {
    @GET("topher/2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> getRecipes();
}
