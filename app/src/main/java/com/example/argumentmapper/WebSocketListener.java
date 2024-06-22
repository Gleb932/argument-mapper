package com.example.argumentmapper;

public interface WebSocketListener {
    void onOperationSuccess();
    void onOperationFailure(int code);
}
