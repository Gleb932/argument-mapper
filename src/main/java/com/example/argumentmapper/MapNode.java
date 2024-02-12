package com.example.argumentmapper;

import java.util.ArrayList;
import java.util.List;

public abstract class MapNode {
    protected ArrayList<MapNode> children = new ArrayList<>();
    protected transient short index;
    protected transient MapNode parent;
    protected transient Integer cachedConclusion;

    public void addChild(MapNode child)
    {
        child.setIndex((short) (children.size()));
        children.add(child);
        child.setParent(this);
    }
    public void removeChild(MapNode child)
    {
        int pos = children.indexOf(child);
        children.remove(pos);
        child.setParent(null);
        for(int i = pos; i < children.size(); ++i)
        {
            children.get(i).setIndex((short)i);
        }
    }
    public abstract void shallowCopy(MapNode other);
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
    protected short getIndex()
    {
        return this.index;
    }
    protected void setIndex(short index)
    {
        this.index = index;
    }
    public ArrayList<Short> getPath()
    {
        ArrayList<Short> path = new ArrayList<>();
        MapNode curNode = this;
        MapNode parentNode = curNode.getParent();
        while(parentNode != null)
        {
            path.add(curNode.getIndex());
            curNode = parentNode;
            parentNode = curNode.getParent();
        }
        return path;
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

    public abstract int getConclusion();

    public void updateConclusion()
    {
        MapNode updatedNode = this;
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
