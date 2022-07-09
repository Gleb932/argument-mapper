package com.example.argumentmapper;

import java.util.ArrayList;
import java.util.List;

public class MapNode {
    protected ArrayList<MapNode> children;
    protected transient MapNode parent;
    protected transient Integer cachedConclusion;

    MapNode()
    {
        this.children = new ArrayList<MapNode>();
    }

    public void addChild(MapNode child)
    {
        children.add(child);
        child.setParent(this);
    }
    public void removeChild(MapNode child)
    {
        children.remove(child);
        child.setParent(null);
    }
    public MapNode getParent()
    {
        return parent;
    }
    protected void setParent(MapNode node)
    {
        this.parent = node;
    }
    public List<MapNode> getChildren()
    {
        return children;
    }

    protected boolean hasCachedConclusion()
    {
        return cachedConclusion != null;
    }
    protected int getCachedConclusion()
    {
        return cachedConclusion;
    }
    protected void saveCachedConclusion(int conclusion){
        cachedConclusion = conclusion;
    }

    public int getConclusion() { return 0; }

    public void updateConclusion()
    {
        this.cachedConclusion = null;
        MapNode updatedNode = parent;
        int oldConclusion;
        int newConclusion;
        do {
            if (updatedNode == null || !updatedNode.hasCachedConclusion()) return;
            oldConclusion = updatedNode.getCachedConclusion();
            updatedNode.cachedConclusion = null;
            newConclusion = updatedNode.getConclusion();
            updatedNode = updatedNode.getParent();
        }while(oldConclusion != newConclusion);
    }
}
