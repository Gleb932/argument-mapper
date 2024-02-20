package com.example.argumentmapper;

import java.util.List;

public class OfflineArgumentMapEditor extends BaseEditor {
    public OfflineArgumentMapEditor(EditorListener listener) {
        super(listener);
    }

    protected boolean addChild(MapNode parent, MapNode child) {
        parent.addChild(child);
        parent.updateConclusion();
        return true;
    }

    protected boolean removeNode(MapNode node) {
        MapNode parent = node.getParent();
        if(parent == null) return false;
        parent.removeChild(node);
        parent.updateConclusion();
        return true;
    }

    protected boolean replaceNode(MapNode oldNode, MapNode newNode) {
        MapNode parent = oldNode.getParent();
        if(parent == null) return false;
        oldNode.shallowCopy(newNode);
        parent.updateConclusion();
        return true;
    }

    public void execute(Command command)
    {
        List<MapNode> nodes = command.nodes;
        boolean result = false;
        switch (command.type)
        {
            case ADD_CHILD:
                result = addChild(nodes.get(0), nodes.get(1));
                break;
            case REPLACE_NODE:
                result = replaceNode(nodes.get(0), nodes.get(1));
                break;
            case REMOVE_NODE:
                result = removeNode(nodes.get(0));
                break;
        }
        if(listener != null) {
            listener.onEditingResult(result);
        }
    }
}
