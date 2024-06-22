package com.example.argumentmapper.dagger;


import com.example.argumentmapper.ui.ArgumentMapActivity;
import com.example.argumentmapper.ui.LoginActivity;
import com.example.argumentmapper.ui.MainActivity;
import com.example.argumentmapper.ui.RegistrationActivity;

import dagger.Component;

@ApplicationScope
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(LoginActivity loginActivity);
    void inject(RegistrationActivity registrationActivity);
    void inject(MainActivity mainActivity);
    void inject(ArgumentMapActivity argumentMapActivity);
    @Component.Builder
    interface Builder {
        ApplicationComponent build();
        Builder applicationModule(ApplicationModule appModule);
    }
}
