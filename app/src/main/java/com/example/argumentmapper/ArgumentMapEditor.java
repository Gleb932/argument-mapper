package com.example.argumentmapper;

public interface ArgumentMapEditor {
    void execute(Command command);
    void setListener(EditorListener listener);
}
