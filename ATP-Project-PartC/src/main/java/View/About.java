package View;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

public class About implements Initializable {
    public javafx.scene.control.Button exit;
    public javafx.scene.control.Label text;
    public javafx.scene.image.ImageView NearExit;

    public void close() {
        Platform.exit();
    }

    public void closew() {
        Stage s = (Stage) exit.getScene().getWindow();
        s.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        text.setWrapText(true);
        text.setText("Welcome to the UnderTale Maze !\n" +
                "Creators: Idan Hefetz, Noam Shani.\n" +
                "We are hoping you are ready to play our marvelous game :)\n" +
                "This is a fan made game based on the indie rpg game known as 'UnderTale' by Toby Fox.\n" +
                "You need to navigate Frisk to the CheckPoint.\n" +
                "Thank you for playing my game.");

//        Image SmallImageNearExit = null;
//        try {
//            SmallImageNearExit = new Image(new File("resources/images/exit.png").toURI().toURL().toExternalForm());
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        NearExit.setImage(SmallImageNearExit);

    }
}