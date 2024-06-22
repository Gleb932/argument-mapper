package com.example.argumentmapper.interceptors;

import com.example.argumentmapper.exceptions.ConnectionException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ConnectionInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
        try
        {
            Request.Builder builder = chain.request().newBuilder();
            return chain.proceed(builder.build());
        }catch(SocketTimeoutException e)
        {
            throw new ConnectionException();
        }
    }
}
