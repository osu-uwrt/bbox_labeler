import java.awt.event.ActionListener;

/**
 * View interface.
 *
 * @author Derek Opdycke
 */
public interface SessionTypeView extends ActionListener {

    /**
     * Register argument as observer/listener of this; this must be done first,
     * before any other methods of this class are called.
     *
     * @param controller
     *            controller to register
     */
    void registerObserver(SessionTypeController controller);

    public void toggleButtons();

    public void setWelcomeLabel(String text);
}
