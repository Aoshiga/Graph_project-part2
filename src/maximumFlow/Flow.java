package maximumFlow;

import m1graf2020.Node;

public class Flow {
    Node to, from;
    int value;

    Flow() {}

    Flow(int from_id, int to_id) {
        this.from = new Node(from_id);
        this.to = new Node(to_id);
    }

    Flow(int from_id, int to_id, int value) {
        this.from = new Node(from_id);
        this.to = new Node(to_id);
        this.value = value;
    }

    Flow(Node from, Node to) {
        this.from = from;
        this.to = to;
    }

    public Flow(Node from, Node to, int value) {
        this.from = new Node(from.getId()+1);
        this.to = new Node(to.getId()+1);
        this.value = value;
    }

    public Node getFrom() { return from; }

    public Node getTo() { return to; }

    public int getValue() { return value; }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        //if (o == this) return true;
        if (o instanceof Flow) {
            Flow toCompare = (Flow) o;
            return this.from.equals(toCompare.from) && this.to.equals(toCompare.to) && this.value == toCompare.value;
        }
        return false;
    }

    @Override
    public String toString() {
        return from + "->" + to + "[label=\"" + value + "\"]";
    }

    @Override
    public int hashCode() {
        return this.from.hashCode() * this.to.hashCode();
    }
}
