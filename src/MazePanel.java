/*
 * File:     MazePanel.java
 * Author:   Taylor Ecton
 * Purpose:  Panel containing the actual maze and functions for generating and solving the maze.
 *           Also contains a label for information about the amount of the maze generated/visited.
 */

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class MazePanel extends JPanel {
    // the maximum number of rows and columns allowable
    private final int MAX_ROWS = 50;
    private final int MAX_COLS = 50;

    // number of rows and columns for the current maze
    private int rows, columns;

    // the width and height for the panel
    private int mazeWidth, mazeHeight;

    // label with information about percent generated/visited
    private JLabel percentCompleteLabel;

    // values used for calculating percent generated/visited
    private double totalCells, cellsVisited, percentGenerated, percentVisited;

    // 2D array of maze cells representing the maze
    private MazeCell[][] maze;

    // Random number generator for random values needed
    private Random RNG = new Random(System.currentTimeMillis());

    // list of visited cells used for tidying up path on display
    private LinkedList<MazeCell> visitedCells;

    // queue is used for DFS in both generation and solving
    private LinkedList<MazeCell> queue = new LinkedList<>();

    // 2D boolean array representing whether MazeCells with corresponding indices
    // have been visited
    private boolean[][] visited;

    // the current MazeCell in the generation or solution process
    private MazeCell curr;

    /**
     * Constructor for MazePanel class.
     */
    public MazePanel() {
        // Set the size and background for the panel
        this.setPreferredSize(new Dimension(775, 900));
        this.setBackground(Color.LIGHT_GRAY);
        this.setOpaque(true);

        // set initial rows and columns values to MAX_ROWS and MAX_COLS
        rows = MAX_ROWS;
        columns = MAX_COLS;

        // sets the width and height for the panel
        setMazeWidthAndHeight();

        // initializes all the maze cells and adds them to the panel
        initializeMaze();

        // initializes the label that shows how much of the maze is generated/visited
        initializeBottom();

    }

    /**
     * Sets the number of rows for the maze.
     * @param rows The new number of rows.
     */
    public void setRows(int rows) { this.rows = rows; }

    /**
     * Sets the number of columns for the maze.
     * @param columns The new number of columns.
     */
    public void setColumns(int columns) { this.columns = columns; }

    /**
     * Gets the current MazeCell in the generation/solution process.
     * @return The current MazeCell.
     */
    public MazeCell getCurr() { return curr; }

    /**
     * Sets the text on the label beneath the maze
     * @param text
     */
    public void setPercentCompleteLabelText(String text) { percentCompleteLabel.setText(text); }

    /**
     * Gets the percent generated as a String of length no greater than 4.
     * @return String of percentGenerated trimmed to length 4.
     */
    public String getPercentGenerated() {
        String value = Double.toString(percentGenerated);
        if (value.length() > 4) {
            value = value.substring(0, 4);
        }
        return value;
    }

    /**
     * Gets the percent visited as a String of length no greater than 4.
     * @return String of percentVisited trimmed to length 4.
     */
    public String getPercentVisited() {
        String value = Double.toString(percentVisited);
        if (value.length() > 4) {
            value = value.substring(0, 4);
        }
        return value;
    }

    /**
     * Clears the maze to a new maze of dimension rows x columns
     */
    public void clear() {
        // set all cells that will be part of the new maze to black
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                for (int wall = 0; wall < 4; wall++) {
                    maze[row][col].setWall(wall, false);
                }
                maze[row][col].setRectangleColor(MazeCell.BACKGROUND);
            }
        }

        // set all cells outside of the new maze to light grey in the rows dimension
        for (int row = rows; row < MAX_ROWS; row++) {
            for (int col = 0; col < MAX_COLS; col++) {
                for (int wall = 0; wall < 4; wall++) {
                    maze[row][col].setWall(wall, false);
                }
                maze[row][col].setRectangleColor(MazeCell.BACKTRACKED);
            }
        }

        // set all cells outside of the new maze to light grey in the columns dimension
        for (int row = 0; row < MAX_ROWS; row++) {
            for (int col = columns; col < MAX_COLS; col++) {
                for (int wall = 0; wall < 4; wall++) {
                    maze[row][col].setWall(wall, false);
                }
                maze[row][col].setRectangleColor(MazeCell.BACKTRACKED);
            }
        }
        this.repaint();
    }

    /**
     * Initializes values for the generation of a new maze.
     */
    public void newMazeInit() {
        // set the number of cells visited to 0, and the total number of cells to rows * columns
        cellsVisited = 0.0;
        totalCells = rows * columns;

        // initialize percentGenerated (to zero)
        percentGenerated = (cellsVisited / totalCells) * 100;

        // set all cells as not visited
        visited = new boolean[rows][columns];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                visited[row][col] = false;
            }
        }

        // randomly select a cell to start the generation from
        int currRow = RNG.nextInt(rows);
        int currCol = RNG.nextInt(columns);

        // Curr is cell N from pseudo-code
        curr = maze[currRow][currCol];

        // resets the maze walls
        resetWalls();

        // sets the start to green and the end to red
        maze[0][0].setRectangleColor(MazeCell.START);
        maze[rows-1][columns-1].setRectangleColor(MazeCell.END);
    }

    /**
     * Generates an entire maze.
     */
    public void generate() {
        // Continue looping as long as there are still nodes to explore
        while (curr != null)
            updateMaze();

        percentGenerated = (cellsVisited / totalCells) * 100;
    }

    /**
     * Explores a single node of the maze and finds the next node to explore.
     */
    public void updateMaze() {
        // tracks the coordinates of the current cell
        int[] currCoords;
        int currRow, currCol;

        // the coordinates of the next cell to explore
        Integer[] nextCoords;

        // coordinates of the cells to the top, right, bottom and left of current cell
        Integer[][] coordsToCheck;

        // ArrayList of valid coordinates adjacent to the current cell
        ArrayList<Integer[]> neighborCoords;

        // return if curr is null; needed for timer in controller
        if (curr == null)
            return;

        // push N onto the queue
        queue.push(curr);

        // mark N as visited
        currCoords = curr.getCoords();
        currRow = currCoords[0];
        currCol = currCoords[1];
        visited[currRow][currCol] = true;

        // keep track of number of visited cells
        cellsVisited++;

        // Randomly select an adjacent cell A of N that has not been visited
        neighborCoords = new ArrayList<>();
        while (neighborCoords.isEmpty()) {
            // initialize all adjacent coordinates
            coordsToCheck = new Integer[][]{
                    {currRow - 1, currCol},
                    {currRow, currCol - 1},
                    {currRow, currCol + 1},
                    {currRow + 1, currCol}
            };

            // check which of the above coordinates correspond to unvisited cells
            neighborCoords = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                int r = coordsToCheck[i][0];
                int c = coordsToCheck[i][1];

                if (!(r < 0) && !(r >= rows) && !(c < 0) && !(c >= columns) && !visited[r][c]) {
                    neighborCoords.add(coordsToCheck[i]);
                }
            }

            // if there are no neighbors that haven't been explored
            if (neighborCoords.isEmpty()) {
                if (queue.isEmpty()) {
                    curr = null;
                    break;
                }
                // pop off the stack until one with a valid neighbor is found
                curr = queue.pop();
                currCoords = curr.getCoords();
                currRow = currCoords[0];
                currCol = currCoords[1];
            }
        }

        // if there is a neighbor
        if (!neighborCoords.isEmpty()) {
            // choose one at random
            int neighbor = RNG.nextInt(neighborCoords.size());
            nextCoords = neighborCoords.get(neighbor);

            // move to the random neighbor that was selected
            breakWalls(currRow, currCol, nextCoords[0], nextCoords[1]);
        }

        // update the percent generated
        percentGenerated = (cellsVisited / totalCells) * 100;
    }

    /**
     * Initializes variables when solving the maze.
     */
    public void initSolver() {
        // set cellsVisited to zero and initialze totalCells
        cellsVisited = 0.0;
        totalCells = rows * columns;

        // initialize percentVisited to zero
        percentVisited = (cellsVisited / totalCells) * 100;

        // initialize new linked list for visited cells
        visitedCells = new LinkedList<>();

        // initialize the queue to a new linked list
        queue = new LinkedList<>();

        // mark all cells as unvisited
        visited = new boolean[rows][columns];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                visited[row][col] = false;
            }
        }

        // set current cell to top left cell
        int currRow = 0;
        int currCol = 0;
        curr = maze[currRow][currCol];
    }

    /**
     * Solves the entire maze
     */
    public void solve() {
        // loop until the end of the maze is found
        while(curr != null && !(curr.getRectangleColor() == MazeCell.END)) {
            updateSolver();
        }

        // fixes cells on the path that are colored incorrectly after popping off stack
        fixGreyBlocks();
    }

    /**
     * Choose the next cell to explore in the solver.
     */
    public void updateSolver() {
        // coordinates of the current cell
        int[] currCoords;
        int currRow, currCol;

        // the next cell to look at
        MazeCell next;

        // coordinates of the next cell
        Integer[] nextCoords;
        int nextRow, nextCol;

        // Adjacent coordinates to current cell
        Integer[][] coordsToCheck;

        // ArrayList of valid adjacent coordinates (i.e. that aren't blocked by a wall or visited)
        ArrayList<Integer[]> neighborCoords;

        // return if there is no current cell or the current cell is the end of the maze
        if ((curr == null) || (curr.getRectangleColor() == MazeCell.END))
            return;

        // push the current cell onto the list of visited cells
        visitedCells.push(curr);

        // push N onto the queue
        queue.push(curr);

        // mark N as visited
        currCoords = curr.getCoords();
        currRow = currCoords[0];
        currCol = currCoords[1];
        visited[currRow][currCol] = true;

        // track the number of cells visited
        cellsVisited++;

        // set each cell to visited color when visited (leaving initial cell green)
        if (!(curr.getCoords()[0] == 0 && curr.getCoords()[1] == 0))
            curr.setRectangleColor(MazeCell.VISITED);

        // Randomly select an adjacent cell A of N that has not been visited
        neighborCoords = new ArrayList<>();
        while (neighborCoords.isEmpty()) {
            // all adjacent coordinates to current cell
            coordsToCheck = new Integer[][]{
                    {currRow - 1, currCol}, // top
                    {currRow, currCol + 1}, // right
                    {currRow + 1, currCol}, // bottom
                    {currRow, currCol - 1} // left
            };

            // create an ArrayList of adjacent cells that can be visited
            neighborCoords = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                int r = coordsToCheck[i][0];
                int c = coordsToCheck[i][1];

                if (!(r < 0) && !(r >= rows) && !(c < 0) && !(c >= columns) && !visited[r][c]
                        && !curr.getWall(i)) {
                    neighborCoords.add(coordsToCheck[i]);
                }
            }

            // if there are no neighbors, pop off the stack until one with neighbors is found
            if (neighborCoords.isEmpty()) {
                if (queue.isEmpty()) {
                    curr = null;
                    break;
                }
                curr = queue.pop();
                if (!(curr.getCoords()[0] == 0 && curr.getCoords()[1] == 0))
                    curr.setRectangleColor(MazeCell.BACKTRACKED);
                currCoords = curr.getCoords();
                currRow = currCoords[0];
                currCol = currCoords[1];
            }
        }

        // if there is at least one neighbor, select the next cell
        if (!neighborCoords.isEmpty()) {
            nextCoords = neighborCoords.get(0);
            nextRow = nextCoords[0];
            nextCol = nextCoords[1];
            next = maze[nextRow][nextCol];

            curr = next;
        }

        // update the percentVisited
        percentVisited = (cellsVisited / totalCells) * 100;
    }

    /**
     * Fixes cells that get set to grey when backtracking that are actually on the solution path.
     */
    public void fixGreyBlocks() {
        // for all visited cells
        for (MazeCell cell : visitedCells) {
            // current cell coordinates
            int[] coords = cell.getCoords();
            int row = coords[0];
            int col = coords[1];

            // adjacent coordinates
            int[][] adjacentCoords;

            // number of adjacent cells on the path; all cells on the path except for the initial and final
            // cell should have exactly two adjacent cells on the path
            int adjacentBlues;

            // only check cells that are the visited color and are not the initial cell
            if (cell.getRectangleColor() == MazeCell.VISITED && !(row == 0 && col == 0)) {
                adjacentCoords = new int[][]{
                        {row - 1, col}, // top
                        {row, col + 1}, // right
                        {row + 1, col}, // bottom
                        {row, col - 1}  // left
                };

                // initialize adjacent cells on path to zero
                adjacentBlues = 0;

                // check adjacent cells incrementing adjacentBlues for each one found on the path
                for (int i = 0; i < 4; i++) {
                    int r = adjacentCoords[i][0];
                    int c = adjacentCoords[i][1];

                    if (!cell.getWall(i) && !(r < 0) && !(r >= rows) && !(c < 0) && !(c >= columns) &&
                            (maze[r][c].getRectangleColor() == MazeCell.VISITED ||
                                    maze[r][c].getRectangleColor() == MazeCell.BACKGROUND ||
                            maze[r][c].getRectangleColor() == MazeCell.START))
                        adjacentBlues++;
                }

                // if there are less than 2, there's a grey adjacent cell that should be blue
                if (adjacentBlues < 2) {
                    for (int i = 0; i < 4; i++) {
                        int r = adjacentCoords[i][0];
                        int c = adjacentCoords[i][1];

                        // set the cell that's on the solution path that is grey to blue
                        if (!cell.getWall(i) && !(r < 0) && !(r >= rows) && !(c < 0) && !(c >= columns) &&
                                maze[r][c].getRectangleColor() == MazeCell.BACKTRACKED &&
                                !(r == 0 && c == 0))
                            maze[r][c].setRectangleColor(MazeCell.VISITED);
                    }
                }
            }
        }
    }

    /**
     * Sets the width and height for the panel.
     */
    private void setMazeWidthAndHeight() {
        mazeWidth = rows * 15;
        mazeHeight = columns * 15;
    }

    /**
     * Constructs new MazeCells and adds them to the panel when constructor is called.
     */
    private void initializeMaze() {
        // create the panel
        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(mazeWidth, mazeHeight));
        topPanel.setLayout(new GridLayout(rows, columns, 0, 0));

        // create all the MazeCells
        maze = new MazeCell[rows][columns];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                maze[row][column] = new MazeCell(15, row, column);
                topPanel.add(maze[row][column]);
            }
        }

        // add the maze to the parent panel
        this.add(topPanel, BorderLayout.NORTH);
    }

    /**
     * Initializes the percentCompleteLabel in a JPanel.
     */
    private void initializeBottom() {
        // create the panel for the label
        JPanel bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(775, 900-mazeHeight));

        // add the label
        percentCompleteLabel = new JLabel();
        bottomPanel.add(percentCompleteLabel, BorderLayout.SOUTH);

        // add bottomPanel to the parent MazePanel
        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Sets all the walls for all the cells to true.
     */
    private void resetWalls() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                for (int wall = 0; wall < 4; wall++) {
                    maze[row][col].setWall(wall, true);
                }
            }
        }
    }

    /**
     * Break through a wall into the next cell.
     * @param currRow Row of the current cell.
     * @param currCol Column of the current cell.
     * @param nextRow Row of the next cell.
     * @param nextCol Column of the next cell.
     */
    private void breakWalls(int currRow, int currCol, int nextRow, int nextCol) {
        // the next cell
        MazeCell next;

        // the wall on the current and next cell
        int currWall, nextWall;

        next = maze[nextRow][nextCol];

        // Break walls between cells
        if (nextRow == currRow) {
            if (nextCol == currCol - 1) {
                currWall = MazeCell.LEFT;
                nextWall = MazeCell.RIGHT;
            } else {
                currWall = MazeCell.RIGHT;
                nextWall = MazeCell.LEFT;
            }
        } else {
            if (nextRow == currRow - 1) {
                currWall = MazeCell.TOP;
                nextWall = MazeCell.BOTTOM;
            } else {
                currWall = MazeCell.BOTTOM;
                nextWall = MazeCell.TOP;
            }
        }

        curr.setWall(currWall, false);
        next.setWall(nextWall, false);

        // update curr to the next cell
        curr = next;
    }
}
