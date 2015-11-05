package gui;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Created by sandyna on 5.5.2015.
 * reprezentuje farebny stvorcek na vyber farby vrcholu
 */
public class SquareColor {
    Canvas square = new Canvas();
    GraphicsContext squareGC = square.getGraphicsContext2D();
    NodeCanvas nodeCanvas;
    ColorPicker colorPicker;
    Color color;
    SquareColor (Color newcolor, NodeCanvas newNodecanvas, double x, double y, ColorPicker colorPicker) {
        square.setWidth(20);
        square.setHeight(20);
        squareGC.setFill(Color.BLACK);
        squareGC.fillRect(0, 0, 20, 20);
        color = newcolor;
        this.colorPicker = colorPicker;
        squareGC.setFill(color);
        squareGC.fillRect(1, 1, 18, 18);
        square.setLayoutX(x);//x
        square.setLayoutY(y);//y
        nodeCanvas = newNodecanvas;
        //ak je kliknute na farbu, zvoli ju a ofarbi nou zvoleny vrchol
        square.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                nodeCanvas.selectedColor = color;
                colorPicker.setValue(color);
                System.out.println("Selected color " + color);
                if(nodeCanvas.isSelected!=null) {
                    nodeCanvas.isSelected.color = color;
                    nodeCanvas.selectNode(nodeCanvas.isSelected);
                }
            }
        });
    }
}
