package Model;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import javafx.scene.input.KeyCode;

import java.io.File;

public interface IModel {

    int[][] generateMaze(int width, int height);

    void moveCharacter(KeyCode movement);

    int[][] getMaze();

    void setGoalPosition(Position goalPosition);

    int[][] getMazeSolutionArr();

    void generateSolution(MyViewModel m, int charRow, int charCol, String x);

    boolean isSolved();

    int getCharacterPositionRow();

    int getCharacterPositionColumn();

    Position getEndPosition();

    boolean gameFinish();

    void setCharacterPositionCol(int col);

    void setCharacterPositionRow(int row);

    void save(File file);

    void load(File file);

    void setMazeOriginal(Maze m);

    void setMaze(int[][] maze);

    Maze getOriginal();
    String getDir();
    void setDir(String dir);

}
