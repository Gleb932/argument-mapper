package com.example.argumentmapper;

public class OfflineArgumentMapEditor implements ArgumentMapEditor {
    @Override
    public boolean addChild(MapNode parent, MapNode child) {
        parent.addChild(child);
        parent.updateConclusion();
        return true;
    }

    @Override
    public boolean removeNode(MapNode node) {
        MapNode parent = node.getParent();
        if(parent == null) return false;
        parent.removeChild(node);
        parent.updateConclusion();
        return true;
    }

    @Override
    public boolean replaceNode(MapNode oldNode, MapNode newNode) {
        if(oldNode.getParent() == null) return false;
        oldNode.shallowCopy(newNode);
        return true;
    }
}
