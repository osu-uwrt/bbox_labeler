import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
            COLUMNS_IN_LABEL_PANEL_GRID = 2, DEFAULT_WIDTH_OF_WINDOW = 640,
            DEFAULT_HEIGHT_OF_WINDOW = 420, COLUMNS_IN_VIDEO_PANEL = 4;

    /**
     * List of panels which are rows for the videos
     */
    private static LinkedList<JPanel> videoPanels;

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
    private final JPanel videoPanel;
    private final JScrollPane videoScrollPane;
    private final JSplitPane splitMain;
    private final JSplitPane splitHeader;

    /**
     * ComboBoxes
     */
    private final JComboBox<String> classComboBox;

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

        videoPanels = new LinkedList<JPanel>();
        this.count = COLUMNS_IN_VIDEO_PANEL;

        // Set up the GUI widgets --------------------------------------------

        /*
         * Create widgets
         */
        /**
         * Buttons
         */
        this.beginLabellingButton = new JButton("Begin Labelling!");
        this.beginLabellingButton.setEnabled(false);

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
        this.videoPanel = new JPanel();
        this.videoPanel
                .setLayout(new BoxLayout(this.videoPanel, BoxLayout.Y_AXIS));
        this.videoScrollPane.getViewport().add(this.videoPanel);
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

    @Override
    public void actionPerformed(ActionEvent event) {
        /*
         * Set cursor to indicate computation on-going; this matters only if
         * processing the event might take a noticeable amount of time as seen
         * by the user
         */
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        Object source = event.getSource();

        if (source == this.beginLabellingButton) {
            this.controller.processBeginLabellingEvent();
        } else if (source == this.classComboBox) {
            this.controller.processClassSelect();
        } else {
            System.out.println("How?");
        }
        this.repaint();
        this.revalidate();
        /*
         * Set the cursor back to normal (because we changed it at the beginning
         * of the method body)
         */
        this.setCursor(Cursor.getDefaultCursor());
    }

    @Override
    public void addVideo(BufferedImage video, String text, Boolean inColor,
            Color color) {
        /*
         * Build this video panel
         */
        JPanel outer = this.makeVideoPanel(video, text, inColor, color);
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
            this.videoPanel.add(this.currentVideoPanel);
            this.count = 0;
        }
        this.count++;
        //add it to the current one
        System.out.println("add the video");
        this.currentVideoPanel.add(outer);
        //add to the video panel list
        videoPanels.add(outer);
        this.repaint();
        this.revalidate();
    }

    /**
     * Puts together a panel for the video.
     *
     * @param video
     *            The thumbnail for the video that will be displayed.
     * @param text
     *            The label for the thumbnail. Should just be the name of the
     *            file.
     * @param inColor
     *            True if the thumbnail should be displayed in color. False for
     *            grayscale.
     * @param color
     *            The color the panel's border should default to.
     * @return
     */
    private JPanel makeVideoPanel(BufferedImage video, String text,
            Boolean inColor, Color color) {
        //Build the outer panel
        JPanel outer = new JPanel();
        outer.setMaximumSize(new Dimension(
                (this.videoScrollPane.getViewport().getWidth() - 20) / 4, 180));
        outer.setBorder(BorderFactory.createLineBorder(color, 3));
        //add a listener for the outer panel
        outer.addMouseListener(new PanelListener());
        //build the split pane
        JSplitPane videoPane = new JSplitPane();
        videoPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        videoPane.setDividerSize(1);
        videoPane.setEnabled(false);
        videoPane.setDividerLocation((132));
        videoPane.addMouseListener(new PanelListener());
        JLabel thumbnail = new JLabel();
        thumbnail.addMouseListener(new PanelListener());
        JLabel name = new JLabel();
        name.addMouseListener(new PanelListener());
        if (!inColor) {
            //change the image to grayscale
            ImageFilter filter = new GrayFilter(true, 50);
            ImageProducer producer = new FilteredImageSource(video.getSource(),
                    filter);
            Image image = Toolkit.getDefaultToolkit().createImage(producer);
            thumbnail.setIcon(new ImageIcon(image));
        } else {
            //keep the image in color
            thumbnail.setIcon(new ImageIcon(video));
        }
        thumbnail.setHorizontalAlignment(SwingConstants.CENTER);
        name.setText("<html>" + text + "</html>");
        videoPane.setTopComponent(thumbnail);
        videoPane.setBottomComponent(name);
        outer.add(videoPane);
        outer.setName(text);
        return outer;
    }

    private class PanelListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            Object source = e.getSource();
            JPanel panelClicked;
            if (source instanceof JPanel) {
                System.out.println("mouse clicked in video panel");
                panelClicked = (JPanel) source;
                NewSessionView1.this.controller
                        .processPanelSelect(panelClicked);
            } else if (source instanceof JLabel) {
                System.out.println("mouse clicked in label");
                panelClicked = (JPanel) ((Component) source).getParent()
                        .getParent();
                NewSessionView1.this.controller
                        .processPanelSelect(panelClicked);
            } else if (source instanceof JSplitPane) {
                System.out.println("mouse clicked in split pane");
                panelClicked = (JPanel) ((Component) source).getParent();
                NewSessionView1.this.controller
                        .processPanelSelect(panelClicked);
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseExited(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mousePressed(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // TODO Auto-generated method stub

        }

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

    @Override
    public void colorBorder(JPanel jpanel, int[] rgb) {
        jpanel.setBorder(BorderFactory.createLineBorder(
                new java.awt.Color(rgb[0], rgb[1], rgb[2]), 3));
    }

    @Override
    public LinkedList<JPanel> getVideoPanelsList() {
        return videoPanels;
    }

    @Override
    public void removeAllVideos() {
        //remove all videos from the panel
        this.videoPanel.removeAll();
        //remove all videos from list
        videoPanels.clear();
        videoPanels = new LinkedList<JPanel>();
        this.count = COLUMNS_IN_VIDEO_PANEL;
        //disable the button
        this.beginLabellingButton.setEnabled(false);
        this.repaint();
        this.revalidate();
    }

    @Override
    public void enableButton() {
        this.beginLabellingButton.setEnabled(true);
    }

    @Override
    public void addListenerToComboBox() {
        this.classComboBox.addActionListener(this);
    }
}
