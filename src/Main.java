import maximumFlow.FlowNetwork;

import java.io.File;
import java.io.IOException;

// added getEdge to get the instance of an edge in a graf from its source and target nodes
public class Main {

    public static void main(String[] args) throws IOException {
        FlowNetwork flowNetwork = new FlowNetwork(new File("/test.dot"));
        System.out.println(flowNetwork.toDotString());
    }
}
