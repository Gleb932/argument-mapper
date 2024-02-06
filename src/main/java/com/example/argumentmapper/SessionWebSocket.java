package com.example.argumentmapper;

import android.util.Log;

import com.example.argumentmapper.exceptions.AuthException;

import org.jetbrains.annotations.NotNull;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class SessionWebSocket extends WebSocketListener {
    private static final String TAG = SessionWebSocket.class.getName();
    private static final String sessionsRoute = "session";
    private static final String webSocketURL = BuildConfig.API_URL.replaceFirst("^http", "ws");
    private final com.example.argumentmapper.WebSocketListener listener;
    private WebSocket webSocket;

    public SessionWebSocket(com.example.argumentmapper.WebSocketListener listener, String token, OkHttpClient client, int sessionID) throws AuthException
    {
        this.listener = listener;
        if(token == null)
        {
            throw new AuthException();
        }
        Request request = new Request.Builder()
                .url(webSocketURL + sessionsRoute + "?sessionID=" + sessionID)
                .addHeader("authorization", "Bearer " + token)
                .build();
        webSocket = client.newWebSocket(request, this);
        //!!! Causes an error on second try to call the newWebSocket method !!!
        //client.dispatcher().executorService().shutdown();
    }

    @Override public void onOpen(WebSocket webSocket, @NotNull Response response) {
        webSocket.send("Hello...");
        webSocket.send("...World!");
        webSocket.close(1000, "Goodbye, World!");
    }

    @Override public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        Log.v(TAG, "MESSAGE: " + text);
    }

    @Override public void onMessage(@NotNull WebSocket webSocket, ByteString bytes) {
        Log.v(TAG, "MESSAGE: " + bytes.hex());
    }

    @Override public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        if(code == 1008)
        {
            listener.onAuthError();
        }
        webSocket.close(1000, null);
        Log.v(TAG, "CLOSE: " + code + " " + reason);
    }

    //network error
    @Override public void onFailure(@NotNull WebSocket webSocket, Throwable t, Response response) {
        t.printStackTrace();
        Log.v(TAG, t.toString() + " " + t.getMessage());
    }
}
