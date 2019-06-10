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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

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

    //Has the progress bar been created yet?
    private static Boolean progressBarMade;
    //Increments for the progress bar will be divided by this scaler
    private static int progressBarScalar;

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
     * Progress bar for downloading
     */
    private final JProgressBar progressBar = new JProgressBar(
            JProgressBar.HORIZONTAL);

    /**
     * No-argument constructor.
     */
    public NewSessionView1() {
        // Create the JFrame being extended

        /*
         * Call the JFrame (superclass) constructor with a String parameter to
         * name the window in its title bar
         */
        super("Scylla");

        videoPanels = new LinkedList<JPanel>();
        this.count = COLUMNS_IN_VIDEO_PANEL;
        progressBarMade = false;
        progressBarScalar = 1000000;

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
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds(dim.width / 2 - this.getSize().width / 2,
                dim.height / 2 - this.getSize().height / 2,
                DEFAULT_WIDTH_OF_WINDOW, DEFAULT_HEIGHT_OF_WINDOW);
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
            this.currentVideoPanel = new JPanel();
            this.currentVideoPanel.setLayout(
                    new BoxLayout(this.currentVideoPanel, BoxLayout.X_AXIS));
            this.videoPanel.add(this.currentVideoPanel);
            this.count = 0;
        }
        this.count++;
        //add it to the current one
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
            name.setForeground(Color.GRAY);
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
                panelClicked = (JPanel) source;
                NewSessionView1.this.controller
                        .processPanelSelect(panelClicked);
            } else if (source instanceof JLabel) {
                panelClicked = (JPanel) ((Component) source).getParent()
                        .getParent();
                NewSessionView1.this.controller
                        .processPanelSelect(panelClicked);
            } else if (source instanceof JSplitPane) {
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

    /**
     * Changes the GUI to have a progress bar
     *
     * @param <T>
     * @param <V>
     */
    private class ProgressbarWorker<T, V> extends javax.swing.SwingWorker {
        private NewSessionView view;
        private long max;

        ProgressbarWorker(NewSessionView view, long max) {
            this.view = view;
            this.max = max;
        }

        @Override
        protected Object doInBackground() throws Exception {
            System.out.println("ProgressbarWorker doInBackground");
            return null;
        }

        @Override
        protected void process(List chunks) {
            System.out.println("Process for Progressbar Worker");
        }

        @Override
        protected void done() {
            System.out.println("Progressbar Worker done");
            //update the progress bar
            this.view.progress(this.max);
            NewSessionView1.this.videoScrollPane.getViewport().removeAll();
            NewSessionView1.this.progressBar
                    .setMaximum((int) (this.max / progressBarScalar));
            NewSessionView1.this.progressBar.setValue(
                    NewSessionView1.this.progressBar.getMaximum() / 2);
            NewSessionView1.this.progressBar.setForeground(Color.GREEN);
            NewSessionView1.this.videoScrollPane.getViewport()
                    .add(NewSessionView1.this.progressBar);
            progressBarMade = true;
            NewSessionView1.this.revalidate();
            NewSessionView1.this.repaint();

        }

    }

    /**
     * Updates the progress bar in the GUI
     *
     * @param <T>
     * @param <V>
     */
    private class ProgressbarUpdater<T, V> extends javax.swing.SwingWorker {
        private NewSessionView view;
        private long increment;

        public void ProgressbarWorker(NewSessionView view, long increment) {
            this.view = view;
            this.increment = increment;
        }

        @Override
        protected Object doInBackground() throws Exception {
            System.out.println("ProgressbarWorker doInBackground");
            //update the progress bar
            this.view.progress(this.increment);
            return null;
        }

    }

    @Override
    public void progress(long max) {
        this.videoScrollPane.getViewport().removeAll();
        this.progressBar.setMaximum((int) (max / progressBarScalar));
        this.progressBar.setValue(this.progressBar.getMaximum() / 2);
        this.progressBar.setForeground(Color.GREEN);
        this.videoScrollPane.getViewport().add(this.progressBar);
        progressBarMade = true;
        this.revalidate();
        this.repaint();

    }

    /**
     * Changes the GUI to a progress bar
     */
    @Override
    public void changeToProgressBar(long max) {
        //ProgressbarWorker<String, Void> myWorker = new ProgressbarWorker<String, Void>(
        //        this, max);
        //myWorker.execute();
        this.progress(max);
    }

    /**
     * Sets the progress bar to the given progress if the GUI has been set to
     * show a progress bar
     */
    @Override
    public void setProgress(long progress) {
        //make sure the progress bar exists
        if (progressBarMade) {
            //change the progress in the progress bar to the given value
            this.progressBar.setValue((int) (progress / progressBarScalar));
            this.repaint();
        }
    }

    @Override
    public com.box.sdk.ProgressListener getProgressListener() {
        return new ProgressListener(this);
    }

    private final class ProgressListener
            implements com.box.sdk.ProgressListener {

        private NewSessionView view;
        private int count;

        //Constructor
        ProgressListener(NewSessionView view) {
            this.view = view;
            this.count = 0;
        }

        @Override
        public void onProgressChanged(long numBytes, long totalBytes) {
            double percentComplete = numBytes / totalBytes * 100;

            this.count++;
            if (this.count > 100) {
                this.count = 0;
                System.out.println("Bytes Downloaded: " + numBytes);
                //System.out.println("Total Bytes: " + totalBytes);
                //System.out.println("Downloaded " + percentComplete + "%");
                class myTask implements Runnable {
                    private NewSessionView view;

                    public myTask(NewSessionView view) {
                        this.view = view;
                    }

                    @Override
                    public void run() {
                        this.view.setProgress(numBytes);

                    }

                }
                SwingUtilities.invokeLater(new myTask(this.view));
            }
        }
    }

    @Override
    public void disposeFrame() {
        this.dispose();
    }

}
