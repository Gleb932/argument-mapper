package com.example.argumentmapper;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.UUID;

public class ArgumentMap implements Parcelable {
    final static String extension = "nd";
    private InductiveNode root;
    transient private String filename;
    private Integer sessionID;

    public ArgumentMap(String description)
    {
        this(description, "");
    }
    public ArgumentMap(String description, String topic)
    {
        this(new InductiveNode(description, topic, 0));
    }
    public ArgumentMap(InductiveNode root)
    {
        this(root, UUID.randomUUID().toString() + "." + extension);
    }
    public ArgumentMap(InductiveNode root, String filename)
    {
        this.root = root;
        this.filename = filename;
    }

    public ArgumentMap(Parcel in) {
        filename = in.readString();
        sessionID = (Integer)in.readValue(Integer.class.getClassLoader());
        root = in.readParcelable(MapNode.class.getClassLoader());
    }

    public String getTopic()
    {
        return root.getName();
    }
    public int getConclusion()
    {
        return root.getConclusion();
    }
    public InductiveNode getRoot()
    {
        return root;
    }
    public void setRoot(InductiveNode root){this.root = root;}
    public MapNode getNode(List<Short> path)
    {
        MapNode curNode = this.root;
        for(short childIndex : path)
        {
            List<MapNode> children = curNode.getChildren();
            if(children.size() <= childIndex) return null;
            curNode = children.get(childIndex);
        }
        return curNode;
    }
    public Integer getSessionID() {return this.sessionID;}
    public void setSessionID(int sessionID) {this.sessionID = sessionID;}
    public void removeSessionID(){this.sessionID = null;}
    public String getFilename()
    {
        return filename;
    }
    public void setFilename(String filename)
    {
        this.filename = filename;
    }
    public static String getExtension(){return extension;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(filename);
        parcel.writeValue(sessionID);
        parcel.writeParcelable(root, 0);
    }

    public static final Creator<ArgumentMap> CREATOR = new Creator<ArgumentMap>() {
        @Override
        public ArgumentMap createFromParcel(Parcel in) {
            return new ArgumentMap(in);
        }
        @Override
        public ArgumentMap[] newArray(int size) {
            return new ArgumentMap[size];
        }
    };
}
