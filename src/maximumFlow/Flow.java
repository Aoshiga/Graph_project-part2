package maximumFlow;

import m1graf2020.Node;

public class Flow {
    Node to, from;
    int flow;

    Flow() {}

    Flow(int from_id, int to_id) {
        this.from = new Node(from_id);
        this.to = new Node(to_id);
    }

    Flow(int from_id, int to_id, int flow) {
        this.from = new Node(from_id);
        this.to = new Node(to_id);
        this.flow = flow;
    }

    Flow(Node from, Node to) {
        this.from = from;
        this.to = to;
    }

    Flow(Node from, Node to, int flow) {
        this.from = from;
        this.to = to;
        this.flow = flow;
    }

    public Node getFrom() { return from; }

    public Node getTo() { return to; }

    public int getFlow() { return flow; }

    public void setFlow(int weight) {
        this.flow = flow;
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
