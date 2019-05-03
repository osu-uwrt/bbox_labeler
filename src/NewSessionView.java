import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

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

    public void toggleButtons();

    public String getSelectedClass();

    public String getSelectedVideo();

    public void addDropdownItem(String text);

    public void closeWindow();

    public void addVideo(BufferedImage video, String text, Boolean inColor);
}
