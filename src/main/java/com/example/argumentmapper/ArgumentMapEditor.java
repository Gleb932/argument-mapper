package com.example.argumentmapper;


public interface ArgumentMapEditor {
    boolean addChild(MapNode parent, MapNode child);
    boolean removeChild(MapNode child);
    boolean replaceChild(MapNode oldChild, MapNode newChild);
}
