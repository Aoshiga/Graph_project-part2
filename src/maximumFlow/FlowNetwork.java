package maximumFlow;

import m1graf2020.Graf;
import m1graf2020.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        BufferedReader reader = new BufferedReader(new FileReader(file));
        reader.readLine();
        reader.readLine();
        String line = reader.readLine();
        while (line != null) {
            if (line.contains("}")) break;
            else {
                int from_id;
                if (String.valueOf(line.charAt(1)) == "s") {
                    from_id = 1;
                } else {
                    from_id = Integer.parseInt(String.valueOf(line.charAt(1))) + 1;
                }
                int to_id;
                if (String.valueOf(line.charAt(6)) == "t") {
                    to_id = Node.getBiggestId() + 1;
                } else {
                    to_id = Integer.parseInt(String.valueOf(line.charAt(6))) + 1;
                }
                StringBuilder weight_str = new StringBuilder();
                int index = 16;
                while (line.charAt(index) != ']') {
                    weight_str.append(line.charAt(index++));
                }
                int weight = Integer.parseInt(weight_str.toString());
                graf.addEdge(from_id, to_id, weight);
            }
            line = reader.readLine();
        }
    }

    /**
     * Returns a String representing the graph in the DOT formalism
     * @return a String representing the graph in the DOT formalism
     */
    public String toDotString() {
        StringBuilder dot = new StringBuilder("digraph {\nrankdir=\"LR\";\n");
        for (Map.Entry<Node, List<Node>> entry : graf.getAdjList().entrySet()) {
            Collections.sort(entry.getValue());
            for (Node to : entry.getValue()) {
                dot.append("\t").append(( entry.getKey().getId() == 1 ? "s" : entry.getKey().getId() -1 ));
                dot.append(" -> ");
                dot.append(( to.getId() == Node.getBiggestId() ? "t" : to.getId() -1 ));
                dot.append(" [label=\"");
                dot.append(graf.getEdge(entry.getKey().getId(), to.getId()));
                dot.append("];\n");
            }
        }
        dot.append("}");
        return dot.toString();
    }
}