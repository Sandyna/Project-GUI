package gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Created by sandyna on 5.5.2015.
 * canvas na vyber farieb
 */
public class ColorCanvas {
    Canvas colorCanvas;
    ArrayList<SquareColor> listOfColors = new ArrayList<>();
    GraphicsContext colorCanvasGC;
    ColorPicker colorPicker;

    NodeCanvas nodeCanvas;
    public ColorCanvas (NodeCanvas newNodeCanvas, ColorPicker colorPicker) {
        nodeCanvas = newNodeCanvas;
        colorCanvas = new Canvas(244, 100);
        colorCanvasGC = colorCanvas.getGraphicsContext2D();
        colorCanvasGC.setFill(Color.BLACK);
        colorCanvasGC.fillRect(0, 0, 244, 100);
        colorCanvasGC.setFill(Color.WHITESMOKE);
        colorCanvasGC.fillRect(1, 1, 242, 98);
        colorCanvas.setLayoutX(940 + 40);
        colorCanvas.setLayoutY(60);
        this.colorPicker = colorPicker;
        //zoznam default farieb
        Color[] colors = {
                Color.AQUA,
                Color.DEEPSKYBLUE,
                Color.DODGERBLUE,
                Color.BLUE,
                Color.NAVY,
                Color.TEAL,
                Color.GREEN,
                Color.LIMEGREEN,
                Color.YELLOWGREEN,
                Color.LIME,
                Color.GREENYELLOW,
                Color.YELLOW,
                Color.GOLD,
                Color.ORANGE,

                Color.MOCCASIN,
                Color.PINK,
                Color.VIOLET,
                Color.MEDIUMPURPLE,
                Color.PURPLE,
                Color.MEDIUMVIOLETRED,
                Color.FUCHSIA,
                Color.CRIMSON,
                Color.DARKRED,
                Color.RED,
                Color.ORANGERED,

                Color.SANDYBROWN,
                Color.CHOCOLATE,
                Color.SADDLEBROWN,
                Color.GRAY,
                Color.BLACK
        };
        //nastavi farby a umiestnenie pre squareColor
        boolean didBreak = false;
        int count = 0;
        for(int i=0; i<3; i++) {
            for(int j=0; j<11; j++){
                if(count >= colors.length){
                    didBreak = true;
                    break;
                }
                else {
                    SquareColor newSqrC = new SquareColor(colors[count], newNodeCanvas, 982+(j*22), 62+(i*22),
                            colorPicker);
                    listOfColors.add(newSqrC);
                    count++;
                }
            }
            if(didBreak){
                break;
            }
        }
    }
}
