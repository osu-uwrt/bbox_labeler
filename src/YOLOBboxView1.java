import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.NumberFormatter;

/**
 * View class.
 *
 * @author Derek Opdycke
 */
@SuppressWarnings("serial")
public final class YOLOBboxView1 extends JFrame implements YOLOBboxView {

    /**
     * Controller object.
     */
    private YOLOBboxController controller;

    /**
     * GUI widgets that need to be in scope in actionPerformed method, and
     * related constants.
     */
    private static final int ROWS_IN_BUTTON_PANEL_GRID = 7,
            COLUMNS_IN_BUTTON_PANEL_GRID = 1, ROWS_IN_THIS_GRID = 1,
            COLUMNS_IN_THIS_GRID = 2, COLUMNS_IN_FRAME_CONTROL_PANEL_GRID = 3,
            ROWS_IN_FRAME_CONTROL_PANEL_GRID = 2,
            COLUMNS_IN_VIDEO_PANEL_GRID = 1, ROWS_IN_VIDEO_PANEL_GRID = 2,
            DEFAULT_WIDTH_OF_WINDOW = 800, DEFAULT_HEIGHT_OF_WINDOW = 600;

    /**
     * Text areas.
     */
    private final JTextArea videoLocationText, exportLocationText;
    private final JFormattedTextField itemIndexText, numberOfFramesText;

    /**
     * Buttons.
     */
    private final JButton browseVideoLocationButton, browseExportLocationButton,
            exportButton, resetButton, framesBackButton, framesForwardButton;

    /**
     * Labels
     */
    private final JLabel framesLabel, frameNumberLabel, imageLabel;

    /**
     * No-argument constructor.
     */
    public YOLOBboxView1() {
        // Create the JFrame being extended

        /*
         * Call the JFrame (superclass) constructor with a String parameter to
         * name the window in its title bar
         */
        super("YOLO Bbox");

        // Set up the GUI widgets --------------------------------------------

        /*
         * Create widgets
         */
        this.videoLocationText = new JTextArea("");
        this.exportLocationText = new JTextArea("");
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        this.itemIndexText = new JFormattedTextField(formatter);
        this.numberOfFramesText = new JFormattedTextField(formatter);
        this.numberOfFramesText.setText("1");
        this.browseVideoLocationButton = new JButton("Browse for video");
        this.browseExportLocationButton = new JButton(
                "Browse for export folder");
        this.exportButton = new JButton("Export");
        this.resetButton = new JButton("Reset");
        this.framesBackButton = new JButton("Back");
        this.framesForwardButton = new JButton("Forward");
        this.framesLabel = new JLabel("Number of frames:");
        this.frameNumberLabel = new JLabel("Current frame:");
        this.imageLabel = new JLabel();

        /*
         * Text areas
         */
        this.videoLocationText.setEditable(true);
        this.videoLocationText.setLineWrap(true);
        this.videoLocationText.setAutoscrolls(true);
        this.exportLocationText.setEditable(false);
        this.exportLocationText.setLineWrap(true);
        this.exportLocationText.setAutoscrolls(true);
        this.itemIndexText.setEditable(true);
        this.numberOfFramesText.setEditable(false);

        /*
         * Create a button panel organized using grid layout
         */
        JPanel buttonPanel = new JPanel(new GridLayout(
                ROWS_IN_BUTTON_PANEL_GRID, COLUMNS_IN_BUTTON_PANEL_GRID));

        /*
         * Create a button panel organized using grid layout
         */
        JPanel frameControlPanel = new JPanel(
                new GridLayout(ROWS_IN_FRAME_CONTROL_PANEL_GRID,
                        COLUMNS_IN_FRAME_CONTROL_PANEL_GRID));

        /*
         * Create a button panel organized using grid layout
         */
        JPanel videoPanel = new JPanel(new GridLayout(ROWS_IN_VIDEO_PANEL_GRID,
                COLUMNS_IN_VIDEO_PANEL_GRID));
        /*
         * Add the buttons to the button panel, from left to right and top to
         * bottom
         */
        buttonPanel.add(this.browseVideoLocationButton);
        buttonPanel.add(this.videoLocationText);
        buttonPanel.add(this.browseExportLocationButton);
        buttonPanel.add(this.exportLocationText);
        buttonPanel.add(this.exportButton);
        buttonPanel.add(this.itemIndexText);
        buttonPanel.add(this.resetButton);
        /*
         * Add the buttons to the frame control panel, from left to right and
         * top to bottom
         */
        frameControlPanel.add(new JLabel());
        frameControlPanel.add(this.framesLabel);
        frameControlPanel.add(new JLabel());
        frameControlPanel.add(this.framesBackButton);
        frameControlPanel.add(this.numberOfFramesText);
        frameControlPanel.add(this.framesForwardButton);

        /*
         * Add the video label and frameControlPanel to the video panel
         */
        videoPanel.add(this.imageLabel);
        videoPanel.add(frameControlPanel);
        /*
         * Organize main window using grid layout
         */
        this.setLayout(new GridLayout(ROWS_IN_THIS_GRID, COLUMNS_IN_THIS_GRID));
        /*
         * Add scroll panes and button panel to main window, from left to right
         * and top to bottom
         */
        this.add(buttonPanel);
        this.add(videoPanel);

        // Set up the observers ----------------------------------------------

        /*
         * Register this object as the observer for all GUI events
         */
        this.resetButton.addActionListener(this);
        this.browseVideoLocationButton.addActionListener(this);
        this.browseExportLocationButton.addActionListener(this);
        this.exportButton.addActionListener(this);
        this.framesBackButton.addActionListener(this);
        this.framesForwardButton.addActionListener(this);

        // Start the main application window --------------------------------

        /*
         * Make sure the main window is appropriately sized for the widgets in
         * it, that it exits this program when closed, and that it becomes
         * visible to the user now
         */
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(30, 30, DEFAULT_WIDTH_OF_WINDOW,
                DEFAULT_HEIGHT_OF_WINDOW);
        this.setVisible(true);
    }

    /**
     * Register argument as observer/listener of this; this must be done first,
     * before any other methods of this class are called.
     *
     * @param controller
     *            controller to register
     */
    @Override
    public void registerObserver(YOLOBboxController controller) {
        this.controller = controller;
    }

    /**
     * Updates video location display based on String provided as argument.
     *
     * @param s
     *            new value of video location display
     */
    @Override
    public void updateVideoLocationTextDisplay(String s) {
        this.videoLocationText.setText(s);
    }

    /**
     * Updates export location display based on String provided as argument.
     *
     * @param s
     *            new value of export location display
     */
    @Override
    public void updateExportLocationTextDisplay(String s) {
        this.exportLocationText.setText(s);
    }

    /**
     * Updates item display based on integer provided as argument.
     *
     * @param i
     *            b new value of item index display
     */
    @Override
    public void updateItemIndexTextDisplay(int i) {
        this.itemIndexText.setText(String.valueOf(i));
    }

    /**
     * Updates total frames display based on integer provided as argument.
     *
     * @param i
     *            new value of total frames display
     */
    @Override
    public void updateTotalFramesTextDisplay(int i) {
        //display does not currently show total frames
    }

    /**
     * Updates current display based on integer provided as argument.
     *
     * @param i
     *            new value of current frame display
     */
    @Override
    public void updateCurrentFrameTextDisplay(int i) {
        this.frameNumberLabel.setText(String.valueOf(i));
    }

    /**
     * Updates frame rate display based on integer provided as argument.
     *
     * @param i
     *            new value of frame rate display
     */
    @Override
    public void updateFrameRateTextDisplay(int i) {
        //display does not currently show frame rate
    }

    /**
     * Updates frame jump display based on integer provided as argument.
     *
     * @param i
     *            new value of frame jump display
     */
    @Override
    public void updateFrameJumpTextDisplay(int i) {
        this.numberOfFramesText.setText(String.valueOf(i));
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        /*
         * Set cursor to indicate computation on-going; this matters only if
         * processing the event might take a noticeable amount of time as seen
         * by the user
         */
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        /*
         * Determine which event has occurred that we are being notified of by
         * this callback; in this case, the source of the event (i.e, the widget
         * calling actionPerformed) is all we need because only buttons are
         * involved here, so the event must be a button press; in each case,
         * tell the controller to do whatever is needed to update the model and
         * to refresh the view
         */

        Object source = event.getSource();

        if (source == this.resetButton) {
            this.controller.processResetEvent();
        } else if (source == this.browseVideoLocationButton) {
            this.controller.processBrowseVideoLocationEvent();
        } else if (source == this.browseExportLocationButton) {
            this.controller.processBrowseExportLocationEvent();
        } else if (source == this.exportButton) {
            this.controller.processExportEvent();
        } else if (source == this.framesBackButton) {
            this.controller.processFramesBackEvent();
        } else if (source == this.framesForwardButton) {
            this.controller.processFramesForwardEvent();
        }
        /*
         * Set the cursor back to normal (because we changed it at the beginning
         * of the method body)
         */
        this.setCursor(Cursor.getDefaultCursor());
    }

}
