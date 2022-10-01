package com.example.argumentmapper;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class InductiveNode extends MapNode implements Parcelable {

    protected String description;
    protected String name;
    protected int weight;
    private static final int MAX_CHAR_IN_NAME = 57;

    InductiveNode(String description)
    {
        this.description = description;
        this.name = "";
    }
    InductiveNode(String description, String name)
    {
        this.description = description;
        this.name = name;
    }
    public InductiveNode(String description, String name, int weight)
    {
        this.description = description;
        this.name = name;
        this.weight = weight;
    }

    private InductiveNode(Parcel in)
    {
        this.name = in.readString();
        this.description = in.readString();
        this.weight = in.readInt();
        ArrayList<InductiveNode> childrenList = new ArrayList<InductiveNode>();
        in.readList(childrenList, InductiveNode.class.getClassLoader());
        for(InductiveNode child: childrenList)
        {
            addChild(child);
        }
    }

    public String getDescription() {
        return description;
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
    public int getWeight() {
        return weight;
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

    @Override
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
