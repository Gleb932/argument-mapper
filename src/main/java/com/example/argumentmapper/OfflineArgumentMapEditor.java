package com.example.argumentmapper;

public class OfflineArgumentMapEditor implements ArgumentMapEditor {
    @Override
    public boolean addChild(MapNode parent, MapNode child) {
        parent.addChild(child);
        parent.updateConclusion();
        return true;
    }

    @Override
    public boolean removeChild(MapNode child) {
        MapNode parent = child.getParent();
        if(parent == null) return false;
        parent.removeChild(child);
        parent.updateConclusion();
        return true;
    }

    @Override
    public boolean replaceChild(MapNode oldChild, MapNode newChild) {
        if(oldChild.getParent() == null) return false;
        oldChild.shallowCopy(newChild);
        return true;
    }
}
