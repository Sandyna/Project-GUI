package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

/**
 * Created by sandyna on 4.5.2015.
 */
public class NodeCanvas {
    Canvas nodeCanvas;
    //suradnice kurzoru
    double mouseX, mouseY;
    ArrayList<Nodes> nodesList = new ArrayList<>();
    //vytvara list vrcholov vpravo, synchronizovany s listom v maine
    ListView<String> list;
    ObservableList<String> items = FXCollections.observableArrayList();
    //vybrany vrchol
    Nodes isSelected;
    //vybrana farba
    Color selectedColor;
    //pozicia vybraneho vrcholu
    int isSelectedPosition = -1;
    //vrchol, ktory prave taham
    final int[] draggedNode = {-1};
    NodeCanvas (ListView<String> list) {
        selectedColor = Color.BLACK;
        mouseX=-1;
        mouseY=-1;
        this.list = list;
        //nastavi canvas
        nodeCanvas = new Canvas(715, 715);
        GraphicsContext nodeCanvasGC = nodeCanvas.getGraphicsContext2D();
        nodeCanvasGC.setFill(Color.BLACK);
        nodeCanvasGC.fillRect(0, 0, 715, 715);
        nodeCanvasGC.setFill(Color.WHITE);
        nodeCanvasGC.fillRect(1, 1, 713, 713);
        nodeCanvas.setLayoutX(30);
        nodeCanvas.setLayoutY(30);

        //zrusenie moznosti presuvania sa medzi polozkami listu sipkami, aby nedoslo k desynchronizacii
        list.setOnKeyPressed(event -> event.consume());

        //kliknutie mysou na list
        list.setOnMouseClicked(event -> {
            //premenna, ktora bude kontrolovat, ci bol uz nejaky vrchol v liste zvoleny predtym
            boolean wasSelected = false;
            //zisti, ci bolo kliknute na nejaku polozku v liste
            if (list.getSelectionModel().getSelectedIndex() >= 0) {
                //ak bolo kliknute na uz oznaceny vrchol alebo mimo vsetkych poloziek, odznaci oznaceny vrchol
                if (isSelected != null && isSelected.equals(nodesList.get(list.getSelectionModel().getSelectedIndex()))) {
                    deselectNode(isSelected);
                    list.getSelectionModel().clearSelection();
                    wasSelected = true;
                }
                //odznaci ten, co bol povodne oznaceny
                deselectNode(isSelected);
                //ak nebol ziaden povodne oznaceny a nasledne odznaceny, oznaci vrchol. (Klasicke oznacenie vrchola)
                if (!wasSelected) {
                    isSelected = nodesList.get(list.getSelectionModel().getSelectedIndex());
                    isSelectedPosition = list.getSelectionModel().getSelectedIndex();
                    selectNode(nodesList.get(list.getSelectionModel().getSelectedIndex()));
                }
            }
        });
        //stlacenie mysi, prida vrchol
        nodeCanvas.setOnMousePressed(event -> {
            addNode(event.getX(), event.getY(), selectedColor);//Color.BLACK
        });
        //potiahnutie mysi, presunie vrchol
        nodeCanvas.setOnMouseDragged(event -> {
            //oznaci tahany vrchol
            selectNode(nodesList.get(draggedNode[0]));
            //zistuje poziciu kurzora
            mouseX = event.getX();
            mouseY = event.getY();
            //hranice plochy - nesmieme vrchol vytiahnut mimo plochy
            if (draggedNode[0] != -1) {
                if (mouseY <= 0) {
                    nodesList.get(draggedNode[0]).y = 5;
                }
                if (mouseY >= 715) {
                    nodesList.get(draggedNode[0]).y = 710;
                }
                if (mouseX <= 0) {
                    nodesList.get(draggedNode[0]).x = 5;
                }
                if (mouseX >= 715) {
                    nodesList.get(draggedNode[0]).x = 710;
                }
                //zmena suradnic vrcholu
                if ((mouseX > 0 && mouseY > 0) && (mouseX < 715 && mouseY < 715)) {
                    nodesList.get(draggedNode[0]).x = mouseX;
                    nodesList.get(draggedNode[0]).y = mouseY;
                }
                //prekreslenie plochy
                drawLines();
            }
        });

        //ak sa pohne kurzor, zapamata si jeho suradnice
        nodeCanvas.setOnMouseMoved(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
        });
    }

    //odznaci vrchol
    void deselectNode (Nodes node){
        if(isSelected!=null){
            isSelected=null;
            GraphicsContext nodeCanvasGC = nodeCanvas.getGraphicsContext2D();
            nodeCanvasGC.setFill(node.color);
            nodeCanvasGC.fillRect(node.x - 9, node.y - 9, 18, 18);
        }
        isSelectedPosition = -1;
    }

    //oznaci vrchol
    void selectNode (Nodes node){
        //nastavi isSelected a aj isSelectedPosition v array
        if(node!=null) {
            isSelected = node;
            isSelectedPosition = nodesList.indexOf(node);
            draggedNode[0] = isSelectedPosition;
            //vycisti vyber v liste a don prida spat vrcholy
            list.getSelectionModel().clearAndSelect(isSelectedPosition);
            items.clear();
            for(int j=0; j<nodesList.size(); j++){
//                        System.out.println("added item " + j);
                items.add(nodesList.get(j).toString());
            }
            //znovu oznaci vrchol, co bol povodne oznaceny a vymienany
            list.getSelectionModel().clearAndSelect(isSelectedPosition);
            GraphicsContext nodeCanvasGC = nodeCanvas.getGraphicsContext2D();
            nodeCanvasGC.setFill(Color.BLACK);
            nodeCanvasGC.fillRect(node.x - 9, node.y - 9, 18, 18);
            nodeCanvasGC.setFill(Color.WHITESMOKE);
            nodeCanvasGC.fillRect(node.x - 7, node.y - 7, 14, 14);
            nodeCanvasGC.setFill(node.color);//ROSYBROWN
            nodeCanvasGC.fillRect(node.x-6, node.y-6, 12, 12);
        }
    }

    //getter na canvas
    public Node getNodeCanvas() {
        return nodeCanvas;
    }

    //prida vrchol
    public void addNode (double x, double y, Color color) {
        GraphicsContext nodeCanvasGC = nodeCanvas.getGraphicsContext2D();
        //premenne zodpovedne za to, ci sa nesnazime pridat vrchol na existujuci a ci bol vrchol predtym oznaceny
        boolean foundNode = false, wasSelected=false;
        for (int i = 0; i < nodesList.size(); i++) {
            //kontroluje, ci neklikneme na nejaky existujuci vrchol
            if ((nodesList.get(i).x >= x-9) && (nodesList.get(i).x <= x+9) &&
                    ((nodesList.get(i).y >= y-9) && (nodesList.get(i).y <= y+9))){
                draggedNode[0] = i;
//              System.out.println("The node already exists here. Selecting.");
                //kontroluje, ci sme klikli na oznaceny vrchol
                if(isSelectedPosition==i){
//                    System.out.println("The node is already selected. Deselecting.");
                    wasSelected=true;
                }
                //odznaci oznaceny vrchol (nech nemame oznacenych viacero naraz)
                list.getSelectionModel().clearSelection();
                deselectNode(isSelected);
                //nasiel existujuci vrchol
                foundNode = true;
                //ak nebol oznaceny vrchol, oznaci ho
                if(!wasSelected) {
                    selectNode(nodesList.get(i));
                }
                break;
            }
        }
        //klikli sme mimo vrchola, vyraba novy vrchol
        if(!foundNode) {
            new Nodes(x, y);
            Nodes newNode = new Nodes(x, y);
            newNode.color=color;
            nodesList.add(newNode);
//          System.out.println("New node added at " + newNode.x + " " + newNode.y);
            nodeCanvasGC.setFill(newNode.color);//here was newNode.color/color
            nodeCanvasGC.fillRect(newNode.x - 9, newNode.y - 9, 18, 18);
            drawLines();
            deselectNode(isSelected);
            //prida ho aj do listu
            items.add(newNode.toString());
            selectNode(newNode);
        }

    }

    //prekresli plochu
    public void drawLines(){
        clearCanvas();
        GraphicsContext nodeCanvasGC = nodeCanvas.getGraphicsContext2D();
        if(nodesList.size()>1) {
            nodeCanvasGC.setStroke(Color.BLACK);
            nodeCanvasGC.setLineWidth(2);
            //vykresli ciary
            for (int i = 0; i < nodesList.size() - 1; i++) {
                nodeCanvasGC.strokeLine(nodesList.get(i).x, nodesList.get(i).y, nodesList.get(i + 1).x, nodesList.get(i + 1).y);
            }
            nodeCanvasGC.strokeLine(nodesList.get(nodesList.size() - 1).x, nodesList.get(nodesList.size() - 1).y,
                    nodesList.get(0).x, nodesList.get(0).y);
        }
        //vykresli vrcholy
        for(int i=0; i<nodesList.size(); i++){
            nodeCanvasGC.setFill(nodesList.get(i).color);
            nodeCanvasGC.fillRect(nodesList.get(i).x - 9, nodesList.get(i).y - 9, 18, 18);
        }
        //oznaci ten, co bol
        selectNode(isSelected);
    }

    //vycisti zoznam vrcholov
    public void clearNodes(){
        nodesList.clear();
//        System.out.println("Nodes cleared");
    }

    //vycisti plochu s vrcholmi
    public void clearCanvas(){
        GraphicsContext nodeCanvasGC = nodeCanvas.getGraphicsContext2D();
        nodeCanvasGC.setFill(Color.BLACK);
        nodeCanvasGC.fillRect(0, 0, 715, 715);
        nodeCanvasGC.setFill(Color.WHITE);
        nodeCanvasGC.fillRect(1, 1, 713, 713);
    }

    //getter na items z listu (zoznamu vrcholov vpravo)
    public ObservableList<String> getItems() {
        return items;
    }

    //posunie oznaceny vrchol hore v liste (v zozname vrcholov vpravo)
    public void swapNodesUp (){
        for(int i=0; i<nodesList.size(); i++){
            if(isSelected.equals(nodesList.get(i))){
                if(i!=0){
                    Collections.swap(nodesList, i, i - 1);
                    drawLines();
                    selectNode(isSelected);
                    //vymaze zoznam vrcholov vpravo a vypise ho uz s vymenenym vrcholom
                    items.clear();
                    for(int j=0; j<nodesList.size(); j++){
                        items.add(nodesList.get(j).toString());
                    }
                    //znovu oznaci vrchol, co bol povodne oznaceny a vymienany
                    list.getSelectionModel().clearAndSelect(isSelectedPosition);
                }
                break;
            }
        }
    }

    //posunie oznaceny vrchol dole v liste (v zozname vrcholov vpravo)
    public void swapNodesDown (){
        for(int i=0; i < nodesList.size(); i++){
            if(isSelected.equals(nodesList.get(i))){
                if(i!=nodesList.size() - 1) {
                    Collections.swap(nodesList, i, i + 1);
                    drawLines();
                    selectNode(isSelected);
                    //vymaze list a vypise ho nanovo s vymenenymi vrcholmi
                    items.clear();
                    for(int j=0; j<nodesList.size(); j++){
                        items.add(nodesList.get(j).toString());
                    }
                    //oznaci povodne oznaceny v liste
                    list.getSelectionModel().clearAndSelect(isSelectedPosition);
                }
                break;
            }
        }
    }

    //vlozi vrchol na zadane suradnice, funkcia tlacitka "insert node"
    public void insert(double x, double y){
        Nodes newNode = new Nodes(x, y);
        boolean exists = false;
        for(int i=0; i<nodesList.size(); i++) {
            //zisti, ci uz vrchol na danom mieste existuje
            if(newNode.toString().equals(nodesList.get(i).toString())){
                exists = true;
                break;
            }
        }
        //ak neexistuje pridam novy vrchol
        if(!exists) {
            addNode(x, y, selectedColor);//Black
        }
        else {
            //chybova hlaska ak uz vrchol existuje na danom mieste
//            System.out.println("The node already exists.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Alert Dialog");
            alert.setHeaderText("The node already exists here.");
            ButtonType buttonOK = new ButtonType("I will be good next time, I promise.");
            alert.getButtonTypes().setAll(buttonOK);
            alert.showAndWait();
        }
    }

    //odstranenie oznaceneho vrcholu, funkcia tlacitka "delete selected node"
    public void remove() {
        if (isSelected != null) {
            //odstrani vrchol
            nodesList.remove(nodesList.get(isSelectedPosition));
            //odstrani ho aj z listu
            items.clear();
            //vygeneruje novy list bez neho
            for (int j = 0; j < nodesList.size(); j++) {
                items.add(nodesList.get(j).toString());
            }
            if(nodesList.size() >0) {
                if(isSelectedPosition==nodesList.size()){
                    isSelected = nodesList.get(isSelectedPosition-1);
                }
                else {
                    isSelected = nodesList.get(isSelectedPosition);
                }
                selectNode(isSelected);
            }
            else {
                isSelected=null;
            }
            drawLines();
        }
        else {
            //chybova hlaska ak nie je oznaceny ziaden vrchol
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Alert Dialog");
            alert.setHeaderText("No node is selected.");
            ButtonType buttonOK = new ButtonType("I will be good next time, I promise.");
            alert.getButtonTypes().setAll(buttonOK);
            alert.showAndWait();
        }
    }

    //vytvori pravidelny n uholnik, funkcia polozky new v menu

    public void generate (int number){
        //pre 1 vvykresli vrchol v strede
        if(number==1){
            addNode(357, 357, selectedColor);//black
        }
        // pre ine cisla vykrelsi mnohouholnik
        else {
            double alpha = 2*Math.PI/number;
            for(int i=0; i<number; i++) {
                double x = 357 + Math.round(Math.sin(alpha * i) * 200);
                double y = 357 + Math.round(Math.cos(alpha * i) * 200);
                addNode(x, y, selectedColor);//black
            }
        }
        drawLines();
    }

    //otoci polygon, funkcia tlacidla rotate
    public void rotate (double angle) {
        angle%=360;
        angle = 2*Math.PI/(360/angle);
        boolean isOut = false;
        for(int i=0; i<nodesList.size(); i++){
            double temporaryX = nodesList.get(i).x - 357;
            double temporaryY = nodesList.get(i).y - 357;
            if(((Math.cos(angle)*(temporaryX) - Math.sin(angle)*(temporaryY)) + 357) < 0 ||
                    (Math.cos(angle)*(temporaryX) - Math.sin(angle)*(temporaryY)) + 357 > 715){
                isOut=true;
            }
            if(((Math.sin(angle)*(temporaryX) + Math.cos(angle)*(temporaryY)) + 357) < 0 ||
                    ((Math.sin(angle)*(temporaryX) + Math.cos(angle)*(temporaryY)) + 357) >715 ){
                isOut=true;
            }
        }
        //po otoceni vykresli co ma
        if(!isOut) {
            for (int i = 0; i < nodesList.size(); i++) {
                double temporaryX = nodesList.get(i).x - 357;
                double temporaryY = nodesList.get(i).y - 357;
                nodesList.get(i).x = (Math.cos(angle) * (temporaryX) -
                        Math.sin(angle) * (temporaryY)) + 357;
                nodesList.get(i).y = (Math.sin(angle) * (temporaryX) +
                        Math.cos(angle) * (temporaryY)) + 357;
            }
            drawLines();
        } else{
            //dialog, ak nejaky vrchol skonci mimo plochy
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Out of boundary");
            alert.setHeaderText("One or more nodes will be out of canvas");
            alert.setContentText("Do you wish to rotate the object anyway?");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() == ButtonType.OK){
                for (int i = 0; i < nodesList.size(); i++) {
                    double temporaryX = nodesList.get(i).x - 357;
                    double temporaryY = nodesList.get(i).y - 357;
                    nodesList.get(i).x = (Math.cos(angle) * (temporaryX) -
                            Math.sin(angle) * (temporaryY)) + 357;
                    nodesList.get(i).y = (Math.sin(angle) * (temporaryX) +
                            Math.cos(angle) * (temporaryY)) + 357;
                }
                drawLines();
            }
        }
    }

    //zvacsenie polygonu, funkcia tlacidla resize
    public void resize (double ratio) {
        boolean isOut = false;
        //zisti, ci nie je nejaky vrchol mimo plochy
        for(int i=0; i<nodesList.size(); i++){
            if(((ratio*(nodesList.get(i).x-357))+357 < 0 || (ratio*(nodesList.get(i).x-357))+357 > 715)){
                isOut=true;
            }
            if(((ratio*(nodesList.get(i).y-357))+357 < 0 || ((ratio*(nodesList.get(i).y-357))+357) >715 )) {
                isOut=true;
            }
        }
        //ak nie je, vykresli co ma
        if(!isOut) {
            for (int i=0; i<nodesList.size(); i++){
                nodesList.get(i).x = (ratio*(nodesList.get(i).x-357))+357;
                nodesList.get(i).y = (ratio*(nodesList.get(i).y-357))+357;
            }
            drawLines();
        } else{
            //ak je, vyhodi hlasku
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Out of boundary");
            alert.setHeaderText("One or more nodes will be out of canvas");
            alert.setContentText("Do you wish to resize the object anyway?");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() == ButtonType.OK){
                for (int i = 0; i < nodesList.size(); i++) {
                    nodesList.get(i).x = (ratio*(nodesList.get(i).x-357))+357;
                    nodesList.get(i).y = (ratio*(nodesList.get(i).y-357))+357;
                }
                drawLines();
            }
        }
    }

    //posunie polygon
    public void shift (double x, double y) {
        boolean isOut = false;
        //zisti, ci nebude mimo plochy
        for(int i=0; i<nodesList.size(); i++){
            if(((nodesList.get(i).x+x < 0 || (nodesList.get(i).x+x > 715)))){
                isOut=true;
            }
            if((nodesList.get(i).y+y < 0 || (nodesList.get(i).y+y >715 ))) {
                isOut=true;
            }
        }
        //ak nie, posunie
        if(!isOut) {
            for (int i=0; i<nodesList.size(); i++) {
                nodesList.get(i).x = nodesList.get(i).x+x;
                nodesList.get(i).y = nodesList.get(i).y+y;
            }
            drawLines();
        } else{
            //ak ano, spyta sa, ci ma posunut
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Out of boundary");
            alert.setHeaderText("One or more nodes will be out of canvas");
            alert.setContentText("Do you wish to shift the object anyway?");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() == ButtonType.OK){
                for (int i=0; i<nodesList.size(); i++) {
                    nodesList.get(i).x = nodesList.get(i).x+x;
                    nodesList.get(i).y = nodesList.get(i).y+y;
                }
                drawLines();
            }
        }
    }
}
