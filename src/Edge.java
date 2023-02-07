import javafx.scene.Group;
import javafx.scene.shape.Line;

import java.io.Serializable;

public class Edge<T> extends Group implements Serializable {
    private T destination;
    private String name;
    private int weight;
    private Line line = new Line();

    public Edge(T destination, String name, int weight) {
        this.destination = destination;
        this.name = name;
        this.weight = weight;
    }

    public void graphicEdge(double x, double y){
        Noden node = (Noden)destination;
        line.setStartX(x);
        line.setStartY(y);
        line.setEndX(node.getX());
        line.setEndY(node.getY());
        line.setStyle("-fx-stroke-width: 2");
        line.setDisable(true);
        getChildren().add(line);
    }

    public T getNodeTo(){
        return destination;
    }

    public String getNameOfDestination(){
        Noden destNode = (Noden)destination;
        return destNode.getName();
    }

    public int getWeight(){
        return weight;
    }

    public String getName(){
        return name;
    }

    public void setWeight(int weight){
        if(weight < 0){
            throw new IllegalArgumentException("Weight cannot be negative!");
        }
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "to " + destination + " by " + name + " takes " + weight;
    }
}
