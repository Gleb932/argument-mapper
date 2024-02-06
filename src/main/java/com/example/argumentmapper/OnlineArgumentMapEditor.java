package com.example.argumentmapper;

import android.content.SharedPreferences;

import com.example.argumentmapper.exceptions.AuthException;

import okhttp3.OkHttpClient;

public class OnlineArgumentMapEditor extends OfflineArgumentMapEditor implements WebSocketListener {
    private TokenExpirationHandler handler;
    private SharedPreferences sharedPreferences;
    private OkHttpClient client;
    private int sessionID;
    private SessionWebSocket sessionWebSocket;

    public OnlineArgumentMapEditor(TokenExpirationHandler handler, SharedPreferences sharedPreferences, OkHttpClient okHttpClient, int sessionID) {
        this.handler = handler;
        this.sharedPreferences = sharedPreferences;
        this.client = okHttpClient;
        this.sessionID = sessionID;
        try {
            openNewWebSocket();
        } catch (AuthException e) {
            handler.fixToken();
        }
    }

    void openNewWebSocket() throws AuthException
    {
        String token = sharedPreferences.getString("access_token", null);
        sessionWebSocket = new SessionWebSocket(this, token, client, sessionID);
    }

    @Override
    public boolean addChild(MapNode parent, MapNode child) {

        return super.addChild(parent, child);
    }

    @Override
    public boolean removeChild(MapNode child) {

        return super.removeChild(child);
    }

    @Override
    public boolean replaceChild(MapNode oldChild, MapNode newChild) {

        return super.replaceChild(oldChild, newChild);
    }

    @Override
    public void onMessageSuccess() {

    }

    @Override
    public void onMessageDenial() {

    }

    @Override
    public void onAuthError() {
        handler.fixToken();
    }
}
