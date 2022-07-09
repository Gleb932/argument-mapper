package com.example.argumentmapper;

public class ArgumentMap {
    private InductiveNode root;

    ArgumentMap(String description)
    {
        root = new InductiveNode(description, "", 0);
    }
    ArgumentMap(String description, String topic)
    {
        root = new InductiveNode(description, topic, 0);
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
}
