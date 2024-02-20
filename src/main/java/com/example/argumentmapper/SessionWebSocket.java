package com.example.argumentmapper;

import android.util.Log;

import com.example.argumentmapper.exceptions.AuthException;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

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
    private final CommandExecutor executor;
    private final AuthHandler handler;
    private com.example.argumentmapper.WebSocketListener listener;
    private WebSocket webSocket;

    public SessionWebSocket(AuthHandler handler, CommandExecutor executor, String token, OkHttpClient client, int sessionID) throws AuthException
    {
        this.handler = handler;
        this.executor = executor;
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

    public void setListener(com.example.argumentmapper.WebSocketListener listener)
    {
        this.listener = listener;
    }

    public boolean sendMessage(String message)
    {
        return webSocket.send(message);
    }

    public boolean close()
    {
        return webSocket.close(1000, null);
    }

    @Override public void onOpen(WebSocket webSocket, @NotNull Response response) {
        Log.v(TAG, "WebSocket open");
    }

    @Override public void onMessage(@NotNull WebSocket webSocket, @NotNull String message) {
        Log.v(TAG, "MESSAGE: " + message);
        try {
            JSONObject jsonResponse = new JSONObject(message);
            if(jsonResponse.has("result"))
            {
                int code = jsonResponse.getInt("result");
                if(code == 200) {
                    listener.onOperationSuccess();
                }else{
                    listener.onOperationFailure(code);
                }
            }else if(jsonResponse.has("operation"))
            {
                executor.execute(message);
            }
        }catch (JSONException err){
            Log.v(TAG, err.toString());
        }
    }

    @Override public void onMessage(@NotNull WebSocket webSocket, ByteString bytes) {
    }

    @Override public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        if(code == 1008)
        {
            handler.onAuthError();
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
