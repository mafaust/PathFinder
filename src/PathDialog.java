import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class PathDialog extends Alert {
    private TextArea textArea = new TextArea();

    public PathDialog(){
        super(AlertType.CONFIRMATION);
        setTitle("Connection");
        VBox box = new VBox();
        box.getChildren().add(textArea);

        getDialogPane().setContent(box);
    }

    public void setText(String text){
        textArea.setText(text);
    }


}
