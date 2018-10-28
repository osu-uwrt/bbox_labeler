import java.awt.event.ActionListener;

/**
 * View interface.
 *
 * @author Derek Opdycke
 */
public interface YOLOBboxView extends ActionListener {

    /**
     * Register argument as observer/listener of this; this must be done first,
     * before any other methods of this class are called.
     *
     * @param controller
     *            controller to register
     */
    void registerObserver(YOLOBboxController controller);

    /**
     * Updates input display based on String provided as argument.
     *
     * @param input
     *            new value of input display
     */
    void updateVideoLocationTextDisplay(String input);

    /**
     * Updates output display based on String provided as argument.
     *
     * @param output
     *            new value of output display
     */
    void updateExportLocationTextDisplay(String output);

    /**
     * Updates input display based on String provided as argument.
     *
     * @param input
     *            new value of input display
     */
    void updateItemIndexTextDisplay(int i);

    /**
     * Updates output display based on String provided as argument.
     *
     * @param output
     *            new value of output display
     */
    void updateNumberOfFramesTextDisplay(int i);

}
