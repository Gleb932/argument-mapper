package com.example.argumentmapper;

public abstract class BaseEditor implements ArgumentMapEditor {
    EditorListener listener;
    BaseEditor(EditorListener listener)
    {
        this.listener = listener;
    }
    @Override
    public void setListener(EditorListener listener) {
        this.listener = listener;
    }
}
