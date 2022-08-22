package com.example.argumentmapper.dagger;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.argumentmapper.APIService;
import com.example.argumentmapper.BuildConfig;
import com.example.argumentmapper.interceptors.AuthInterceptor;
import com.example.argumentmapper.interceptors.ConnectionInterceptor;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ApplicationModule {
    Application app;
    SharedPreferences sharedPreferences;

    public ApplicationModule(Application app)
    {
        this.app = app;
        this.sharedPreferences = app.getSharedPreferences("default", Context.MODE_PRIVATE);
    }

    @ApplicationScope
    @Provides
    public OkHttpClient provideOkHttpClient()
    {
        return new OkHttpClient().newBuilder()
                .addInterceptor(new ConnectionInterceptor())
                .addInterceptor(new AuthInterceptor(app, sharedPreferences))
                .build();
    }
    @ApplicationScope
    @Provides
    static public Retrofit provideRetrofit(OkHttpClient client)
    {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    @ApplicationScope
    @Provides
    static public APIService provideAPIService(Retrofit retrofit) {
        return retrofit.create(APIService.class);
    }
    @ApplicationScope
    @Provides
    public SharedPreferences provideSharedPreferences(Application application) {
        return sharedPreferences;
    }
    @ApplicationScope
    @Provides
    Application providesApplication() {
        return app;
    }
}
