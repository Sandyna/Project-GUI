package gui;

import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Created by sandyna on 30.4.2015.
 * Trieda popisujuca jeden vrchol na ploche
 */
public class Nodes {
    double x, y;
    public Rectangle square = new Rectangle();
    Color color;
    //konstruktor, ktory spravi stvorcek s konkretnymi vlastnostami
    Nodes(double newx, double newy) {
        x = newx;
        y = newy;
        color = Color.BLACK;
        square.setY(newy);
        square.setX(newx);
        square.setHeight(10);
        square.setWidth(10);
    }

    //vrati svoju stringovu formu vhodnu pre list
    @Override
    public String toString (){
        return ("[" + Math.round(x) +
                ", " + Math.round(y) + "] " + color.toString());
    }
}
