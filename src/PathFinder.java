import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class PathFinder  extends Application {
    private ImageView imageView = new ImageView();
    private Stage stage;
    private BorderPane root;
    private Pane center;
    private ListGraph graph = new ListGraph();
    private Button findButton;
    private Button showConnButton;
    private Button newPlaceButton;
    private Button newConnButton;
    private Button changeConnButton;
    private boolean changed;
    private Noden n1;
    private Noden n2;
    private Collection<Noden> nodes = graph.getNodes();
    private ClickHandler cl = new ClickHandler();

    @Override
    public void start(Stage stage){
        this.stage = stage;
        root = new BorderPane();
        n1 = null;
        n2 = null;
        changed = false;

        // MENU
        BorderPane upper = new BorderPane();
        MenuBar menuBar = new MenuBar();
        menuBar.setId("menu");
        upper.setTop(menuBar);

        Menu fileMenu = new Menu("File");
        fileMenu.setId("menuFile");
        menuBar.getMenus().add(fileMenu);
        MenuItem newMapItem = new MenuItem("New Map");
        newMapItem.setId("menuNewMap");
        newMapItem.setOnAction(new NewMapHandler());
        MenuItem openItem = new MenuItem("Open");
        openItem.setId("menuOpenFile");
        openItem.setOnAction(new OpenHandler());
        MenuItem saveItem = new MenuItem("Save");
        saveItem.setId("menuSaveFile");
        saveItem.setOnAction(new SaveHandler());
        MenuItem saveImageItem = new MenuItem("Save Image");
        saveImageItem.setId("menuSaveImage");
        saveImageItem.setOnAction(new SaveImageHandler());
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setId("menuExit");
        exitItem.setOnAction(new ExitItemHandler());
        fileMenu.getItems().addAll(newMapItem, openItem, saveItem,
                saveImageItem, exitItem);

        //PANE WITH BUTTONS
        FlowPane buttonPane = new FlowPane();
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.setPrefSize(600,50);
        buttonPane.setPadding(new Insets(5));
        buttonPane.setHgap(10);
        upper.setCenter(buttonPane);


        findButton = new Button("Find Path");
        findButton.setId("btnFindPath");
        findButton.setOnAction(new FindHandler());
        showConnButton = new Button("Show Connection");
        showConnButton.setId("btnShowConnection");
        showConnButton.setOnAction(new ShowConnHandler());
        newPlaceButton = new Button("New Place");
        newPlaceButton.setId("btnNewPlace");
        newPlaceButton.setOnAction(new NewPlaceHandler());
        newConnButton = new Button("New Connection");
        newConnButton.setId("btnNewConnection");
        newConnButton.setOnAction(new NewConnHandler());
        changeConnButton = new Button("Change Connection");
        changeConnButton.setId("btnChangeConnection");
        changeConnButton.setOnAction(new ChangeConnHandler());
        buttonPane.getChildren().addAll(findButton, showConnButton, newPlaceButton, newConnButton, changeConnButton);
        root.setTop(upper);


        center = new Pane();
        center.setId("outputArea");
        center.getChildren().add(imageView);
        root.setCenter(center);


        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("PathFinder");
        stage.setOnCloseRequest(new ExitHandler());
        stage.show();

    }

    class NewMapHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event){
            if(!nodes.isEmpty()){
                ObservableList<Node> obsNodes = center.getChildren();
                center.getChildren().removeAll(obsNodes);
                center.getChildren().add(imageView);
                graph = new ListGraph();
                nodes = graph.getNodes();
            }
            n1 = null;
            n2 = null;

            Image image = new Image("file:europa.gif");
            imageView.setImage(image);
            stage.sizeToScene();
            changed = true;
        }
    }

    class OpenHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event){
            try{
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Unsaved changes, continue anyway?");
                alert.setTitle("Warning!");
                alert.setHeaderText(null);
                Optional<ButtonType> res = alert.showAndWait();
                if (res.isPresent() && res.get().equals(ButtonType.CANCEL)) {
                    event.consume();
                }

                //Reset center pane and database
                ObservableList<Node> obsNodes = center.getChildren();
                center.getChildren().removeAll(obsNodes);
                center.getChildren().add(imageView);
                graph = new ListGraph();
                nodes = graph.getNodes();
                n1 = null;
                n2 = null;
                Image image = new Image("file:europa.gif");
                imageView.setImage(image);
                stage.sizeToScene();


                FileReader fr = new FileReader("europa.graph");
                BufferedReader bf = new BufferedReader(fr);
                String line;
                bf.readLine();

                line = bf.readLine();
                String[] tokensNodes = line.split(";");
                int i = 0;
                while(i<tokensNodes.length){
                    String name = tokensNodes[i];
                    double x = Double.parseDouble(tokensNodes[i+1]);
                    double y = Double.parseDouble(tokensNodes[i+2]);
                    Noden node = new Noden(name, x, y);
                    Label label = new Label(name);
                    label.relocate(x + 4 , y + 4);
                    label.setDisable(true);
                    graph.add(node);
                    center.getChildren().add(node);
                    center.getChildren().add(label);
                    i += 3;
                }

                while((line = bf.readLine()) != null) {
                    String[] tokensEdges = line.split(";");
                    String from = tokensEdges[0];
                    String to = tokensEdges[1];
                    String name = tokensEdges[2];
                    int weight = Integer.parseInt(tokensEdges[3]);
                    Set<Noden> graphNodes = (Set)graph.getNodes();
                    for (Noden nodeFrom : graphNodes)
                        if (nodeFrom.getName().equals(from)) {
                            for (Noden nodeTo : graphNodes)
                                if (nodeTo.getName().equals(to)) {
                                    if (graph.getEdgeBetween(nodeFrom, nodeTo) == null) {
                                        graph.connect(nodeFrom, nodeTo, name, weight);
                                    }
                                    Edge edge = graph.getEdgeBetween(nodeFrom, nodeTo);
                                    edge.graphicEdge(nodeFrom.getX(), nodeFrom.getY());
                                    center.getChildren().add(edge);
                                }

                        }
                }

                //Update clickable nodes
                nodes = graph.getNodes();
                for(Noden n : nodes){
                    if(!center.getChildren().contains(n))
                        center.getChildren().add(n);
                        n.setOnMouseClicked(cl);
                }

                bf.close();
                fr.close();
            }catch(FileNotFoundException e){
                System.err.println("File is nonexistent!");
            }catch(IOException e){
                System.err.println("IO-error" + e.getMessage());
            }
        }
    }

    class SaveHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event){
            try{
                FileWriter fw = new FileWriter("europa.graph");
                PrintWriter pw = new PrintWriter(fw);
                Set<Noden> nodes = (Set)graph.getNodes();
                Set<Edge> edges = new HashSet<>();

                pw.println("file:europa.gif");
                for(Noden node : nodes){
                    pw.print(node.getName() + ";" + node.getX() + ";" + node.getY() + ";");
                }
                pw.println();
                for(Noden node : nodes){
                    edges = (Set)graph.getEdgesFrom(node);
                    for(Edge edge : edges)
                        pw.println(node.getName() + ";" + edge.getNameOfDestination()
                                + ";" + edge.getName() + ";" + edge.getWeight());
                }
                pw.close();
                fw.close();
            }catch(FileNotFoundException e){
                System.err.println("File not found!");
            }catch(IOException e){
                System.err.println("IO-error" + e.getMessage());
            }
        }
    }

    class SaveImageHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event){
            try{
                WritableImage image = center.snapshot(null, null);
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                ImageIO.write(bufferedImage, "png", new File("capture.png"));
            }catch(IOException e){
                Alert alert = new Alert(Alert.AlertType.ERROR, "IO error" + e.getMessage());
                alert.showAndWait();
            }

        }
    }

    class ExitHandler implements EventHandler<WindowEvent> {
        @Override
        public void handle(WindowEvent event) {
            if (changed) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Unsaved changes, exit anyway?");
                alert.setTitle("Warning!");
                alert.setHeaderText(null);
                Optional<ButtonType> res = alert.showAndWait();
                if (res.isPresent() && res.get().equals(ButtonType.CANCEL)) {
                    event.consume();
                }
            }
        }
    }

    class ExitItemHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        }
    }

    class FindHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event){
            if(!graph.pathExists(n1, n2)){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Path doesn't exist");
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.showAndWait();
                return;
            }

            PathDialog pd = new PathDialog();
            pd.setTitle("Find path");
            pd.setHeaderText("Connection from " + n1.getName() + " to " + n2.getName());
            List<Edge> path = graph.getPath(n1, n2);

            int totalWeight = 0;
            String finalMessage = "";
            for(Edge e : path) {
                totalWeight += e.getWeight();
                finalMessage += e + "\n";
            }
            finalMessage += "Total " + totalWeight;
            pd.setText(finalMessage);
            pd.showAndWait();
        }
    }

    class ShowConnHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event){
            if(n1 == null || n2 == null){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Two places must be selected");
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.showAndWait();
                return;
            }

            if(graph.getEdgeBetween(n1, n2) == null){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Connection doesn't exists");
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.showAndWait();
                return;
            }

            Edge edge = graph.getEdgeBetween(n1, n2);
            ConnectionAlert connAlert = new ConnectionAlert();
            connAlert.setHeaderText("Connection from "
                    + n1.getName() + " to " + n2.getName());
            connAlert.setName(edge.getName());
            connAlert.setTime(String.valueOf(edge.getWeight()));
            Optional<ButtonType> res = connAlert.showAndWait();
            if (res.isPresent() && res.get().equals(ButtonType.CANCEL)) {
                event.consume();
            }
        }
    }

    class NewPlaceHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event){
            center.setOnMouseClicked(new ClickHandlerNew());
            newPlaceButton.setDisable(true);
            center.setCursor(Cursor.CROSSHAIR);

        }
    }

    class NewConnHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event){
            if(n1 == null || n2 == null){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Two places must be selected");
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.showAndWait();
                return;
            }

            if(graph.getEdgeBetween(n1, n2) != null){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Connection already exists");
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.showAndWait();
                return;
            }

            try{
                ConnectionAlert connAlert = new ConnectionAlert();
                connAlert.setHeaderText("Connection from "
                        + n1.getName() + " to " + n2.getName());
                Optional<ButtonType> res = connAlert.showAndWait();
                if (res.isPresent() && res.get().equals(ButtonType.CANCEL)) {
                    event.consume();
                }
                if(connAlert.getName().trim().isEmpty()){
                    Alert a =new Alert(Alert.AlertType.ERROR, "Name cannot be empty");
                    a.setHeaderText(null);
                    a.setTitle("Input error");
                    a.showAndWait();
                    return;
                }
                graph.connect(n1, n2, connAlert.getName(), connAlert.getTime());
                Edge edge = graph.getEdgeBetween(n1, n2);
                edge.graphicEdge(n1.getX(), n1.getY());
                center.getChildren().add(edge);
                changed = true;
            }catch(NumberFormatException e){
                Alert a =new Alert(Alert.AlertType.ERROR, "Wrong input");
                a.setHeaderText(null);
                a.setTitle("Input error");
                a.showAndWait();
            }
        }
    }

    class ChangeConnHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event){
            if(n1 == null || n2 == null){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Two places must be selected");
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.showAndWait();
                return;
            }

            if(graph.getEdgeBetween(n1, n2) == null){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Connection doesn't exists");
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.showAndWait();
                return;
            }

            try{
                Edge edge = graph.getEdgeBetween(n1, n2);
                ConnectionAlert connAlert = new ConnectionAlert();
                connAlert.setHeaderText("Connection from "
                        + n1.getName() + " to " + n2.getName());
                connAlert.setName(edge.getName());
                Optional<ButtonType> res = connAlert.showAndWait();
                if (res.isPresent() && res.get().equals(ButtonType.CANCEL)) {
                    event.consume();
                }
                graph.setConnectionWeight(n1, n2, connAlert.getTime());
                changed = true;
            }catch(NumberFormatException e){
                Alert a =new Alert(Alert.AlertType.ERROR, "Wrong input");
                a.setHeaderText(null);
                a.setTitle("Input error");
                a.showAndWait();
            }
        }
    }

    class ClickHandlerNew implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent event){
            double x = event.getX();
            double y = event.getY();
            TextInputDialog textDialog = new TextInputDialog();
            textDialog.setTitle("Name");
            textDialog.setHeaderText(null);
            textDialog.setContentText("Name of place");
            textDialog.showAndWait();
            String name = textDialog.getResult();
            Noden node = new Noden(name, x, y);
            Label label = new Label(name);
            label.relocate(x + 4 , y + 4);
            label.setDisable(true);
            graph.add(node);
            center.getChildren().add(node);
            center.getChildren().add(label);
            center.setOnMouseClicked(null);
            newPlaceButton.setDisable(false);
            center.setCursor(Cursor.DEFAULT);
            changed = true;

            // Update clickable nodes
            for(Noden n : nodes){
                if(!center.getChildren().contains(n))
                    center.getChildren().add(n);
                n.setOnMouseClicked(cl);

            }
        }
    }


    class ClickHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            Noden n = (Noden)event.getSource();
            if(n == n1){
                n1.setUnmarked(true);
                n1 = null;
            }else if(n == n2){
                n2.setUnmarked(true);
                n2 = null;
            }else if(n1 == null && n2 != n){
                n1 = n;
                n1.setUnmarked(false);
            }else if(n2 == null && n1 != n){
                n2 = n;
                n2.setUnmarked(false);
            }
        }
    }
}
