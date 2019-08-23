/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package splitterlive;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class StatisticsForm {

    private static JFrame mainForm;
    private static GraphPanel graphPanel;
    private static Dimension formSize = new Dimension(600, 400);
    
    // the only method in the class, constructs the statistics form with various inputs
    public static void constructStatisticsForm(int[] runTimes, int numberOfResets, int numberOfCompleted, String fastest) {
        int numberOfRunsDisplayed = 20;

        //create the main form of the class:
        mainForm = new JFrame("Statistics");
        mainForm.setSize(formSize);
        mainForm.setResizable(false);

        //if less than 20 runs are in the timer file, set the displayed number to however many there are
        if (runTimes.length < numberOfRunsDisplayed) {
            numberOfRunsDisplayed = runTimes.length;
        }
        int[] lastRuns = new int[numberOfRunsDisplayed];
        for (int i = 0; i < numberOfRunsDisplayed; i++) {
            lastRuns[i] = runTimes[runTimes.length - (numberOfRunsDisplayed - i)];
        }
        //create the graph panel using the last 20 or less runs found
        graphPanel = new GraphPanel(lastRuns);

        //next chunk handles the layout for the form and objects:
        mainForm.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        mainForm.add(new JLabel("Number of Completed Runs: " + numberOfCompleted), c);//top
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 1;
        mainForm.add(new JLabel("Personal Best time: " + fastest), c);//2nd down
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.weighty = 50;
        c.gridx = 0;
        c.gridy = 2;
        mainForm.add(new JLabel("Final times of last 20 runs"), c);//3rd down
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 3;
        mainForm.add(graphPanel, c);//bottom

        mainForm.setVisible(true);

    }
}
