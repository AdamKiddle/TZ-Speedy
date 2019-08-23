package splitterlive;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TimerForm extends JFrame implements KeyListener, MouseMotionListener, MouseListener {

    // Complex view constants:
    private final int TIMER_BAR_HEIGHT = 30;
    private final int MAIN_TITLE_BAR_HEIGHT = 30;
    private final int SPLIT_ROW_HEIGHT = 20;
    private final int WIDTH_UNIT = 70;
    //Simple view constants:
    private final Color SIMPLE_VIEW_BG = Color.decode("#101010"); //very dark grey (0x101010 in Hexidecimal)
    private final Color SIMPLE_VIEW_ACTIVE_TEXT = Color.decode("#FFFFFF"); //white
    private final Color SIMPLE_VIEW_STOPPED_TEXT = Color.decode("#990000"); //red
    private final int SIMPLE_X_DIMENSION = 150;
    private final int SIMPLE_Y_DIMENSION = 40;
    //Variables used in the methods
    private Point relativePositionOfCursor;
    private int numberOfSplits = 0;
    private int splitNumberReached = 0;
    private String speedrunTitle = "";
    private String[] splitNames;
    //New timer file objects:
    private JFrame createFileFrame;
    private JLabel splitNameLabel;
    private JTextField splitNameField;
    private JTextField runTitleField;
    private JButton nextSplitButton;
    //Main form objects
    private PreferencesFile userPreferences;
    private JLabel mainTimerLabel;
    private SplitRow[] splitRow;
    private JPanel splitRowsPanel;
    private TimerSaveFile selectedSaveFile;

    //add mouse and key listeners to the form
    public TimerForm() {
        if (SplitterLive.getIsInComplexView()) {
            constructFormComplexView();
        } else {
            constructFormSimpleView();
        }
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
    }

    // create the form but in simple view
    private void constructFormSimpleView() {
        //laying out all the elements, no other logic is done here:
        mainTimerLabel = new JLabel(SplitterLive.DEFAULT_TIMER_TEXT);

        setLayout(new GridBagLayout());
        GridBagConstraints labelLayout = new GridBagConstraints();
        labelLayout.fill = GridBagConstraints.BOTH;
        labelLayout.insets = new Insets(10, 10, 10, 10);
        labelLayout.weightx = 1.0;

        mainTimerLabel.setHorizontalAlignment(JLabel.RIGHT);
        mainTimerLabel.setForeground(SIMPLE_VIEW_ACTIVE_TEXT);
        setUndecorated(true);
        setSize(SIMPLE_X_DIMENSION, SIMPLE_Y_DIMENSION);
        getContentPane().setBackground(SIMPLE_VIEW_BG);
        getContentPane().add(mainTimerLabel, labelLayout);
        setVisible(true);
    }

    //contains the logic for opening the file.
    public void openFile() {
        //opens up the filechooser with the SPT file-filter
        final JFileChooser fc = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("SPT SpliterLive File", "SPT");
        fc.setFileFilter(filter);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setCurrentDirectory(null);
        int returnVal = fc.showOpenDialog(this);

        //open and read the selected file if OK is pressed
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            SplitterLive.setCurrentFile(file);
            selectedSaveFile = new TimerSaveFile(file);
            SplitterLive.setThisSave(selectedSaveFile);
            this.getContentPane().removeAll();
            this.dispose();
            SplitterLive.setComplexView(true);
            constructFormComplexView();
        }
    }

    // construct the form in complex view
    private void constructFormComplexView() {
        // Gets the save file and all the neccessary data resources:
        selectedSaveFile = new TimerSaveFile(SplitterLive.getCurrentFile());
        SplitterLive.setThisSave(selectedSaveFile);
        userPreferences = new PreferencesFile();
        int numberOfDisplayedRows = userPreferences.getNumberOfRowsDisplayed();
        getContentPane().setBackground(userPreferences.getMainFormBackgroundColor());
        setForeground(userPreferences.getMainFormTextColor());
        
        // creates the elements
        JPanel mainTimerBar = constructTimerBarComplex(SplitterLive.DEFAULT_TIMER_TEXT, TIMER_BAR_HEIGHT);
        JPanel mainTitleBar = constructMainTitleBar(MAIN_TITLE_BAR_HEIGHT);
        splitRowsPanel = constructSplitRowsPanel(selectedSaveFile);
        SplitRow splitTitlesBar = constructColumnTitlesBar();

        int widthInUnits = countWidthUnits();
        Dimension formSize = new Dimension();
        formSize.height = MAIN_TITLE_BAR_HEIGHT + TIMER_BAR_HEIGHT
                + SPLIT_ROW_HEIGHT * (numberOfDisplayedRows + 1);
        formSize.width = widthInUnits * WIDTH_UNIT;

        setLayout(new GridBagLayout());
        setUndecorated(true);
        setSize(formSize);

        // adding all the form elements with the correct placement:
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 0;
        c.insets = new Insets(1, 1, 0, 1);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        this.add(mainTitleBar, c);
        c.weighty = 1;
        c.gridy = 1;
        this.add(splitTitlesBar, c);
        c.weighty = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 2;
        this.add(splitRowsPanel, c);
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 3;
        this.add(mainTimerBar, c);
        setVisible(true);

    }

    //method checks if the input filename is valid:
    private boolean isValidFileName(String fileName) {
        try {
            File file = new File(fileName);
            file.createNewFile();
            if (file.exists()) {
                file.delete();
            }
            return true;
        } catch (Exception ex) {
        }
        return false;
    }

    //Contains all the code for creating a new timer file
    public void createNewTimerFile() {
        //First section brings up an input box for number of splits, fully validated:
        boolean getInteger = false;
        createFileFrame = new JFrame("New Timer File");
        while (getInteger == false) { //keep asking until a valid input is
            String numberOfSplitsValidate = JOptionPane.showInputDialog("Enter the number of splits.");
            try {
                numberOfSplits = Integer.parseInt(numberOfSplitsValidate);
                if (numberOfSplits <= 2 || numberOfSplits >= 50) {
                    JOptionPane.showMessageDialog(createFileFrame, "Please enter a numeric value greater than 2 and less than 50.");
                    getInteger = false;
                } else {
                    getInteger = true;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(createFileFrame, "Please enter a numeric value greater than 2 and less than 50.");
                getInteger = false;
            }
        }
        
        // Creates the variables and elements required for the second window
        splitNames = new String[numberOfSplits];
        createFileFrame.setSize(SplitterLive.CREATE_FILE_WINDOW_SIZE);
        createFileFrame.setResizable(false);

        runTitleField = new JTextField();
        splitNameLabel = new JLabel("Split 1 Name:");
        splitNameField = new JTextField();
        nextSplitButton = new JButton("Next");

        createFileFrame.setLayout(new GridBagLayout());

        //sets out the form
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        createFileFrame.add(new JLabel("Run Title:"), c);
        c.weightx = 10;
        c.weighty = 1;
        c.gridx = 1;
        c.gridy = 0;
        createFileFrame.add(runTitleField, c);
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 1;
        createFileFrame.add(splitNameLabel, c);
        c.weightx = 10;
        c.weighty = 1;
        c.gridx = 1;
        c.gridy = 1;
        createFileFrame.add(splitNameField, c);
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 2;
        c.gridy = 1;
        createFileFrame.add(nextSplitButton, c);
        createFileFrame.setVisible(true);


        // adds the action listener for the next button, and when it is pressed
        nextSplitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                splitNames[splitNumberReached] = splitNameField.getText();
                splitNumberReached++;
                if (splitNumberReached == numberOfSplits) {
                    // if the file-name entered is valid, accept it:
                    if (isValidFileName(runTitleField.getText())) {
                        speedrunTitle = runTitleField.getText();
                        createFileFrame.setVisible(false);
                        createFileFrame.dispose();
                        if (!speedrunTitle.equals("")) {
                            File saveFile = TimerSaveFile.createSaveFile(splitNames, speedrunTitle);
                            if (saveFile.exists()) {
                                SplitterLive.setCurrentFile(saveFile);
                                SplitterLive.setComplexView(true);
                                constructFormComplexView();
                            }
                        }
                    } else {
                        //on the last split, if the title is not a valid file name, let the user know:
                        splitNumberReached--;
                        JOptionPane.showMessageDialog(createFileFrame, "The title must be a valid file name.");
                    }
                } else {
                    //advance the split number that is being input
                    splitNameLabel.setText("Split " + (splitNumberReached + 1) + " Name:");
                    splitNameField.setText("");
                }
            }
        });

    }

    /* 
     * resets the form by deleting it and re-constructing it. This way, the timer and
     * preferences are all reset to their true values. It's almost a copy of the
     * original construct timer method, but containing less parts and modifications.
     */ 
    private void resetFormComplexView() {
        selectedSaveFile = new TimerSaveFile(SplitterLive.getCurrentFile());
        userPreferences = new PreferencesFile();
        JPanel mainTimerBar = constructTimerBarComplex(SplitterLive.DEFAULT_TIMER_TEXT, TIMER_BAR_HEIGHT);
        JPanel mainTitleBar = constructMainTitleBar(MAIN_TITLE_BAR_HEIGHT);
        splitRowsPanel = constructSplitRowsPanel(selectedSaveFile);
        SplitRow splitTitlesBar = constructColumnTitlesBar();

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;

        c.weighty = 0;
        c.insets = new Insets(1, 1, 0, 1);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        this.add(mainTitleBar, c);
        c.weighty = 1;
        c.gridy = 1;
        this.add(splitTitlesBar, c);
        c.weighty = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 2;
        this.add(splitRowsPanel, c);
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 3;
        this.add(mainTimerBar, c);
    }

    public void scrollDown() {
        int bottomRow = SplitterLive.getSelectedRow() + 1;
        splitRowsPanel.remove(0);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.ipadx = 10;
        c.weightx = 10;
        c.weighty = 0;
        c.gridy = bottomRow;

        splitRowsPanel.add(splitRow[bottomRow], c);
    }

    // Resets all the splits by resetting the whole form:
    public void resetSplits() {
        getContentPane().removeAll();
        resetFormComplexView();
    }

    //Count the number of columns (for setting the size of the window)
    private int countWidthUnits() {
        int widthUnits = 0;
        boolean[] displayedColumns = userPreferences.getDisplayedColumns();
        if (displayedColumns[0] == true) {
            widthUnits = 2;
        }
        for (int i = 1; i < displayedColumns.length; i++) {
            if (displayedColumns[i] == true) {
                widthUnits++;
            }
        }
        return widthUnits;
    }

    /* Creates the panel which the splits are on, containing logic which creates
     * multiple instances of the split row object.
     */
    
    private JPanel constructSplitRowsPanel(TimerSaveFile selectedSaveFile) {
        int numberOfSplitsToDisplay = userPreferences.getNumberOfRowsDisplayed();
        int numberOfSplitsTotal = selectedSaveFile.getTotalNumberOfSplits();
        System.out.print(numberOfSplitsTotal);
        splitRow = new SplitRow[numberOfSplitsTotal];
        splitRowsPanel = new JPanel();
        splitRowsPanel.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        splitRowsPanel.setForeground(this.getForeground());

        //loops through the number of splits and adds the rows to the panel
        splitRowsPanel.setLayout(new GridBagLayout());
        for (int i = 0; i < numberOfSplitsTotal; i++) {
            splitRow[i] = new SplitRow(userPreferences, WIDTH_UNIT, SPLIT_ROW_HEIGHT, JLabel.RIGHT);
            splitRow[i].setSplitName(selectedSaveFile.getSplitName(i));
            if (selectedSaveFile.getTotalNumberOfRuns() == 0) {
                splitRow[i].setSplitFromPB(SplitterLive.NULL_VALUE_STRING);
            } else {
                String splitFromPB = selectedSaveFile.getSplitsFromFastestCompletedRun()[i];
                if (SplitterLive.isInteger(splitFromPB)) {
                    int PBSplit = Integer.valueOf(splitFromPB);
                    splitRow[i].setSplitFromPB(SplitterLive.formatTime(PBSplit, SplitterLive.SHORT_FORMAT));
                } else {
                    splitRow[i].setSplitFromPB(splitFromPB);
                }

            }

        }
        c.fill = GridBagConstraints.BOTH;
        c.ipadx = 10;
        c.weightx = 10;
        c.weighty = 0;
        for (int i = 0; i < numberOfSplitsToDisplay - 1; i++) {
            c.gridy = i;
            splitRowsPanel.add(splitRow[i], c);
        }
        c.weighty = 1;
        c.anchor = GridBagConstraints.NORTH;
        c.gridy = numberOfSplitsToDisplay - 1;
        splitRowsPanel.add(splitRow[numberOfSplitsToDisplay - 1], c);

        return splitRowsPanel;
    }

    //construct the main title bar, using properties from the user preferences
    private JPanel constructMainTitleBar(int titleBarHeight) {
        Dimension preferredSize = new Dimension(WIDTH, titleBarHeight);
        JPanel titleBar = new JPanel();
        JLabel titleLabel = new JLabel();
        titleLabel.setForeground(this.getForeground());
        titleLabel.setText(selectedSaveFile.getSpeedrunTitle());
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleBar.add(titleLabel);
        titleBar.setMinimumSize(preferredSize);
        titleBar.setPreferredSize(preferredSize);
        titleBar.setBackground(userPreferences.getMainTitleBackgroundColor());
        titleBar.setVisible(true);
        return titleBar;
    }

    // sets the layout and constructs the timer bar in complex view, using user preferences
    private JPanel constructTimerBarComplex(String defaultLabelText, int timerHeight) {
        Dimension preferredSize = new Dimension(WIDTH, timerHeight);
        JPanel timerBar = new JPanel();
        mainTimerLabel = new JLabel(defaultLabelText);
        mainTimerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainTimerLabel.setForeground(this.getForeground());
        timerBar.setBackground(userPreferences.getMainTimerBackgroundColor());
        timerBar.setMinimumSize(preferredSize);
        timerBar.setPreferredSize(preferredSize);
        timerBar.add(mainTimerLabel);

        return timerBar;
    }

    // sets the layout and creates the column titles bar
    public SplitRow constructColumnTitlesBar() {
        SplitRow titlesBar = new SplitRow(userPreferences, WIDTH_UNIT, SPLIT_ROW_HEIGHT, JLabel.CENTER);
        titlesBar.setSplitName("Split Name");
        titlesBar.setCurrentSplit("Split");
        titlesBar.setSplitFromPB("PB");
        titlesBar.setDeltaSplitFromPB(SplitterLive.NULL_VALUE_STRING, "Delta PB");
        titlesBar.setBackground(userPreferences.getColumnTitlesBackgroundColor());
        titlesBar.setOpaque(true);
        return titlesBar;
    }

    // the following setters set the labels on the timer:
    public void setDeltaPB(int rowIndex, String deltaType, String valueToSet) {
        splitRow[rowIndex].setDeltaSplitFromPB(deltaType, valueToSet);
    }

    public void setSplit(int rowIndex, String valueToSet) {
        splitRow[rowIndex].setCurrentSplit(valueToSet);
    }

    public void setMainTimerLabel(String valueToSet) {
        mainTimerLabel.setText(valueToSet);
    }

    /* setter sets the main timer to either stopped or started colours, based 
     * on the user preferences file.
     */
    public void setMainTimerMode(boolean complexView, boolean timerActive) {
        if (complexView) {
            if (timerActive) {
                mainTimerLabel.setForeground(userPreferences.getActiveTimerTextColor());
            } else {
                mainTimerLabel.setForeground(userPreferences.getStoppedTimerTextColor());
            }
        } else {
            if (timerActive) {
                mainTimerLabel.setForeground(SIMPLE_VIEW_ACTIVE_TEXT);
            } else {
                mainTimerLabel.setForeground(SIMPLE_VIEW_STOPPED_TEXT);
            }
        }
    }

    // forces the slits to update and display properly
    public void forceSplitsUpdate() {
        splitRowsPanel.updateUI();
    }

    //getter for the number of split rows
    public int getNumberOfRows() {
        return splitRow.length;
    }

    //selects and highlights the next split row
    public void selectNextRow(int selectedRow) {
        setDeltaPB(selectedRow, "", SplitterLive.NULL_VALUE_STRING);
        if (selectedRow != 0) {
            splitRow[selectedRow - 1].setHighlighted(false);
        }
        splitRow[selectedRow].setHighlighted(true);
    }

    //check if the new file window is open already
    public boolean isNewFileWindowOpen() {
        if (createFileFrame.isVisible()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    //detects a key press and sends it to SplitterLive to be handled
    public void keyPressed(KeyEvent keyPressedEvent) {
        int keyPressedCode = keyPressedEvent.getKeyCode();
        SplitterLive.timerFormKeyPress(keyPressedCode);
    }

    @Override
    public void mousePressed(MouseEvent mousePressedEvent) {
        relativePositionOfCursor = mousePressedEvent.getPoint();
    }

    @Override
    public void mouseDragged(MouseEvent mouseDraggedEvent) {
        // get location of Window
        int thisX = this.getLocation().x;
        int thisY = this.getLocation().y;

        // Determine how much the mouse moved since the initial click
        int xMoved = (thisX + mouseDraggedEvent.getX()) - (thisX + relativePositionOfCursor.x);
        int yMoved = (thisY + mouseDraggedEvent.getY()) - (thisY + relativePositionOfCursor.y);

        // Move it
        int X = thisX + xMoved;
        int Y = thisY + yMoved;
        this.setLocation(X, Y);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        SplitterLive.timerFormKeyRelease(e.getKeyCode());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
