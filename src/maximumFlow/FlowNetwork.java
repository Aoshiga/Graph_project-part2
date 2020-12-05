package maximumFlow;

import m1graf2020.Edge;
import m1graf2020.Graf;
import m1graf2020.Node;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

// - fonction d'algo Ford principale
// - fonction qui calcule le graphe résiduel
// - fonction qui trouve un chemin augmenté dans un graphe augmenté
// - fonction qui augmente le flow du flowNetwork
// - toDotString doit prendre en compte deux cas : graphe résiduel et flowNetwork
public class FlowNetwork {
    public Graf graf;
    public List<Flow> flows;
    /**
     * Builds a flow network from a Dot file
     * @param file file to be read, must be .dot file
     * @throws IOException if the file was not found or if it is not .dot
     */
    public FlowNetwork(File file) throws IOException {
        String extension = "";
        int i = file.getName().lastIndexOf('.');
        if (i > 0) {
            extension = file.getName().substring(i + 1);
        }
        if (!extension.equals("dot")) throw new IOException("File is not .dot");

        graf = new Graf();
        flows = new ArrayList<>();
        Scanner reader = new Scanner(new FileReader(file));

        int from_id;
        int to_id;
        int weight;
        String current;

        while (reader.hasNext()) {
            current = reader.next();

            if(reader.hasNext("->")) {
                if(current.equals("s")) from_id = 1;
                else from_id = Integer.parseInt(current)+1;

                reader.next(); //jump "->"

                if(reader.hasNext("t")) {
                    reader.next(); //jump "t"
                    to_id = 999;
                }
                else to_id = reader.nextInt()+1;

                String label = reader.next().replaceAll("[^0-9]", "");
                weight = Integer.parseInt(label);

                graf.addEdge(from_id, to_id, weight);

            }
        }
    }

    FlowNetwork() {
        this.graf = new Graf();
        this.flows = new ArrayList<>();
    }

    /**
     * Getter for a flow network
     * @param from_id The id of the node from
     * @param to_id The id of the node to
     * @return The flow if it find, null elsewhere
     */
    public Flow getFlow(int from_id, int to_id) {
        for (Flow f : flows) {
            if (f.getTo().getId() == to_id && f.getFrom().getId() == from_id) {
                return f;
            }
        }
        return null;
    }

    /**
     * Getter for a flow network
     * @param from The node from
     * @param to The node to
     * @return The flow if it find, null elsewhere
     */
    public Flow getFlow(Node from, Node to) {
        for (Flow f : flows) {
            if (f.getTo().getId() == to.getId() && f.getFrom().getId() == from.getId()) {
                return f;
            }
        }
        return null;
    }

    /**
     * Getter for a flow network
     * @param e The edge to find the flow
     * @return The flow if it find, null elsewhere
     */
    public Flow getFlow(Edge e) {
        for (Flow f : flows) {
            if (f.getTo().getId() == e.getTo().getId() && f.getFrom().getId() == e.getFrom().getId()) {
                return f;
            }
        }
        return null;
    }

    /**
     * To add a flow
     * @param f The flow to add
     */
    public void addFlow(Flow f) { flows.add(f);}

    /**
     * To find if a flow exist
     * @param f The flow to add
     * @return True if it exist flow elsewhere
     */
    public boolean existsFlow(Flow f) {
        return flows.contains(f);
    }

    /**
     * Returns a String representing the graph in the DOT formalism
     * @return a String representing the graph in the DOT formalism
     */
    public String toDotString() {
        StringBuilder dot = new StringBuilder("digraph {\n\trankdir=\"LR\";\n");
        int biggestId = Node.getBiggestId();
        for (Map.Entry<Node, List<Node>> entry : graf.getAdjList().entrySet()) {
            Collections.sort(entry.getValue());

            for (Node to : entry.getValue()) {
                dot.append("\t");
                int from = entry.getKey().getId();
                if (from == 1) dot.append("s");
                else if (from == biggestId) dot.append("t");
                else dot.append(entry.getKey().getId() -1 );
                dot.append(" -> ");
                if (to.getId() == 1) dot.append("s");
                else if (to.getId() == biggestId) dot.append("t");
                else dot.append(to.getId() -1 );
                dot.append(" [label=\"");
                dot.append(graf.getEdge(entry.getKey().getId(), to.getId()).getWeight());
                dot.append("\"];\n");
            }
        }
        dot.append("}");

        return dot.toString();
    }

    /**
     * Returns a String representing the graph in the DOT formalism
     * @return a String representing the graph in the DOT formalism
     */
    public String toDotString(int graphNumber) {
        StringBuilder dot = new StringBuilder("digraph flow")
                .append(graphNumber)
                .append(" {\n\trankdir=\"LR\";\n")
                .append("\tlabel = \"(")
                .append(graphNumber)
                .append(") Flow induced from residual graph ")
                .append(graphNumber-1)
                .append(". Value: ")
                .append(this.maxFlow())
                .append("\";\n");
        int biggestId = Node.getBiggestId();
        for (Map.Entry<Node, List<Node>> entry : graf.getAdjList().entrySet()) {
            Collections.sort(entry.getValue());

            for (Node to : entry.getValue()) {
                dot.append("\t");
                if (entry.getKey().getId() == 1) dot.append("s");
                else if (entry.getKey().getId() == biggestId) dot.append("t");
                else dot.append(entry.getKey().getId() -1 );
                dot.append(" -> ");
                if (to.getId() == 1) dot.append("s");
                else if (to.getId() == biggestId) dot.append("t");
                else dot.append(to.getId() -1 );
                dot.append(" [label=\"")
                    .append(getFlow(entry.getKey(), to).getValue())
                    .append("/")
                    .append(graf.getEdge(entry.getKey().getId(), to.getId()).getWeight())
                    .append("\"];\n");
            }
        }
        dot.append("}");

        return dot.toString();
    }

    /**
     * Returns a String representing the graph in the DOT formalism
     * @return a String representing the graph in the DOT formalism
     */
    public String toDotString(int graphNumber, LinkedHashSet<Node> path, int flowCapacity) {
        StringBuilder pathString = new StringBuilder();
        int biggestId = Node.getBiggestId();
        pathString.append("[");
        for (Node n : path) {
            if (n.getId() == 1) pathString.append("s");
            else if (n.getId() == biggestId) {
                pathString.append("t");
                break;
            }
            else pathString.append(n.getId() -1);
            pathString.append(", ");
        }
        pathString.append("]");
        StringBuilder dot = new StringBuilder("digraph residualGraph")
                .append(graphNumber)
                .append(" {\n\trankdir=\"LR\";\n")
                .append("\tlabel = \"(")
                .append(graphNumber)
                .append(") residual graph.\n")
                .append("\tAugmenting path: ")
                .append(pathString)
                .append("\n\tResidual Capacity: ")
                .append(flowCapacity)
                .append("\";\n");
        for (Map.Entry<Node, List<Node>> entry : graf.getAdjList().entrySet()) {
            Collections.sort(entry.getValue());

            for (Node to : entry.getValue()) {
                dot.append("\t");
                if (entry.getKey().getId() == 1) dot.append("s");
                else if (entry.getKey().getId() == biggestId) dot.append("t");
                else dot.append(entry.getKey().getId() -1 );
                dot.append(" -> ");
                if (to.getId() == 1) dot.append("s");
                else if (to.getId() == biggestId) dot.append("t");
                else dot.append(to.getId() -1 );
                dot.append(" [label=\"")
                    .append(graf.getEdge(entry.getKey().getId(), to.getId()).getWeight())
                    .append("\"];\n");
            }
        }
        dot.append("}");

        return dot.toString();
    }

    /**
     * Exports the graph in a .dot file in the DOT formalism
     */
    public void toDotFile(int cpt){
        /* if filename is empty (""), give a default filename */
        try {
            String dot = this.toDotString(cpt);
            FileWriter fw = new FileWriter("resources/output/flow" + cpt +".dot");
            fw.write(dot);
            fw.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Exports the graph in a .dot file in the DOT formalism
     */
    public void toDotFile(int cpt, LinkedHashSet path, int flowCapacity){
        /* if filename is empty (""), give a default filename */
        try {
            String dot = this.toDotString(cpt, path, flowCapacity);
            FileWriter fw = new FileWriter("resources/output/residGraph" + cpt +".dot");
            fw.write(dot);
            fw.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
    input : G = (V,E) the flow network graph, s belonging to V the source state of the
    network, t belonging to V the sink state of the network
    output : f a maximum flow for G
    begin
    Initialize flow f to 0;
    while there exists an augmenting path p do
        increase the flow f along p
    end while
        return f
    end
     */
    public int fordFulkerson() throws Exception {
        FlowNetwork inducedFlow = this.clone();
        initFlow(inducedFlow);

        FlowNetwork residualNetwork = this.clone();
        int cpt = 1; //use to know the number of iteration

        LinkedHashSet<Node> path = new LinkedHashSet<>();
        while(residualNetwork.existsAugmentingPathDFS(path)) {
            if(cpt >= 10) {
                throw new Exception("To much file created: stop programm Execution");
            }

            int flowCapacity = searchFlowCapacity(path, residualNetwork, inducedFlow);
            increaseFlow(inducedFlow, path, flowCapacity);

            inducedFlow.toDotFile(cpt);

            residualNetwork.toDotFile(cpt, path, flowCapacity);
            residualNetwork = makeResidual(inducedFlow);

            cpt++;
        }

        return inducedFlow.maxFlow();
    }

    /**
     * Search the maximum flow capacity in a given path
     * @param path The path find from s to t in the residualNetwork
     * @param residualNetwork The residual network where the path was find
     * @return The flow capacity
     */
    private int searchFlowCapacity(LinkedHashSet<Node> path, FlowNetwork residualNetwork, FlowNetwork inducedFlow){
        Node prevN = null;
        int flowCapacity = Integer.MAX_VALUE;

        //Search the maximum flow capacity on the current finding path
        for(Node n : path) {
            if(prevN != null) {
                int currentWeight = residualNetwork.graf.getEdge(prevN.getId(), n.getId()).getWeight();
                flowCapacity = Integer.min(flowCapacity, currentWeight);

                int flowCapacityOnInduced = inducedFlow.graf.getEdge(prevN.getId(), n.getId()).getWeight()
                        - inducedFlow.getFlow(prevN, n).getValue();
                flowCapacity = Integer.min(flowCapacity, flowCapacityOnInduced);
            }
            prevN = n;
        }

        return flowCapacity;
    }

    /**
     * Increase the flow network
     * @param fn The flow network to increase
     * @param path The augmenting path
     * @param flowCapacity The maximum flow capacity along the path
     */
    public void increaseFlow(FlowNetwork fn, LinkedHashSet<Node> path, int flowCapacity) {
        Node prevN = null;
        int currentFlowCapacity;

        for(Node n : path) {
            if(prevN != null) {
                if(!fn.existsFlow(fn.getFlow(prevN, n))) this.addFlow(new Flow(prevN, n, 0));
                currentFlowCapacity = fn.getFlow(prevN, n).getValue();
                fn.getFlow(prevN, n).setValue(flowCapacity + currentFlowCapacity);
            }
            prevN = n;
        }
    }


    /*public void increaseFlow(FlowNetwork fn, LinkedHashSet<Node> path, int flowCapacity) {
        Node prevN = null;
        int currentFlowCapacity;
        int currentWeightCapacity;

        System.out.println(path);

        for(Node n : path) {
            if(prevN != null) {
                if(!fn.existsFlow(fn.getFlow(prevN, n))) this.addFlow(new Flow(prevN, n, 0));
                currentFlowCapacity = fn.getFlow(prevN, n).getValue();
                Edge e = fn.graf.getEdge(prevN.getId(), n.getId());
                if(e != null)
                    currentWeightCapacity = fn.graf.getEdge(prevN.getId(), n.getId()).getWeight();
                else currentWeightCapacity = 0;
                if(currentFlowCapacity + flowCapacity > currentWeightCapacity) {
                    System.out.println("currentFlowCapacity + flowCapacity > currentWeightCapacity");
                    fn.getFlow(prevN, n).setValue(currentWeightCapacity);
                    currentFlowCapacity += flowCapacity;
                    currentFlowCapacity -= currentWeightCapacity;

                    for (Edge successor: fn.graf.getOutEdges(prevN)) {
                        System.out.println(successor);
                        int successorFlow = fn.getFlow(successor).getValue();
                        int successorWeight = successor.getWeight();
                        System.out.println("currentFlowCapacity " + currentFlowCapacity);
                        System.out.println("successorFlow " + successorFlow);
                        System.out.println("successorWeight " + successorWeight);


                        if(successorWeight > successorFlow) {
                            System.out.println("successorWeight > successorFlow");
                            if(successorFlow + currentFlowCapacity <= successorWeight) {
                                System.out.println("if");
                                fn.getFlow(successor).setValue(successorFlow + currentFlowCapacity);
                                currentFlowCapacity = 0;

                            } else {
                                System.out.println("else");
                                fn.getFlow(successor).setValue(successorWeight);
                                currentFlowCapacity -= (successorWeight-successorFlow);
                            }
                        }
                    }
                }
                else fn.getFlow(prevN, n).setValue(flowCapacity + currentFlowCapacity);
            }
            prevN = n;
        }
    }*/

    /**
     * Get the maximum flow of a flow network
     * @return The maximum flow value
     */
    private int maxFlow() {
        int maxFlow = 0;
        for (Node s : this.graf.getSuccessors(1)) {
            maxFlow += getFlow(this.graf.getNode(1), s).getValue();
        }
        return maxFlow;
    }

    /**
     * Create the residual network
     * @param fn The induced flow network
     * @return The residual network
     */
    public static FlowNetwork makeResidual(FlowNetwork fn) {
        FlowNetwork rn = new FlowNetwork();
        for (Node n : fn.graf.getAllNodes()) rn.graf.addNode(n);
        // for all nodes in fn
        for (Node from : rn.graf.getAllNodes()) {
            // for each out edge
            for (Edge outEdge : fn.graf.getOutEdges(from)) {
                //if(!rn.graf.existsEdge(outEdge.getTo().getId(), from.getId())) {
                    int flow;
                    if (fn.existsFlow(fn.getFlow(from, outEdge.getTo())))
                        flow = fn.getFlow(from, outEdge.getTo()).getValue();
                    else flow = 0;
                    int weight = outEdge.getWeight() - flow;
                    int reverse_flow = 0;
                    if (fn.existsFlow(fn.getFlow(outEdge.getTo(), from))) {
                        reverse_flow += fn.getFlow(outEdge.getTo(), from).getValue();
                        weight += reverse_flow;
                    }
                    // add an edge with weight equal to the weight of the out edge minus current flow on that edge in fn
                    if (weight > 0) {
                        if (rn.graf.existsEdge(from.getId(), outEdge.getTo().getId())) {
                            Edge edge = rn.graf.getEdge(from.getId(), outEdge.getTo().getId());
                            edge.setWeight(weight);
                        } else {
                            rn.graf.addEdge(new Edge(from.getId(), outEdge.getTo().getId(), weight));
                        }
                    }
                    // add a reverse edge with weight equal to the current flow on the outer edge in fn, if that flow is positive
                    if (flow > 0) {
                        rn.graf.addEdge(new Edge(outEdge.getTo().getId(), from.getId(), flow));
                    }
                //}
            }
        }
        return rn;
    }

    /**
     * Initialise all flow in FlowNetwork to 0
     * @param inducedFlow
     */
    private void initFlow(FlowNetwork inducedFlow) {
        for (Edge e : inducedFlow.graf.getEdgeList()) {
            inducedFlow.flows.add(new Flow(e.getFrom().getId(), e.getTo().getId(), 0));
            inducedFlow.flows.add(new Flow(e.getTo().getId(), e.getFrom().getId(), 0));
        }
    }

    /**
     * Computes a depth-first-search of the graph
     * @return a list of nodes representing a depth-first-search of the graph in order
     */
    public boolean existsAugmentingPathDFS(LinkedHashSet<Node> chosenPath) {
        chosenPath.clear();
        Map<Node, Integer> index = new HashMap<>();
        TreeMap<Node, List<Node>> adjList = this.graf.getAdjList();

        Graf.color[] color = new Graf.color[adjList.size()];

        int cpt = 0;
        for (Map.Entry<Node, List<Node>> entry : adjList.entrySet()) {
            index.put(entry.getKey(), cpt);
            color[cpt] = Graf.color.WHITE;
            cpt++;
        }

        if (color[index.get(new Node(1))] == Graf.color.WHITE) {
            dfs_visit(chosenPath, new Node(1), color, index, this);
        }

        if(chosenPath.contains(new Node(999))) return true;

        return false;
    }

    /**
     * Recursively searches (depth-first) from a node
     */
    private void dfs_visit(LinkedHashSet<Node> chosenPath, Node u, Graf.color[] color, Map<Node, Integer> index, FlowNetwork fn) {
        chosenPath.add(u);

        color[index.get(u)] = Graf.color.GREY;
        List<Node> successor = fn.graf.getSuccessors(u);


        successor.sort((n1, n2) -> {
            return fn.graf.getEdge(u.getId(), n1.getId()).getWeight() - fn.graf.getEdge(u.getId(), n2.getId()).getWeight();
        });

        //browse successor and break the dfs search if we find t
        if(successor.contains(new Node(999))) {
            chosenPath.add(new Node(999));
            return;
        }

        for (Node v : successor) {
            if(chosenPath.contains(new Node(999))){
                return;
            }
            if (color[index.get(v)] == Graf.color.WHITE) {
                dfs_visit(chosenPath, v, color, index, fn);
            }
        }
        color[index.get(u)] = Graf.color.BLACK;

        if(!chosenPath.contains(new Node(999))){
            chosenPath.remove(u);
        }
    }

    @Override
    public FlowNetwork clone() {
        FlowNetwork fn = new FlowNetwork();
        for (Edge n : this.graf.getEdgeList()) fn.graf.addEdge(n);
        fn.flows.addAll(this.flows);
        return fn;
    }
}