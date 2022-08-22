package com.example.argumentmapper;

import android.app.Application;

import com.example.argumentmapper.dagger.ApplicationComponent;
import com.example.argumentmapper.dagger.ApplicationModule;
import com.example.argumentmapper.dagger.DaggerApplicationComponent;


public class ArgumentMapperApplication extends Application {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build();
    }

    public ApplicationComponent getApplicationComponent()
    {
        return applicationComponent;
    }
}
