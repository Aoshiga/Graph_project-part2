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

    Flow(Node from, Node to, int value) {
        this.from = from;
        this.to = to;
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
            return this.from.equals(toCompare.from) && this.to.equals(toCompare.to) && this.flow == toCompare.flow;
        }
        return false;
    }

    @Override
    public String toString() {
        return from + "->" + to + "[label=\"" + flow + "\"]";
    }

    @Override
    public int hashCode() {
        return this.from.hashCode() * this.to.hashCode();
    }
}
