package gui;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.util.Optional;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        //vytvaranie listu vrcholov vpravo

        ListView<String> list;
        list = new ListView<>();

        //zakladne nastavenie zobrazenia

        Pane mainPane = new Pane();
        Scene mainScene = new Scene(mainPane, 1280, 800);
        primaryStage.setTitle("Frantisek");
        primaryStage.setScene(mainScene);
        primaryStage.setMinWidth(1280);
        primaryStage.setMaxWidth(1280);
        primaryStage.setMinHeight(800);
        primaryStage.setMaxHeight(800);
        primaryStage.show();

        //vypise verziu javy
        System.out.println("javafx.runtime.version: " + System.getProperties().get("javafx.runtime.version"));

        //color picker
        final ColorPicker colorPicker = new ColorPicker(Color.BLACK);
        colorPicker.setLayoutX(1077);
        colorPicker.setLayoutY(130);

        //oblasti aktivity - canvasy

        //hlavny canvas na ktorom to vsetko bude ulozene
        Canvas mainCanvas = new Canvas(1280, 800);
        //biele okno, kde sa vykresluju vrcholy
        NodeCanvas nodeCanvas = new NodeCanvas(list);
        //okno na vyber farby
        ColorCanvas colorCanvas = new ColorCanvas(nodeCanvas, colorPicker);
        GraphicsContext mainGC = mainCanvas.getGraphicsContext2D();
        mainGC.setFill(Color.LAVENDER);
        mainGC.fillRect(0, 0, 1280, 800);
        mainPane.getChildren().add(mainCanvas);
        mainPane.getChildren().add(nodeCanvas.getNodeCanvas());
        mainPane.getChildren().add(colorCanvas.colorCanvas);
        for(int i=0; i<colorCanvas.listOfColors.size(); i++) {
            mainPane.getChildren().add(colorCanvas.listOfColors.get(i).square);
        }

        //nastavenie listView a pridanie listu itemov

        ObservableList<String> items = nodeCanvas.getItems();
        list.setPrefWidth(200);
        list.setPrefHeight(500);
        list.setLayoutX(750);
        list.setLayoutY(60);
        list.setItems(items);
        mainPane.getChildren().add(list);

        //napis "Zoznam vrcholov"

        Label vrcholy = new Label("List of Nodes:");
        vrcholy.setLayoutX(750);
        vrcholy.setLayoutY(35);
        mainPane.getChildren().add(vrcholy);

        //napis "Farby"

        Label farby = new Label("Colours:");
        farby.setLayoutX(980);
        farby.setLayoutY(35);
        mainPane.getChildren().add(farby);

        //pouzivatelske info
        Text info = new Text(990, 350, "Keys: \n\nDelete to remove a vertex. \n\n" +
                "F to colorize the selected vertex with selected colour.\n\n" +
                "ESC to close the stage.");
        info.setWrappingWidth(250);
        mainPane.getChildren().add(info);

        //buttony

        //up button, presuva vybrany vrchol dohora
        Button moveUp = new Button("^");
        moveUp.setMinWidth(30);
        moveUp.setLayoutX(955);
        moveUp.setLayoutY(240);
        moveUp.setOnMouseClicked(event -> {
            if (nodeCanvas.isSelected != null) {
                nodeCanvas.swapNodesUp();
            }
        });

        //down button, presuva vybrany vrchol dole

        Button moveDown = new Button("v");
        moveDown.setMinWidth(30);
        moveDown.setLayoutX(955);
        moveDown.setLayoutY(270);
        moveDown.setOnMouseClicked(event -> {
            if (nodeCanvas.isSelected != null) {
                nodeCanvas.swapNodesDown();
            }
        });

        //insert button, vlozi novy vrchol
        Button insertNode = new Button("Insert node");
        insertNode.setMinWidth(200);
        insertNode.setLayoutX(750);
        insertNode.setLayoutY(570);
        insertNode.setOnMouseClicked(event -> {
            //dialogove okno na zadanie suradnic
            TextInputDialog textinx = new TextInputDialog();
            textinx.setTitle("TextInput");
            textinx.setHeaderText("Set the x for new Node:");
            textinx.setContentText("Double value between 0.00 and 713.00");
            Optional<String> resultX = textinx.showAndWait();
            TextInputDialog textiny = new TextInputDialog();
            textiny.setTitle("TextInput");
            textiny.setHeaderText("Set the y for new Node:");
            textiny.setContentText("Double value between 0.00 and 713.00");
            Optional<String> resultY = textiny.showAndWait();
            if (resultX.isPresent() && resultY.isPresent()) {
                String stringX = resultX.get();
                String stringY = resultY.get();
                try {
                    double x = Double.parseDouble(stringX);
                    double y = Double.parseDouble(stringY);
                    if (x > 713 || y > 713 || x < 0 || y < 0) {
                        //chybova hlaska ak su suradnice mimo plochy na vrcholy
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Alert Dialog");
                        alert.setHeaderText("Invalid input");
                        ButtonType buttonOK = new ButtonType("I will be good next time, I promise.");
                        alert.getButtonTypes().setAll(buttonOK);
                        alert.initStyle(StageStyle.UTILITY);
                        alert.showAndWait();
                    } else {
                        nodeCanvas.insert(x, y);
                        nodeCanvas.drawLines();
                    }
                } catch (Exception e) {
                    //chybova hlaska nespravneho vstupu
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Alert Dialog");
                    alert.setHeaderText("Invalid input");
                    ButtonType buttonOK = new ButtonType("I will be good next time, I promise.");
                    alert.getButtonTypes().setAll(buttonOK);
                    alert.initStyle(StageStyle.UTILITY);
                    alert.showAndWait();
                }
            }
        });

        //button na vymazavanie vybraneho vrcholu
        Button deleteNode = new Button("Delete selected node");
        deleteNode.setMinWidth(200);
        deleteNode.setLayoutX(750);
        deleteNode.setLayoutY(600);
        deleteNode.setOnMouseClicked(event -> nodeCanvas.remove());

        //button na otacanie
        Button rotate = new Button("Rotate polygon");
        rotate.setMinWidth(200);
        rotate.setLayoutX(750);
        rotate.setLayoutY(630);
        rotate.setOnMouseClicked(event -> {
            TextInputDialog angleCHoice = new TextInputDialog();
            angleCHoice.setTitle("Rotation");
            angleCHoice.setHeaderText("Set new angle of rotation (clockwise):");
            angleCHoice.setContentText("Double value");
            Optional<String> resultX = angleCHoice.showAndWait();
            if (resultX.isPresent()) {
                String stringX = resultX.get();
                try {
                    double angle = Double.parseDouble(stringX);
                    nodeCanvas.rotate(angle);
//                        nodeCanvas.drawLines();
                } catch (Exception e) {
                    //chybova hlaska nespravneho vstupu
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Alert Dialog");
                    alert.setHeaderText("Invalid input");
                    ButtonType buttonOK = new ButtonType("I will be good next time, I promise.");
                    alert.getButtonTypes().setAll(buttonOK);
                    alert.initStyle(StageStyle.UTILITY);
                    alert.showAndWait();
                }
            }
        });

        //button na zmenu velkosti polygonu
        Button scale = new Button("Scale polygon");
        scale.setMinWidth(200);
        scale.setLayoutX(750);
        scale.setLayoutY(660);
        scale.setOnMouseClicked(event -> {
            TextInputDialog scaleChoice = new TextInputDialog();
            scaleChoice.setTitle("Size");
            scaleChoice.setHeaderText("Set enlargement for polygon:");
            scaleChoice.setContentText("Double value");
            Optional<String> resultX = scaleChoice.showAndWait();
            if (resultX.isPresent()) {
                String stringX = resultX.get();
                try {
                    double ratio = Double.parseDouble(stringX);
                    nodeCanvas.resize(ratio);
                } catch (Exception e) {
                    //chybova hlaska nespravneho vstupu
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Alert Dialog");
                    alert.setHeaderText("Invalid input");
                    ButtonType buttonOK = new ButtonType("I will be good next time, I promise.");
                    alert.getButtonTypes().setAll(buttonOK);
                    alert.initStyle(StageStyle.UTILITY);
                    alert.showAndWait();
                }
            }
        });

        //button na presunutie polygonu
        Button move = new Button("Move polygon");
        move.setMinWidth(200);
        move.setLayoutX(750);
        move.setLayoutY(690);
        move.setOnMouseClicked(event -> {
            //dialogove okno na zadanie suradnic
            TextInputDialog textinx = new TextInputDialog();
            textinx.setTitle("TextInput");
            textinx.setHeaderText("Set horizontal shift:");
            textinx.setContentText("Double value");
            Optional<String> resultX = textinx.showAndWait();
            TextInputDialog textiny = new TextInputDialog();
            textiny.setTitle("TextInput");
            textiny.setHeaderText("Set vertical shift:");
            textiny.setContentText("Double value");
            Optional<String> resultY = textiny.showAndWait();
            if (resultX.isPresent() && resultY.isPresent()) {
                String stringX = resultX.get();
                String stringY = resultY.get();
                try {
                    double x = Double.parseDouble(stringX);
                    double y = Double.parseDouble(stringY);
                    nodeCanvas.shift(x, y);
                } catch (Exception e) {
                    //chybova hlaska nespravneho vstupu
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Alert Dialog");
                    alert.setHeaderText("Invalid input");
                    ButtonType buttonOK = new ButtonType("I will be good next time, I promise.");
                    alert.getButtonTypes().setAll(buttonOK);
                    alert.initStyle(StageStyle.UTILITY);
                    alert.showAndWait();
                }
            }
        });

        //button na volbu farby
        Button insertColor = new Button("Insert Color");
        insertColor.setLayoutX(982);
        insertColor.setLayoutY(130);
        //okno na vstup
        insertColor.setOnMouseClicked(event -> {
            TextInputDialog colourChoice = new TextInputDialog();
            colourChoice.setTitle("Color choice");
            colourChoice.setHeaderText("Pick new color:");
            colourChoice.setContentText("Input hexadecimal value, predefinied color name, " +
                    "or three to four digit int. ");
            colourChoice.initStyle(StageStyle.UTILITY);
            Optional<String> resultX = colourChoice.showAndWait();
            if (resultX.isPresent()) {
                String stringX = resultX.get();
                try {
                    //vyberie farbu a zrusi priesvitnost
                    Color newColor = Color.valueOf(stringX);
                    Color color = Color.color(newColor.getRed(), newColor.getGreen(), newColor.getBlue());
                    //zmeni ju vybranemu vrcholu
                    if (nodeCanvas.isSelected != null) {
                        nodeCanvas.isSelected.color = color;
                        nodeCanvas.selectNode(nodeCanvas.isSelected);
                    }
                    System.out.println("Selected color " + nodeCanvas.selectedColor);
                    nodeCanvas.selectedColor = color;
                    colorPicker.setValue(color);
                } catch (Exception e) {
                    //chybova hlaska nespravneho vstupu
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Alert Dialog");
                    alert.setHeaderText("Invalid input");
                    ButtonType buttonOK = new ButtonType("I will be good next time, I promise.");
                    alert.getButtonTypes().setAll(buttonOK);
                    alert.initStyle(StageStyle.UTILITY);
                    alert.showAndWait();
                }
            }
        });

        //color picker
        colorPicker.setOnAction(event -> {
            //zrusenie priesvitnosti
            Color newColor = colorPicker.getValue();
            Color color = Color.color(newColor.getRed(), newColor.getGreen(), newColor.getBlue());
            //prefarbenie vybraneho vrcholu
            if (nodeCanvas.isSelected != null) {
                nodeCanvas.isSelected.color = color;
                nodeCanvas.selectNode(nodeCanvas.isSelected);
            }
            System.out.println("Selected color " + nodeCanvas.selectedColor);
            nodeCanvas.selectedColor = color;
            colorPicker.setValue(color);
        });

        mainPane.getChildren().addAll(moveUp, moveDown, insertNode, deleteNode, insertColor, colorPicker,
                rotate, scale, move);


        //menu vlavo hore

        //polozka new
        final MenuBar menuBar = new MenuBar();
        Menu mainMenu = new Menu("File");
        menuBar.getMenus().add(mainMenu);
        MenuItem newMenu = new MenuItem("New");
        newMenu.setOnAction(event -> {
            //dialogove okno na zadanie poctu vrcholov
            TextInputDialog textinx = new TextInputDialog();
            textinx.setTitle("TextInput");
            textinx.setHeaderText("Set number of new nodes:");
            textinx.setContentText("Value between 0 and 12");
            Optional<String> resultX = textinx.showAndWait();
            if(resultX.isPresent()) {
                String stringX = resultX.get();
                try {
                    int x = Integer.parseInt(stringX);
                    if (x > 20 || x < 0) {
                        //chybova hlaska nespravneho vstupu
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Alert Dialog");
                        alert.setHeaderText("Invalid input");
                        ButtonType buttonOK = new ButtonType("I will be good next time, I promise.");
                        alert.getButtonTypes().setAll(buttonOK);
                        alert.initStyle(StageStyle.UTILITY);
                        alert.showAndWait();
                    } else {
                        //odznacovanie oznaceneho vrcholu
                        nodeCanvas.deselectNode(nodeCanvas.isSelected);
                        //vycistenie plochy, aby tam neostali povodne vrcholy a ciary
                        nodeCanvas.clearNodes();
                        nodeCanvas.clearCanvas();
                        items.clear();
//                            System.out.println("Stage cleared");
                        try {
                            //vygenerovanie vrcholov podla vstupu z dialogu
                            nodeCanvas.generate(x);
                            nodeCanvas.list.getSelectionModel().clearSelection();
                            nodeCanvas.deselectNode(nodeCanvas.isSelected);
                        } catch (Exception e) {
                            //chybova hlaska
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Alert Dialog");
                            alert.setHeaderText("Something went wrong.");
                            ButtonType buttonOK = new ButtonType("Whoops.");
                            alert.getButtonTypes().setAll(buttonOK);
                            alert.initStyle(StageStyle.UTILITY);
                            alert.showAndWait();
                        }
                    }
                } catch (Exception e) {
                    //chybova hlaska nespravneho vstupu
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Alert Dialog");
                    alert.setHeaderText("Invalid input");
                    ButtonType buttonOK = new ButtonType("I will be good next time, I promise.");
                    alert.getButtonTypes().setAll(buttonOK);
                    alert.initStyle(StageStyle.UTILITY);
                    alert.showAndWait();
                }
            }
        });
        //polozka open
        MenuItem openMenu = new MenuItem("Open");
        openMenu.setOnAction(event -> {
            try {
                //nacitanie vstupu zo suboru
                BufferedReader br = new BufferedReader(new FileReader("mnohouholniky.txt"));
                int numberOfNodes = Integer.parseInt(br.readLine());
                try {
                    //vycistenie plochy s vrcholmi aj listu
                    nodeCanvas.clearNodes();
                    nodeCanvas.clearCanvas();
                    items.clear();
//                        primaryStage.show();
                    for (int i = 0; i < numberOfNodes; i++) {
                        String[] input = br.readLine().split(" ");
                        double x = Double.parseDouble(input[0]);
                        double y = Double.parseDouble(input[1]);
                        try {
                            Color newColor = Color.valueOf(input[2]);
                            Color color = Color.color(newColor.getRed(), newColor.getGreen(), newColor.getBlue());
                            System.out.println(color.toString());
                            nodeCanvas.addNode(x, y, color);
                        }
                        //ak farby nie su pritomne, budu vrcholy cierne
                        catch (Exception e){
                            System.out.println("Program was unable to load colors from file.");
                            nodeCanvas.addNode(x, y, Color.BLACK);
                        }
                    }
                    nodeCanvas.list.getSelectionModel().clearSelection();
                    nodeCanvas.deselectNode(nodeCanvas.isSelected);
                }
                catch (Exception e) {
                    //chybova hlaska zleho vstupneho suboru
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Alert Dialog");
                    alert.setHeaderText("Failed to load from file. Will try to display whatever can be displayed. ");
                    ButtonType buttonOK = new ButtonType("Tudududum... :(");
                    alert.getButtonTypes().setAll(buttonOK);
                    alert.initStyle(StageStyle.UTILITY);
                    alert.showAndWait();
                }
                nodeCanvas.drawLines();
            } catch (IOException e) {
                //chybova hlaska zleho vstupneho suboru
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Alert Dialog");
                alert.setHeaderText("Failed to load from file.");
                ButtonType buttonOK = new ButtonType("Bad file!");
                alert.getButtonTypes().setAll(buttonOK);
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
            }
        });

        //polozka save
        MenuItem saveMenu = new MenuItem("Save");
        saveMenu.setOnAction(event -> {
            PrintStream out = null;
            try {
                //presmeruje output do suboru
                out = new PrintStream(new FileOutputStream("mnohouholniky.txt"));
                out.println(nodeCanvas.nodesList.size());
                for (int i=0; i<nodeCanvas.nodesList.size(); i++){
                    out.println(nodeCanvas.nodesList.get(i).x + " " + nodeCanvas.nodesList.get(i).y + " " +
                            nodeCanvas.nodesList.get(i).color.toString());
                }
            } catch (FileNotFoundException e) {
                //chybova hlaska nespravneho ulozenia
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Alert Dialog");
                alert.setHeaderText("Failed to save to file.");
                ButtonType buttonOK = new ButtonType("How could you?");
                alert.getButtonTypes().setAll(buttonOK);
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
            }
        });
        //polozka exit
        MenuItem exitMenu = new MenuItem("Exit");
        exitMenu.setOnAction(event -> {
            //spyta sa na ulozenie pred zavretim
            Alert exit = new Alert(Alert.AlertType.CONFIRMATION);
            exit.setTitle("Exit dialoge");
            exit.setHeaderText("You are exiting the program.");
            exit.setContentText("Do you wish to save before exiting?");
            ButtonType buttonTypeSave = new ButtonType("Save");
            ButtonType buttonTypeExit = new ButtonType("Discard");
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            exit.getButtonTypes().setAll(buttonTypeSave, buttonTypeExit, buttonTypeCancel);
            exit.initStyle(StageStyle.UTILITY);
            Optional<ButtonType> result = exit.showAndWait();
            if (result.get() == buttonTypeSave) {
                PrintStream out = null;
                try {
                    //presmeruje output do suboru
                    out = new PrintStream(new FileOutputStream("mnohouholniky.txt"));
                    out.println(nodeCanvas.nodesList.size());
                    for (int i = 0; i < nodeCanvas.nodesList.size(); i++) {
                        out.println(nodeCanvas.nodesList.get(i).x + " " + nodeCanvas.nodesList.get(i).y + " " +
                                nodeCanvas.nodesList.get(i).color.toString());
                    }
                    //informacia o ulozeni suboru
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Alert Dialog");
                    alert.setHeaderText("File saved.");
                    ButtonType buttonOK = new ButtonType("OK");
                    alert.getButtonTypes().setAll(buttonOK);
                    alert.initStyle(StageStyle.UTILITY);
                    alert.showAndWait();
                    primaryStage.close();
                } catch (FileNotFoundException e) {
                    //chybova hlaska nespravneho ulozenia
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Alert Dialog");
                    alert.setHeaderText("Failed to save to file.");
                    ButtonType buttonOK = new ButtonType("Whoops?");
                    alert.getButtonTypes().setAll(buttonOK);
                    alert.initStyle(StageStyle.UTILITY);
                    alert.showAndWait();
                }
            } else {
                //zatvori stage
                if (result.get() == buttonTypeExit) {
                    primaryStage.close();
                }
            }
        });

        //stlacenie urcitych klaves
        mainPane.setOnKeyReleased(event -> {
//                System.out.println("Someone pressed a key");
            //vymaze vybrany vrchol
            if (event.getCode() == (KeyCode.DELETE)) {
                nodeCanvas.remove();
            }
            //ofarbi vybrany vrchol vybranou farbou
            if (event.getCode() == (KeyCode.F)) {
                if(nodeCanvas.isSelected!=null) {
                    nodeCanvas.isSelected.color = nodeCanvas.selectedColor;
                    nodeCanvas.selectNode(nodeCanvas.isSelected);
                }
            }
            //zatvori stage
            if(event.getCode() == (KeyCode.ESCAPE)) {
                //spyta sa na ulozenie pred zavretim
                Alert exit = new Alert(Alert.AlertType.CONFIRMATION);
                exit.setTitle("Exit dialoge");
                exit.setHeaderText("You are exiting the program.");
                exit.setContentText("Do you wish to save before exiting?");
                ButtonType buttonTypeSave = new ButtonType("Save");
                ButtonType buttonTypeExit = new ButtonType("Discard");
                ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                exit.getButtonTypes().setAll(buttonTypeSave, buttonTypeExit, buttonTypeCancel);
                exit.initStyle(StageStyle.UTILITY);
                Optional<ButtonType> result = exit.showAndWait();
                if (result.get() == buttonTypeSave) {
                    PrintStream out = null;
                    try {
                        //presmeruje output do suboru
                        out = new PrintStream(new FileOutputStream("mnohouholniky.txt"));
                        out.println(nodeCanvas.nodesList.size());
                        for (int i = 0; i < nodeCanvas.nodesList.size(); i++) {
                            out.println(nodeCanvas.nodesList.get(i).x + " " + nodeCanvas.nodesList.get(i).y + " " +
                                    nodeCanvas.nodesList.get(i).color.toString());
                        }
                        //informacia o ulozeni suboru
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Alert Dialog");
                        alert.setHeaderText("File saved.");
                        ButtonType buttonOK = new ButtonType("OK");
                        alert.getButtonTypes().setAll(buttonOK);
                        alert.initStyle(StageStyle.UTILITY);
                        alert.showAndWait();
                        primaryStage.close();
                    } catch (FileNotFoundException e) {
                        //chybova hlaska nespravneho ulozenia
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Alert Dialog");
                        alert.setHeaderText("Failed to save to file.");
                        ButtonType buttonOK = new ButtonType("Whoops?");
                        alert.getButtonTypes().setAll(buttonOK);
                        alert.initStyle(StageStyle.UTILITY);
                        alert.showAndWait();
                    }
                } else {
                    //zatvori stage
                    if (result.get() == buttonTypeExit) {
                        primaryStage.close();
                    }
                }
            }
        });

        mainMenu.getItems().addAll(newMenu, openMenu, saveMenu, exitMenu);
        mainPane.getChildren().add(menuBar);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
