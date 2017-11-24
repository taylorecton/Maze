/*
 * File:     Maze.java
 * Author:   Taylor Ecton
 * Purpose:  Driver for the maze. Initializes all the components and starts the program.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Maze extends JFrame {

    /**
     * Constructor for Maze class.
     */
    public Maze() {
        super("Maze Game");

        Container container = getContentPane();

        // initialize all the components
        MazePanel mazePanel = new MazePanel();
        MazeController controller = new MazeController(mazePanel);
        ControlPanel controlPanel = new ControlPanel(controller);
        controller.setControlPanel(controlPanel);

        // add the components to the container
        container.add(mazePanel, BorderLayout.WEST);
        container.add(controlPanel, BorderLayout.EAST);

        // set the container size and make it visible
        this.setSize(1025, 900);
        this.setResizable(false);
        this.setVisible(true);
    }

    /**
     * Start the program.
     * @param args No arguments.
     */
    public static void main(String[] args) {
        Maze maze = new Maze();

        maze.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}
