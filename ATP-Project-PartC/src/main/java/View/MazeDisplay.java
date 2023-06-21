package View;

import algorithms.mazeGenerators.Position;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MazeDisplay extends Canvas {

    private int[][] maze;
    private int characterPositionRow;
    private int characterPositionColumn;
    private String dir;
    private Position endPosition;
    private int[][] solved;
    private boolean isSolved;

    public void setMaze(int[][] maze) {
        this.maze = maze;
    }

    public void endposition(algorithms.mazeGenerators.Position end) {
        endPosition = end;
    }

    public void Solved(int[][] answer) {
        solved = answer;
    }


    public void setCharacterPosition(int row, int column) {
        characterPositionRow = row;
        characterPositionColumn = column;
    }
    public void setDir(String dir) {
        this.dir = dir;
    }

    public void redraw() {
        if (maze != null) {
//            if (this.getScene()!=null) {
//                this.setWidth(this.getScene().getHeight()-136);
//                this.setHeight(this.getScene().getWidth());
//            }
            double canvasHeight = getWidth();
            double canvasWidth = getHeight();
            double cellHeight = canvasHeight / maze[0].length;
            double cellWidth = canvasWidth / maze.length;

            try {
                GraphicsContext graphicsContext2D = getGraphicsContext2D();
                graphicsContext2D.clearRect(0, 0, getWidth(), getHeight()); //Clears the canvas
                Image wallImage = new Image(new FileInputStream("resources/images/red_leaves.jpg"));

                //Draw Maze
                for (int i = 0; i < maze.length; i++) {
                    for (int j = 0; j < maze[i].length; j++) {
                        if (maze[i][j] == 1) {
                            graphicsContext2D.drawImage(wallImage, j * cellHeight, i * cellWidth, cellHeight, cellWidth);
                        }
//                        graphicsContext2D.setFill(Color.WHITE);
//                        graphicsContext2D.fillRect(i,j,cellHeight,cellWidth);
                    }
                }

                //draw end point
                Image endPos = new Image(new FileInputStream("resources/images/check_point.png"));
                graphicsContext2D.drawImage(endPos, endPosition.getColumnIndex() * cellHeight, endPosition.getRowIndex() * cellWidth, cellHeight, cellWidth);

                //Draw solution
                if (isSolved) {
                    //Image SolutionImage = new Image(new FileInputStream("resources/images/eat.png"));
                    Image SolutionImage = new Image(new FileInputStream("resources/images/yellow_flowers_cut.png"));
                    for (int i = 0; i < solved[0].length - 1; i++) {
                        int x = solved[0][i];
                        int y = solved[1][i];
                        graphicsContext2D.drawImage(SolutionImage, y * cellHeight, x * cellWidth, cellHeight, cellWidth);
                    }
                }




                //gc.setFill(Color.RED);
                //gc.fillOval(characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);
                //draw start point
                Image StartPoint = new Image(new FileInputStream("resources/images/house.png"));
                graphicsContext2D.drawImage(StartPoint, 0, 0, cellHeight, cellWidth);
                //Draw Character
                Image characterImage;
                if (dir == null) {
                    characterImage = new Image(new FileInputStream("resources/images/frisk_down_cut.png"));
                }
                else if (dir.equals("up"))
                    characterImage = new Image(new FileInputStream("resources/images/frisk_up_cut.png"));
                else if (dir.equals("left")){
                    characterImage = new Image(new FileInputStream("resources/images/frisk_left_cut.png"));
                }
                else if (dir.equals("right")){
                    characterImage = new Image(new FileInputStream("resources/images/frisk_right_cut.png"));
                }
                else{
                    characterImage = new Image(new FileInputStream("resources/images/frisk_down_cut.png"));
                }
                graphicsContext2D.drawImage(characterImage, characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);
            } catch (FileNotFoundException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(String.format("Image doesn't exist: %s", e.getMessage()));
                alert.show();
            }
        }
    }

    //region Properties
    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();

    public String getImageFileNameWall() {
        return ImageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.ImageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }

    public void setImageFileNameCharacter(String imageFileNameCharacter) {
        this.ImageFileNameCharacter.set(imageFileNameCharacter);
    }

    public void isSolved(boolean solved) {
        this.isSolved = solved;
    }

    public void setGoalPosition(Position goalPosition) {
        this.endPosition = goalPosition;
    }
    //endregion

}
