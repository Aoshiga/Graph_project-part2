import m1graf2020.Node;
import maximumFlow.Flow;
import maximumFlow.FlowNetwork;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        try {
            File f = new File("./Ressources/lectureFlowNetwork.dot");
            FlowNetwork flowNetwork = new FlowNetwork(f);
            flowNetwork.addFlow(new Flow(new Node(0), new Node(1), 11));
            flowNetwork.addFlow(new Flow(new Node(1), new Node(3), 4));
            flowNetwork.addFlow(new Flow(new Node(1), new Node(2), 4));
            flowNetwork.addFlow(new Flow(new Node(2), new Node(4), 11));
            flowNetwork.addFlow(new Flow(new Node(3), new Node(2), 4));
            flowNetwork.addFlow(new Flow(new Node(3), new Node(998), 7));
            flowNetwork.addFlow(new Flow(new Node(4), new Node(3), 7));
            flowNetwork.addFlow(new Flow(new Node(4), new Node(998), 4));
            System.out.println(FlowNetwork.makeResidual(flowNetwork).toDotString());

        } catch (FileNotFoundException e) {
            System.out.println("Erreur : Aucun fichier trouvé");
            //System.out.println(e);
        }
    }

    /**
     public static void main(String[] args) {

     boolean stop = false;
     Map<String, Graf> grafCreate = new HashMap<>();
     String grafName;
     Scanner reader = new Scanner(System.in);  // Reading from System.in
     List<Integer> sa_edge = new ArrayList<>();
     int rand_edge;

     do {
     reader = new Scanner(System.in);

     System.out.println("Enter the number of the instruction you want to follow: ");
     System.out.println("0 : Quit the application");
     System.out.println("1 : Create a graph");
     System.out.println("2 : Modify an existing graph");
     System.out.println("3 : Get information about a graph");
     System.out.println("4 : Graph export");
     System.out.println("5 : Graph representation");
     System.out.println("6 : Graph transformation");
     System.out.println("7 : Graph traversal");

     try {
     int n = -1;
     if(reader.hasNext()) n = reader.nextInt();

     switch (n) {
     case 0:
     System.out.println("Goodbye!");
     stop = true;
     break;

     case 1:
     System.out.println("Choose a kind of graph:");
     System.out.println("0 : Return to main menu");
     System.out.println("1 : Empty graph");
     System.out.println("2 : From SA");
     System.out.println("3 : From DOT file");
     System.out.println("4 : Random graph");
     System.out.println("5 : Random connected graph");
     System.out.println("6 : Random dense graph");
     System.out.println("7 : Random sparse graph");
     System.out.println("8 : Random parametrized graph");
     System.out.println("9 : Random directed acyclic graph");

     int kindOfGraf = reader.nextInt();
     while (!stop) {
     stop = true;

     // Definition of variable use in the switch
     List<Integer> sa = new ArrayList<>();
     int nbrOfVertices;
     int nbrOfEdges;
     int[] array;
     int min;
     int max;
     int rand;

     switch (kindOfGraf) {
     case 0:
     break;

     case 1:
     System.out.println("Give the graph name:");
     grafName = reader.next();

     System.out.println("Enter 1 : directed graph || Enter 2 : undirected graph");
     n = reader.nextInt();
     if (n == 1) grafCreate.put(grafName, new Graf());
     else if (n == 2) grafCreate.put(grafName, new UndirectedGraf());
     else System.out.println("Unknown command : graph not created:");

     break;

     case 2:
     System.out.println("Give the graph name:");
     grafName = reader.next();

     System.out.println("Enter 1 : directed graph || Enter 2 : undirected graph");
     n = reader.nextInt();
     if (n != 1 && n != 2) {
     System.out.println("Unknown command : graph not created:");
     break;
     }

     System.out.println("Give sa with a space between each element. Ex : x x x");
     System.out.println("(EOF or non-integer to terminate): ");

     while (reader.hasNextInt()) {
     sa.add(reader.nextInt());
     }

     int[] saTab = sa.stream().mapToInt(i -> i).toArray();
     if (n == 1) grafCreate.put(grafName, new Graf(saTab));
     else if (n == 2) grafCreate.put(grafName, new UndirectedGraf(saTab));
     break;

     case 3:
     System.out.println("Give the graph name:");
     grafName = reader.next();

     System.out.println("Give the file path:");
     String path = reader.next();

     System.out.println("Enter 1 : directed graph || Enter 2 : undirected graph");
     n = reader.nextInt();
     if (n == 1) grafCreate.put(grafName, new Graf(new File(path)));
     if (n == 2) grafCreate.put(grafName, new UndirectedGraf(new File(path)));
     break;

     case 4:
     System.out.println("Give the graph name:");
     grafName = reader.next();
     max = 20;
     min = 0;
     nbrOfVertices = (new Random()).nextInt(max - min + 1) + min;
     array = Graf.randomGraph(nbrOfVertices);
     System.out.println("Enter 1 : directed graph || Enter 2 : undirected graph");
     n = reader.nextInt();
     if (n == 1) grafCreate.put(grafName, new Graf(array));
     else if (n == 2) grafCreate.put(grafName, new UndirectedGraf(array));
     else System.out.println("Unknown command : graph not created:");

     break;

     case 5:
     System.out.println("Give the graph name:");
     grafName = reader.next();
     max = 20;
     min = 0;
     nbrOfVertices = (new Random()).nextInt(max - min + 1) + min;
     array = Graf.randomGraph(nbrOfVertices);
     System.out.println("Enter 1 : directed graph || Enter 2 : undirected graph");
     n = reader.nextInt();
     if (n == 1) grafCreate.put(grafName, new Graf(array));
     else if (n == 2) grafCreate.put(grafName, new UndirectedGraf(array));
     else {
     System.out.println("Unknown command : graph not created:");
     break;
     }

     List<Node> S = grafCreate.get(grafName).getAllNodes();

     rand = (new Random()).nextInt(nbrOfVertices);
     Node current_node = S.get(rand);
     S.remove(current_node);

     while (!S.isEmpty()) {
     rand = (new Random()).nextInt((--nbrOfVertices));
     Node neighbor_node = S.get(rand);
     grafCreate.get(grafName).addEdge(current_node.getId(), neighbor_node.getId());
     S.remove(neighbor_node);
     current_node = neighbor_node;
     }

     break;

     case 6:
     System.out.println("Give the graph name:");
     grafName = reader.next();
     max = 20;
     min = 0;
     nbrOfVertices = (new Random()).nextInt(max - min + 1) + min;
     System.out.println("Enter 1 : directed graph || Enter 2 : undirected graph");
     n = reader.nextInt();

     sa_edge.clear();
     if(n==1) min = (int) Math.ceil(0.75 * nbrOfVertices * nbrOfVertices / nbrOfVertices);
     else min = (int) Math.ceil((0.75 * nbrOfVertices * nbrOfVertices)/(2*nbrOfVertices));
     max = nbrOfVertices;

     for (int i = 0; i < nbrOfVertices; ++i) {
     rand = (new Random()).nextInt(max - min + 1) + min;
     for (int j = 1; j <= rand; j++) {
     rand_edge = (new Random()).nextInt(nbrOfVertices) + 1;
     if (!sa_edge.contains(rand_edge)) sa_edge.add(rand_edge);
     else --j;
     }
     sa.addAll(sa_edge);
     sa_edge.clear();
     sa.add(0);
     }
     array = sa.stream().mapToInt(i -> i).toArray();

     if (n == 1) grafCreate.put(grafName, new Graf(array));
     else if (n == 2) grafCreate.put(grafName, new UndirectedGraf(array));
     else System.out.println("Unknown command : graph not created:");
     break;

     case 7:
     System.out.println("Give the graph name:");
     grafName = reader.next();
     max = 20;
     min = 0;
     nbrOfVertices = (new Random()).nextInt(max - min + 1) + min;
     System.out.println("Enter 1 : directed graph || Enter 2 : undirected graph");
     n = reader.nextInt();

     sa_edge.clear();
     min = 0;
     if(n==1) max = (int) Math.floor(0.25 * nbrOfVertices * nbrOfVertices / nbrOfVertices);
     else max = (int) Math.floor((0.25 * nbrOfVertices * nbrOfVertices)/(2*nbrOfVertices));

     for (int i = 0; i < nbrOfVertices; ++i) {
     rand = (new Random()).nextInt(max - min + 1) + min;
     for (int j = 1; j <= rand; j++) {
     rand_edge = (new Random()).nextInt(nbrOfVertices) + 1;
     if (!sa_edge.contains(rand_edge)) sa_edge.add(rand_edge);
     else --j;
     }
     sa.addAll(sa_edge);
     sa_edge.clear();
     sa.add(0);
     }
     array = sa.stream().mapToInt(i -> i).toArray();

     if (n == 1) grafCreate.put(grafName, new Graf(array));
     else if (n == 2) grafCreate.put(grafName, new UndirectedGraf(array));
     else System.out.println("Unknown command : graph not created:");
     break;

     case 8:
     System.out.println("Give the graph name:");
     grafName = reader.next();

     System.out.println("Enter 1 : directed graph || Enter 2 : undirected graph");
     n = reader.nextInt();
     if (n == 1) grafCreate.put(grafName, new Graf());
     else if (n == 2) grafCreate.put(grafName, new UndirectedGraf());
     else System.out.println("Unknown command : graph not created:");

     System.out.println("Give the number of vertices:");
     nbrOfVertices = reader.nextInt();
     int maxEdges;
     if(n==1) maxEdges = nbrOfVertices*nbrOfVertices;
     else maxEdges =  nbrOfVertices*(nbrOfVertices+1)/2;

     System.out.println("Give the number of edges (maximum " + maxEdges + "):");
     nbrOfEdges = reader.nextInt();

     if(nbrOfEdges > maxEdges) {
     System.out.println("The number of edges is greater than the maximum");
     System.out.println("Graph creation aborted, return to main menu");
     break;
     }

     for (int i = 1; i <= nbrOfVertices; ++i) {
     grafCreate.get(grafName).addNode(i);
     }

     int cptEdges = 0;
     int rand1;
     int rand2;
     while(cptEdges < nbrOfEdges) {
     rand1 = (new Random()).nextInt(nbrOfVertices) + 1;
     rand2 = (new Random()).nextInt(nbrOfVertices) + 1;
     if(grafCreate.get(grafName).existsEdge(rand1, rand2)) cptEdges--;
     else grafCreate.get(grafName).addEdge(rand1, rand2);
     cptEdges++;
     }

     break;

     case 9:
     System.out.println("Give the graph name:");
     grafName = reader.next();

     sa_edge.clear();
     max = 30;
     min = 0;
     nbrOfVertices = (new Random()).nextInt(max - min + 1) + min;

     for (int i = 1; i < nbrOfVertices; ++i) {
     rand = (new Random()).nextInt((nbrOfVertices - i));
     for (int j = 1; j <= rand; j++) {
     rand_edge = (new Random()).nextInt((nbrOfVertices - (i+1) + 1)) + (i+1);
     if (!sa_edge.contains(rand_edge)) sa_edge.add(rand_edge);
     else --j;
     }
     sa.addAll(sa_edge);
     sa_edge.clear();
     sa.add(0);
     }
     array = sa.stream().mapToInt(i -> i).toArray();

     grafCreate.put(grafName, new Graf(array));
     break;

     default:
     System.out.println("Unknown command, enter a valid command : ");
     //stop=false;
     break;
     }
     }
     stop = false;
     break;

     case 2:
     System.out.println("Enter the name of the graph you want to modify:");
     grafName = reader.next();
     if (!grafCreate.containsKey(grafName)) {
     System.out.println("This graph doesn't exist. Return to main menu.");
     break;
     }

     while (!stop) {
     System.out.println("What you want to do:");
     System.out.println("1 : Add a node");
     System.out.println("2 : Remove a node");
     System.out.println("3 : Add an edge");
     System.out.println("4 : Remove an edge");
     System.out.println("5 : Return to main menu");
     int grafToModify = reader.nextInt();
     int from;

     switch (grafToModify) {
     case 1:
     System.out.println("Enter the node number. Be careful, if the node already exist, it will be erase and replace!");
     grafCreate.get(grafName).addNode(reader.nextInt());
     break;
     case 2:
     System.out.println("Enter the number of the node. This action is irreversible!");
     grafCreate.get(grafName).removeNode(reader.nextInt());
     break;
     case 3:
     System.out.println("Enter the first node:");
     from = reader.nextInt();
     System.out.println("Enter the second node:");
     grafCreate.get(grafName).addEdge(from, reader.nextInt());
     break;
     case 4:
     System.out.println("Enter the first node:");
     from = reader.nextInt();
     System.out.println("Enter the second node:");
     grafCreate.get(grafName).removeEdge(from, reader.nextInt());
     break;
     case 5:
     stop = true;
     break;
     default:
     System.out.println("Unknown command, enter a valid command : ");
     break;
     }
     System.out.println();
     }
     stop = false;
     break;

     case 3:
     System.out.println("Enter the name of the graph:");
     grafName = reader.next();
     if (!grafCreate.containsKey(grafName)) {
     System.out.println("This graph doesn't exist. Return to main menu.");
     stop = true;
     break;
     }

     while (!stop) {
     System.out.println("What you want to do:");
     System.out.println("1 : Get the number of nodes");
     System.out.println("2 : Get all nodes (list)");
     System.out.println("3 : Get successor (list)");
     System.out.println("4 : Check if a node exist");
     System.out.println("5 : Check if two nodes are adjacent");
     System.out.println("6 : Get the number of edges");
     System.out.println("7 : Get all edges (list)");
     System.out.println("8 : Check if an edge exist");
     System.out.println("9 : Get the list of all edges leaving node");
     System.out.println("10 : Get the list of all edges entering node");
     System.out.println("11 : Get incident edges. This is the union of the out and in edge");
     System.out.println("12 : For knowing the in-degree of node");
     System.out.println("13 : For knowing the out-degree of node");
     System.out.println("14 : For knowing the degree of node");
     System.out.println("15 : Return to main menu");

     int grafToModify = reader.nextInt();
     int node;

     switch (grafToModify) {
     case 1:
     System.out.println("Graph " + grafName + " has " + grafCreate.get(grafName).nbNodes() + " node(s).");
     break;
     case 2:
     System.out.println("Nodes of graph " + grafName + ":");
     System.out.println(grafCreate.get(grafName).getAllNodes());
     break;
     case 3:
     System.out.println("Enter a node number:");
     node = reader.nextInt();
     System.out.println("Successor of node " + node + ":");
     System.out.println(grafCreate.get(grafName).getSuccessors(node));
     break;
     case 4:
     System.out.println("Enter a node number:");
     if (grafCreate.get(grafName).existsNode(reader.nextInt()))
     System.out.println("Node exist");
     else System.out.println("Node doesn't exist");
     break;
     case 5:
     System.out.println("Number of the first node:");
     node = reader.nextInt();
     System.out.println("Number of the second node:");
     if (grafCreate.get(grafName).adjacent(node, reader.nextInt()))
     System.out.println("Nodes are adjacent");
     else System.out.println("Nodes are not adjacent");
     break;
     case 6:
     System.out.println("Graph " + grafName + " has " + grafCreate.get(grafName).nbEdges() + " edges");
     break;
     case 7:
     System.out.println("Edges of graph " + grafName + ":");
     System.out.println(grafCreate.get(grafName).getAllEdges());
     break;
     case 8:
     System.out.println("Number of the first node:");
     node = reader.nextInt();
     System.out.println("Number of the second node:");
     if (grafCreate.get(grafName).existsEdge(node, reader.nextInt()))
     System.out.println("Edge exist");
     else System.out.println("Edge doesn't exist");
     break;
     case 9:
     System.out.println("Enter a node number:");
     System.out.println("The out-edges are : " + grafCreate.get(grafName).getOutEdges(reader.nextInt()));
     break;
     case 10:
     System.out.println("Enter a node number:");
     System.out.println("The in-edges are : " + grafCreate.get(grafName).getInEdges(reader.nextInt()));
     break;
     case 11:
     System.out.println("Enter a node number:");
     System.out.println("The incident edge are: " + grafCreate.get(grafName).getIncidentEdges(reader.nextInt()));
     break;
     case 12:
     System.out.println("Enter a node number:");
     node = reader.nextInt();
     System.out.println("The in-degree of node: " + node + "is:" + grafCreate.get(grafName).inDegree(node));
     break;
     case 13:
     System.out.println("Enter a node number:");
     node = reader.nextInt();
     System.out.println("The out-degree of node: " + node + "is:" + grafCreate.get(grafName).outDegree(node));
     break;
     case 14:
     System.out.println("Enter a node number:");
     node = reader.nextInt();
     System.out.println("The degree of node: " + node + "is:" + grafCreate.get(grafName).degree(node));
     break;
     case 15:
     stop = true;
     break;
     default:
     System.out.println("Unknown command, enter a valid command : ");
     break;
     }

     System.out.println();
     }
     stop = false;
     break;

     case 4:
     System.out.println("Enter the name of the graph you want to print:");
     grafName = reader.next();
     if (!grafCreate.containsKey(grafName)) {
     System.out.println("This graph doesn't exist. Return to main menu.");
     break;
     }

     System.out.println("Enter 1 to show the graph in the DOT file or 2 to export the graph to a DOT file");
     n = reader.nextInt();
     if (n == 1) System.out.println(grafCreate.get(grafName).toDotString());
     else if (n == 2) {
     grafCreate.get(grafName).toDotFile(grafName);
     } else System.out.println("Unknown command, return to main menu");
     break;

     case 5:
     System.out.println("Enter the name of the graph you want to print:");
     grafName = reader.next();
     if (!grafCreate.containsKey(grafName)) {
     System.out.println("This graph doesn't exist. Return to main menu.");
     break;
     }

     System.out.println("Enter 1 to get the successor array or 2 to get the adjacency matrix");
     n = reader.nextInt();
     if (n == 1) System.out.println(Arrays.toString(grafCreate.get(grafName).toSuccessorArray()));
     else if (n == 2) {
     int[][] matrix = grafCreate.get(grafName).toAdjMatrix();
     for (int[] ints : matrix) {
     System.out.println(Arrays.toString(ints));
     }
     } else System.out.println("Unknown command, return to main menu");
     break;

     case 6:
     System.out.println("Enter the name of the origin graph:");
     grafName = reader.next();
     if (!grafCreate.containsKey(grafName)) {
     System.out.println("This graph doesn't exist. Return to main menu.");
     break;
     }
     System.out.println("Enter the name of the final graph:");
     String finalGrafName = reader.next();

     System.out.println("Enter 1 to get the reverse graph of " + grafName + " or 2 to get the transitive closure of " + grafName);
     n = reader.nextInt();
     if (n == 1) {
     grafCreate.put(finalGrafName, grafCreate.get(grafName).getReverse());
     System.out.println("Graph successfully created");
     } else if (n == 2) {
     grafCreate.put(finalGrafName, grafCreate.get(grafName).getTransitiveClosure());
     System.out.println("Graph successfully created");
     } else System.out.println("Unknown command, return to main menu");
     break;

     case 7:
     System.out.println("Enter the name of the graph to traversal:");
     grafName = reader.next();
     if (!grafCreate.containsKey(grafName)) {
     System.out.println("This graph doesn't exist. Return to main menu.");
     break;
     }

     System.out.println("Enter 1 to get the DFS traversal of " + grafName + " or 2 to get the BFS traversal of " + grafName);
     n = reader.nextInt();
     if (n == 1) System.out.println("DFS : " + grafCreate.get(grafName).getDFS());
     else if (n == 2) System.out.println("BFS : " + grafCreate.get(grafName).getBFS());
     else System.out.println("Unknown command, return to main menu");
     break;

     default:
     System.out.println("Unknown command, try again :");
     }
     } catch (InputMismatchException | IOException e) {
     System.out.println("Unknown command, try again :");
     }

     } while(!stop);
     reader.close();
     }
     */
}
