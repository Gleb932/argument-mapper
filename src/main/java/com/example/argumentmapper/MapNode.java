package com.example.argumentmapper;

import java.util.ArrayList;
import java.util.List;

public class MapNode {
    protected ArrayList<MapNode> children;
    protected transient byte index;
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
        child.setIndex((byte) (children.size() - 129)); //byte range start -128, -1 because to compensate for 0 index
    }
    public void removeChild(MapNode child)
    {
        int pos = children.indexOf(child);
        children.remove(pos);
        child.setParent(null);
        for(int i = pos; i < children.size(); ++i)
        {
            children.get(i).setIndex((byte)(i - 128));
        }
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
    protected byte getIndex()
    {
        return this.index;
    }
    protected void setIndex(byte index)
    {
        this.index = index;
    }
    public ArrayList<Byte> getPath()
    {
        ArrayList<Byte> path = new ArrayList<>();
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
