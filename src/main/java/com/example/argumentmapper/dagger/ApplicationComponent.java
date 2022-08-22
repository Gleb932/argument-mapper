package com.example.argumentmapper.dagger;


import com.example.argumentmapper.LoginActivity;
import com.example.argumentmapper.MainActivity;
import com.example.argumentmapper.RegistrationActivity;

import dagger.Component;

@ApplicationScope
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(LoginActivity loginActivity);
    void inject(RegistrationActivity registrationActivity);
    void inject(MainActivity mainActivity);
    @Component.Builder
    interface Builder {
        ApplicationComponent build();
        Builder applicationModule(ApplicationModule appModule);
    }
}
