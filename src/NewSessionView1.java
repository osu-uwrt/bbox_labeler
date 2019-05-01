import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;

import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 * View class.
 *
 * @author Derek Opdycke
 */
@SuppressWarnings("serial")
public final class NewSessionView1 extends JFrame implements NewSessionView {

    /**
     * Controller object.
     */
    private NewSessionController controller;

    /**
     * GUI widgets that need to be in scope in actionPerformed method, and
     * related constants.
     */
    private static final int ROWS_IN_LABEL_PANEL_GRID = 1,
            COLUMNS_IN_LABEL_PANEL_GRID = 2, DEFAULT_WIDTH_OF_WINDOW = 400,
            DEFAULT_HEIGHT_OF_WINDOW = 200, ROWS_IN_VIDEO_PANEL = 2,
            COLUMNS_IN_VIDEO_PANEL = 1;

    /**
     * Buttons.
     */
    private final JButton beginLabellingButton;

    /**
     * Labels
     */
    private final JLabel selectClassLabel;
    private final JPanel labelPanel, headerPanel, thumbnailPanel;
    private final JSplitPane splitMain;
    private final JComboBox<String> classComboBox;

    /**
     * No-argument constructor.
     */
    public NewSessionView1() {
        // Create the JFrame being extended

        /*
         * Call the JFrame (superclass) constructor with a String parameter to
         * name the window in its title bar
         */
        super("Begin New Session");

        // Set up the GUI widgets --------------------------------------------

        /*
         * Create widgets
         */
        this.beginLabellingButton = new JButton("Begin Labelling!");
        this.selectClassLabel = new JLabel(
                "Select the class for these labels:");
        this.classComboBox = new JComboBox<String>();
        this.classComboBox.setEditable(false);

        this.splitMain = new JSplitPane();
        //this.splitMain.setDividerSize(1);
        this.splitMain.setEnabled(false);
        this.splitMain
                .setDividerLocation((int) (DEFAULT_HEIGHT_OF_WINDOW / 2.5));
        this.splitMain.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.labelPanel = new JPanel(new GridLayout(ROWS_IN_LABEL_PANEL_GRID,
                COLUMNS_IN_LABEL_PANEL_GRID));

        this.headerPanel = new JPanel(new GridLayout(ROWS_IN_LABEL_PANEL_GRID,
                COLUMNS_IN_LABEL_PANEL_GRID));
        this.thumbnailPanel = new JPanel();

        /*
         * Organize main window using grid layout
         */
        this.add(this.splitMain);
        /*
         * Add scroll panes and button panel to main window, from left to right
         * and top to bottom
         */
        this.splitMain.setTopComponent(this.headerPanel);
        this.labelPanel.add(this.selectClassLabel);
        this.labelPanel.add(this.classComboBox);
        this.splitMain.setBottomComponent(this.thumbnailPanel);
        this.add(this.splitMain);
        this.setMinimumSize(new Dimension(DEFAULT_WIDTH_OF_WINDOW,
                DEFAULT_HEIGHT_OF_WINDOW));
        this.setResizable(false);

        // Set up the observers ----------------------------------------------

        /*
         * Register this object as the observer for all GUI events
         */
        this.beginLabellingButton.addActionListener(this);

        // Start the main application window --------------------------------

        /*
         * Make sure the main window is appropriately sized for the widgets in
         * it, that it exits this program when closed, and that it becomes
         * visible to the user now
         */
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds(dim.width / 2 - this.getSize().width / 2,
                dim.height / 2 - this.getSize().height / 2,
                DEFAULT_WIDTH_OF_WINDOW, DEFAULT_HEIGHT_OF_WINDOW);
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
    public void registerObserver(NewSessionController controller) {
        this.controller = controller;
    }

    /*
     * Toggles the buttons to enable/disable them
     */
    @Override
    public void toggleButtons() {
        this.beginLabellingButton
                .setEnabled(!this.beginLabellingButton.isEnabled());
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

        if (source == this.beginLabellingButton) {
            this.toggleButtons();
            //TODO: process event
            //this.controller.processNewEvent();
            this.toggleButtons();
        } else {
            System.out.println("How?");
        }
        /*
         * Set the cursor back to normal (because we changed it at the beginning
         * of the method body)
         */
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void addClasses(String text) {
        this.classComboBox.addItem(text);
    }

    private void addVideos() {
        // TODO Auto-generated method stub

    }

    private void addClass(String text) {
        this.classComboBox.addItem(text);
    }

    private void addVideo(BufferedImage video, Boolean inColor, String text) {
        JPanel videoPanel = new JPanel(new GridLayout(this.ROWS_IN_VIDEO_PANEL,
                this.COLUMNS_IN_VIDEO_PANEL));
        JLabel thumbnail = new JLabel();
        JLabel name = new JLabel();
        if (!inColor) {
            //change the image to grayscale
            ImageFilter filter = new GrayFilter(true, 50);
            ImageProducer producer = new FilteredImageSource(video.getSource(),
                    filter);
            Image image = Toolkit.getDefaultToolkit().createImage(producer);
            thumbnail.setIcon(new ImageIcon(image));
        } else {
            thumbnail.setIcon(new ImageIcon(video));
        }
        name.setText(text);
        videoPanel.add(thumbnail);
        videoPanel.add(name);
        this.thumbnailPanel.add(videoPanel);
    }

    @Override
    public String getSelectedClass() {
        return this.classComboBox.getSelectedItem().toString();
    }

    @Override
    public String getSelectedVideo() {
        return null;
    }

    @Override
    public void closeWindow() {
        this.dispose();
    }

    @Override
    public void addDropdownItem(String text) {
        this.classComboBox.addItem(text);
    }
}
