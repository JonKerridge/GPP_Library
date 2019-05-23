package GPP_Library.gppVis;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashMap;

/**
 * Author: Scott Hendrie
 * ProcessMap is a data structure for storing/organizing the processes
 * which appear in a network.
 * Updated by Jon Kerridge to refer to GPP components directly
 */

public class ProcessMap extends HashMap<String, Process> {

    //add a single process.
    public Process populateMap(String name) {
        Process process = new Process(name);
        put(name, process);
        return process;
    }

    //create worker
    public VBox addWorker(String name) {
        VBox vb = new VBox();
        vb.getChildren().add(populateMap(name));
        vb.setAlignment(Pos.CENTER);
        return vb;
    }

    // create a group of workers
    public VBox addGroup(int numOfWorkers, String name) {
        VBox vb = new VBox();
        for (int i = 0; i < numOfWorkers; i++) {
            //add a process to the vbox and to the process map
            vb.getChildren().add(populateMap(i + ", " + name));
        }
        vb.setStyle(
                "-fx-border-width: 1;" +
                        "-fx-border-insets: 1;" +
                        "-fx-border-color: black;");
        vb.setAlignment(Pos.CENTER);
        return vb;
    }

    // create a pipeline of workers
    public HBox addPipe(int stages, String... phases) {
        HBox hb = new HBox();
        for (String name : phases) {
            //add a process to the hbox and to the process map
            hb.getChildren().add(populateMap(name));
        }
        hb.setStyle(
                "-fx-border-width: 1;" +
                        "-fx-border-insets: 1;" +
                        "-fx-border-color: black;");
        hb.setAlignment(Pos.CENTER);
        return hb;
    }

    //create the processes of the group of pipelines.
    public VBox addGoP(int groups, String... stages) {
        VBox vb = new VBox();
        for (int i = 0; i < groups; i++) {
            HBox hb = new HBox();
            for (String phase : stages) {
                //create a process in the pipeline and add it to a hbox
                hb.getChildren().add(populateMap(i + ", " + phase));
                //put a border around the pipeline
                hb.setStyle(
                        "-fx-border-width: 1;" +
                                "-fx-border-insets: 1;" +
                                "-fx-border-color: black;");
            }
            //add the complete pipeline to the vbox
            vb.getChildren().add(hb);

        }
        vb.setAlignment(Pos.CENTER);
        return vb;
    }

    //create the processes of a pipeline of groups
    public HBox addPoG(int workers, String... stages) {
        HBox hb = new HBox();
        for (String phase : stages) {
            VBox vb = new VBox();
            for (int i = 0; i < workers; i++) {
                //create a process in the pipeline and add it to a vbox
                vb.getChildren().add(populateMap(i + ", " + phase));
                //put a border around the group
                vb.setStyle(
                        "-fx-border-width: 1;" +
                                "-fx-border-insets: 1;" +
                                "-fx-border-color: black;");
            }
            hb.getChildren().add(vb);
        }
        hb.setAlignment(Pos.CENTER);
        return hb;
    }

    // create the processes of a  multicore engine
    public HBox addMCEngine(int nodes) {
//        System.out.println("MCE: " + nodes);
        HBox hb = new HBox();
        hb.getChildren().add(populateMap("root"));
//        System.out.println("MCE: added root");
        VBox vb = new VBox();
        for (int i = 0; i < nodes; i++) {
            vb.getChildren().add(populateMap(i + ", node"));
//            System.out.println("MCE: added node-" + i);
        }
        HBox hbAll = new HBox();
        hbAll.getChildren().addAll(hb, vb);
//        System.out.println("MCE: created hbAll");
        hbAll.setStyle("-fx-border-width: 1;" +
                "-fx-border-insets: 1;" +
                "-fx-border-color: black;");
//        System.out.println("MCE: set style");
        hbAll.setAlignment(Pos.CENTER);
        return hbAll;
    }

}
