import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ConnectionAlert extends Alert {
    private TextField nameField = new TextField();
    private TextField timeField = new TextField();

    public ConnectionAlert(){
        super(AlertType.CONFIRMATION);
        setTitle("Connection");
        GridPane grid = new GridPane();
        grid.addRow(0, new Label("Name:"), nameField);
        grid.addRow(1, new Label("Time:"), timeField);
        getDialogPane().setContent(grid);
    }

    public String getName(){
        return nameField.getText();
    }

    public void setName(String s){
        nameField.setText(s);
        nameField.setEditable(false);
    }

    public int getTime(){
        return Integer.parseInt(timeField.getText());
    }

    public void setTime(String s){
        timeField.setText(s);
        timeField.setEditable(false);
    }
}
