package GPP_Library.gppVis;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

/**
 * Author: Scott Hendrie
 * Connnector creates visual elements using JavaFX to represent Spreader, Reducers and horizontal
 * lines which can appear in networks.
 */

public class Connector extends VBox {

    public enum TYPE {LINE, SPREADER, REDUCER}

    public Connector(TYPE type){
        switch(type){
            case LINE:
                getChildren().add(new Line(0,0,10,0));
                break;
            case SPREADER:
                getChildren().add(new Line(0,0,10,-7));
                getChildren().add(new Line(0,0,10,0));
                getChildren().add(new Line(0,0,10,7));
                break;
            case REDUCER:
                getChildren().add(new Line(0,0,10,7));
                getChildren().add(new Line(0,0,10,0));
                getChildren().add(new Line(0,0,10,-7));
                break;
        }
        setAlignment(Pos.CENTER);
        //bring the lines together so there are no gaps.
        setSpacing(-0.8);
        setPadding(new Insets(10));
    }
}