package com.example.argumentmapper;

import android.content.SharedPreferences;

import com.example.argumentmapper.exceptions.AuthException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;

import okhttp3.OkHttpClient;

public class OnlineArgumentMapEditor implements ArgumentMapEditor, WebSocketListener {
    private OfflineArgumentMapEditor offlineEditor;
    private Gson gson;
    private TokenExpirationHandler handler;
    private SharedPreferences sharedPreferences;
    private OkHttpClient client;
    private int sessionID;
    private SessionWebSocket sessionWebSocket;

    public OnlineArgumentMapEditor(Gson gson, TokenExpirationHandler handler, SharedPreferences sharedPreferences, OkHttpClient okHttpClient, int sessionID) {
        this.gson = gson;
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

    public boolean addChild(MapNode parent, MapNode child) {
        String parentPath = StringUtils.join(parent.getPath(),";");
        JsonObject childJson = gson.toJsonTree(child).getAsJsonObject();
        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("operation", "addChild");
        jsonRequest.addProperty("parentPath", parentPath);
        jsonRequest.add("child", childJson);
        sessionWebSocket.sendMessage(jsonRequest.toString());
        return false;
    }

    public boolean removeNode(MapNode node) {
        return offlineEditor.removeNode(node);
    }

    public boolean replaceNode(MapNode oldNode, MapNode newNode) {
        return offlineEditor.replaceNode(oldNode, newNode);
    }

    @Override
    public void onOperationSuccess() {

    }

    @Override
    public void onOperationFailure(int code) {

    }

    @Override
    public void onCommand(String jsonCommand) {

    }

    @Override
    public void onAuthError() {
        handler.fixToken();
    }
}
