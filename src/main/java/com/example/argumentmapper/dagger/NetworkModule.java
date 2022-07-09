package com.example.argumentmapper.dagger;

import com.example.argumentmapper.APIService;
import com.example.argumentmapper.BuildConfig;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {
    @ApplicationScope
    @Provides
    static public APIService provideAPIService() {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(APIService.class);
    }
}
