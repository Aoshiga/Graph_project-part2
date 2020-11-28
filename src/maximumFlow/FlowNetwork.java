package maximumFlow;

import m1graf2020.Graf;
import m1graf2020.Node;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FlowNetwork {
    Graf graf;
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

                //System.out.println("from : " + from_id + " - to : " + to_id + " - with weight : " + weight);

                graf.addEdge(from_id, to_id, weight);
            }
        }

        System.out.println("\n ---- \n");
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

            //System.out.println(entry);

//            for (Node n : entry.getValue()) {
//                System.out.println(n.getId());
//            }

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
}