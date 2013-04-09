package rlmusic;

public class Node {
    private Node parent;
    private float value; //Qvalue
    private byte noteValue;//what action
    private float reward;
    private boolean expanded = false;
    private Node[] children = new Node[25];
    private float eTrace;
    
    public Node(Node parent, byte noteValue, float value) {
        this.parent = parent;
        this.value = value;
        this.noteValue = noteValue;
    }
    
    public void setReward(float reward) {
        this.reward = reward;
    }
    
    public float getReward() {
        return reward;
    }

    public byte getNoteValue() {
        return noteValue;
    }

    public void setNoteValue(byte noteValue) {
        this.noteValue = noteValue;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
    
    public Node getChild(int i) { return children[i];}
    
    public void putChild(Node child) {
        children[child.getNoteValue()] = child;
    }
    
    public Node getParent() {
        return parent;
    }

    public Node[] getChildren() {
        return children;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public float geteTrace() {
        return eTrace;
    }

    public void seteTrace(float eTrace) {
        this.eTrace = eTrace;
    }
    
    
    
}
