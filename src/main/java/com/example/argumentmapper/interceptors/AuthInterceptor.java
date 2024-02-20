package com.example.argumentmapper.interceptors;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.argumentmapper.exceptions.AuthException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final SharedPreferences sharedPreferences;
    private final Context context;

    public AuthInterceptor(Context context, SharedPreferences sharedPreferences)
    {
        this.context = context;
        this.sharedPreferences = sharedPreferences;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException  {
        List<String> urlSegments = chain.request().url().pathSegments();
        if(urlSegments.get(0).equalsIgnoreCase("auth")) return chain.proceed(chain.request());
        String token = sharedPreferences.getString("access_token", null);
        if(token == null)
        {
            throw new AuthException();
        }
        Request newRequest = chain.request().newBuilder()
                .addHeader("authorization", "Bearer " + token)
                .build();

        Response response = chain.proceed(newRequest);
        if(response.code() == 401)
        {
            throw new AuthException();
        }else
        {
            return response;
        }
    }
}
