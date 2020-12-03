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
    List<Flow> flows;
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
                flows.add(new Flow(from_id, to_id));
                flows.add(new Flow(to_id, from_id));

            }
        }
    }

    FlowNetwork() {
        this.graf = new Graf();
        this.flows = new ArrayList<>();
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

    public boolean existsFlow(Flow f) {return flows.contains(f);}

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
                dot.append("\t").append(( entry.getKey().getId() == 1 ? "s" : entry.getKey().getId() -1 ));
                dot.append(" -> ");
                dot.append(( to.getId() == biggestId ? "t" : to.getId() -1 ));
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
        StringBuilder dot = new StringBuilder("digraph {\n\trankdir=\"LR\";\n");
        dot.append("label =\"(")
                .append(graphNumber)
                .append(") Flow induced from residual graph ")
                .append(graphNumber-1)
                .append(". Value: ")
                .append(this.maxFlow())
                .append("\";");
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
        while(residualNetwork.existsAugmentingPath(path)) {
            // toDotFile(residualNetwork, path)
            // update inducedFlow
            Node prevN = null;
            int flowCapacity = Integer.MAX_VALUE;

            for(Node n : path) {
                System.out.println(prevN + "   -   " + n);

                if(prevN != null) {
                    System.out.println(residualNetwork.graf.getEdge(prevN.getId(), n.getId()));
                    int currentWeight = residualNetwork.graf.getEdge(prevN.getId(), n.getId()).getWeight();
                    System.out.println(currentWeight);
                    flowCapacity = Integer.min(flowCapacity, currentWeight);
                }
                prevN = n;
            }

            prevN = null;
            int currentFlowCapacity;
            for(Node n : path) {
                if(prevN != null) {
                    currentFlowCapacity = inducedFlow.getFlow(prevN, n).getValue();
                    inducedFlow.getFlow(prevN, n).setValue(flowCapacity + currentFlowCapacity);
                }
                prevN = n;
            }
            // toDotFile(inducedFlow)
            inducedFlow.toDotString(cpt);

            // residualNetwork = residualNetwork(inducedFlow);
            prevN = null;
            int currentWeight;
            Edge e;
            for(Node n : path) {
                if(prevN != null) {
                    e = residualNetwork.graf.getEdge(n.getId(), prevN.getId());
                    currentWeight = e.getWeight();
                    e.setWeight(currentWeight - flowCapacity);
                    e = residualNetwork.graf.getEdge(prevN.getId(), n.getId());
                    currentWeight = e.getWeight();
                    e.setWeight(currentWeight + flowCapacity);
                }
                prevN = n;
            }

            cpt++;
        }



        return inducedFlow.maxFlow();
    }

    /**
     * Get the maximum flow of a flow network
     * @return The maximum flow value
     */
    private int maxFlow() {
        int maxFlow = 0;
        for (Node s : this.graf.getSuccessors(1)) {
            maxFlow += getFlow(graf.getNode(1), s).getValue();
        }
        return this.maxFlow();
    }

    public static FlowNetwork makeResidual(FlowNetwork fn) {
        FlowNetwork rn = new FlowNetwork();
        for (Node n : fn.graf.getAllNodes()) rn.graf.addNode(n);
        // for all nodes in fn
        for (Node to : rn.graf.getAllNodes()) {
            // for each out edge
            for (Edge outEdge : fn.graf.getOutEdges(to)) {
                int flow;
                if (fn.existsFlow(fn.getFlow(to, outEdge.getFrom()))) flow = fn.getFlow(to, outEdge.getFrom()).getValue();
                else flow = 0;
                int weight = outEdge.getWeight() - flow;
                // add an edge with weight equal to the weight of the out edge minus current flow on that edge in fn
                rn.graf.addEdge(new Edge(to.getId()+1, outEdge.getFrom().getId()+1, weight));
                // add a reverse edge with weight equal to the current flow on the outer edge in fn, if that flow is positive
                if (flow > 0) rn.graf.addEdge(new Edge(outEdge.getFrom().getId()+1, to.getId()+1, flow));
            }
        }
        return rn;
    }

    public void increaseFlow() {

    }

    /**
     * Initialise all flow in FlowNetwork to 0
     * @param fn The flowNetwork who need to be initialise
     */
    private void initFlow(FlowNetwork fn) {
        for (Flow f : fn.flows) f.setValue(0);
    }

    @Override
    public FlowNetwork clone() {
        FlowNetwork fn = new FlowNetwork();
        for (Edge n : this.graf.getEdgeList()) fn.graf.addEdge(n);
        fn.flows.addAll(this.flows);
        return fn;
    }


    /**
     * Computes a breadth-first-search of the graph
     * @return true if a path is fine, false instead
     */
    public boolean existsAugmentingPath(LinkedHashSet<Node> choosenPath){
        choosenPath.clear();
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
            System.out.print(u.getId() + " - ");
            for (Node n : this.graf.getSuccessors(u)) {
                //if vertex is not already visited (white) and u-v edge weight >0
                if (color[index.get(n)] == Graf.color.WHITE && this.graf.getEdge(u.getId(), n.getId()).getWeight()>0) {
                    //return true if t is reach (t = node 999)
                    if(n.getId() == 999) {
                        choosenPath.add(new Node(999));
                        System.out.print(999);
                        System.out.print("\n\n");
                        return true;
                    }

                    color[index.get(n)] = Graf.color.GREY;
                    queue.add(n);
                }
            }
            color[index.get(u)] = Graf.color.BLACK;
            choosenPath.add(u);
        }
        System.out.print("\n\n--");

        return false;
    }

}