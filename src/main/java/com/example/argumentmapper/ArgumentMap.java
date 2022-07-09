package com.example.argumentmapper;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class ArgumentMap implements Parcelable {
    private final static String extension = "nd";
    private InductiveNode root;
    private String filename;

    ArgumentMap(String description)
    {
        this(description, "");
    }
    ArgumentMap(String description, String topic)
    {
        this(new InductiveNode(description, topic, 0));
    }
    ArgumentMap(InductiveNode root)
    {
        this(root, UUID.randomUUID().toString() + "." + extension);
    }
    ArgumentMap(InductiveNode root, String filename)
    {
        this.root = root;
        this.filename = filename;
    }

    protected ArgumentMap(Parcel in) {
        filename = in.readString();
        root = in.readParcelable(InductiveNode.class.getClassLoader());
    }

    String getTopic()
    {
        return root.getName();
    }
    int getConclusion()
    {
        return root.getConclusion();
    }
    InductiveNode getRoot()
    {
        return root;
    }
    void setRoot(InductiveNode root){this.root = root;}
    String getFilename()
    {
        return filename;
    }
    static String getExtension(){return extension;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(filename);
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
