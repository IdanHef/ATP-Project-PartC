package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import View.Main;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.InetAddress;
import java.net.UnknownHostException;

/*
responsible for all the function part
 */

public class MyModel extends Observable implements IModel {
    private int[][] maze;
    private Maze Original;
    private boolean solved;
    private boolean gameFinish;
    private int characterPositionRow;
    private int characterPositionColumn;
    private Position endPosition;
    private int[][] mazeSolutionArr;
    private Server serverMazeGenerator;
    private Server serverSolveMaze;
    private String dir;
    private ExecutorService threadPool = Executors.newCachedThreadPool();

    private static final Logger logger = Logger.getLogger(MyModel.class);
    private static int portNumGenerate = 5402;
    private static int portNumSolve = 6400;

    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    public void setCharacterPositionRow(int row) {
        this.characterPositionRow = row;
    }


    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public Position getEndPosition() {
        return endPosition;
    }

    @Override
    public boolean gameFinish() {
        return gameFinish;
    }

    private void MazeToArr(Maze m) {
        int row = m.getMyMaze().length;
        int col = m.getMyMaze()[0].length;
        maze = new int[row][col];
        for (int i = 0; i < row; i++)
            for (int j = 0; j < col; j++)
                maze[i][j] = m.getBlockValue(i, j);
    }

    @Override
    public int[][] generateMaze(int width, int height) {
        portNumGenerate++;
        serverMazeGenerator = new Server(portNumGenerate, 1000, new ServerStrategyGenerateMaze());
        serverMazeGenerator.start();
        try {
            Client client = new Client(InetAddress.getLocalHost(), portNumGenerate, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        solved = false;
                        gameFinish = false;
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{width, height};
                        toServer.writeObject(mazeDimensions); //send mazedimensions to server
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed withMyCompressor)from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[mazeDimensions[0] * mazeDimensions[1] + 8 /*CHANGESIZE ACCORDING TO YOU MAZE SIZE*/]; //allocating byte[] for the decompressedmaze -
                        is.read(decompressedMaze); //Fill decompressedMazewith bytes
                        Maze maze = new Maze(decompressedMaze);
                        Position UpdatePos = new Position(1, 1);
                        UpdatePos = maze.getStartPosition();
                        Original = maze;
                        characterPositionColumn = UpdatePos.getColumnIndex();
                        characterPositionRow = UpdatePos.getRowIndex();
                        endPosition = maze.getGoalPosition();
                        MazeToArr(maze);

                        //write here
                            try {
                            InetAddress localhost = InetAddress.getLocalHost();
                            String computerName = localhost.getHostName();
                            logger.info("Computer Name: " + computerName);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                        logger.info("Maze Generator Properties : ");
                        logger.info("Number of Rows : " + maze.getMyMaze().length);
                        logger.info("Number of Columns : " + maze.getMyMaze()[0].length);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        serverMazeGenerator.stop();
        setChanged();
        notifyObservers();
        return maze;
    }

    private boolean isNotLegalMove(int x, int y) {
        if (x < 0 || y < 0 || x > maze.length - 1 || y > maze[0].length - 1)
            return true;
        return maze[x][y] == 1;
    }

    @Override
    public void moveCharacter(KeyCode movement) {
        int x = characterPositionRow;
        int y = characterPositionColumn;
        switch (movement) {
            case NUMPAD8:
            case W:
                if (!isNotLegalMove(x - 1, y)){
                    characterPositionRow--;
                    dir = "up";
                }
                break;
            case NUMPAD2:
            case S:
                if (!isNotLegalMove(x + 1, y)){
                    characterPositionRow++;
                    dir = "down";
                }
                break;
            case NUMPAD6:
            case D:
                if (!isNotLegalMove(x, y + 1)){
                    characterPositionColumn++;
                    dir = "right";
                }
                break;
            case A:
            case NUMPAD4:
                if (!isNotLegalMove(x, y - 1)){
                    characterPositionColumn--;
                    dir = "left";
                }
                break;
            case NUMPAD3:
                if (!isNotLegalMove(x + 1, y + 1))
                    if (!isNotLegalMove(x, y + 1) || !isNotLegalMove(x + 1, y)) {
                        characterPositionColumn++;
                        characterPositionRow++;
                    }
                break;
            case NUMPAD1:
                if (!isNotLegalMove(x + 1, y - 1))
                    if (!isNotLegalMove(x, y - 1) || !isNotLegalMove(x + 1, y)) {
                        characterPositionColumn--;
                        characterPositionRow++;
                    }
                break;
            case NUMPAD9:
                if (!isNotLegalMove(x - 1, y + 1))
                    if (!isNotLegalMove(x - 1, y) || !isNotLegalMove(x, y + 1)) {
                        characterPositionColumn++;
                        characterPositionRow--;
                    }
                break;
            case NUMPAD7:
                if (!isNotLegalMove(x - 1, y - 1))
                    if (!isNotLegalMove(x, y - 1) || !isNotLegalMove(x - 1, y)) {
                        characterPositionColumn--;
                        characterPositionRow--;
                    }
                break;
        }
        if (endPosition.getColumnIndex() == getCharacterPositionColumn() && endPosition.getRowIndex() == getCharacterPositionRow())
            gameFinish = true;
        setChanged();
        notifyObservers();

    }

    @Override
    public int[][] getMaze() {
        return maze;
    }


    @Override
    public void setGoalPosition(Position goalPosition) {
        this.endPosition = goalPosition;
    }

    public Maze getOriginal() {
        return Original;
    }

    @Override
    public void setMaze(int[][] maze) {
        this.maze = maze;
    }

    public void setMazeOriginal(Maze m){
        this.Original=m;
        MazeToArr(m);
    }

    public boolean isSolved() {
        return this.solved;
    }

    @Override
    public void generateSolution(MyViewModel m, int charRow, int charCol, String x) {
        portNumSolve++;
        serverSolveMaze = new Server(portNumSolve, 1000, new ServerStrategySolveSearchProblem());
        serverSolveMaze.start();
        try {
            Client client = new Client(InetAddress.getLocalHost(), portNumSolve, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        Maze maze = Original;
                        maze.setStartPos(new Position(0,0));
                        toServer.writeObject(maze); //send maze to server
                        toServer.flush();
                        Solution mazeSolution = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor)from server
                        //Print Maze Solution retrieved from the server
                        //TODO if (x == "solve") ? I USE THIS FUNCTION FOR HINTS AND SOLVE
                        solved = true;
                        ArrayList<AState> mazeSolutionSteps = mazeSolution.getSolutionPath();
                        int sizeOfSolution = mazeSolutionSteps.size();
                        mazeSolutionArr = new int[2][sizeOfSolution];
                        if(x == "solve") {
                            for (int i = 0; i < mazeSolutionSteps.size(); i++) {
                                mazeSolutionArr[0][i] = ((MazeState) (mazeSolutionSteps.get(i))).getRow();
                                mazeSolutionArr[1][i] = ((MazeState) (mazeSolutionSteps.get(i))).getCol();
                            }
                        }
                        String mazeProperties = "Maze Solver Properties : \n" +
                                "\t threadPoolSize=2\n" +
                                "\t mazeSearchingAlgorithm=BreadthFirstSearch\n" +
                                "\t mazeGeneratingAlgorithm=MyMazeGenerator";

                        logger.info(mazeProperties);
                        setChanged();
                        notifyObservers();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        serverSolveMaze.stop();
    }

    public int[][] getMazeSolutionArr() {
        return mazeSolutionArr;
    }

    public void setCharacterPositionCol(int col) {
        this.characterPositionColumn = col;
    }

    public void save(File file)
    {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream os = new ObjectOutputStream(fileOutputStream);
            //TODO CHANGED 2 lines
            solved = false;
            Original.setStartPos(new Position(characterPositionRow,characterPositionColumn));
            //myMaze.setM_startPosition(new Position(characterRow, characterColumn));
            os.writeObject(Original);
            os.flush();
            os.close();
        } catch (IOException ignored) {

        }
    }

    public void load(File file)
    {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream os = new ObjectInputStream(fileInputStream);
            Maze temp = (Maze)os.readObject();
            setMazeOriginal(temp);
            //TODO CHANGED 3 LINES
            setGoalPosition(temp.getGoalPosition());
            setCharacterPositionRow(temp.getStartPosition().getRowIndex());
            setCharacterPositionCol(temp.getStartPosition().getColumnIndex());
            solved=false;
            os.close();
            setChanged();
            notifyObservers();
        } catch (IOException ignored) {


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

//    public static void printMazeSolutionSteps(ArrayList<AState> solutionSteps) {
//        for (int i = 0; i < solutionSteps.size(); i++) {
//            AState state = solutionSteps.get(i);
//            System.out.println("Step " + i + ": " + state.toString());
//        }
//    }


}
