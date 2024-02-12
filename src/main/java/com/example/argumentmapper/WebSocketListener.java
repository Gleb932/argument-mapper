package com.example.argumentmapper;

public interface WebSocketListener {
    void onOperationSuccess();
    void onOperationFailure(int code);
    void onCommand(String jsonCommand);
    void onAuthError();
}
