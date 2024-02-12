package com.example.argumentmapper;


public interface ArgumentMapEditor {
    boolean addChild(MapNode parent, MapNode child);
    boolean removeNode(MapNode node);
    boolean replaceNode(MapNode oldCNode, MapNode newNode);
}
