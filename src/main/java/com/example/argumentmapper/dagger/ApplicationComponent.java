package com.example.argumentmapper.dagger;

import com.example.argumentmapper.LoginActivity;
import com.example.argumentmapper.RegistrationActivity;

import dagger.Component;

@ApplicationScope
@Component(modules = NetworkModule.class)
public interface ApplicationComponent {
    void inject(LoginActivity loginActivity);
    void inject(RegistrationActivity registrationActivity);
}
