package com.example.argumentmapper;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.example.argumentmapper.dagger.ApplicationComponent;
import com.example.argumentmapper.dagger.ApplicationModule;
import com.example.argumentmapper.dagger.DaggerApplicationComponent;
import com.example.argumentmapper.ui.LoginActivity;


public class ArgumentMapperApplication extends Application {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build();
    }

    public void redirectToLogin()
    {
        this.getSharedPreferences("default", Context.MODE_PRIVATE).edit().remove("access_token").apply();
        Intent intent = new Intent(this, LoginActivity.class);
        this.startActivity(intent);
    }

    public ApplicationComponent getApplicationComponent()
    {
        return applicationComponent;
    }
}
