package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {

    private IModel model;

    private int characterPositionRowIndex = 0;
    private int characterPositionColumnIndex = 0;
    private String dir = "down";

    public StringProperty characterPositionRow = new SimpleStringProperty("1"); //For Binding
    public StringProperty characterPositionColumn = new SimpleStringProperty("1"); //For Binding

    public StringProperty dirPosition = new SimpleStringProperty("down");
    public MyViewModel(IModel model) {
        this.model = model;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == model) {
            characterPositionRowIndex = model.getCharacterPositionRow();
            characterPositionRow.set(characterPositionRowIndex + "");
            characterPositionColumnIndex = model.getCharacterPositionColumn();
            characterPositionColumn.set(characterPositionColumnIndex + "");
            dir = model.getDir();
            dirPosition.set(dir + "");

            setChanged();
            notifyObservers();
        }
    }

    public int[][] generateMaze(int width, int height) {
        return model.generateMaze(width, height);
    }

    public Position getEndPosition() {
        return model.getEndPosition();

    }

    public void moveCharacter(KeyCode movement) {
        model.moveCharacter(movement);
    }

    public boolean gameFinish() {
        return model.gameFinish();
    }

    public int[][] getMaze() {
        return model.getMaze();
    }

    public Maze getOriginal() {
        return model.getOriginal();
    }

    public int getCharacterPositionRow() {
        return characterPositionRowIndex;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumnIndex;
    }

    public void getSolution(MyViewModel m, int charRow, int charCol, String x) {
        model.generateSolution(m, charRow, charCol, x);
    }

    public boolean isSolved() {
        return model.isSolved();
    }

    public void setCharacterPositionRow(int row) {
        characterPositionRowIndex = row;
        model.setCharacterPositionRow(row);
    }

    public void setCharacterPositionColumn(int col) {
        characterPositionColumnIndex = col;
        model.setCharacterPositionCol(col);
    }

    public void setMaze(int[][] maze) {
        model.setMaze(maze);
    }


    public void setGoalPosition(Position goalPosition) {
        model.setGoalPosition(goalPosition);
    }

    public int[][] getMazeSolutionArr() {
        return model.getMazeSolutionArr();
    }

    public void setMazeOriginal(Maze mazeOriginal) {
        model.setMazeOriginal(mazeOriginal);
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public void save(File file) {
        model.save(file);
    }

    public void load(File file) {
        model.load(file);
    }
}
