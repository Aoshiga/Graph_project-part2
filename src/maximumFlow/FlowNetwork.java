package maximumFlow;

import m1graf2020.Edge;
import m1graf2020.Graf;
import m1graf2020.Node;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

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
//                flows.add(new Flow(from_id, to_id));
//                flows.add(new Flow(to_id, from_id));

            }
        }
    }

    FlowNetwork() {
        this.graf = new Graf();
        this.flows = new ArrayList<>();
    }

    public Flow getFlow(int from_id, int to_id) {
        for (Flow f : flows) {
            if (f.getTo().getId() == to_id && f.getFrom().getId() == from_id) {
                return f;
            }
        }
        return null;
    }

    public Flow getFlow(Node from, Node to) {
        for (Flow f : flows) {
            if (f.getTo().getId() == to.getId() && f.getFrom().getId() == from.getId()) {
                return f;
            }
        }
        return null;
    }

    public Flow getFlow(Edge e) {
        for (Flow f : flows) {
            if (f.getTo().getId() == e.getTo().getId() && f.getFrom().getId() == e.getFrom().getId()) {
                return f;
            }
        }
        return null;
    }

    public void addFlow(Flow f) { flows.add(f);}

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
                dot.append("\t").append(( entry.getKey().getId() == 1 ? "s" : entry.getKey().getId() -1 ))
                        .append(" -> ")
                        .append(( to.getId() == biggestId ? "t" : to.getId() -1 ))
                        .append(" [label=\"")
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
    Input : G = (V,E) the flow network graph, s belonging to V the source state of the
    network, t belonging to V the sink state of the network
    Output : f a maximum flow for G
    begin
    Initialize flow f to 0;
    while there exists an augmenting path p do
        increase the flow f along p
    end while
        return f
    end
     */
    public int fordFulkerson() {
        FlowNetwork inducedFlow = this.clone();
        initFlow(inducedFlow);
        FlowNetwork residualNetwork = this.clone();
        int cpt = 1; //use to know the number of iteration

        LinkedHashSet<Node> path = new LinkedHashSet<>();
        while(residualNetwork.existsAugmentingPathDFS(path)) {
            if(cpt == 10) break;
            // toDotFile(residualNetwork, path)
            // search flow capacity
            int flowCapacity = searchFlowCapacity(path, residualNetwork);
            inducedFlow = increaseFlow(path, inducedFlow, flowCapacity);
            //Complete the induced flow with the maximum flow capacity we find

            System.out.println(inducedFlow.toDotString(cpt));
            System.out.println(existsFlow(inducedFlow.getFlow(new Node(1), new Node(2))));

            residualNetwork = makeResidual(inducedFlow);

            System.out.println(residualNetwork.toDotString());

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
    private int searchFlowCapacity(LinkedHashSet<Node> path, FlowNetwork residualNetwork){
        Node prevN = null;
        int flowCapacity = Integer.MAX_VALUE;

        //Search the maximum flow capacity on the current finding path
        for(Node n : path) {
            if(prevN != null) {
                int currentWeight = residualNetwork.graf.getEdge(prevN.getId(), n.getId()).getWeight();
                flowCapacity = Integer.min(flowCapacity, currentWeight);
            }
            prevN = n;
        }

        return flowCapacity;
    }

    /**
     *
     */
    public FlowNetwork increaseFlow(LinkedHashSet<Node> path, FlowNetwork inducedFlow, int flowCapacity) {
        Node prevN = null;
        int currentFlowCapacity;

        for(Node n : path) {
            if(prevN != null) {
                currentFlowCapacity = inducedFlow.getFlow(prevN, n).getValue();
                inducedFlow.getFlow(prevN, n).setValue(flowCapacity + currentFlowCapacity);
            }
            prevN = n;
        }

        return inducedFlow;
    }

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

    public static FlowNetwork makeResidual(FlowNetwork fn) {
        FlowNetwork rn = new FlowNetwork();
        for (Node n : fn.graf.getAllNodes()) rn.graf.addNode(n);
        // for all nodes in fn
        for (Node from : rn.graf.getAllNodes()) {
            // for each out edge
            for (Edge outEdge : fn.graf.getOutEdges(from)) {
                int flow;
                if (fn.existsFlow(fn.getFlow(from, outEdge.getTo()))) flow = fn.getFlow(from, outEdge.getTo()).getValue();
                else flow = 0;
                int weight = outEdge.getWeight() - flow;
                if (from.getId() == 3 && outEdge.getTo().getId() == 2) {
                    System.out.println("Weight of edge from " + outEdge.getFrom() + " to " + outEdge.getTo() + " : " + outEdge.getWeight());
                    System.out.println("Flow from " + from + " to " + outEdge.getTo() + " : " + flow);
                    System.out.println("Adding edge : " + from.getId() + " -> " + outEdge.getTo().getId() + ", of value " + weight);
                }
                // add an edge with weight equal to the weight of the out edge minus current flow on that edge in fn
                if (weight > 0) rn.graf.addEdge(new Edge(from.getId(), outEdge.getTo().getId(), weight));
                // add a reverse edge with weight equal to the current flow on the outer edge in fn, if that flow is positive
                if (flow > 0) {
                    rn.graf.addEdge(new Edge(outEdge.getTo().getId(), from.getId(), flow));
                }
            }
        }
        return rn;
    }

    /**
     * Initialise all flow in FlowNetwork to 0
     * @param fn The flowNetwork who need to be initialise
     */
    private void initFlow(FlowNetwork fn) {
        for (Flow f : fn.flows) f.setValue(0);
    }


    /**
     * Computes a breadth-first-search of the graph
     * @return true if a path is fine, false instead
     */
    public boolean existsAugmentingPath(LinkedHashSet<Node> chosenPath){
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

        color[0] = Graf.color.GREY;
        //PriorityQueue doesn't conserve the order : use Linked instead
        LinkedBlockingQueue<Node> queue = new LinkedBlockingQueue<>();
        queue.add(new Node(1));

        while (!queue.isEmpty()) {
            Node u = queue.poll();
            for (Node n : this.graf.getSuccessors(u)) {
                //if vertex is not already visited (white) and u-v edge weight >0
                if (color[index.get(n)] == Graf.color.WHITE && this.graf.getEdge(u.getId(), n.getId()).getWeight()>0) {
                    //return true if t is reach (t = node 999)
                    if(n.getId() == 999) {
                        chosenPath.add(new Node(999));
                        return true;
                    }

                    color[index.get(n)] = Graf.color.GREY;
                    queue.add(n);
                }
            }
            color[index.get(u)] = Graf.color.BLACK;
            chosenPath.add(u);
        }
        return false;
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
        if(u.getId() == 999) System.out.println("999");
        else System.out.print(u.getId()-1  + " - ");

        color[index.get(u)] = Graf.color.GREY;
        List<Node> successor = fn.graf.getSuccessors(u);

        //browse successor and break the dfs search if we find t
        for (Node v : successor) {
            if(chosenPath.contains(new Node(999))){
                return;
            }
            if (color[index.get(v)] == Graf.color.WHITE) {
                dfs_visit(chosenPath, v, color, index, fn);
            }
        }
        color[index.get(u)] = Graf.color.BLACK;
    }

    @Override
    public FlowNetwork clone() {
        FlowNetwork fn = new FlowNetwork();
        for (Edge n : this.graf.getEdgeList()) fn.graf.addEdge(n);
        fn.flows.addAll(this.flows);
        return fn;
    }
}