package com.example.argumentmapper;

import com.google.gson.JsonObject;

public class OnlineArgumentMapEditor extends OfflineArgumentMapEditor implements WebSocketListener {
    SessionWebSocket webSocket;
    CommandToJsonHandler commandToJsonHandler;
    Command currentCommand;

    public OnlineArgumentMapEditor(EditorListener listener, SessionWebSocket webSocket, CommandToJsonHandler commandToJsonHandler)
    {
        super(listener);
        this.webSocket = webSocket;
        this.commandToJsonHandler = commandToJsonHandler;
    }

    public void execute(Command command)
    {
        currentCommand = command;
        JsonObject json = commandToJsonHandler.process(command);
        webSocket.sendMessage(json.toString());
    }

    @Override
    public void onOperationSuccess() {
        super.execute(currentCommand);
    }

    @Override
    public void onOperationFailure(int code) {
        listener.onEditingResult(false);
    }
}
