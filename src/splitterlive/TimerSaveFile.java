package splitterlive;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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

public class TimerSaveFile {

    private String speedrunTitle = "SPEEDRUN";
    private File timerSaveFile;
    private int numberOfRuns = 0;//5
    private int numberOfSplits = 0;//5
    private String[] splitNames = new String[numberOfSplits];
    private String[][] splitsFromAllRuns = new String[numberOfRuns][numberOfSplits];
    private static String TITLE_ATTRIBUTE = "Title";

    // The main method will read the selected file using the readSaveFile method
    public TimerSaveFile(File file) {
        readSaveFile(file);
    }

    //method saves speedrun data to an existing file
    public void saveToFile(String[] splits) {
        try {
            //find the file
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(timerSaveFile);

            //find the elments within the xml file:
            Element rootElement = doc.getDocumentElement();
            NodeList rootList = rootElement.getChildNodes();
            Node splitsNode = rootList.item(1);

            //add the new speedrun
            Element run = doc.createElement("Run" + numberOfRuns);
            splitsNode.appendChild(run);

            //create all the split elements
            for (int i = 0; i < numberOfSplits; i++) {
                Element split = doc.createElement("Split" + i);
                split.appendChild(doc.createTextNode(splits[i]));
                run.appendChild(split);
            }

            // write the file content into an xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(timerSaveFile);
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

    // method reads the selected save file
    public void readSaveFile(File saveFile) {
        try {
            timerSaveFile = saveFile;
            int SPLIT_NAMES = 0;
            int SPLIT_TIMES = 1;

            // Find the save file.
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(timerSaveFile);

            // Normalize text representation
            doc.getDocumentElement().normalize();
            Node mainElement = doc.getDocumentElement();
            NodeList rootElements = mainElement.getChildNodes();

            speedrunTitle = mainElement.getAttributes().getNamedItem(TITLE_ATTRIBUTE).getNodeValue();

            // loading split names
            Node splitNameGroup = rootElements.item(SPLIT_NAMES);
            NodeList splitNameList = splitNameGroup.getChildNodes();
            splitNames = new String[splitNameList.getLength()];
            numberOfSplits = splitNameList.getLength();


            //read each field in the file and put it into the fieldValue array
            for (int i = 0; i < numberOfSplits; i++) {
                if (splitNameList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    splitNames[i] = splitNameList.item(i).getTextContent();
                }//end of if clause
            }//end of for loop with i var

            //loading split times
            Node splitTimesGroup = rootElements.item(SPLIT_TIMES);
            NodeList splitTimesList = splitTimesGroup.getChildNodes();
            if (splitTimesList.getLength() != 0) {
                numberOfRuns = splitTimesList.getLength();
                splitsFromAllRuns = new String[numberOfRuns][numberOfSplits];

                //read each field in the file and put it into the splitsFromAllRuns array
                for (int i = 0; i < numberOfRuns; i++) {
                    Node run = splitTimesList.item(i);
                    NodeList splitsList = run.getChildNodes();

                    for (int x = 0; x < numberOfSplits; x++) {

                        splitsFromAllRuns[i][x] = splitsList.item(x).getTextContent();

                    }//end of for loop with x var
                }//end of for loop with i var
            }

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

    // Creates a new save file for the timer.
    public static File createSaveFile(String[] listOfSplitNames, String speedrunTitle) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // create the document and root element
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("SaveFile");
            rootElement.setAttribute(TITLE_ATTRIBUTE, speedrunTitle);
            doc.appendChild(rootElement);

            Element splitNamesElement = doc.createElement("SplitNames");
            rootElement.appendChild(splitNamesElement);

            Element splitTimesElement = doc.createElement("Times");
            rootElement.appendChild(splitTimesElement);

            // create and add the preferences elements to the root element
            Element[] splitName = new Element[listOfSplitNames.length];

            for (int i = 0; i < listOfSplitNames.length; i++) {
                splitName[i] = doc.createElement("Split" + i);
                splitName[i].appendChild(doc.createTextNode(listOfSplitNames[i]));
                splitNamesElement.appendChild(splitName[i]);
            }

            // write the file content into an xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            File saveFile = new File(speedrunTitle + ".SPT");
            StreamResult result = new StreamResult(saveFile);
            transformer.transform(source, result);

            SplitterLive.setCurrentFile(saveFile);
            return saveFile;
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
        return null;
    }

    //counts the number of completed speedruns.
    public int countCompletedRuns() {
        int completedRunsCount = 0;
        for (int i = 0; i < numberOfRuns; i++) {
            String selectedCellValue = splitsFromAllRuns[i][numberOfSplits - 1];

            if (!selectedCellValue.startsWith(SplitterLive.NULL_VALUE_STRING)) {
                completedRunsCount++;
            }
        }
        return completedRunsCount;
    }

    /* 
     * Method takes the final split of every found speedrun and returns it as an
     * int array.
     */
    public int[] getFinalTimesOfCompletedRuns() {
        int[] completedRuns = new int[countCompletedRuns()];
        int completedRunsCount = 0;
        for (int i = 0; i < numberOfRuns; i++) {
            String selectedCellValue = splitsFromAllRuns[i][numberOfSplits - 1];
            if (!selectedCellValue.startsWith(SplitterLive.NULL_VALUE_STRING)) {
                completedRuns[completedRunsCount] = Integer.valueOf(selectedCellValue);
                completedRunsCount++;
            }
        }
        return completedRuns;
    }

    /* method calculates the fastest completed runs by cycling through the
     * final times and keeping track of the fastest.
     */
    private int getIndexOfFastestCompletedRun() {
        int[] completedRuns = getFinalTimesOfCompletedRuns();
        int currentFastestTimeFound = completedRuns[0];
        int indexOfPBRun = 0;
        for (int i = 0; i < completedRuns.length; i++) {
            if (completedRuns[i] < currentFastestTimeFound) {
                currentFastestTimeFound = completedRuns[i];
                indexOfPBRun = i;
            }
        }
        return indexOfPBRun;
    }

    // The rest of the simple getters for the class:
    public String[] getSplitsFromFastestCompletedRun() {
        return splitsFromAllRuns[getIndexOfFastestCompletedRun()];
    }

    public String getSplitName(int splitIndex) {
        return splitNames[splitIndex];
    }

    public String getSpeedrunTitle() {
        return speedrunTitle;
    }

    public String[][] getSplitsFromAllRuns() {
        return splitsFromAllRuns;
    }

    public int getTotalNumberOfSplits() {
        return numberOfSplits;
    }

    public int getTotalNumberOfRuns() {
        return numberOfRuns;
    }
}
