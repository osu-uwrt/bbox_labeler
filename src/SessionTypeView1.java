import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;

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
public final class SessionTypeView1 extends JFrame implements SessionTypeView {

    /**
     * Controller object.
     */
    private SessionTypeController controller;

    /**
     * GUI widgets that need to be in scope in actionPerformed method, and
     * related constants.
     */
    private static final int ROWS_IN_BUTTON_PANEL_GRID = 1,
            COLUMNS_IN_BUTTON_PANEL_GRID = 2, ROWS_IN_THIS_GRID = 1,
            COLUMNS_IN_THIS_GRID = 2, DEFAULT_WIDTH_OF_WINDOW = 400,
            DEFAULT_HEIGHT_OF_WINDOW = 200;

    /**
     * Buttons.
     */
    private final JButton newButton, loadButton, startButton;

    /**
     * Labels
     */
    private final JLabel welcomeLabel;
    private final JPanel buttonPanel;
    private final JSplitPane splitMain;

    /**
     * JComboBox
     */
    private final JComboBox<String> loadComboBox;

    /**
     * No-argument constructor.
     */
    public SessionTypeView1() {
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
        this.newButton = new JButton("Begin New Session");
        this.loadButton = new JButton("Load Previous Session");
        this.startButton = new JButton("Resume Labelling");
        this.welcomeLabel = new JLabel(
                "<html>Welcome, {User}<br>What would you like to do?</html>");
        this.loadComboBox = new JComboBox<String>();

        this.splitMain = new JSplitPane();
        //this.splitMain.setDividerSize(1);
        this.splitMain.setEnabled(false);
        this.splitMain
                .setDividerLocation((int) (DEFAULT_HEIGHT_OF_WINDOW / 2.5));
        this.splitMain.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.buttonPanel = new JPanel(new GridLayout(ROWS_IN_BUTTON_PANEL_GRID,
                COLUMNS_IN_BUTTON_PANEL_GRID));
        this.loadComboBox.setEnabled(false);
        this.fillInLoadComboBox();

        /*
         * Organize main window using grid layout
         */
        this.setLayout(new GridLayout(ROWS_IN_THIS_GRID, COLUMNS_IN_THIS_GRID));
        this.add(this.splitMain);
        /*
         * Add scroll panes and button panel to main window, from left to right
         * and top to bottom
         */
        this.splitMain.setTopComponent(this.welcomeLabel);
        this.buttonPanel.add(this.newButton);
        this.buttonPanel.add(this.loadButton);
        this.splitMain.setBottomComponent(this.buttonPanel);
        this.add(this.splitMain);
        this.setMinimumSize(new Dimension(DEFAULT_WIDTH_OF_WINDOW,
                DEFAULT_HEIGHT_OF_WINDOW));
        this.setResizable(false);

        // Set up the observers ----------------------------------------------

        /*
         * Register this object as the observer for all GUI events
         */
        this.newButton.addActionListener(this);
        this.loadButton.addActionListener(this);
        this.startButton.addActionListener(this);

        // Start the main application window --------------------------------

        /*
         * Make sure the main window is appropriately sized for the widgets in
         * it, that it exits this program when closed, and that it becomes
         * visible to the user now
         */
        this.pack();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds(dim.width / 2 - this.getSize().width / 2,
                dim.height / 2 - this.getSize().height / 2,
                DEFAULT_WIDTH_OF_WINDOW, DEFAULT_HEIGHT_OF_WINDOW);
        this.setVisible(true);

    }

    private void fillInLoadComboBox() {
        //load a list of videos
        File videoFolder = new File(FileHelper.userVideoUrl());
        videoFolder.mkdirs();
        File[] videoFiles = videoFolder.listFiles();
        //add the name of each video
        for (int i = 0; i < videoFiles.length; i++) {
            this.loadComboBox.addItem(videoFiles[i].getName());
        }
    }

    /**
     * Register argument as observer/listener of this; this must be done first,
     * before any other methods of this class are called.
     *
     * @param controller
     *            controller to register
     */
    @Override
    public void registerObserver(SessionTypeController controller) {
        this.controller = controller;
    }

    /*
     * Toggles the buttons to enable/disable them
     */
    @Override
    public void toggleButtons() {
        this.newButton.setEnabled(!this.newButton.isEnabled());
        this.loadButton.setEnabled(!this.loadButton.isEnabled());
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
        //TODO: Add startButton Code
        if (source == this.newButton) {
            this.toggleButtons();
            this.controller.processNewEvent();
            this.toggleButtons();
        } else if (source == this.loadButton) {
            this.toggleButtons();
            this.controller.processLoadEvent();
            this.toggleButtons();
        } else if (source == this.startButton) {
            this.toggleButtons();
            this.controller.processStartEvent();
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
    public void setWelcomeLabel(String text) {
        this.welcomeLabel.setText(text);
    }

    @Override
    public void disposeFrame() {
        this.dispose();
    }

    @Override
    public void swapToLoadView() {
        this.buttonPanel.removeAll();
        this.buttonPanel.add(this.loadComboBox);
        this.buttonPanel.add(this.startButton);
        this.revalidate();
        this.repaint();
    }

    @Override
    public String getSelectedVideo() {
        return (String) this.loadComboBox.getSelectedItem();
    }
}
