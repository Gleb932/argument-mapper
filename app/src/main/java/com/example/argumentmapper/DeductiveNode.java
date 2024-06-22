package com.example.argumentmapper;

import android.os.Parcel;

import com.google.gson.JsonObject;

import java.util.ArrayList;

public class DeductiveNode extends MapNode{
    //0 - AND, 1 - OR, 3 - NOT...
    protected byte operator;
    static final private String[] OPERATOR_NAMES = {"AND", "OR", "NOT"};

    public DeductiveNode(byte operator)
    {
        this.operator = operator;
    }

    public DeductiveNode(JsonObject jsonObject)
    {
        this.operator = jsonObject.get("operator").getAsByte();
    }

    public DeductiveNode(Parcel in)
    {
        this.operator = in.readByte();
    }

    public byte getOperator()
    {
        return operator;
    }

    public void setOperator(byte operator)
    {
        this.operator = operator;
    }

    @Override
    protected int _getConclusion()
    {
        if(hasCachedConclusion())
        {
            return cachedConclusion;
        }
        int operatorResult = applyOperator(children);
        int conclusion = MAX_WEIGHT * operatorResult;
        saveCachedConclusion(conclusion);
        return conclusion;
    }

    @Override
    protected String _getText()
    {
        return OPERATOR_NAMES[operator];
    }

    @Override
    protected void _shallowCopy(MapNode other) {
        DeductiveNode node = (DeductiveNode) other;
        setOperator(node.getOperator());
    }

    private int applyOperator(ArrayList<MapNode> nodes)
    {
        switch (operator)
        {
            case 0:
                if(nodes.size() < 2) return 0;
                for (int i = 0; i < nodes.size(); i++) {
                    if(nodes.get(i).getConclusion() <= 0) return 0;
                }
                return 1;
            case 1:
                if(nodes.size() < 2) return 0;
                for (int i = 0; i < nodes.size(); i++) {
                    if(nodes.get(i).getConclusion() > 0) return 1;
                }
                return 0;
            case 2:
                if(nodes.size() != 1) return 0;
                int childConclusion = nodes.get(0).getConclusion();
                if(childConclusion == 0) return 0;
                return (childConclusion < 0) ? 1 : -1;
            default: return 0;
        }
    }

    public void writeToParcelSpecific(Parcel parcel) {
        parcel.writeInt(1);
        parcel.writeByte(operator);
    }
}
