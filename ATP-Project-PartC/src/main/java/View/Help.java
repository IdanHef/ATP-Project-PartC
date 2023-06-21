package View;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Help implements Initializable {
    public javafx.scene.control.Button exit2;
    public javafx.scene.control.Label text;

    public void close() {
        Platform.exit();
    }

    public void closew() {
        Stage s = (Stage) exit2.getScene().getWindow();
        s.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}