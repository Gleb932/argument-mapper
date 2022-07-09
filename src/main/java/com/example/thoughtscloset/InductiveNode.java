package com.example.thoughtscloset;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

class InductiveNode implements Parcelable {

    private String description;
    private String name;
    private int weight;
    private final ArrayList<InductiveNode> children;
    private InductiveNode parent;
    private Integer cachedConclusion;
    private static final int MAX_CHAR_IN_NAME = 57;

    InductiveNode(String description)
    {
        this.description = description;
        this.name = "";
        this.weight = 1;
        this.children = new ArrayList<InductiveNode>();
    }
    InductiveNode(String description, String name)
    {
        this.description = description;
        this.name = name;
        this.weight = 1;
        this.children = new ArrayList<InductiveNode>();
    }
    InductiveNode(String description, String name, int weight)
    {
        this.description = description;
        this.name = name;
        this.weight = weight;
        this.children = new ArrayList<InductiveNode>();
    }

    private InductiveNode(Parcel in)
    {
        this.name = in.readString();
        this.description = in.readString();
        this.weight = in.readInt();
        ArrayList<InductiveNode> childrenList = new ArrayList<InductiveNode>();
        in.readList(childrenList, InductiveNode.class.getClassLoader());
        this.children = new ArrayList<InductiveNode>();
        for(InductiveNode child: childrenList)
        {
            addChild(child);
        }
    }

    public String getDescription() {
        return description;
    }
    public int getWeight() {
        return weight;
    }
    public String getName() {
        String out;
        if(!name.isEmpty()) {
            out =  name;
        }else {
            out = description;
        }
        if(out.length() <= MAX_CHAR_IN_NAME)
        {
            return out;
        }else
        {
            return out.substring(0, MAX_CHAR_IN_NAME) + "...";
        }
    }

    public String getFullName()
    {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setWeight(int weight) {
        this.weight = weight;
        updateConclusion();
    }

    public void addChild(InductiveNode child)
    {
        children.add(child);
        child.setParent(this);
    }
    public void removeChild(InductiveNode child)
    {
        children.remove(child);
        child.setParent(null);
    }
    public InductiveNode getParent()
    {
        return parent;
    }
    protected void setParent(InductiveNode node)
    {
        this.parent = node;
    }
    public List<InductiveNode> getChildren()
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

    private void saveCachedConclusion(int conclusion){
        cachedConclusion = conclusion;
    }
    public int getConclusion()
    {
        if(hasCachedConclusion())
        {
            return cachedConclusion;
        }
        int conclusion = 0;
        if(children.size() == 0)
        {
            saveCachedConclusion(weight);
            return weight;
        }
        else {
            for (int i = 0; i < children.size(); i++) {
                conclusion += children.get(i).getConclusion();
            }
        }
        if(parent == null)
        {
            saveCachedConclusion(conclusion);
            return conclusion;
        }else{
            if(conclusion <= 0)
            {
                saveCachedConclusion(0);
                return 0;
            }else{
                saveCachedConclusion(weight);
                return weight;
            }
        }
    }

    public void updateConclusion()
    {
        this.cachedConclusion = null;
        InductiveNode updatedNode = parent;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeInt(weight);
        parcel.writeList(children);
    }

    public static final Parcelable.Creator<InductiveNode> CREATOR
            = new Parcelable.Creator<InductiveNode>() {
        public InductiveNode createFromParcel(Parcel in) {
            return new InductiveNode(in);
        }

        public InductiveNode[] newArray(int size) {
            return new InductiveNode[size];
        }
    };
}
