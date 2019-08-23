package splitterlive;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * the SplitRow class is the class for a JPanel-based object which
 * is used on the main timer Form.
 */
public class SplitRow extends JPanel {

    /*
     * The array 'columnLabel' holds the JLabels for each column in this object.
     * These JLabels can be changed by the setters at the bottom.
     */
    private JLabel[] columnLabel;
    private Color neutralDeltaText;
    private Color negativeDeltaText;
    private Color positiveDeltaText;
    private Color normalText;

    // the main class defines all the class variables to contain user-preference options
    public SplitRow(PreferencesFile userPreferences, int widthOfRow, int heightOfRow, int labelAlignment) {
        columnLabel = constructColumnLabels(userPreferences, new Dimension(widthOfRow, heightOfRow));
        constructSplitRow(userPreferences, heightOfRow, labelAlignment);

        neutralDeltaText = userPreferences.getNeutralDeltaTextColor();
        negativeDeltaText = userPreferences.getNegativeDeltaTextColor();
        positiveDeltaText = userPreferences.getPositiveDeltaTextColor();
        normalText = userPreferences.getMainFormTextColor();
    }

    // The labels for all the labels
    private JLabel[] constructColumnLabels(PreferencesFile userPreferences, Dimension minimumLabelSize) {
        JLabel[] columnLabels = new JLabel[SplitterLive.NUMBER_OF_COLUMNS];
        for (int i = 0; i < SplitterLive.NUMBER_OF_COLUMNS; i++) {
            columnLabels[i] = new JLabel(SplitterLive.NULL_VALUE_STRING);
            columnLabels[i].setForeground(userPreferences.getMainFormTextColor());
            columnLabels[i].setMinimumSize(minimumLabelSize);
            columnLabels[i].setPreferredSize(minimumLabelSize);
        }
        return columnLabels;
    }

    // creates the split rows, the method is all setting the layout of the form
    private void constructSplitRow(PreferencesFile userPreferences, int heightOfRow, int labelAlignment) {
        boolean[] columnIsDisplayed = userPreferences.getDisplayedColumns();
        Dimension preferredSize = new Dimension(WIDTH, heightOfRow);
        GridBagConstraints c = new GridBagConstraints();

        setBackground(userPreferences.getHighlightedRowBackgroundColor());
        setLayout(new GridBagLayout());
        setHighlighted(false);

        c.weighty = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        int columnIndex = 0;
        for (int i = 0; i < columnIsDisplayed.length; i++) {
            c.gridx = columnIndex;
            if (i == SplitterLive.SPLIT_NAME_COLUMN) {
                c.weightx = 10;
                c.gridwidth = 2;
                c.anchor = GridBagConstraints.WEST;
                columnLabel[i].setHorizontalAlignment(JLabel.LEFT);
                columnIndex++;
            } else {
                c.weightx = 1;
                c.gridwidth = 1;
                c.anchor = GridBagConstraints.EAST;
                columnLabel[i].setHorizontalAlignment(labelAlignment);
            }
            if (columnIsDisplayed[i]) {
                this.add(columnLabel[i], c);
                columnIndex++;
            }
        }
        this.setMinimumSize(preferredSize);
        this.setPreferredSize(preferredSize);
    }

    //setters for the different labels
    public void setSplitName(String splitName) {
        columnLabel[SplitterLive.SPLIT_NAME_COLUMN].setText(splitName);
    }

    public void setCurrentSplit(String currentSplit) {
        columnLabel[SplitterLive.CURRENT_SPLIT_COLUMN].setText(currentSplit);
    }

    public void setSplitFromPB(String splitFromPB) {
        columnLabel[SplitterLive.SPLIT_FROM_PB_COLUMN].setText(splitFromPB);
    }

    public void setDeltaSplitFromPB(String deltaType, String deltaSplitFromPB) {
        columnLabel[SplitterLive.DELTA_SPLIT_FROM_PB_COLUMN].setText(deltaSplitFromPB);
        switch (deltaType) {
            case SplitterLive.NULL_VALUE_STRING:
                columnLabel[SplitterLive.DELTA_SPLIT_FROM_PB_COLUMN].setForeground(normalText);
                break;
            case SplitterLive.NEUTRAL_SPLIT:
                columnLabel[SplitterLive.DELTA_SPLIT_FROM_PB_COLUMN].setForeground(neutralDeltaText);
                break;
            case SplitterLive.NEGATIVE_SPLIT:
                columnLabel[SplitterLive.DELTA_SPLIT_FROM_PB_COLUMN].setForeground(negativeDeltaText);
                break;
            case SplitterLive.POSITIVE_SPLIT:
                columnLabel[SplitterLive.DELTA_SPLIT_FROM_PB_COLUMN].setForeground(positiveDeltaText);
                columnLabel[SplitterLive.DELTA_SPLIT_FROM_PB_COLUMN].setText("+" + deltaSplitFromPB);
                break;
        }
    }
    
    // sets the row to be highlighted or not highlighted
    public void setHighlighted(boolean rowIsHighlighted) {
        setOpaque(rowIsHighlighted);
    }
}
