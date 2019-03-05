import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.text.NumberFormatter;

/**
 * View class.
 *
 * @author Derek Opdycke
 */
@SuppressWarnings("serial")
public final class YOLOBboxView1 extends JFrame
        implements YOLOBboxView, MouseListener, MouseMotionListener {

    /**
     * Controller object.
     */
    private YOLOBboxController controller;

    /**
     * GUI widgets that need to be in scope in actionPerformed method, and
     * related constants.
     */
    private static final int ROWS_IN_BUTTON_PANEL_GRID = 12,
            COLUMNS_IN_BUTTON_PANEL_GRID = 1, ROWS_IN_THIS_GRID = 1,
            COLUMNS_IN_THIS_GRID = 2, COLUMNS_IN_FRAME_CONTROL_PANEL_GRID = 3,
            ROWS_IN_FRAME_CONTROL_PANEL_GRID = 2,
            DEFAULT_WIDTH_OF_WINDOW = 1000, DEFAULT_HEIGHT_OF_WINDOW = 700,
            DEFUALT_BUTTON_PANEL_WIDTH = 200, DEFAULT_BUTTON_PANEL_HEIGHT = 200;

    /**
     * JPanels
     */
    private final JPanel videoContainer, frameControlPanel, buttonPanel;
    private final JSplitPane splitMain, splitVideo;

    /**
     * Text areas.
     */
    private final JTextArea videoLocationText, usernameText;
    private final JFormattedTextField itemIndexText, frameJumpText;
    private final JPasswordField passwordText;

    /**
     * Buttons.
     */
    private final JButton browseVideoLocationButton, reviewButton,
            fillInFramesButton, resetButton, framesBackButton,
            framesForwardButton, exportButton;

    /**
     * Labels
     */
    private final JLabel framesLabel, frameNumberLabel, imageLabel,
            itemIndexLabel, usernameLabel, passwordLabel;

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
        this.usernameText = new JTextArea("");
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        this.itemIndexText = new JFormattedTextField(formatter);
        this.frameJumpText = new JFormattedTextField(formatter);
        this.frameJumpText.setText("1");
        this.passwordText = new JPasswordField();
        this.browseVideoLocationButton = new JButton("Browse for video");
        this.reviewButton = new JButton("Review");
        this.resetButton = new JButton("Reset");
        this.fillInFramesButton = new JButton("Fill in frames");
        this.exportButton = new JButton("Export");
        this.framesBackButton = new JButton("Back");
        this.framesForwardButton = new JButton("Forward");
        this.framesLabel = new JLabel("Number of frames:");
        this.frameNumberLabel = new JLabel("Current frame:");
        this.imageLabel = new JLabel();
        this.itemIndexLabel = new JLabel("Item Index:");
        this.usernameLabel = new JLabel("Username:");
        this.passwordLabel = new JLabel("Password:");

        /*
         * Text areas
         */
        this.videoLocationText.setEditable(true);
        this.videoLocationText.setLineWrap(true);
        this.videoLocationText.setAutoscrolls(true);
        this.usernameText.setEditable(true);
        this.usernameText.setLineWrap(true);
        this.usernameText.setAutoscrolls(true);
        this.itemIndexText.setEditable(true);
        this.passwordText.setEditable(true);
        this.passwordText.setAutoscrolls(true);
        this.frameJumpText.setEditable(true);

        /*
         * Create a button panel organized using grid layout
         */
        this.buttonPanel = new JPanel(new GridLayout(ROWS_IN_BUTTON_PANEL_GRID,
                COLUMNS_IN_BUTTON_PANEL_GRID));

        /*
         * Create a button panel organized using grid layout
         */
        this.frameControlPanel = new JPanel(
                new GridLayout(ROWS_IN_FRAME_CONTROL_PANEL_GRID,
                        COLUMNS_IN_FRAME_CONTROL_PANEL_GRID));

        /*
         * Create a panel organized using grid layout for the video
         */
        this.videoContainer = new JPanel(new FlowLayout());
        /*
         * Add the buttons to the button panel, from left to right and top to
         * bottom
         */
        this.buttonPanel.add(this.browseVideoLocationButton);
        this.buttonPanel.add(this.videoLocationText);
        this.buttonPanel.add(this.itemIndexLabel);
        this.buttonPanel.add(this.itemIndexText);
        this.buttonPanel.add(this.usernameLabel);
        this.buttonPanel.add(this.usernameText);
        this.buttonPanel.add(this.passwordLabel);
        this.buttonPanel.add(this.passwordText);
        this.buttonPanel.add(this.fillInFramesButton);
        this.buttonPanel.add(this.reviewButton);
        this.buttonPanel.add(this.exportButton);
        this.buttonPanel.add(this.resetButton);
        this.buttonPanel.setPreferredSize(
                new Dimension(DEFUALT_BUTTON_PANEL_WIDTH, 800));
        /*
         * Add the buttons to the frame control panel, from left to right and
         * top to bottom
         */
        this.frameControlPanel.add(new JLabel());
        this.frameControlPanel.add(this.framesLabel);
        this.frameControlPanel.add(new JLabel());
        this.frameControlPanel.add(this.framesBackButton);
        this.frameControlPanel.add(this.frameJumpText);
        this.frameControlPanel.add(this.framesForwardButton);

        /*
         * Add the video label and frameControlPanel to the video panel
         */
        this.videoContainer.add(this.imageLabel);
        this.splitVideo = new JSplitPane();
        this.frameControlPanel.setMinimumSize(new Dimension(100, 150));
        this.frameControlPanel.setMaximumSize(new Dimension(100, 100));
        this.frameControlPanel.setPreferredSize(new Dimension(100, 200));
        this.splitVideo.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.splitVideo.setTopComponent(this.videoContainer);
        this.splitVideo.setBottomComponent(this.frameControlPanel);
        this.splitVideo.setDividerSize(0);
        this.splitVideo.setDividerLocation(
                DEFAULT_HEIGHT_OF_WINDOW - DEFAULT_BUTTON_PANEL_HEIGHT);
        this.splitVideo.setEnabled(false);
        this.imageLabel.setOpaque(false);
        /*
         * Organize main window using grid layout
         */
        this.setLayout(new GridLayout(ROWS_IN_THIS_GRID, COLUMNS_IN_THIS_GRID));
        /*
         * Add scroll panes and button panel to main window, from left to right
         * and top to bottom
         */
        this.splitMain = new JSplitPane();
        this.splitMain.setLeftComponent(this.buttonPanel);
        this.splitMain.setRightComponent(this.splitVideo);
        this.splitMain.setDividerSize(0);
        this.splitMain.setEnabled(false);
        this.add(this.splitMain);
        this.setMinimumSize(new Dimension(500, 400));

        // Set up the observers ----------------------------------------------

        /*
         * Register this object as the observer for all GUI events
         */
        this.resetButton.addActionListener(this);
        this.browseVideoLocationButton.addActionListener(this);
        this.fillInFramesButton.addActionListener(this);
        this.exportButton.addActionListener(this);
        this.reviewButton.addActionListener(this);
        this.framesBackButton.addActionListener(this);
        this.framesForwardButton.addActionListener(this);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                if (YOLOBboxView1.this.controller != null) {
                    YOLOBboxView1.this.controller.processResizeEvent();
                }
            }
        });
        this.imageLabel.addMouseListener(this);
        this.imageLabel.addMouseMotionListener(this);

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
        this.usernameText.setText(s);
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
        //TODO: show frame rate
    }

    /**
     * Updates frame jump display based on integer provided as argument.
     *
     * @param i
     *            new value of frame jump display
     */
    @Override
    public void updateFrameJumpTextDisplay(int i) {
        this.frameJumpText.setText(String.valueOf(i));
    }

    @Override
    public void updateUsernameTextDisplay(String s) {
        this.usernameText.setText(s);
    }

    @Override
    public void updatePasswordTextDisplay(String s) {
        this.passwordText.setText(s);
    }

    @Override
    public void loadFrame(BufferedImage img) {
        System.out.println("Image Loaded");
        ImageIcon icon = new ImageIcon(img);
        this.imageLabel.setIcon(icon);
    }

    @Override
    public void update() {
        this.revalidate();
        this.repaint();
    }

    /*
     * Toggles the buttons to enable/disable them
     */
    @Override
    public void toggleButtons() {
        this.resetButton.setEnabled(!this.resetButton.isEnabled());
        this.browseVideoLocationButton
                .setEnabled(!this.browseVideoLocationButton.isEnabled());
        this.exportButton.setEnabled(!this.exportButton.isEnabled());
        this.reviewButton.setEnabled(!this.reviewButton.isEnabled());
        this.fillInFramesButton
                .setEnabled(!this.fillInFramesButton.isEnabled());
        this.framesBackButton.setEnabled(!this.framesBackButton.isEnabled());
        this.framesForwardButton
                .setEnabled(!this.framesForwardButton.isEnabled());
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
            this.toggleButtons();
            this.controller.processResetEvent();
            this.toggleButtons();
        } else if (source == this.browseVideoLocationButton) {
            this.toggleButtons();
            this.controller.processBrowseVideoLocationEvent();
            this.toggleButtons();
        } else if (source == this.exportButton) {
            this.toggleButtons();
            this.controller.processExportEvent();
            this.toggleButtons();
        } else if (source == this.reviewButton) {
            this.toggleButtons();
            this.controller.processReviewEvent();
            this.toggleButtons();
        } else if (source == this.fillInFramesButton) {
            this.toggleButtons();
            this.controller.processFillInFramesEvent();
            this.toggleButtons();
        } else if (source == this.framesBackButton) {
            this.toggleButtons();
            this.controller.processFramesBackEvent();
            this.toggleButtons();
        } else if (source == this.framesForwardButton) {
            this.toggleButtons();
            this.controller.processFramesForwardEvent();
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

    @Override
    public int getFrameAreaHeight() {
        return this.videoContainer.getHeight();
    }

    @Override
    public int getFrameAreaWidth() {
        return this.videoContainer.getWidth();
    }

    @Override
    public int getFrameJump() {
        String text = this.frameJumpText.getText();
        int i = 0;
        StringBuilder sb = new StringBuilder();
        while (i < text.length()) {
            if (text.charAt(i) != ',') {
                sb.append(text.charAt(i));
            }
            i++;
        }
        return Integer.parseInt(sb.toString());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("Image Clicked");
        System.out.println("X-coord: " + e.getX());
        System.out.println("Y-coord: " + e.getY());
        System.out.println("Image Clicked");
        this.controller.processMouseClickedEvent(e.getX(), e.getY());
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        this.controller.processMouseEnteredEvent(e.getX(), e.getY());

    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.controller.processMouseExitedEvent(e.getX(), e.getY());

    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.controller.processMousePressedEvent(e.getX(), e.getY());

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.controller.processMouseReleasedEvent(e.getX(), e.getY());

    }

    @Override
    public void mouseDragged(MouseEvent arg0) {
        this.controller.processMouseDraggedEvent(arg0.getX(), arg0.getY());

    }

    @Override
    public void mouseMoved(MouseEvent arg0) {
        //System.out.println("Mouse Moved in image");
        //System.out.println("X-coord: " + arg0.getX());
        //System.out.println("Y-coord: " + arg0.getY());
        this.controller.processMouseMovedEvent(arg0.getX(), arg0.getY());

    }

}
