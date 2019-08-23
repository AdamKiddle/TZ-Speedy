package splitterlive;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class PreferencesFile {

    /*
     * the PreferencesFile object reads from an xml file and stores
     * a set of variables which are used by the TimerForm object. If
     * no xml file is found and the user does not wish to create one
     * then the default values will be used.
     */
    // Constants in the class
    private final int NUMBER_OF_COLUMNS_TOTAL = 4;
    private final int FIELD_NAME = 0;
    private final int FIELD_VALUE = 1;
    private final String PREFERENCES_FILE_NAME = "preferences.xml";
    //Array containing the names for all the stored variables and the default values of each.
    /*
     * Names ending in "Background" or "Text" store hexadecimal values representing
     * colours. The rest consist of 1 integer and 6 booleans
     */

    //This array holds all the default string values for the preferences file. These are used if no xml file is found.
    private final String[][] DEFAULT_PREFERENCES_FILE_CONTENT = new String[][]{
        {
            "mainFormBackground",
            "mainFormText",
            "mainTitleBackground",
            "columnTitlesBackground",
            "highlightedRowBackground",
            "negativeDeltaText",
            "positiveDeltaText",
            "neutralDeltaText",
            "mainTimerBackground",
            "activeTimerText",
            "stoppedTimerText",
            "numberOfRowsDisplayed",
            "displaySplitNameColumn",
            "displaySplitColumn",
            "displayPBColumn",
            "displayDeltaPBColumn",
            "startStopSplitKeyCode",
            "resetKeyCode",
            "goForwardsKeyCode"
        }, {
            "#000000",
            "#FFFFFF",
            "#404040",
            "#252525",
            "#200000",
            "#005500",
            "#550000",
            "#000055",
            "#101010",
            "#FFFFFF",
            "#00FFFF",
            "3",
            "true",
            "true",
            "true",
            "true",
            "88",//X key
            "81",//Q key
            "75" //K key
        }
    };
    /*
     * the following variables are given values by the methods in the class
     * and are the main variables which are read from by the TimerForm object.
     */
    private Color mainFormBackground;
    private Color mainFormText;
    private Color mainTitleBackground;
    private Color columnTitlesBackground;
    private Color highlightedRowBackground;
    private Color negativeDeltaText;
    private Color positiveDeltaText;
    private Color neutralDeltaText;
    private Color mainTimerBackground;
    private Color activeTimerText;
    private Color stoppedTimerText;
    private int numberOfRowsDisplayed;
    private boolean displayColumn[] = new boolean[NUMBER_OF_COLUMNS_TOTAL];
    private int startStopSplitKeyCode;
    private int resetKeyCode;
    private int goForwardsKeyCode;

    /*
     * The main method of the class checks for a peferences file in the program's
     * root directory. If there is, then this file is read by the
     * readPreferencesFile() method. If there isn't, the user is asked in the
     * askUserToCreateFile() method whether they want to create one.
     */
    public PreferencesFile() {
        File preferencesFile = new File(PREFERENCES_FILE_NAME);
        if (preferencesFile.exists()) {
            readPreferencesFile();
        } else {
            askUserToCreateFile();
        }
    }

    /*
     * The user is asked if they would like to create a new file by bringing up
     * a message box with yes/no options. Selecting 'yes' creates the
     * preferences form. Selecting 'no' passes the default values to the
     * fillPreferenceVariables() method.
     */
    private void askUserToCreateFile() {
        int YES = 0;
        int newFileOption = JOptionPane.showOptionDialog(new JFrame(),
                "Preferences file " + PREFERENCES_FILE_NAME + " not found, create a new file?",
                "SplitterLive",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, null, 1);

        if (newFileOption == YES) {
            createPreferencesFile(DEFAULT_PREFERENCES_FILE_CONTENT[FIELD_VALUE]);
            readPreferencesFile();
        } else {
            fillPreferenceVariables(DEFAULT_PREFERENCES_FILE_CONTENT[FIELD_VALUE]);
        }
    }

    /*
     * This method takes a string array and fills the class variables with
     * the values, converting each string to the correct datatype for the
     * variable.
     */
    private void fillPreferenceVariables(String[] value) {
        mainFormBackground = Color.decode(value[0]);
        mainFormText = Color.decode(value[1]);
        mainTitleBackground = Color.decode(value[2]);
        columnTitlesBackground = Color.decode(value[3]);
        highlightedRowBackground = Color.decode(value[4]);
        negativeDeltaText = Color.decode(value[5]);
        positiveDeltaText = Color.decode(value[6]);
        neutralDeltaText = Color.decode(value[7]);
        mainTimerBackground = Color.decode(value[8]);
        activeTimerText = Color.decode(value[9]);
        stoppedTimerText = Color.decode(value[10]);
        numberOfRowsDisplayed = Integer.valueOf(value[11]);
        displayColumn[0] = Boolean.valueOf(value[12]);
        displayColumn[1] = Boolean.valueOf(value[13]);
        displayColumn[2] = Boolean.valueOf(value[14]);
        displayColumn[3] = Boolean.valueOf(value[15]);
        startStopSplitKeyCode = Integer.valueOf(value[16]);
        resetKeyCode = Integer.valueOf(value[17]);
        goForwardsKeyCode = Integer.valueOf(value[18]);
    }

    /*
     * Reads the XML file and stores the field values into an array. It then
     * calls the fillPreferenceVariables() method with the array.
     */
    private void readPreferencesFile() {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(PREFERENCES_FILE_NAME));
            String fieldValue[] = new String[SplitterLive.NUMBER_OF_PREFERENCE_OPTIONS];

            // normalize text representation
            doc.getDocumentElement().normalize();
            Node mainElement = doc.getDocumentElement();
            NodeList preferences = mainElement.getChildNodes();

            //read each field in the file and put it into the fieldValue array
            for (int i = 0; i < preferences.getLength(); i++) {
                if (preferences.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    fieldValue[i] = preferences.item(i).getTextContent();
                }//end of if clause
            }//end of for loop with i var
            fillPreferenceVariables(fieldValue);

            //various exceptions must be caught:
        } catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line "
                    + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());

        } catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /*
     * A new preferences file is created using the values from the input
     * string.
     */
    private void createPreferencesFile(String[] value) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // create the document and root element
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("PreferencesFile");
            doc.appendChild(rootElement);

            // create and add the preferences elements to the root element
            Element[] preferenceOption = new Element[SplitterLive.NUMBER_OF_PREFERENCE_OPTIONS];
            for (int i = 0; i < SplitterLive.NUMBER_OF_PREFERENCE_OPTIONS; i++) {
                preferenceOption[i] = doc.createElement(DEFAULT_PREFERENCES_FILE_CONTENT[FIELD_NAME][i]);
                preferenceOption[i].appendChild(doc.createTextNode(value[i]));
                rootElement.appendChild(preferenceOption[i]);
            }

            // write the file content into an xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(PREFERENCES_FILE_NAME));
            transformer.transform(source, result);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    //all the getters for each class variable:
    public Color getMainFormBackgroundColor() {
        return mainFormBackground;
    }

    public Color getMainFormTextColor() {
        return mainFormText;
    }

    public Color getMainTitleBackgroundColor() {
        return mainTitleBackground;
    }

    public Color getColumnTitlesBackgroundColor() {
        return columnTitlesBackground;
    }

    public Color getHighlightedRowBackgroundColor() {
        return highlightedRowBackground;
    }

    public Color getNegativeDeltaTextColor() {
        return negativeDeltaText;
    }

    public Color getPositiveDeltaTextColor() {
        return positiveDeltaText;
    }

    public Color getNeutralDeltaTextColor() {
        return neutralDeltaText;
    }

    public Color getMainTimerBackgroundColor() {
        return mainTimerBackground;
    }

    public Color getActiveTimerTextColor() {
        return activeTimerText;
    }

    public Color getStoppedTimerTextColor() {
        return stoppedTimerText;
    }

    public int getNumberOfRowsDisplayed() {
        return numberOfRowsDisplayed;
    }

    public boolean[] getDisplayedColumns() {
        return displayColumn;
    }

    public int getStartStopSplitKeyCode() {
        return startStopSplitKeyCode;
    }

    public int getResetKeyCode() {
        return resetKeyCode;
    }

    public int getGoForwardsKeyCode() {
        return goForwardsKeyCode;
    }

    /* 
     * Method saves a string of preference properties into the preferences xml
     * file.
     */
    public void savePreferencesToFile(String[] preferences){
        try {

            //Find the document:
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(PREFERENCES_FILE_NAME);

            //Create the elements and hierachy within the xml file:
            Element rootElement = doc.getDocumentElement();
            NodeList rootList = rootElement.getChildNodes();
            for (int i = 0; i<SplitterLive.NUMBER_OF_PREFERENCE_OPTIONS;i++){
                rootList.item(i).removeChild(rootList.item(i).getFirstChild());
                rootList.item(i).appendChild(doc.createTextNode(preferences[i]));
            }
            // write the file content into an xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(PREFERENCES_FILE_NAME);
            transformer.transform(source, result);

        } catch (TransformerException ex) {
            Logger.getLogger(TimerSaveFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(TimerSaveFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TimerSaveFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(TimerSaveFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
