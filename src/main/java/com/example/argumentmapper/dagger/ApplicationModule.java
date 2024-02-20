package com.example.argumentmapper.dagger;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.argumentmapper.APIService;
import com.example.argumentmapper.BuildConfig;
import com.example.argumentmapper.DeductiveNode;
import com.example.argumentmapper.InductiveNode;
import com.example.argumentmapper.MapNode;
import com.example.argumentmapper.interceptors.AuthInterceptor;
import com.example.argumentmapper.interceptors.ConnectionInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

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
    public SharedPreferences provideSharedPreferences() {
        return sharedPreferences;
    }
    @ApplicationScope
    @Provides
    Context providesContext(){return app;}
    @ApplicationScope
    @Provides
    static public Gson providesGson()
    {
        GsonBuilder gsonBuilder = new GsonBuilder();
        JsonDeserializer<MapNode> mapNodeDeserializer = new JsonDeserializer<MapNode>() {
            @Override
            public MapNode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jsonNode = json.getAsJsonObject();
                MapNode node;
                if(jsonNode.has("operator"))
                {
                    node = new DeductiveNode(jsonNode);
                }else{
                    node = new InductiveNode(jsonNode);
                }
                JsonArray jsonChildren = jsonNode.get("children").getAsJsonArray();
                for(int i = 0; i < jsonChildren.size(); i++)
                {
                    node.addChild(deserialize(jsonChildren.get(i), typeOfT, context));
                }
                return node;
            }
        };
        gsonBuilder.registerTypeAdapter(MapNode.class, mapNodeDeserializer);
        gsonBuilder.registerTypeAdapter(InductiveNode.class, mapNodeDeserializer);
        gsonBuilder.registerTypeAdapter(DeductiveNode.class, mapNodeDeserializer);
        return gsonBuilder.create();
    }
}
