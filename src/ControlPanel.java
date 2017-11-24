/*
 * File:     ControlPanel.java
 * Author:   Taylor Ecton
 * Purpose:  Panel containing the controls for the maze.
 */

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {

    // Buttons
    private JButton generateButton, solveButton, stopButton;

    // Checkboxes
    private JCheckBox showGeneration, showSolver;

    // Sliders
    private JSlider speedSlider, rowSlider, columnSlider;

    // Labels
    private JLabel speedLabel, rowLabel, columnLabel;

    // Sub-Panels
    private JPanel generatePanel, solvePanel,
                   speedPanel, rowPanel, columnPanel;

    /**
     * Constructor for the controlPanel.
     * @param controller The controller class for the maze.
     */
    public ControlPanel(MazeController controller) {
        super();

        // set the size and layout for the ControlPanel
        this.setPreferredSize(new Dimension(250, 900));
        this.setLayout(new GridLayout(6, 1, 0, 5));

        // set up panel with generate button and show generation check box
        setUpGeneratePanel(controller);

        // set up panel with solve button and show solver check box
        setUpSolvePanel(controller);

        // set up panel with speed slider bar
        setUpSpeedPanel(controller);

        // set up panel with row slider bar
        setUpRowPanel(controller);

        // set up panel with column slider bar
        setUpColumnPanel(controller);

        // set up stop button
        stopButton = new JButton("Stop");
        stopButton.setActionCommand("stop");
        stopButton.addActionListener(controller);

        // add all the sub-panels to the ControlPanel
        this.add(generatePanel);
        this.add(solvePanel);
        this.add(speedPanel);
        this.add(rowPanel);
        this.add(columnPanel);
        this.add(stopButton);
    }

    /**
     * Tells other classes if show generation check box is checked.
     * @return A boolean that is true if the check box is checked.
     */
    public boolean getShowGeneration() { return showGeneration.isSelected(); }

    /**
     * Tells other classes if show solver check box is checked.
     * @return A boolean that is true if the check box is checked.
     */
    public boolean getShowSolver() { return showSolver.isSelected(); }

    /**
     * Allows other classes to set the row label text.
     * @param text The new text to apply to the label.
     */
    public void setRowLabelText(String text) { rowLabel.setText(text); }

    /**
     * Allows other classes to set the column label text.
     * @param text The new text to apply to the label.
     */
    public void setColumnLabelText(String text) { columnLabel.setText(text); }

    /**
     * Allows other classes to set the text on the stop/resume button.
     * @param text The new text to apply to the button.
     */
    public void setStopResumeButtonText(String text) { stopButton.setText(text); }

    /**
     * Allows other classes to set the action command of the stop/resume button.
     * @param action The new action command to set.
     */
    public void setStopResumeButtonAction(String action) { stopButton.setActionCommand(action); }

    /**
     * Gets the value of the speed slider bar.
     * @return The current speed.
     */
    public int getSpeed() { return speedSlider.getValue(); }

    /**
     * Initializes the generate panel.
     * @param controller The controller class for the maze.
     */
    private void setUpGeneratePanel(MazeController controller) {
        // panel for the components
        generatePanel = new JPanel();

        // generate button
        generateButton = new JButton("Generate");
        generateButton.setActionCommand("generate");
        generateButton.addActionListener(controller);

        // show generation checkbox
        showGeneration = new JCheckBox("Show Generation");

        // add these components to the panel
        generatePanel.add(generateButton, BorderLayout.WEST);
        generatePanel.add(showGeneration, BorderLayout.EAST);
    }

    /**
     * Sets up the panel for the solver button and check box.
     * @param controller The controller class for the maze.
     */
    private void setUpSolvePanel(MazeController controller) {
        // panel for the components
        solvePanel = new JPanel();

        // the solve button
        solveButton = new JButton ("Solve");
        solveButton.setActionCommand("solve");
        solveButton.addActionListener(controller);

        // the solver check box
        showSolver = new JCheckBox("Show Solver");

        // add these components to the panel
        solvePanel.add(solveButton, BorderLayout.WEST);
        solvePanel.add(showSolver, BorderLayout.EAST);
    }

    /**
     * Sets up the panel with the speed slider bar.
     * @param controller The controller class for the maze.
     */
    private void setUpSpeedPanel(MazeController controller) {
        // panel for speed components
        speedPanel = new JPanel();

        // speed slider bar set up
        speedSlider = new JSlider(JSlider.HORIZONTAL, 1, 11, 6);
        speedSlider.setMinorTickSpacing(1);
        speedSlider.setMajorTickSpacing(5);
        speedSlider.setPaintTicks(true);
        speedSlider.setPreferredSize(new Dimension(225, 25));
        speedSlider.setName("speed");

        // label for the speed slider bar
        speedLabel = new JLabel("Speed:");

        // add these components to the panel
        speedPanel.add(speedLabel, BorderLayout.NORTH);
        speedPanel.add(speedSlider, BorderLayout.SOUTH);

        speedSlider.addChangeListener(controller);
    }

    /**
     * Panel for the row slider bar.
     * @param controller The controller class for the maze.
     */
    private void setUpRowPanel(MazeController controller) {
        // panel for row components
        rowPanel = new JPanel();

        // row slider setup
        rowSlider = new JSlider(JSlider.HORIZONTAL, 10, 50, 50);
        rowSlider.setMinorTickSpacing(5);
        rowSlider.setMajorTickSpacing(10);
        rowSlider.setPaintTicks(true);
        rowSlider.setPreferredSize(new Dimension(225, 25));
        rowSlider.setName("row");

        // label for the row slider
        rowLabel = new JLabel("Rows: " + rowSlider.getValue());

        // add these components to the panel
        rowPanel.add(rowLabel, BorderLayout.NORTH);
        rowPanel.add(rowSlider, BorderLayout.SOUTH);

        rowSlider.addChangeListener(controller);
    }

    /**
     * Set up for the column slider bar.
     * @param controller The controller class for the maze.
     */
    private void setUpColumnPanel(MazeController controller) {
        // panel for the column components
        columnPanel = new JPanel();

        // column slider set up
        columnSlider = new JSlider(JSlider.HORIZONTAL, 10, 50, 50);
        columnSlider.setMinorTickSpacing(5);
        columnSlider.setMajorTickSpacing(10);
        columnSlider.setPaintTicks(true);
        columnSlider.setPreferredSize(new Dimension(225, 25));
        columnSlider.setName("column");

        // label for the column slider
        columnLabel = new JLabel("Columns: " + columnSlider.getValue());

        // add these components to the panel
        columnPanel.add(columnLabel, BorderLayout.NORTH);
        columnPanel.add(columnSlider, BorderLayout.SOUTH);

        columnSlider.addChangeListener(controller);
    }
}
