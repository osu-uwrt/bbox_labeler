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
     * Updates video location display based on String provided as argument.
     *
     * @param s
     *            new value of video location display
     */
    void updateVideoLocationTextDisplay(String s);

    /**
     * Updates export location display based on String provided as argument.
     *
     * @param s
     *            new value of export location display
     */
    void updateExportLocationTextDisplay(String s);

    /**
     * Updates item index display based on integer provided as argument.
     *
     * @param i
     *            new value of input display
     */
    void updateItemIndexTextDisplay(int i);

    /**
     * Updates number of frames display based on integer provided as argument.
     *
     * @param i
     *            new value of output display
     */
    void updateTotalFramesTextDisplay(int i);

    /**
     * Updates current frame display based on integer provided as argument.
     *
     * @param i
     *            new value of output display
     */
    void updateCurrentFrameTextDisplay(int i);

    /**
     * Updates frame rate display based on integer provided as argument.
     *
     * @param i
     *            new value of output display
     */
    void updateFrameRateTextDisplay(int i);

    /**
     * Updates frame jump display based on integer provided as argument.
     *
     * @param i
     *            new value of output display
     */
    void updateFrameJumpTextDisplay(int i);

}
