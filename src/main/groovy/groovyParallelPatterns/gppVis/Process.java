package groovyParallelPatterns.gppVis;

import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

/**
 * Author: Scott Hendrie
 * Process creates visual elements using JavaFX to represent the process which appear in networks.
 */

public class Process extends VBox {

    public Button btn;
    public Polyline holder;
    private Label lbl;

    public Process(String text) {
        lbl = new Label(text);
        holder = new Polyline();
        btn = new Button();

        update(-1, "");

        getChildren().addAll(btn, holder, lbl);
        setAlignment(Pos.CENTER);
        setMaxWidth(75);
        setMinHeight(70);

        setPadding(new Insets(5,10,5,10));

        lbl.setStyle(
//              "-fx-border-style: solid inside;" +
                "-fx-border-width: 1;" +
                "-fx-border-insets: 2;" +
                "-fx-border-color: grey;");

        disappearAnimation();
    }

    private void disappearAnimation() {
        //shrink animation
        ScaleTransition a1 = new ScaleTransition(Duration.millis(100), btn);
        a1.setToX(0);
        a1.setToY(0);

        //drop down animation
        TranslateTransition a2 = new TranslateTransition(Duration.millis(15), btn);
        a2.setToY(15);

        ParallelTransition pt = new ParallelTransition(a1, a2);
        pt.play();
    }

    private void appearAnimation() {
        //enlarge animation
        ScaleTransition a1 = new ScaleTransition(Duration.millis(100), btn);
        a1.setToX(1);
        a1.setToY(1);

        //rise up animation
        TranslateTransition a2 = new TranslateTransition(Duration.millis(150), btn);
        a2.setFromY(15);
        a2.setToY(0);

        ParallelTransition pt = new ParallelTransition(a1, a2);
        pt.play();
        btn.setVisible(true);
    }

    public void update (int tag, String packet){

        switch(tag){
            case 0: //started
                holder.setStroke(Color.LIGHTGRAY);
                holder.getPoints().clear();
                holder.getPoints().addAll(0., -5.,   0., 0.,   40., 0.,   40., -5.);
                break;
            case 1: //init
                holder.setStroke(Color.GRAY);
                break;
            case 2: //input ready
                //disappearAnimation();
                holder.setStroke(Color.ORANGE);
                break;
            case 3: //input complete
                holder.setStroke(Color.BLACK);
                btn.setText(packet);
                appearAnimation();
                break;
            case 4: //output ready
                if(getText().contains("emit"))
                    appearAnimation();
                holder.setStroke(Color.BLUE);
                btn.setText(packet);
                break;
            case 5: //output complete
                holder.setStroke(Color.BLACK);
                disappearAnimation();
                break;
            case 6: //end
                holder.getPoints().clear();
                holder.getPoints().addAll(
                        18.,0.,   22.,0.
                );
                holder.setStroke(Color.BLACK);
                break;
            case 7: //work start
                holder.setStroke(Color.PURPLE);
                break;
            case 8: //work end
                holder.setStroke(Color.YELLOW);
                break;
            case -1: //before the program has even started
                disappearAnimation();
                holder.getPoints().clear();
                holder.setStrokeWidth(3.);
                holder.getPoints().addAll(
                        19.,0.,   21.,0.
                );
                holder.setStroke(Color.LIGHTGRAY);
                break;
        }
    }

    public String getText(){
        return lbl.getText();
    }

//    @Override
//    public String getUserAgentStylesheet() {
//        return Process.class.getResource("javaone-button.css").toExternalForm();
//    }
}