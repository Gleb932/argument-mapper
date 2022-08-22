package com.example.argumentmapper.interceptors;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.argumentmapper.LoginActivity;
import com.example.argumentmapper.exceptions.AuthException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final SharedPreferences sharedPreferences;
    private final Context context;

    @Inject
    public AuthInterceptor(Context context, SharedPreferences sharedPreferences)
    {
        this.context = context;
        this.sharedPreferences = sharedPreferences;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException  {
        String path = chain.request().url().encodedPath();
        if("/login".equalsIgnoreCase(path) || "/register".equalsIgnoreCase(path)) return chain.proceed(chain.request());
        String token = sharedPreferences.getString("access_token", null);
        if(token == null)
        {
            redirectToLogin();
            throw new AuthException();
        }
        Request newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Response response = chain.proceed(newRequest);
        if(response.code() == 401)
        {
            redirectToLogin();
            throw new AuthException();
        }else
        {
            return response;
        }
    }

    void redirectToLogin()
    {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
