package View;

import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Main extends Application {

    private MyViewController view;
    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        PropertyConfigurator.configure("log4j.properties");
        logger.info("Application started");
        //logger.error("An error occurred");


        MyModel model = new MyModel();
        MyViewModel viewModel = new MyViewModel(model);
        model.addObserver(viewModel);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        primaryStage.setTitle("Undertale Ruins Maze!");
        String buttonCSS = getClass().getResource("button.css").toExternalForm();
        scene.getStylesheets().add(buttonCSS);
        String textCSS = getClass().getResource("text.css").toExternalForm();
        scene.getStylesheets().add(textCSS);
        Font.loadFont(getClass().getResourceAsStream("/fonts/8-Bit Operator JVE.ttf"), 14);
        String textFieldCSS = getClass().getResource("text_field.css").toExternalForm();
        scene.getStylesheets().add(textFieldCSS);

        view = fxmlLoader.getController();
        view.setResizeEvent(scene);
        view.setViewModel(viewModel);
        viewModel.addObserver(view);
        primaryStage.setScene(scene);

        setStageCloseEvent(primaryStage);

        primaryStage.show();
    }

    private void setStageCloseEvent(Stage primaryStage) {
        primaryStage.setOnCloseRequest(windowEvent -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Don't forget to save the game.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Save the game here
                primaryStage.close();
            } else {
                windowEvent.consume();
            }
        });
    }

}
