package splitterlive;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import javax.swing.WindowConstants;



/*
 * The LiveRunSplit class is the main class of the program and
 * is where the majority of the calculations for the program
 * run. The other classes are called from here.
 */
public class SplitterLive {

    static final int NUMBER_OF_PREFERENCE_OPTIONS = 19;
    static final int NUMBER_OF_COLUMNS = 6;
    static final int SPLIT_NAME_COLUMN = 0;
    static final int CURRENT_SPLIT_COLUMN = 1;
    static final int SPLIT_FROM_PB_COLUMN = 2;
    static final int DELTA_SPLIT_FROM_PB_COLUMN = 3;
    static final int TIMER_ITERATION_PERIOD_MS = 11;
    static final Dimension CREATE_FILE_WINDOW_SIZE = new Dimension(400, 90);
    static final String NULL_VALUE_STRING = "--";
    static final String NEUTRAL_SPLIT = "NEUTRAL";
    static final String NEGATIVE_SPLIT = "NEGATIVE";
    static final String POSITIVE_SPLIT = "POSITIVE";
    static final String DEFAULT_TIMER_TEXT = "0:00:00.000";
    static final int CTRL_KEYCODE = 17;
    static final int NEW_KEYCODE = 78;//N key
    static final int OPEN_KEYCODE = 79;//O key
    static final int SETTINGS_KEYCODE = 68;//D key
    static final int STATISTICS_KEYCODE = 83;//S key
    static final int START_STOP_DEFAULT_KEYCODE = 88;//X key
    static final int RESET_DEFAULT_KEYCODE = 81;//Q key
    static final int LONG_FORMAT = 0;
    static final int SHORT_FORMAT = 1;
    private static boolean ctrlIsPressed = false;
    private static boolean isInComplexView = false;
    private static boolean timerIsRunning = false;
    private static boolean runIsUp = false;
    private static String[] thisRun;
    private static int selectedRow = 0;
    private static int timeStartedAt = 0;
    private static File currentFile;
    private static TimerForm mainForm;
    private static TimerSaveFile selectedSaveFile;
    private static PreferencesFile userPreferences = new PreferencesFile();

    
    /* 
     * main method starts a new thread in the TimerForm class, and then
     * constructs the main form, sets the program to end when the window
     * closes and finally it visible.
     */
    public static void main(String[] args) {
        (new Thread(new TimerObject())).start();
        mainForm = new TimerForm();
        mainForm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainForm.setVisible(true);
    }

    /* 
     * timerFormKeyPress is the first method which handles the key
     * presses in the program. The if clause checks for what kind
     * of key is pressed, and if the control key is pressed and then
     * sends the keypress data to the matching method to be dealt with.
     */
    public static void timerFormKeyPress(int keyPressedKeycode) {
        if (ctrlIsPressed) {
            keyPressCtrlHeld(keyPressedKeycode);
        }
        if (keyPressedKeycode == CTRL_KEYCODE) {
            ctrlIsPressed = true;
        } else if (isInComplexView) {
            keyPressComplexView(keyPressedKeycode);
        } else {
            keyPressSimpleView(keyPressedKeycode);
        }
    }

    public static void resetEverything(){
        Point loc = mainForm.getLocation();
        mainForm.dispose();
        //mainForm.dispatchEvent(new WindowEvent(mainForm, WindowEvent.WINDOW_CLOSING));
        mainForm = new TimerForm();
        mainForm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainForm.setLocation(loc);
        mainForm.setVisible(true);
    }
    /* 
     * method is used to reset the selected save file of the class
     * to the save file from the arguments
     */
    
    public static void setThisSave(TimerSaveFile saveFile) {
        selectedSaveFile = saveFile;
        thisRun = new String[selectedSaveFile.getTotalNumberOfSplits()];
    }

    /* 
     * handles the logic for when a key is released, if the key that is
     * released is the control key, then the class boolean 'ctrlIsPressed'
     * is set to false.
     */
    public static void timerFormKeyRelease(int keyReleasedKeycode) {
        if (keyReleasedKeycode == CTRL_KEYCODE) {
            ctrlIsPressed = false;
        }
    }

    /*
     * method handles the events which occour when a key is pressed in
     * the timer's simple view state. If the key pressed is either the
     * universal keycode for the start/stop function or the universal
     * keycode for the reset function, the respective methods are called.
     */
    private static void keyPressSimpleView(int keyPressedKeycode) {
        if (keyPressedKeycode == START_STOP_DEFAULT_KEYCODE) {
            startStopSplit();
        } else if (keyPressedKeycode == RESET_DEFAULT_KEYCODE) {
            reset();
        }
    }

    /* 
     * The following method handles all the logic for a key press on the main
     * form in complex view. Depending on the key and the user-set hotkey for
     * each function located in the userPreferences object, the if structure
     * will call the different functions of the timer, stop/start/spit, reset
     * and skip ahead a split.
     */
    private static void keyPressComplexView(int keyPressedKeycode) {
        if (keyPressedKeycode == userPreferences.getStartStopSplitKeyCode()) {
            startStopSplit();
        } else if (keyPressedKeycode == userPreferences.getResetKeyCode()) {
            reset();
        } else if (keyPressedKeycode == userPreferences.getGoForwardsKeyCode()) {
            goForwards();
        }
    }

    /* 
     * The startStopSplit method handles how to advance the timer. It will
     * perform different actions based on whether the timer is in complex
     * or simple view and whether the timer is running or not.
     */
    private static void startStopSplit() {
        //Section handles if it's running, both in complex and simple view:
        if (timerIsRunning && isInComplexView) {
            hitSplit();
        } else if (timerIsRunning && !isInComplexView) {
            setRunning(false);
        //Section handles if it's not running, both in complex and simple view:    
        } else if (!timerIsRunning && isInComplexView) {
            //Checks whether the state is just ended or before starting the run
            if (runIsUp) {
                runIsUp = false;
                resetEverything();
            } else {
                setRunning(true);
                runIsUp = true;
                mainForm.selectNextRow(selectedRow);
                selectedRow++;
            }
        } else if (!timerIsRunning && !isInComplexView) {
            setRunning(true);
        }
    }

    /* 
     * reset method resets all the elements on the form and all the relevant
     * class variables to be how they are when the timer is not running.
     */
    private static void reset() {
        setRunning(false);
        mainForm.setMainTimerLabel(DEFAULT_TIMER_TEXT);
        mainForm.setMainTimerMode(isInComplexView, true);

        if (isInComplexView) {
            selectedSaveFile = new TimerSaveFile(currentFile);
            mainForm.resetSplits();
            runIsUp = false;
            selectedRow = 0;
            mainForm.forceSplitsUpdate();
        }
    }

    /*
     * Checks that the timer is able to advance any more splits, and if so,
     * advances the selected row on the main timer.
     */
    private static void goForwards() {
        if (timerIsRunning && isInComplexView) {
            if (selectedRow < mainForm.getNumberOfRows()) {
                thisRun[selectedRow - 1] = NULL_VALUE_STRING;
                checkRows();
                mainForm.selectNextRow(selectedRow);
                selectedRow++;
            }
        }
        mainForm.forceSplitsUpdate();
    }

    /*
     * The hitSplit class is called when the user presses the split key
     * while the itmer is running. It's purpose is to advance the selected
     * split of the timer and calculate and display the information about
     * that split
     */
    private static void hitSplit() {
        //section handles the split field:
        /*
         * the split time is calculated and turned into a string format using
         * the formatTime method, and then displayed on the split field
         */ 
        int splitTime = (int) System.currentTimeMillis() - timeStartedAt;
        String splitTimeFormatted = formatTime(splitTime, SHORT_FORMAT);
        mainForm.setSplit(selectedRow - 1, splitTimeFormatted);
        //section handles the delta PB field (only checks if previous runs exist):
        if (selectedSaveFile.getTotalNumberOfRuns() != 0) {
            //providing the PB split isn't null, calculate the delta PB time, including type
            if (!selectedSaveFile.getSplitsFromFastestCompletedRun()[selectedRow - 1].equals(SplitterLive.NULL_VALUE_STRING)) {
                int pBTime = Integer.valueOf(selectedSaveFile.getSplitsFromFastestCompletedRun()[selectedRow - 1]);
                int deltaPB = splitTime - pBTime;
                String deltaPBFormatted = formatTime(deltaPB, SHORT_FORMAT);
                String deltaType;
                if (deltaPB < 0) {
                    deltaType = NEGATIVE_SPLIT;
                } else if (deltaPB > 0) {
                    deltaType = POSITIVE_SPLIT;
                } else {
                    deltaType = NEUTRAL_SPLIT;
                }
                //set the PB field to display the formatted comparative time to the split
                mainForm.setDeltaPB(selectedRow - 1, deltaType, deltaPBFormatted);
            }
        }
        //If on the last split, the timer should stop running
        thisRun[selectedRow - 1] = String.valueOf(splitTime);
        if (selectedRow < mainForm.getNumberOfRows()) {
            checkRows();
            mainForm.selectNextRow(selectedRow);
            selectedRow++;
        } else {
            setRunning(false);
            selectedRow = 0;
            selectedSaveFile.saveToFile(thisRun);
        }
        //force the interface to visually update
        mainForm.forceSplitsUpdate();
    }

    /*
     * method is used to check if the timer should scroll down upon splitting
     * or moving forwards one split, based off how close the selected row is
     * to the bottom of the window.
     */
    private static void checkRows() {
        if (selectedRow > (userPreferences.getNumberOfRowsDisplayed() - 2)
                && selectedRow < (selectedSaveFile.getTotalNumberOfSplits() - 1)) {
            userPreferences.getNumberOfRowsDisplayed();
            getSelectedRow();
            mainForm.scrollDown();
        }
    }
    /*
     * sets the timer running or stops the timer, depending on the
     * input boolean 'setRunning'.
     */
    private static void setRunning(boolean setRunning) {
        if (!timerIsRunning && setRunning) {
            timerRun();
            mainForm.setMainTimerMode(isInComplexView, timerIsRunning);
            if (isInComplexView) {
                mainForm.forceSplitsUpdate();
            }
        } else if (timerIsRunning && !setRunning) {
            timerStop();
            mainForm.setMainTimerMode(isInComplexView, timerIsRunning);
        }
    }

    /*
     * Turns on the timer and keeps track of the system time when the timer
     * was started.
     */
    private static void timerRun() {
        timerIsRunning = true;
        timeStartedAt = (int) System.currentTimeMillis();
        TimerObject.setRunning(true);

    }

    /*
     * Stops the timer and freezes the timer label
     */
    private static void timerStop() {
        timerIsRunning = false;
        setTimerLabel();
    }

    // a series of getters for the class variables:
    public static boolean getTimerIsRunning() {
        return timerIsRunning;
    }

    public static boolean getIsInComplexView() {
        return isInComplexView;
    }

    public static int getSelectedRow() {
        return selectedRow;
    }

    public static File getCurrentFile() {
        return currentFile;
    }
    /*
     * This method runs when control is held and a key is pressed. It's
     * purpose is to perform different functions based on what hotkey is
     * pressed, either openning the 'new file' dialogue, the 'open' prompt,
     * the 'settings' form or the 'statistics' form
     */
    private static void keyPressCtrlHeld(int keyPressedKeycode) {
        switch (keyPressedKeycode) {
            case NEW_KEYCODE:
                setCtrlIsPressed(false);
                mainForm.createNewTimerFile();
                break;
            case OPEN_KEYCODE:
                setCtrlIsPressed(false);
                mainForm.openFile();
                reset();
                break;
            case SETTINGS_KEYCODE:
                setCtrlIsPressed(false);
                if (isInComplexView) {
                    PreferencesForm.createPreferencesForm();
                    userPreferences = new PreferencesFile();
                }
                break;
            case STATISTICS_KEYCODE:
                if (isInComplexView && selectedSaveFile.countCompletedRuns() != 0) {
                    setCtrlIsPressed(false);
                    int lastSplitIndex = selectedSaveFile.getSplitsFromFastestCompletedRun().length - 1;
                    int fastestEndTime = Integer.valueOf(selectedSaveFile.getSplitsFromFastestCompletedRun()[lastSplitIndex]);
                    StatisticsForm.constructStatisticsForm(selectedSaveFile.getFinalTimesOfCompletedRuns(), selectedSaveFile.getTotalNumberOfRuns() - selectedSaveFile.countCompletedRuns(), selectedSaveFile.countCompletedRuns(), formatTime(fastestEndTime, LONG_FORMAT));
                }
                break;
            default:
        }

    }

    /*
     * Called by the Timer object every tick to update the main timer at the
     * bottom's label
     */
    public static void onTimerTick() {
        if (timerIsRunning) {
            setTimerLabel();
        }
    }
    
    /*
     * Sets the main timer's label
     */
    private static void setTimerLabel() {
        int timeDifference = (int) System.currentTimeMillis() - timeStartedAt;
        mainForm.setMainTimerLabel(formatTime(timeDifference, LONG_FORMAT));
    }

    /*
     * This string method is used in different places in the program to format
     * an integer number of milliseconds into a time format, either in long or
     * short format.
     */
    public static String formatTime(int inputTimeMillis, int timeFormat) {
        //if the integer is less than 0, prepare a negative sign
        String numberSign = "";
        if (inputTimeMillis < 0) {
            numberSign = "-";
            inputTimeMillis = -inputTimeMillis;
        }

        //calculate the different segments of the time
        int milli = inputTimeMillis % 1000;
        int second = (inputTimeMillis / 1000) % 60;
        int minute = (inputTimeMillis / (1000 * 60)) % 60;
        int hour = (inputTimeMillis / (1000 * 60 * 60)) % 1000;

        //change the number of decimal places on the segments to be consistent
        String hoursSegment = String.format("%01d", hour);
        String minutesSegment = String.format("%02d", minute);
        String secondsSegment = String.format("%02d", second);
        String milliSecondsSegment = String.format("%03d", milli);

        //if needed in short format, put the numbers together without milliseconds
        String returnString = "";
        if (timeFormat == SHORT_FORMAT) {
            if (inputTimeMillis < 1000 * 60) {
                returnString = numberSign + "0:" + secondsSegment;
            } else if (inputTimeMillis < 1000 * 60 * 60) {
                minutesSegment = String.format("%01d", minute);
                returnString = numberSign + minutesSegment + ":" + secondsSegment;
            } else {
                returnString = numberSign + hoursSegment + ":" + minutesSegment + ":" + secondsSegment;
            }
        //if needed in long format, put the numbers together with milliseconds
        } else if (timeFormat == LONG_FORMAT) {
            returnString = numberSign + hoursSegment + ":" + minutesSegment + ":" + secondsSegment + "." + milliSecondsSegment;
        }

        return returnString;
    }

    /* 
     * function is used throughout the program to check if the input string
     * is convertable into an integer or not.
     */
    
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    // A series of setters for the class:
    public static void setComplexView(boolean setComplexView) {
        isInComplexView = setComplexView;
    }

    public static void setCurrentFile(File file) {
        currentFile = file;
        mainForm.getContentPane().removeAll();
        mainForm.dispose();
    }    

    public static void setCtrlIsPressed(Boolean pressed) {
        ctrlIsPressed = pressed;
    }
}
