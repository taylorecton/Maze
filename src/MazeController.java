/*
 * File:     MazeController.java
 * Author:   Taylor Ecton
 * Purpose:  Controller class for the maze.
 */

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MazeController implements ChangeListener, ActionListener {
    // the panel containing the maze
    private MazePanel mazePanel;

    // the panel containing the controls
    private ControlPanel controlPanel;

    // the timer for animations
    private Timer timer;

    // whether the maze is currently solved or not
    private boolean solved = false;

    /**
     * Constructor for MazeController class.
     * @param mazePanel The panel containing the maze.
     */
    public MazeController(MazePanel mazePanel) {
        this.mazePanel = mazePanel;
    }

    /**
     * Sets the control panel for access to control panel values.
     * @param controlPanel
     */
    public void setControlPanel(ControlPanel controlPanel) {
        this.controlPanel = controlPanel;
    }

    /**
     * Overrides actionPerformed to implement ActionListener interface.
     * @param e ActionEvent (Button click in the case of this project)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clicked = (JButton) e.getSource();

        // call appropriate function based on action command of button
        switch (clicked.getActionCommand()) {
            case "generate":
                generateMaze();
                break;
            case "solve":
                solveMaze();
                break;
            case "stop":
                stopTimer();
                break;
            case "resume":
                resumeTimer();
                break;
            default:
                System.err.println("Unexpected string in actionPerformed: " + clicked.getActionCommand());
                System.exit(1);
        }
    }

    /**
     * Overrides stateChanged to implement the ChangeListener interface.
     * @param e Slider bar being changed.
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider adjusted = (JSlider) e.getSource();

        // take appropriate action based on which bar is being adjusted
        if (!adjusted.getValueIsAdjusting()) {
            switch (adjusted.getName()) {
                case "speed":
                    break;
                case "row":
                    if (timer != null && timer.isRunning()) {
                        timer.stop();
                        mazePanel.setPercentCompleteLabelText("Maze dimension adjusted. Animation stopped.");
                    }
                    mazePanel.setRows(adjusted.getValue());
                    controlPanel.setRowLabelText("Rows: " + adjusted.getValue());
                    break;
                case "column":
                    if (timer != null && timer.isRunning()) {
                        timer.stop();
                        mazePanel.setPercentCompleteLabelText("Maze dimension adjusted. Animation stopped.");
                    }
                    mazePanel.setColumns(adjusted.getValue());
                    controlPanel.setColumnLabelText("Columns: " + adjusted.getValue());
                    break;
                default:
                    System.err.println("Unexpected string in stateChanged: " + adjusted.getName());
                    System.exit(1);
            }
        }
    }

    /**
     * Has the mazePanel generate a new maze. Does so in a timer if the checkbox is checked on
     * the control panel.
     */
    private void generateMaze() {
        // set solved to false since a new maze is being generated
        solved = false;

        // stop the timer if one is running
        if (timer != null)
            timer.stop();

        // set the stop/resume button to say stop (in case it said resume before button was clicked)
        controlPanel.setStopResumeButtonText("Stop");
        controlPanel.setStopResumeButtonAction("stop");

        // clear the maze in the maze panel and initialize a new one
        mazePanel.clear();
        mazePanel.newMazeInit();

        // set the label on the maze indicating percent generated
        mazePanel.setPercentCompleteLabelText("Generating maze...\n" +
                                              "Percent complete: " +
                                              mazePanel.getPercentGenerated() + "%");

        // if showGeneration check box is checked on control panel, start a timer that
        // updates the maze at fixed intervals; if it isn't checked, do the whole generation then repaint
        if (controlPanel.getShowGeneration()) {
            timer = new Timer(1, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // update the maze repeatedly to control speed of animation
                    for (int i = 0; i < controlPanel.getSpeed(); i++) {
                        mazePanel.updateMaze();
                    }
                    // update the label on the maze
                    mazePanel.setPercentCompleteLabelText("Generating maze...\n" +
                                                          "Percent complete: " +
                                                          mazePanel.getPercentGenerated() + "%");

                    // repaint
                    mazePanel.repaint();

                    // stop if every MazeCell has been visited
                    if (mazePanel.getCurr() == null) {
                        timer.stop();
                    }
                }
            });
            timer.start();
        } else {
            // generate a new maze
            mazePanel.generate();
            // set the label text
            mazePanel.setPercentCompleteLabelText("Generating maze...\n" +
                                                  "Percent complete: " +
                                                  mazePanel.getPercentGenerated() + "%");
        }
    }

    /**
     * Calls the solver on the maze.
     */
    private void solveMaze() {
        // stop timer if one is running
        if (timer != null)
            timer.stop();

        // if the maze is still being generated, don't allow user to start solver
        if (!mazePanel.getPercentGenerated().contains("100.")) {
            mazePanel.setPercentCompleteLabelText("Error: Maze not fully generated.");
            controlPanel.setStopResumeButtonText("Resume");
            controlPanel.setStopResumeButtonAction("resume");
            return;
        }

        // if the maze is already solved, don't allow solver to run again (causes odd animation if show solver
        // is enabled)
        if (solved) {
            return;
        }

        // set the text and action on the stop/resume button
        controlPanel.setStopResumeButtonText("Stop");
        controlPanel.setStopResumeButtonAction("stop");

        // initialize the solver
        mazePanel.initSolver();

        // set the label on the maze
        mazePanel.setPercentCompleteLabelText("Solving maze...\n" +
                                              "Percent visited: " +
                                              mazePanel.getPercentVisited() + "%");

        // if the show solver is checked, animate the solver, otherwise solve and repaint
        if (controlPanel.getShowSolver()) {
            timer = new Timer(1, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (int i = 0; i < controlPanel.getSpeed(); i++) {
                        mazePanel.updateSolver();
                    }
                    mazePanel.setPercentCompleteLabelText("Solving maze...\n" +
                                                          "Percent visited: " +
                                                          mazePanel.getPercentVisited() + "%");

                    mazePanel.repaint();

                    // stopping condition for timer
                    if (mazePanel.getCurr() == null || mazePanel.getCurr().getRectangleColor() == MazeCell.END) {
                        timer.stop();
                        mazePanel.fixGreyBlocks();
                        mazePanel.setPercentCompleteLabelText("Maze solved. Percent visited: " +
                                                              mazePanel.getPercentVisited() + "%");
                        solved = true;
                    }
                }
            });
            timer.start();
            mazePanel.repaint();
        } else {
            // solve the maze and repaint
            mazePanel.solve();
            mazePanel.setPercentCompleteLabelText("Maze solved.\n" +
                                                  "Percent visited: " +
                                                  mazePanel.getPercentVisited() + "%");
            solved = true;
            mazePanel.repaint();
        }
    }

    /**
     * Stops the timer when the stop button is pressed.
     */
    private void stopTimer() {
        if (timer == null) {
            return;
        } else {
            timer.stop();

            // stop button becomes resume when pressed
            controlPanel.setStopResumeButtonText("Resume");
            controlPanel.setStopResumeButtonAction("resume");
        }
    }

    /**
     * Resumes timer when resume button is pressed.
     */
    private void resumeTimer() {
        if (timer != null)
            timer.start();

        controlPanel.setStopResumeButtonText("Stop");
        controlPanel.setStopResumeButtonAction("stop");
    }
}
