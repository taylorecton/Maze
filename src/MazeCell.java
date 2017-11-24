/*
 * File:     MazeCell.java
 * Author:   Taylor Ecton
 * Purpose:  Single cell on the maze grid.
 */

import javax.swing.*;
import java.awt.*;

public class MazeCell extends JComponent {
    // static constants for easy access outside of this class
    // indices for the walls
    public static final int TOP = 0;
    public static final int RIGHT = 1;
    public static final int BOTTOM = 2;
    public static final int LEFT = 3;

    // different colors used by the cells
    public static final Color BACKGROUND = Color.BLACK;
    public static final Color WALL = Color.WHITE;
    public static final Color START = Color.GREEN;
    public static final Color END = Color.RED;
    public static final Color BACKTRACKED = Color.LIGHT_GRAY;
    public static final Color VISITED = Color.BLUE;

    // boolean values representing the walls of this cell
    private boolean[] walls = new boolean[]{ false, false, false, false };

    // internal color of the cell
    private Color rectangleColor;

    // internal rectangle of the cell
    private Polygon rectangle;

    // length of a side
    private int sideLength;

    // coordinates of the cell
    private int[] coords;

    /**
     * Constructor for the cell.
     * @param sideLength Length of a side.
     * @param row Row index for this cell.
     * @param column Column index for this cell.
     */
    public MazeCell(int sideLength, int row, int column) {
        // set the side length and coordinates
        this.sideLength = sideLength;
        this.coords = new int[]{row, column};

        // set the size of the cell
        setPreferredSize(new Dimension(sideLength, sideLength));

        // create the internal rectangle for this cell (leaving 3 pixels on each edge for the walls)
        int[] xPoints = {0+3, sideLength-3, sideLength-3, 0+3};
        int[] yPoints = {0+3, 0+3, sideLength-3, sideLength-3};
        rectangle = new Polygon(xPoints, yPoints, 4);

        // set the initial rectangle color
        rectangleColor = BACKGROUND;
    }

    /**
     * Allows setting the internal cell color.
     * @param rectangleColor The color to set.
     */
    public void setRectangleColor(Color rectangleColor) {
        this.rectangleColor = rectangleColor;
    }

    /**
     * Gets the current color of the cell.
     * @return The current rectangle color.
     */
    public Color getRectangleColor() {
        return rectangleColor;
    }

    /**
     * Gets the coordinates for this cell.
     * @return The cell's coordinates.
     */
    public int[] getCoords() {
        return coords;
    }

    /**
     * Sets a wall to true or false.
     * @param wall The wall to set.
     * @param value The value to set the wall to.
     */
    public void setWall(int wall, boolean value) {
        walls[wall] = value;
    }

    /**
     * Gets whether there is a wall at the specified side.
     * @param wall The number representing which wall (see constants at top).
     * @return Whether there is a wall there or not.
     */
    public boolean getWall(int wall) {
        return walls[wall];
    }

    /**
     * Specifies how the cell should draw itself.
     * @param g The graphics context.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponents(g);

        Graphics2D g2 = (Graphics2D) g;

        // set stroke to 3 pixels
        g2.setStroke(new BasicStroke(3));

        // draw the four walls
        for (int i = 0; i < 4; i++) {
            if (walls[i]) {
                g2.setColor(Color.WHITE);
            } else {
                g2.setColor(rectangleColor);
            }
            drawWall(i, g2);
        }

        // draw the internal rectangle of the cell and fill it
        g2.setColor(rectangleColor);
        g2.drawPolygon(rectangle);
        g2.fill(rectangle);
    }

    /**
     * Draws a wall
     * @param wall Which wall is being drawn.
     * @param g2 The graphics context.
     */
    private void drawWall(int wall, Graphics2D g2) {
        int start = 0;
        int stop = sideLength;

        switch (wall) {
            case TOP:
                g2.drawLine(start, start, stop, start);
                break;
            case RIGHT:
                g2.drawLine(stop, start, stop, stop);
                break;
            case BOTTOM:
                g2.drawLine(start, stop, stop, stop);
                break;
            case LEFT:
                g2.drawLine(start, start, start, stop);
                break;
        }
    }

}
