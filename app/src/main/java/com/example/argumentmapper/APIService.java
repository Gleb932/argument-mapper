package com.example.argumentmapper;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {
    @FormUrlEncoded
    @POST("/auth/login")
    Call<ResponseBody> login(
            @Field("username") String username,
            @Field("password") String password
    );
    @FormUrlEncoded
    @POST("/auth/register")
    Call<ResponseBody> register(
            @Field("email") String email,
            @Field("username") String username,
            @Field("password") String password
    );
    @GET("/api/test")
    Call<ResponseBody> authTest();
    @GET("/api/session/{sessionID}")
    Call<ResponseBody> getSessionMap(
            @Path("sessionID") int sessionID
    );
    @FormUrlEncoded
    @POST("/api/session")
    Call<ResponseBody> createSession(
            @Field("mapTree") String mapTree
    );
    @DELETE("/api/session/{sessionID}")
    Call<ResponseBody> deleteSession(
            @Path("sessionID") int sessionID
    );
}
