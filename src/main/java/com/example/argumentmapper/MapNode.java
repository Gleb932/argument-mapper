package com.example.argumentmapper;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public abstract class MapNode implements Parcelable {
    protected ArrayList<MapNode> children = new ArrayList<>();
    protected transient short index;
    protected transient MapNode parent;
    protected transient Integer cachedConclusion;
    protected static final int MAX_WEIGHT = 100;
    protected static final int MAX_CHAR_IN_TEXT = 57;

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
    public void shallowCopy(MapNode other){
        _shallowCopy(other);
        updateConclusion();
    }
    protected abstract void _shallowCopy(MapNode other);
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
            path.add(0, curNode.getIndex());
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
        return limitConclusion(cachedConclusion);
    }
    protected void saveCachedConclusion(int conclusion){
        cachedConclusion = limitConclusion(conclusion);
    }
    public final int getConclusion()
    {
        if(hasCachedConclusion()) getCachedConclusion();
        return limitConclusion(_getConclusion());
    }
    protected abstract int _getConclusion();
    protected final int limitConclusion(int conclusion)
    {
        return Math.min(MAX_WEIGHT, Math.max(-MAX_WEIGHT, conclusion));
    }
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

    public final String getText()
    {
        String text = _getText();
        if(text.length() <= MAX_CHAR_IN_TEXT)
        {
            return text;
        }else
        {
            return text.substring(0, MAX_CHAR_IN_TEXT) + "...";
        }
    }
    protected abstract String _getText();

    @Override
    public int describeContents() {
        return 0;
    }

    abstract public void writeToParcelSpecific(Parcel in);

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        writeToParcelSpecific(parcel);
        parcel.writeTypedList(children);
    }

    public static final Parcelable.Creator<MapNode> CREATOR
            = new Parcelable.Creator<MapNode>() {
        public MapNode createFromParcel(Parcel in) {
            int type = in.readInt();
            MapNode node;
            switch(type)
            {
                case 0:
                    node = new InductiveNode(in);
                    break;
                case 1:
                    node = new DeductiveNode(in);
                    break;
                default:
                    node = new InductiveNode(in);
                    break;
            }
            List<MapNode> childrenList = new ArrayList<>();
            in.readTypedList(childrenList, MapNode.CREATOR);
            for(MapNode child: childrenList)
            {
                node.addChild(child);
            }
            return node;
        }
        public MapNode[] newArray(int size) {
            return new MapNode[size];
        }
    };
}
