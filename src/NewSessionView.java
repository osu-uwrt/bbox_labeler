import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.JPanel;

/**
 * View interface.
 *
 * @author Derek Opdycke
 */
public interface NewSessionView extends ActionListener {

    /**
     * Register argument as observer/listener of this; this must be done first,
     * before any other methods of this class are called.
     *
     * @param controller
     *            controller to register
     */
    void registerObserver(NewSessionController controller);

    public String getSelectedClass();

    public void addDropdownItem(String text);

    public void closeWindow();

    public void addVideo(BufferedImage video, String text, Boolean inColor,
            Color color);

    public void colorBorder(JPanel jpanel, int[] rgb);

    public LinkedList<JPanel> getVideoPanelsList();

    public void removeAllVideos();

    public void enableButton();

    public void addListenerToComboBox();

    public void changeToProgressBar(long increment);

    public void setProgress(long progress);

    public void progress(long progress);

    public com.box.sdk.ProgressListener getProgressListener();
}
