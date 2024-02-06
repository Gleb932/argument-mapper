package com.example.argumentmapper;

public interface WebSocketListener {
    void onMessageSuccess();
    void onMessageDenial();
    void onAuthError();
}
