import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import javax.swing.JSplitPane;
import javax.swing.text.NumberFormatter;

/**
 * View class.
 *
 * @author Derek Opdycke
 */
@SuppressWarnings("serial")
public final class YOLOBboxView1 extends JFrame implements YOLOBboxView,
        MouseListener, MouseMotionListener, KeyListener {

    /**
     * Controller object.
     */
    private YOLOBboxController controller;

    /**
     * GUI widgets that need to be in scope in actionPerformed method, and
     * related constants.
     */
    private static final int COLUMNS_IN_FRAME_CONTROL_PANEL_GRID = 6,
            ROWS_IN_FRAME_CONTROL_PANEL_GRID = 2,
            DEFAULT_WIDTH_OF_WINDOW = 1000, DEFAULT_HEIGHT_OF_WINDOW = 700,
            BUTTON_PANEL_HEIGHT = 130;

    /**
     * JPanels
     */
    private final JPanel videoContainer, frameControlPanel;
    private final JSplitPane splitMain;

    /**
     * Text areas.
     */
    private final JFormattedTextField frameJumpText;

    /**
     * Buttons.
     */
    private final JButton reviewButton, fillInFramesButton, resetButton,
            framesBackButton, framesForwardButton, exportButton, saveButton;

    /**
     * Labels
     */
    private final JLabel frameNumberLabel, imageLabel, frameRateLabel,
            totalFramesLabel;

    /**
     * No-argument constructor.
     */
    public YOLOBboxView1() {
        // Create the JFrame being extended

        /*
         * Call the JFrame (superclass) constructor with a String parameter to
         * name the window in its title bar
         */
        super("Scylla");

        // Set up the GUI widgets --------------------------------------------

        /*
         * Create widgets
         */
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        this.frameJumpText = new JFormattedTextField(formatter);
        this.frameJumpText.setText("1");
        this.reviewButton = new JButton("Review");
        this.resetButton = new JButton("Reset");
        this.fillInFramesButton = new JButton("<html>Fill in frames</html>");
        this.exportButton = new JButton("Export");
        this.framesBackButton = new JButton("Back");
        this.framesForwardButton = new JButton("Forward");
        this.saveButton = new JButton("Save");
        this.totalFramesLabel = new JLabel("Number of frames:");
        this.frameNumberLabel = new JLabel("Current frame:");
        this.imageLabel = new JLabel();
        this.frameRateLabel = new JLabel();

        /*
         * Text areas
         */
        this.frameJumpText.setEditable(true);

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
         * Add the buttons to the frame control panel, from left to right and
         * top to bottom
         */
        //First row
        this.frameControlPanel.add(new JLabel());
        this.frameControlPanel.add(this.resetButton);
        this.frameControlPanel.add(this.fillInFramesButton);
        this.frameControlPanel.add(this.frameRateLabel);
        this.frameControlPanel.add(this.frameNumberLabel);
        this.frameControlPanel.add(this.totalFramesLabel);
        //Second row
        this.frameControlPanel.add(this.saveButton);
        this.frameControlPanel.add(this.reviewButton);
        this.frameControlPanel.add(this.exportButton);
        this.frameControlPanel.add(this.framesBackButton);
        this.frameControlPanel.add(this.frameJumpText);
        this.frameControlPanel.add(this.framesForwardButton);
        this.frameControlPanel.setMinimumSize(new Dimension(0, 100));

        /*
         * Add the video label and frameControlPanel to the video panel
         */
        this.videoContainer.add(this.imageLabel);
        this.splitMain = new JSplitPane();
        this.splitMain.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.splitMain.setTopComponent(this.videoContainer);
        this.splitMain.setBottomComponent(this.frameControlPanel);
        this.splitMain.setDividerSize(5);
        System.out.println(DEFAULT_HEIGHT_OF_WINDOW - BUTTON_PANEL_HEIGHT);
        this.splitMain.setDividerLocation(
                DEFAULT_HEIGHT_OF_WINDOW - BUTTON_PANEL_HEIGHT);
        this.splitMain.setEnabled(false);
        this.imageLabel.setOpaque(false);
        /*
         * Organize main window using grid layout
         */
        this.setLayout(new GridLayout(1, 1));
        /*
         * Add scroll panes and button panel to main window, from left to right
         * and top to bottom
         */
        this.add(this.splitMain);
        this.setMinimumSize(new Dimension(500, 400));

        // Set up the observers ----------------------------------------------

        /*
         * Register this object as the observer for all GUI events
         */
        this.resetButton.addActionListener(this);
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
        this.fillInFramesButton.addActionListener(this);
        this.saveButton.addActionListener(this);

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
        this.setIconImage(new ImageIcon("data/Scylla.jpg").getImage());
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
     * Updates total frames display based on integer provided as argument.
     *
     * @param i
     *            new value of total frames display
     */
    @Override
    public void updateTotalFramesTextDisplay(int i) {
        this.totalFramesLabel.setText("<html>Total Frames: " + i + "</html>");
    }

    /**
     * Updates current display based on integer provided as argument.
     *
     * @param i
     *            new value of current frame display
     */
    @Override
    public void updateCurrentFrameTextDisplay(int i) {
        this.frameNumberLabel
                .setText("<html>Current Frame: " + (i + 1) + "</html>");
    }

    /**
     * Updates frame rate display based on integer provided as argument.
     *
     * @param i
     *            new value of frame rate display
     */
    @Override
    public void updateFrameRateTextDisplay(int i) {
        this.frameRateLabel.setText("<html>Frame Rate: " + i + "</html>");
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
    public void loadFrame(BufferedImage img) {
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
        } else if (source == this.saveButton) {
            this.toggleButtons();
            this.controller.processSaveEvent();
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
        this.controller.processMouseMovedEvent(arg0.getX(), arg0.getY());

    }

    @Override
    public void updateButtonAreaSize() {
        this.splitMain
                .setDividerLocation(this.getHeight() - BUTTON_PANEL_HEIGHT);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        System.out.println("Key Pressed");
        if (evt.getKeyChar() == KeyEvent.VK_LEFT) {
            this.controller.processFramesBackEvent();
        } else if (evt.getKeyChar() == KeyEvent.VK_RIGHT) {
            this.controller.processFramesForwardEvent();
        } else if (evt.getKeyChar() == KeyEvent.VK_UP) {
            this.controller.incrmentFrameJump();
        } else if (evt.getKeyChar() == KeyEvent.VK_DOWN) {
            this.controller.decrementFrameJump();
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        System.out.println("Key Pressed");
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        System.out.println("Key Pressed");
    }
}
