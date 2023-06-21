package View;

import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import java.io.*;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

public class MyViewController implements Observer, IView {

    @FXML
    private MyViewModel viewModel = new MyViewModel(new MyModel());
    public MazeDisplay mazeDisplayer = new MazeDisplay();
    int i = 0;
    boolean showOnce = false;
    boolean songonce = true;
    Thread t1;
    boolean hint = false;
    public javafx.scene.control.TextField txt_row;
    public javafx.scene.control.TextField txt_col;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;//where user wants to go
    public javafx.scene.control.Button GenerateMaze;
    public javafx.scene.control.Button SolveMaze;
    private MediaPlayer backgroundMP;
    private MediaPlayer endingMediaPlayer;
    private boolean ctrlKeyPressed = false;

    public StringProperty characterPositionRow = new SimpleStringProperty();
    public StringProperty characterPositionColumn = new SimpleStringProperty();

    public StringProperty dir = new SimpleStringProperty();

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        bindProperties(viewModel);
    }


    private void bindProperties(MyViewModel viewModel) {
        lbl_rowsNum.textProperty().bind(viewModel.characterPositionRow);
        lbl_columnsNum.textProperty().bind(viewModel.characterPositionColumn);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == viewModel) {
            mazeDisplayer.setMaze(viewModel.getMaze());
            mazeDisplayer.setCharacterPosition(viewModel.getCharacterPositionRow(), viewModel.getCharacterPositionColumn());
            mazeDisplayer.setGoalPosition(viewModel.getEndPosition());
            displayMaze(viewModel.getMaze());
            GenerateMaze.setDisable(false);
            mazeDisplayer.setDir(viewModel.getDir());

            if (viewModel.gameFinish() && !showOnce) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Game Done");

                alert.show();
                playEndingMusic();  // Play the ending music
                showOnce = true;
            }
            mazeDisplayer.redraw();
        }
    }

    @Override
    public void displayMaze(int[][] maze) {
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        mazeDisplayer.endposition(viewModel.getEndPosition());
        mazeDisplayer.Solved(viewModel.getMazeSolutionArr());
        mazeDisplayer.isSolved(viewModel.isSolved());
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
        if (viewModel.isSolved())
            mazeDisplayer.redraw();
    }

    public void generateMaze() {
        showOnce = false;

        // Stop the ending music if it is playing
        if (endingMediaPlayer != null) {
            endingMediaPlayer.stop();
        }

        // Stop the background music if it is playing
        if (backgroundMP != null) {
            backgroundMP.stop();
        }

        int height;
        int width;

        try {
            height = Integer.valueOf(txt_row.getText());
        } catch (Exception e) {
            height = 10;
        }

        try {
            width = Integer.valueOf(txt_col.getText());
        } catch (Exception e) {
            width = 10;
        }

        int[][] temp = viewModel.generateMaze(height, width);
        mazeDisplayer.setMaze(temp);
        mazeDisplayer.endposition(viewModel.getEndPosition());
        SolveMaze.setVisible(true);
        displayMaze(temp);

        // Start playing the background music from the beginning
        playBackgroundMusic();
    }


    public void solveMaze(ActionEvent actionEvent) {
        showAlert("Solving maze..");
        viewModel.getSolution(this.viewModel, this.viewModel.getCharacterPositionRow(), this.viewModel.getCharacterPositionColumn(), "solve");
//TODO        SolveMaze.setVisible(false);
    }


    public void exit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirm Exit");
        alert.setContentText("Are you sure you want to exit? Don't forget to save the game!");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        }
    }


    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    public void KeyPressed(KeyEvent keyEvent) {
        viewModel.moveCharacter(keyEvent.getCode());
        keyEvent.consume();
    }

    //region String Property for Binding

    public String getCharacterPositionRow() {
        return characterPositionRow.get();
    }

    public StringProperty characterPositionRowProperty() {
        return characterPositionRow;
    }

    public String getCharacterPositionColumn() {
        return characterPositionColumn.get();
    }

    public StringProperty characterPositionColumnProperty() {
        return characterPositionColumn;
    }

    public void setResizeEvent(Scene scene) {
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                mazeDisplayer.redraw();
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                mazeDisplayer.redraw();
            }
        });
    }

    public void MazeInfo() {
        String text = null;
        OutputStream output = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("./resources/config.properties"));
            if (bufferedReader == null) {//check if file exthist
                output = new FileOutputStream("Resources/config.properties");
            }
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line + ",");
                stringBuilder.append(System.lineSeparator());
                line = bufferedReader.readLine();
            }
            text= stringBuilder.toString();
            bufferedReader.close();
        } catch (IOException e) { }
        if(text==null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Setting Found");
            alert.setContentText("Please go to option and set what settings you want");
            alert.show();
        }
        else {
            String[] split = text.split(",");
            String content = "";
            content = "Maze Algo: " + splitLine(split[0] + "\n");
            content += "Thread Number: " + splitLine(split[1].substring(4) + "\n");
            content += "Maze Type: " + splitLine(split[2].substring(4) + "\n");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Properties");
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.show();
        }
    }

    private String splitLine(String s) {
        String [] splitedLine = s.split("=");
        return splitedLine[1];
    }

    public void About(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("About");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
            Scene scene = new Scene(root, 450, 245);
            String textCSS = this.getClass().getResource("menu_text.css").toExternalForm();
            scene.getStylesheets().add(textCSS);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
//            System.out.println(e);
            System.out.println("Error About.fxml not found");
        }
    }

    public void Help(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Help");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Help.fxml").openStream());
            Scene scene = new Scene(root);
            String textCSS = this.getClass().getResource("menu_text.css").toExternalForm();
            scene.getStylesheets().add(textCSS);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
            System.out.println("Error Help.fxml not found");
        }
    }

    @FXML
    public void playBackgroundMusic()
    {
        String musicFile = "resources/music/ruins_song.mp3";
        Media sound = new Media(new File(musicFile).toURI().toString());
        backgroundMP = new MediaPlayer(sound);
        backgroundMP.play();
    }

    public void playEndingMusic() {
        backgroundMP.stop();
        String musicFile = "resources/music/ending_music.mp3";
        Media sound = new Media(new File(musicFile).toURI().toString());
        endingMediaPlayer = new MediaPlayer(sound);
        endingMediaPlayer.play();
    }


    public void Option(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Option");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Option.fxml").openStream());
            Scene scene = new Scene(root);
            scene.getStylesheets().add("box.css");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
            System.out.println("Error Option.fxml not found");
        }
    }

    public void restartGame() {
        if (endingMediaPlayer != null) {
            endingMediaPlayer.stop();
        }
        if (backgroundMP != null) {
            backgroundMP.stop();
        }

        Platform.runLater(() -> {
            Stage primaryStage = (Stage) mazeDisplayer.getScene().getWindow();
            primaryStage.close();

            try {
                new Main().start(new Stage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void saveGame() {
        FileChooser fc = new FileChooser();
        File filePath = new File("./Mazes/");
        if (!filePath.exists())
            filePath.mkdir();
        fc.setTitle("Saving maze");
        fc.setInitialFileName("Maze Number " + i + "");
        i++;
        fc.setInitialDirectory(filePath);
        File file = fc.showSaveDialog((Stage) mazeDisplayer.getScene().getWindow());
        if (file != null)
            viewModel.save(file);
    }

    public void loadGame() {

        FileChooser fc = new FileChooser();
        fc.setTitle("Loading maze");
        File filePath = new File("./Mazes/");
        if (!filePath.exists())
            filePath.mkdir();
        fc.setInitialDirectory(filePath);

        File file = fc.showOpenDialog(new PopupWindow() {
        });
        if (file != null && file.exists() && !file.isDirectory()) {
            viewModel.load(file);
            if (songonce==true)
                mazeDisplayer.redraw();
        }
    }

    public void zoom(ScrollEvent event) {
        if (ctrlKeyPressed) {
            double zoomFactor = 1.1;
            double deltaY = event.getDeltaY();

            if (deltaY < 0) {
                // Zoom out
                zoomOut(zoomFactor);
            } else {
                // Zoom in
                zoomIn(zoomFactor);
            }

            event.consume();
        }
    }

    private void zoomIn(double zoomFactor) {
        mazeDisplayer.setScaleX(mazeDisplayer.getScaleX() * zoomFactor);
        mazeDisplayer.setScaleY(mazeDisplayer.getScaleY() * zoomFactor);
    }

    private void zoomOut(double zoomFactor) {
        mazeDisplayer.setScaleX(mazeDisplayer.getScaleX() / zoomFactor);
        mazeDisplayer.setScaleY(mazeDisplayer.getScaleY() / zoomFactor);
    }


    public void onKeyPressed(KeyEvent event) {
        if (event.isControlDown()) {
            ctrlKeyPressed = true;
        }
    }

    public void onKeyReleased(KeyEvent event) {
        if (!event.isControlDown()) {
            ctrlKeyPressed = false;
        }
    }


}
