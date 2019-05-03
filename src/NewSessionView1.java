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
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

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
            COLUMNS_IN_LABEL_PANEL_GRID = 2, DEFAULT_WIDTH_OF_WINDOW = 600,
            DEFAULT_HEIGHT_OF_WINDOW = 400, ROWS_IN_A_VIDEO_PANEL = 2,
            COLUMNS_IN_A_VIDEO_PANEL = 1, COLUMNS_IN_VIDEO_PANEL = 4;

    /**
     * Buttons.
     */
    private final JButton beginLabellingButton;

    /**
     * Labels
     */
    private final JLabel selectClassLabel;

    /**
     * Panels
     */
    private final JPanel labelPanel;
    private final JScrollPane videoScrollPane;
    private final JSplitPane splitMain;
    private final JSplitPane splitHeader;

    /**
     * ComboBoxes
     */
    private final JComboBox<String> classComboBox;

    /**
     * List of panels which are rows for the videos
     */
    private LinkedList<JPanel> videoPanels;
    /**
     * How many videos are in the last row of videoPanels
     */
    private int count;
    /**
     * the current panel to add videos to
     */
    private JPanel currentVideoPanel;

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

        this.videoPanels = new LinkedList<JPanel>();
        this.count = COLUMNS_IN_VIDEO_PANEL;

        // Set up the GUI widgets --------------------------------------------

        /*
         * Create widgets
         */
        /**
         * Buttons
         */
        this.beginLabellingButton = new JButton("Begin Labelling!");

        this.selectClassLabel = new JLabel(
                "<html>Select the class for these labels:</html>");

        /**
         * Panels
         */
        this.labelPanel = new JPanel(new GridLayout(ROWS_IN_LABEL_PANEL_GRID,
                COLUMNS_IN_LABEL_PANEL_GRID));
        this.videoScrollPane = new JScrollPane(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.splitMain = new JSplitPane();
        this.splitMain.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.splitMain.setDividerSize(1);
        this.splitMain.setEnabled(false);
        this.splitMain.setDividerLocation((50));
        this.splitHeader = new JSplitPane();
        this.splitHeader.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        this.splitHeader.setDividerSize(1);
        this.splitHeader.setEnabled(false);
        this.splitHeader
                .setDividerLocation((int) (DEFAULT_WIDTH_OF_WINDOW * 0.6));

        /**
         * ComboBoxes
         */
        this.classComboBox = new JComboBox<String>();
        this.classComboBox.setEditable(false);

        /*
         * Organize window
         */
        this.add(this.splitMain);
        this.setMinimumSize(new Dimension(DEFAULT_WIDTH_OF_WINDOW,
                DEFAULT_HEIGHT_OF_WINDOW));
        this.setResizable(false);
        /*
         * Organize top side
         */
        this.splitMain.setTopComponent(this.splitHeader);
        this.splitHeader.setLeftComponent(this.labelPanel);
        this.labelPanel.add(this.selectClassLabel);
        this.labelPanel.add(this.classComboBox);
        this.splitHeader.setRightComponent(this.beginLabellingButton);

        /*
         * Organize bottom side
         */
        this.splitMain.setBottomComponent(this.videoScrollPane);

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

    @Override
    public void addVideo(BufferedImage video, String text, Boolean inColor) {
        /*
         * Build this video panel
         */
        JPanel outer = new JPanel();
        outer.setBorder(BorderFactory
                .createLineBorder(new java.awt.Color(169, 169, 169), 4));
        JSplitPane videoPane = new JSplitPane();
        videoPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        videoPane.setDividerSize(1);
        videoPane.setEnabled(false);
        videoPane.setDividerLocation((132));
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
        thumbnail.setHorizontalAlignment(SwingConstants.CENTER);
        name.setText(text);
        videoPane.setTopComponent(thumbnail);
        videoPane.setBottomComponent(name);
        outer.add(videoPane);
        outer.setMaximumSize(new Dimension(
                this.videoScrollPane.getViewport().getWidth() / 4, 180));
        /*
         * Insert this video panel
         */
        //Does it need to be on a new row?
        if (this.count == COLUMNS_IN_VIDEO_PANEL) {
            //make a new row
            System.out.println("make a new row");
            this.currentVideoPanel = new JPanel();
            this.currentVideoPanel.setLayout(
                    new BoxLayout(this.currentVideoPanel, BoxLayout.X_AXIS));
            this.videoScrollPane.getViewport().add(this.currentVideoPanel);
            this.count = 0;
        }
        this.count++;
        //add it to the current one
        System.out.println("add the video");
        this.currentVideoPanel.add(outer);
    }

    private void addVideo2(BufferedImage video, String text, Boolean inColor) {
        /*
         * Build this video panel
         */
        JPanel outer = new JPanel();
        outer.setBorder(BorderFactory
                .createLineBorder(new java.awt.Color(169, 169, 169), 4));
        JSplitPane videoPane = new JSplitPane();
        videoPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        videoPane.setDividerSize(1);
        videoPane.setEnabled(false);
        videoPane.setDividerLocation((132));
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
        thumbnail.setHorizontalAlignment(SwingConstants.CENTER);
        name.setText(text);
        videoPane.setTopComponent(thumbnail);
        videoPane.setBottomComponent(name);
        outer.add(videoPane);
        /*
         * Insert this video panel
         */
        //Does it need to be on a new row?
        if (this.count == COLUMNS_IN_VIDEO_PANEL) {
            //make a new row
            System.out.println("make a new row");
            this.currentVideoPanel = new JPanel();
            this.currentVideoPanel.setLayout(
                    new BoxLayout(this.currentVideoPanel, BoxLayout.X_AXIS));
            outer.setMaximumSize(new Dimension(
                    this.videoScrollPane.getViewport().getWidth() / 4, 180));
            this.videoScrollPane.getViewport().add(this.currentVideoPanel);
            this.count = 0;
        }
        this.count++;
        //add it to the current one
        System.out.println("add the video");
        this.currentVideoPanel.add(outer);
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
