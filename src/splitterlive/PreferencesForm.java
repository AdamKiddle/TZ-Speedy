/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package splitterlive;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class PreferencesForm {
    public PreferencesForm() {
    }
    
    private static JFrame preferencesForm;
    private static PreferencesFile userPreferences;
    private static String[] preferencesString = new String[SplitterLive.NUMBER_OF_PREFERENCE_OPTIONS];

    // the main form which is called from splitterlive, creates the form and sets the elements
    public static void createPreferencesForm() {
        int NUMBER_OF_COLOR_OPTIONS = 11;

        //creating the form:
        userPreferences = new PreferencesFile();
        preferencesForm = new JFrame("Preferences");
        preferencesForm.setSize(500, 650);

        //creating the form elements:
        JButton mainFormBackground = new JButton("Pick");
        JButton mainFormText = new JButton("Pick");
        JButton mainTitleBackground = new JButton("Pick");
        JButton columnTitlesBackground = new JButton("Pick");
        JButton highlightedRowBackground = new JButton("Pick");
        JButton negativeDeltaText = new JButton("Pick");
        JButton positiveDeltaText = new JButton("Pick");
        JButton neutralDeltaText = new JButton("Pick");
        JButton mainTimerBackground = new JButton("Pick");
        JButton activeTimerText = new JButton("Pick");
        JButton stoppedTimerText = new JButton("Pick");
        final JComboBox numberOfRowsDisplayed = new JComboBox();
        final JCheckBox displaySplitNameColumn = new JCheckBox();
        final JCheckBox displaySplitColumn = new JCheckBox();
        final JCheckBox displayPBColumn = new JCheckBox();
        final JCheckBox displayDeltaPBColumn = new JCheckBox();
        final JButton startStopSplitKeyCode = new JButton("Pick");
        final JButton resetKeyCode = new JButton("Pick");
        final JButton goForwardsKeyCode = new JButton("Pick");

        //filling the combobox for numebr of splits to display:
        numberOfRowsDisplayed.addItem("3");
        numberOfRowsDisplayed.addItem("4");
        numberOfRowsDisplayed.addItem("5");
        numberOfRowsDisplayed.addItem("6");
        numberOfRowsDisplayed.addItem("7");
        numberOfRowsDisplayed.addItem("8");
        numberOfRowsDisplayed.addItem("9");
        numberOfRowsDisplayed.addItem("10");
        numberOfRowsDisplayed.addItem("11");
        numberOfRowsDisplayed.addItem("12");
        numberOfRowsDisplayed.addItem("13");
        numberOfRowsDisplayed.addItem("14");
        numberOfRowsDisplayed.addItem("15");

        //creaing the elements which do not directly relate to preferences
        final JPanel[] colorDisplayer = new JPanel[NUMBER_OF_COLOR_OPTIONS];
        final JLabel splitKeyCodeDisplay = new JLabel("KeyCode: " + userPreferences.getStartStopSplitKeyCode());
        final JLabel resetKeyCodeDisplay = new JLabel("KeyCode: " + userPreferences.getResetKeyCode());
        final JLabel skipSplitKeyCodeDisplay = new JLabel("KeyCode: " + userPreferences.getGoForwardsKeyCode());
        
        //setting the initial states for the checkboxes
        displaySplitNameColumn.setSelected(userPreferences.getDisplayedColumns()[0]);
        displaySplitColumn.setSelected(userPreferences.getDisplayedColumns()[1]);
        displayPBColumn.setSelected(userPreferences.getDisplayedColumns()[2]);
        displayDeltaPBColumn.setSelected(userPreferences.getDisplayedColumns()[3]);

        //turns all the preference options in the user preferences file into strings
        preferencesToStrings();
        
        //creating the save and cancel buttons
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        //creating all the color displayer squares:
        for (int i = 0; i < NUMBER_OF_COLOR_OPTIONS; i++) {
            colorDisplayer[i] = new JPanel();
        }

        //setting all the colors of the color displayer squares:
        colorDisplayer[0].setBackground(userPreferences.getMainFormBackgroundColor());
        colorDisplayer[1].setBackground(userPreferences.getMainFormTextColor());
        colorDisplayer[2].setBackground(userPreferences.getMainTitleBackgroundColor());
        colorDisplayer[3].setBackground(userPreferences.getColumnTitlesBackgroundColor());
        colorDisplayer[4].setBackground(userPreferences.getHighlightedRowBackgroundColor());
        colorDisplayer[5].setBackground(userPreferences.getNegativeDeltaTextColor());
        colorDisplayer[6].setBackground(userPreferences.getPositiveDeltaTextColor());
        colorDisplayer[7].setBackground(userPreferences.getNeutralDeltaTextColor());
        colorDisplayer[8].setBackground(userPreferences.getMainTimerBackgroundColor());
        colorDisplayer[9].setBackground(userPreferences.getActiveTimerTextColor());
        colorDisplayer[10].setBackground(userPreferences.getStoppedTimerTextColor());
        numberOfRowsDisplayed.setSelectedIndex(userPreferences.getNumberOfRowsDisplayed() - 3);

        //setting the layout for all the elements to be placed on the form:
        preferencesForm.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.ipadx = 20;
        c.ipady = 20;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1;
        c.weighty = 1;

        //Left hand side:
        //Preference option for the main form background colour:
        c.gridy = 0;
        c.gridx = 0;
        c.gridwidth = 2;
        preferencesForm.add(new JLabel("Main form background color:"), c);//topCenter
        c.gridy = 1;
        c.gridx = 0;
        c.gridwidth = 1;
        preferencesForm.add(mainFormBackground, c);//bottomLeft
        c.gridx = 1;
        c.gridwidth = 1;
        preferencesForm.add(colorDisplayer[0], c);//bottomRight

        //Preference option for the main form text colour:
        c.gridy = 2;
        c.gridx = 0;
        c.gridwidth = 2;
        preferencesForm.add(new JLabel("Main form text color:"), c);//topCenter
        c.gridy = 3;
        c.gridx = 0;
        c.gridwidth = 1;
        preferencesForm.add(mainFormText, c);//bottomLeft
        c.gridx = 1;
        c.gridwidth = 1;
        preferencesForm.add(colorDisplayer[1], c);//bottomRight

        //Preference option for the main title background color:
        c.gridy = 4;
        c.gridx = 0;
        c.gridwidth = 2;
        preferencesForm.add(new JLabel("Main title background color:"), c);//topCenter
        c.gridy = 5;
        c.gridx = 0;
        c.gridwidth = 1;
        preferencesForm.add(mainTitleBackground, c);//bottomLeft
        c.gridx = 1;
        c.gridwidth = 1;
        preferencesForm.add(colorDisplayer[2], c);//bottomRight

        //Preference option for the column titles background color:
        c.gridy = 6;
        c.gridx = 0;
        c.gridwidth = 2;
        preferencesForm.add(new JLabel("Column titles background color:"), c);//topCenter
        c.gridy = 7;
        c.gridx = 0;
        c.gridwidth = 1;
        preferencesForm.add(columnTitlesBackground, c);//bottomLeft
        c.gridx = 1;
        c.gridwidth = 1;
        preferencesForm.add(colorDisplayer[3], c);//bottomRight

        //Preference option for the color of the highlighted row:
        c.gridy = 8;
        c.gridx = 0;
        c.gridwidth = 2;
        preferencesForm.add(new JLabel("Highlighted row color:"), c);//topCenter
        c.gridy = 9;
        c.gridx = 0;
        c.gridwidth = 1;
        preferencesForm.add(highlightedRowBackground, c);//bottomLeft
        c.gridx = 1;
        c.gridwidth = 1;
        preferencesForm.add(colorDisplayer[4], c);//bottomRight

        //Preference option for the negative time text colour:
        c.gridy = 10;
        c.gridx = 0;
        c.gridwidth = 2;
        preferencesForm.add(new JLabel("Negative delta time text color:"), c);//topCenter
        c.gridy = 11;
        c.gridx = 0;
        c.gridwidth = 1;
        preferencesForm.add(negativeDeltaText, c);//bottomLeft
        c.gridx = 1;
        c.gridwidth = 1;
        preferencesForm.add(colorDisplayer[5], c);//bottomRight

        //Preference option for the positive time text colour:
        c.gridy = 12;
        c.gridx = 0;
        c.gridwidth = 2;
        preferencesForm.add(new JLabel("Positive delta time text color:"), c);//topCenter
        c.gridy = 13;
        c.gridx = 0;
        c.gridwidth = 1;
        preferencesForm.add(positiveDeltaText, c);//bottomLeft
        c.gridx = 1;
        c.gridwidth = 1;
        preferencesForm.add(colorDisplayer[6], c);//bottomRight

        //Preference option for the neutral time text colour:
        c.gridy = 14;
        c.gridx = 0;
        c.gridwidth = 2;
        preferencesForm.add(new JLabel("Neutral delta time text color:"), c);//topCenter
        c.gridy = 15;
        c.gridx = 0;
        c.gridwidth = 1;
        preferencesForm.add(neutralDeltaText, c);//bottomLeft
        c.gridx = 1;
        c.gridwidth = 1;
        preferencesForm.add(colorDisplayer[7], c);//bottomRight

        //Preference option for the main timer background colour:
        c.gridy = 16;
        c.gridx = 0;
        c.gridwidth = 2;
        preferencesForm.add(new JLabel("Main timer background color:"), c);//topCenter
        c.gridy = 17;
        c.gridx = 0;
        c.gridwidth = 1;
        preferencesForm.add(mainTimerBackground, c);//bottomLeft
        c.gridx = 1;
        c.gridwidth = 1;
        preferencesForm.add(colorDisplayer[8], c);//bottomRight

        //Preference option for the active timer text colour:
        c.gridy = 18;
        c.gridx = 0;
        c.gridwidth = 2;
        preferencesForm.add(new JLabel("Timer text color when active:"), c);//topCenter
        c.gridy = 19;
        c.gridx = 0;
        c.gridwidth = 1;
        preferencesForm.add(activeTimerText, c);//bottomLeft
        c.gridx = 1;
        c.gridwidth = 1;
        preferencesForm.add(colorDisplayer[9], c);//bottomRight

        //Preference option for the neutral time text colour:
        c.gridy = 20;
        c.gridx = 0;
        c.gridwidth = 2;
        preferencesForm.add(new JLabel("Timer text color when stopped:"), c);//topCenter
        c.gridy = 21;
        c.gridx = 0;
        c.gridwidth = 1;
        preferencesForm.add(stoppedTimerText, c);//bottomLeft
        c.gridx = 1;
        c.gridwidth = 1;
        preferencesForm.add(colorDisplayer[10], c);//bottomRight

        //Right hand side
        //Preference option for number of splits to display:
        c.gridy = 0;
        c.gridx = 2;
        c.gridwidth = 3;
        preferencesForm.add(new JLabel("Number of splits to display:"), c);//topCenter
        c.gridy = 1;
        c.gridx = 2;
        preferencesForm.add(numberOfRowsDisplayed, c);//bottomLeft

        //Preference option for displaying the split name column:
        c.gridy = 2;
        c.gridx = 2;
        c.gridwidth = 2;
        preferencesForm.add(new JLabel("Display split name column:"), c);//topLeft
        c.gridx = 4;
        c.gridwidth = 1;
        preferencesForm.add(displaySplitNameColumn, c);//topRight

        //Preference option for displaying the split time column:
        c.gridy = 3;
        c.gridx = 2;
        c.gridwidth = 2;
        preferencesForm.add(new JLabel("Display split time column:"), c);//topLeft
        c.gridx = 4;
        c.gridwidth = 1;
        preferencesForm.add(displaySplitColumn, c);//TopRight

        //Preference option for displaying the PB time column:
        c.gridy = 4;
        c.gridx = 2;
        c.gridwidth = 2;
        preferencesForm.add(new JLabel("Display PB time column:"), c);//topLeft
        c.gridx = 4;
        c.gridwidth = 1;
        preferencesForm.add(displayPBColumn, c);//TopRight

        //Preference option for displaying the delta-PB column:
        c.gridy = 5;
        c.gridx = 2;
        c.gridwidth = 2;
        preferencesForm.add(new JLabel("Display delta-PB time column:"), c);//topLeft
        c.gridx = 4;
        c.gridwidth = 1;
        preferencesForm.add(displayDeltaPBColumn, c);//TopRight

        //Preference option for the start/stop/split keycode:
        c.gridy = 6;
        c.gridx = 2;
        c.gridwidth = 3;
        preferencesForm.add(new JLabel("Start/Split hotkey:"), c);//topCenter
        c.gridy = 7;
        c.gridx = 2;
        c.gridwidth = 1;
        preferencesForm.add(startStopSplitKeyCode, c);//bottomLeft
        c.gridx = 3;
        c.gridwidth = 2;
        preferencesForm.add(splitKeyCodeDisplay, c);//bottomRight

        //Preference option for the reset keycode:
        c.gridy = 8;
        c.gridx = 2;
        c.gridwidth = 3;
        preferencesForm.add(new JLabel("Reset hotkey:"), c);//topCenter
        c.gridy = 9;
        c.gridx = 2;
        c.gridwidth = 1;
        preferencesForm.add(resetKeyCode, c);//bottomLeft
        c.gridx = 3;
        c.gridwidth = 2;
        preferencesForm.add(resetKeyCodeDisplay, c);//bottomRight

        //Preference option for the go forwards keycode:
        c.gridy = 10;
        c.gridx = 2;
        c.gridwidth = 3;
        preferencesForm.add(new JLabel("Skip split hotkey:"), c);//topCenter
        c.gridy = 11;
        c.gridx = 2;
        c.gridwidth = 1;
        preferencesForm.add(goForwardsKeyCode, c);//bottomLeft
        c.gridx = 3;
        c.gridwidth = 2;
        preferencesForm.add(skipSplitKeyCodeDisplay, c);//bottomRight

        //Save/confirm button:
        c.gridy = 13;
        c.gridx = 2;
        c.gridwidth = 3;
        preferencesForm.add(saveButton, c);

        //Cancel button:
        c.gridy = 14;
        c.gridx = 2;
        c.gridwidth = 3;
        preferencesForm.add(cancelButton, c);

        //Bottom text:
        c.gridy = 15;
        c.gridx = 2;
        c.gridwidth = 3;
        preferencesForm.add(new JLabel("Note: most changes to preference options"), c);
        //Bottom text cont.:
        c.gridy = 16;
        c.gridx = 2;
        c.gridwidth = 3;
        preferencesForm.add(new JLabel("will come into effect upon opening the proram."), c);

        preferencesForm.setVisible(true);

        
        /* 
         * The action listeners for all the buttons handle all the button presses.
         * Each button press will change the temporarily saved preference option
         * string array to be the option picked.
         */ 
        mainFormBackground.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                preferencesString[0] = colorToHex(colorPicker(userPreferences.getMainFormBackgroundColor()));
                colorDisplayer[0].setBackground(userPreferences.getMainFormBackgroundColor());
            }
        });
        mainFormText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                preferencesString[1] = colorToHex(colorPicker(userPreferences.getMainFormTextColor()));
                colorDisplayer[1].setBackground(userPreferences.getMainFormTextColor());
                colorDisplayer[1].updateUI();
            }
        });
        mainTitleBackground.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                preferencesString[2] = colorToHex(colorPicker(userPreferences.getMainTitleBackgroundColor()));
                colorDisplayer[2].setBackground(userPreferences.getMainTitleBackgroundColor());
            }
        });
        columnTitlesBackground.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                preferencesString[3] = colorToHex(colorPicker(userPreferences.getColumnTitlesBackgroundColor()));
                colorDisplayer[3].setBackground(userPreferences.getColumnTitlesBackgroundColor());
            }
        });
        highlightedRowBackground.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                preferencesString[4] = colorToHex(colorPicker(userPreferences.getHighlightedRowBackgroundColor()));
                colorDisplayer[4].setBackground(userPreferences.getHighlightedRowBackgroundColor());
            }
        });
        negativeDeltaText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                preferencesString[5] = colorToHex(colorPicker(userPreferences.getNegativeDeltaTextColor()));
                colorDisplayer[5].setBackground(userPreferences.getNegativeDeltaTextColor());
            }
        });
        positiveDeltaText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                preferencesString[6] = colorToHex(colorPicker(userPreferences.getPositiveDeltaTextColor()));
                colorDisplayer[6].setBackground(userPreferences.getPositiveDeltaTextColor());
            }
        });
        neutralDeltaText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                preferencesString[7] = colorToHex(colorPicker(userPreferences.getNeutralDeltaTextColor()));
                colorDisplayer[7].setBackground(userPreferences.getNeutralDeltaTextColor());
            }
        });
        mainTimerBackground.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                preferencesString[8] = colorToHex(colorPicker(userPreferences.getMainTimerBackgroundColor()));
                colorDisplayer[8].setBackground(userPreferences.getMainTimerBackgroundColor());
            }
        });
        activeTimerText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                preferencesString[9] = colorToHex(colorPicker(userPreferences.getActiveTimerTextColor()));
                colorDisplayer[9].setBackground(userPreferences.getActiveTimerTextColor());
            }
        });
        stoppedTimerText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                preferencesString[10] = colorToHex(colorPicker(userPreferences.getStoppedTimerTextColor()));
                colorDisplayer[10].setBackground(userPreferences.getStoppedTimerTextColor());
            }
        });
        
       /* 
        * The keycode buttons are slightly different as these bring up an input
        * dialogue to recieve the property and store it in the array.
        */
        startStopSplitKeyCode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startStopSplitKeyCode.setSelected(true);
                String keycode = JOptionPane.showInputDialog("Input the desired keycode for this hotkey ");
                if (SplitterLive.isInteger(keycode)) {
                    int code = Integer.valueOf(keycode);
                    if (code > 40 && code < 110) {
                        preferencesString[16] = String.valueOf(keycode);
                        splitKeyCodeDisplay.setText("KeyCode: " + String.valueOf(keycode));
                    }
                    startStopSplitKeyCode.setSelected(false);
                }
            }
        });
        resetKeyCode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetKeyCode.setSelected(true);
                String keycode = JOptionPane.showInputDialog("Input the desired keycode for this hotkey ");
                if (SplitterLive.isInteger(keycode)) {
                    int code = Integer.valueOf(keycode);
                    if (code > 40 && code < 110) {
                        preferencesString[17] = String.valueOf(keycode);
                        resetKeyCodeDisplay.setText("KeyCode: " + String.valueOf(keycode));

                    }
                    resetKeyCode.setSelected(false);
                }
            }
        });
        goForwardsKeyCode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goForwardsKeyCode.setSelected(true);
                String keycode = JOptionPane.showInputDialog("Input the desired keycode for this hotkey ");
                if (SplitterLive.isInteger(keycode)) {
                    int code = Integer.valueOf(keycode);
                    if (code > 40 && code < 110) {
                        preferencesString[18] = String.valueOf(keycode);
                        skipSplitKeyCodeDisplay.setText("KeyCode: " + String.valueOf(keycode));
                    }
                    goForwardsKeyCode.setSelected(false);
                }
            }
        });
        
        /* 
         * The save button checks what the combo-box property is as well as the
         * checkboxes. If no checkboxes are checked, the split column is automatically
         * still shown. This is then all saved to the preferences xml file and the winow closed
         */
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                preferencesString[11] = String.valueOf(numberOfRowsDisplayed.getSelectedIndex() + 3);
                preferencesString[12] = String.valueOf(displaySplitNameColumn.isSelected());
                preferencesString[13] = String.valueOf(displaySplitColumn.isSelected());
                preferencesString[14] = String.valueOf(displayPBColumn.isSelected());
                preferencesString[15] = String.valueOf(displayDeltaPBColumn.isSelected());
                if (!displaySplitNameColumn.isSelected()
                        && !displaySplitColumn.isSelected()
                        && !displayPBColumn.isSelected()
                        && !displayDeltaPBColumn.isSelected()) {
                    preferencesString[12] = "true";
                }

                userPreferences.savePreferencesToFile(preferencesString);
                preferencesForm.setVisible(false);
                preferencesForm.dispose();
                SplitterLive.resetEverything();
            }
        });
        
        /* 
         * The cancel button ignores all changes and closes the window
         */
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                preferencesForm.setVisible(false);
                preferencesForm.dispose();
            }
        });
    }

    //method for bringing up java's color picker
    private static Color colorPicker(Color inputColor) {
        Color color = JColorChooser.showDialog(
                preferencesForm,
                "Choose Background Color",
                inputColor);
        return color;
    }

    //method for converting a color into the hex representation of the color
    private static String colorToHex(Color color) {
        String returnString = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        return returnString;
    }

    // method where the default preference string values are all initially set
    private static void preferencesToStrings() {

        preferencesString[0] = colorToHex(userPreferences.getMainFormBackgroundColor());
        preferencesString[1] = colorToHex(userPreferences.getMainFormTextColor());
        preferencesString[2] = colorToHex(userPreferences.getMainTitleBackgroundColor());
        preferencesString[3] = colorToHex(userPreferences.getColumnTitlesBackgroundColor());
        preferencesString[4] = colorToHex(userPreferences.getHighlightedRowBackgroundColor());
        preferencesString[5] = colorToHex(userPreferences.getNegativeDeltaTextColor());
        preferencesString[6] = colorToHex(userPreferences.getPositiveDeltaTextColor());
        preferencesString[7] = colorToHex(userPreferences.getNeutralDeltaTextColor());
        preferencesString[8] = colorToHex(userPreferences.getMainTimerBackgroundColor());
        preferencesString[9] = colorToHex(userPreferences.getActiveTimerTextColor());
        preferencesString[10] = colorToHex(userPreferences.getStoppedTimerTextColor());
        preferencesString[11] = String.valueOf(userPreferences.getNumberOfRowsDisplayed());
        preferencesString[12] = String.valueOf(userPreferences.getDisplayedColumns()[0]);
        preferencesString[13] = String.valueOf(userPreferences.getDisplayedColumns()[1]);
        preferencesString[14] = String.valueOf(userPreferences.getDisplayedColumns()[2]);
        preferencesString[15] = String.valueOf(userPreferences.getDisplayedColumns()[3]);
        preferencesString[16] = String.valueOf(userPreferences.getStartStopSplitKeyCode());
        preferencesString[17] = String.valueOf(userPreferences.getResetKeyCode());
        preferencesString[18] = String.valueOf(userPreferences.getGoForwardsKeyCode());
    }
}
