package groovy_parallel_patterns.gppVis;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Author: Scott Hendrie
 * The main JavaFX class which deals the the GUI elements and their behaviours.
 */

public class Visualiser extends Application {

    private static Stage ps;
    public static ProcessMap p = new ProcessMap();
    public static HBox hb = new HBox();

    private static ArrayList<String> logEvents;
    private static int logPointer = 0; //points the line containing the next logEvent in the sequence.
    //UI controls
    private static Slider slider;
    private static Button btnStepBack;
    private static Button btnStepForward;
    //labels for stats
    private static Label lblCurrentEvent;
    private static Label lblCurrentEventTimestamp;
    private static Label lblLastEventTimeDiff;
    private static Label lblElapsedTime;
    private static Label lblTotalTime;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ps = primaryStage;
    }

    public static void networkScene(String logFileName) {
        Pane controls = getControlsPane();

        Pane stats = getStatsPane();

        hb.setMaxHeight(hb.getHeight()); //stop hb expanding to fit the window if the window is maximized.

        BorderPane bp = new BorderPane();
        bp.setLeft(hb);
        bp.setBottom(controls);
        bp.setRight(stats);
        bp.setMargin(stats, new Insets(0,0,0,20));
        bp.setPadding(new Insets(5));

        ps.setScene(new Scene(bp));
        ps.setTitle(logFileName);
        ps.centerOnScreen();
        ps.show();
    }

    private static Pane getControlsPane(){
        HBox playControls = new HBox();

        btnStepBack = new Button("<");
        btnStepBack.setMinWidth(25);
        btnStepBack.setOnAction(e -> slider.decrement());

        slider = new Slider();
        slider.setValue(slider.getMax());
        //make the slider scale with the window.
        slider.prefWidthProperty().bind(playControls.widthProperty());
//        slider.setPrefWidth(300);
        slider.valueProperty().addListener( (observable, oldV, newV) -> sliderClick(newV.intValue()));

        btnStepForward = new Button(">");
        btnStepForward.setMinWidth(25);
        btnStepForward.setOnAction(e -> slider.increment());

        playControls.getChildren().addAll(btnStepBack, slider, btnStepForward);
        playControls.setAlignment(Pos.CENTER);
        playControls.setPadding(new Insets(5,3,0,3));

        //disable controls until the network has completed its run
        btnStepBack.setDisable(true);
        btnStepForward.setDisable(true);
        slider.setDisable(true);

        return playControls;
    }

    private static Pane getStatsPane(){
        GridPane grd = new GridPane();
        grd.setHgap(10);
        grd.setVgap(10);
        grd.setMinWidth(250);
        grd.setPadding(new Insets(10));
        grd.setAlignment(Pos.CENTER);
        grd.setStyle("-fx-border-color: black;"+
                "-fx-background-color: lightgrey;");

        Label lblHeading = new Label("Statistics");
        grd.add(lblHeading, 0, 0, 2, 2);
        grd.setHalignment(lblHeading, HPos.CENTER);
        lblHeading.setStyle("-fx-font-weight: bold;"+
                "-fx-font-size: 20px;");

        Label lblCurrentEventText = new Label("Showing event:");
        lblCurrentEvent = new Label("----");
        grd.add(lblCurrentEventText, 0, 2, 1, 1);
        grd.add(lblCurrentEvent, 1, 2, 1, 1);
        grd.setHalignment(lblCurrentEventText, HPos.RIGHT);

        Label lblCurrentEventTimestampText = new Label("Event timestamp(ms):");
        lblCurrentEventTimestamp = new Label("----");
        grd.add(lblCurrentEventTimestampText, 0, 3, 1, 1);
        grd.add(lblCurrentEventTimestamp, 1, 3, 1, 1);
        grd.setHalignment(lblCurrentEventTimestampText, HPos.RIGHT);

        Label lblLastEventTimeDiffText = new Label("Last event diff(ms):");
        lblLastEventTimeDiff = new Label("----");
        grd.add(lblLastEventTimeDiffText, 0, 4, 1, 1);
        grd.add(lblLastEventTimeDiff, 1, 4, 1, 1);
        grd.setHalignment(lblLastEventTimeDiffText, HPos.RIGHT);

        Label lblElapsedTimeText = new Label("Elapsed time(ms):");
        lblElapsedTime = new Label("----");
        grd.add(lblElapsedTimeText, 0, 5, 1, 1);
        grd.add(lblElapsedTime, 1, 5, 1, 1);
        grd.setHalignment(lblElapsedTimeText, HPos.RIGHT);

        Label lblTotalTimeText = new Label("Total Time(ms):");
        lblTotalTime = new Label("----");
        grd.add(lblTotalTimeText, 0, 6, 1, 1);
        grd.add(lblTotalTime, 1, 6, 1, 1);
        grd.setHalignment(lblTotalTimeText, HPos.RIGHT);

        return grd;
    }

    public static void readLog(String logPath){
        logEvents = new ArrayList<>();
        //read file
        try {
            BufferedReader br = new BufferedReader(new FileReader(logPath));
            String st;
            while ((st = br.readLine()) != null) {
                logEvents.add(st);
            }
        }catch(IOException e) {
            System.out.println("Log File Not Found");
        }

        //log files aren't always in timestamp order so must be ordered.
        //this comparator lamda sorts list events using the timestamps.
        logEvents.sort((String o1, String o2)->
                (int)(Long.parseLong(o1.split(",")[0])-Long.parseLong(o2.split(",")[0]))
        );

        //set up slider control.
        slider.setMin(-1); //The -1 event shows the display before any event has occurred.
        slider.setBlockIncrement(1);
        slider.setMax(logEvents.size()-1);
        slider.setValue(slider.getMax());

        //enable controls
        btnStepBack.setDisable(false);
        btnStepForward.setDisable(false);
        slider.setDisable(false);

        //calc total time
        String[] startLine = logEvents.get(0).split(", ");
        String[] endLine = logEvents.get(logEvents.size()-1).split(", ");
        Long start = Long.parseLong(startLine[0]), end = Long.parseLong(endLine[0]);
        Long total = end-start;
        lblTotalTime.setText(Long.toString(total));
    }

    private static void updateStatsWindow(int currentLineNum){
        String[] firstLine = logEvents.get(0).split(", ");
        Long startTime = Long.parseLong(firstLine[0]);

        String[] currentLine = logEvents.get(currentLineNum).split(", ");
        Long currentTime = Long.parseLong(currentLine[0]);

        //event number
        lblCurrentEvent.setText((logPointer+1) + " of " + logEvents.size());

        //current event's timestamp
        lblCurrentEventTimestamp.setText(Long.toString(currentTime));

        //Elapsed time
        lblElapsedTime.setText(Long.toString(currentTime-startTime));

        if(currentLineNum > 0){
            String[] previousLine = logEvents.get(currentLineNum-1).split(", ");
            Long last = Long.parseLong(previousLine[0]);

            //calc time from last event
            Long sum = currentTime-last;
            lblLastEventTimeDiff.setText(Long.toString(sum));


        }
    }

    private static void rewind(){
        logPointer = 0;
        //reset network display
        p.forEach((key, value) -> updateProcess(key, -1, ""));
        lblCurrentEvent.setText("0 of " + logEvents.size());
        lblCurrentEventTimestamp.setText("----");
        lblLastEventTimeDiff.setText("----");
        lblElapsedTime.setText("----");
    }

    private static void sliderClick(int newV) {
        if(logPointer-1 > newV){
            rewind();
        }
        while(logPointer-1 < newV){
            stepForward();
        }
    }

    private static void stepForward(){
        if(logPointer < logEvents.size()){
            String st = logEvents.get(logPointer);
            String[] sts = st.split(", ");

            //some log events have 4 args, some have 5 (because they include their group num).
            if(sts.length == 4){
                String processName = sts[2];
                updateProcess(processName, Integer.parseInt(sts[1]), sts[3]);
            }else{
                String processName = sts[2] + ", " + sts[3];
                updateProcess(processName, Integer.parseInt(sts[1]), sts[4]);
            }

            updateStatsWindow(logPointer);
            logPointer++;
        }
    }

    public static void updateProcess(String process, int tag, String packet){
//        System.out.println ("UP: process " + process + "::tag " +  tag +"::packet " + packet);
        p.get(process).update(tag, packet);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Platform.exit();
        System.exit(0);
    }
}
