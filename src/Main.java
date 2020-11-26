import maximumFlow.FlowNetwork;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

// added getEdge to get the instance of an edge in a graf from its source and target nodes
public class Main {

    public static void main(String[] args) throws IOException {
        try {
            File f = new File("./Ressources/test.dot");
            FlowNetwork flowNetwork = new FlowNetwork(f);
            System.out.println(flowNetwork.toDotString());
        } catch (FileNotFoundException e) {
            System.out.println("Erreur : Aucun fichier trouvé");
            //System.out.println(e);
        }
    }
}
