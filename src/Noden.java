import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class Noden extends Circle {
    private final String name;
    private final double x;
    private final double y;
    private Label label;
    private boolean unmarked = true;

    public Noden(String name, double x, double y) {
        super(x, y, 8);
        this.name = name;
        this.x = x;
        this.y = y;
        setFill(Color.BLUE);
        setStroke(Color.BLUE);
        setId(name);
    }


    public void setUnmarked(boolean on){
        unmarked = on;
        if(unmarked){
            paintUnmarked();
        }else{
            paintMarked();
        }
    }

    public void paintMarked(){
        setFill(Color.RED);
        setStroke(Color.RED);
    }

    public void paintUnmarked(){
        setFill(Color.BLUE);
        setStroke(Color.BLUE);
    }

    public String getName() {
        return name;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    @Override
    public String toString() {
        return name;
    }
}
