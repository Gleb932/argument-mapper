package com.example.argumentmapper;

import android.os.Parcel;

import com.google.gson.JsonObject;

public class InductiveNode extends MapNode {

    protected String description;
    protected String name;
    protected int weight;

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

    public InductiveNode(Parcel in)
    {
        this.name = in.readString();
        this.description = in.readString();
        this.weight = in.readInt();
    }

    public InductiveNode(JsonObject jsonObject)
    {
        this.description = jsonObject.get("description").getAsString();
        this.name = jsonObject.get("name").getAsString();
        this.weight = jsonObject.get("weight").getAsInt();
    }

    public String getDescription() {
        return description;
    }
    public int getWeight() {
        return weight;
    }
    public String getName()
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
        this.weight = Math.min(MAX_WEIGHT, Math.max(-MAX_WEIGHT, weight));
        updateConclusion();
    }
    @Override
    protected int _getConclusion()
    {
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
    protected String _getText()
    {
        if(!name.isEmpty()) {
            return name;
        }else {
            return description;
        }
    }

    @Override
    protected void _shallowCopy(MapNode other) {
        InductiveNode node = (InductiveNode) other;
        setDescription(node.getDescription());
        setName(node.getName());
        setWeight(node.getWeight());
    }

    public void writeToParcelSpecific(Parcel parcel) {
        parcel.writeInt(0);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeInt(weight);
    }
}
