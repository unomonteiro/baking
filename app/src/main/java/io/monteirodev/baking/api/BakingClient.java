package io.monteirodev.baking.api;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.gson.FieldNamingPolicy.*;

public class BakingClient {

    static final String Base_url = "https://d17h27t6h515a5.cloudfront.net";


    private static Retrofit retrofit = null;

    static Retrofit getClient() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES)
                    .create();
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
