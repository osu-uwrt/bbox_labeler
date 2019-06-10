/**
 * Controller interface.
 *
 *
 * @author Derek Opdycke
 *
 * @mathmodel <pre>
 * type DemoController is modeled by
 *   (model: DemoModel,
 *    view: DemoView)
 * </pre>
 * @initially <pre>
 * (DemoModel model, DemoView view):
 *   ensures
 *     this.model = model  and
 *     this.view = view
 * </pre>
 */
public interface YOLOBboxController {

    /**
     * Processes event to reset model.
     */
    void processResetEvent();

    /**
     * Creates txt and image files for every frame with a BBox, zips up the
     * folder, and uploads it to box.
     */
    void processExportEvent();

    /**
     * Opens a new window to quickly cycle through them before finalizing
     */
    void processReviewEvent();

    /**
     * Fills in the frames between the frames where BBoxes were given
     */
    void processFillInFramesEvent();

    /**
     * Moves the video back the given number of frames or to the first frame if
     * there are too few frames left. Note: there is a bug where it wont move
     * only only a single frame sometimes. I suspect it has something to do with
     * audio in the video.
     */
    void processFramesBackEvent();

    /**
     * Moves the video forward the given number of frames or to the end if there
     * are not enough frames left. Note: there is a bug where it wont move
     * forward only a single frame sometimes. I suspect it has something to do
     * with audio in the video.
     */
    void processFramesForwardEvent();

    /**
     * Saves the bbox data from the current session to the saves folder.
     */
    void processSaveEvent();

    /**
     * Refactors the window based on the new size of the window
     */
    void processResizeEvent();

    /**
     * Updates the BBox for the current frame by making the coordinates for the
     * last corner to be changed. So the BBox will always hold the location of
     * the last 2 clicks. Updates the crosshairs.
     */
    void processMouseClickedEvent(int x, int y);

    /**
     * Updates the crosshairs.
     */
    void processMouseEnteredEvent(int x, int y);

    /**
     * Updates the crosshairs.
     */
    void processMouseExitedEvent(int x, int y);

    /**
     * Updates the crosshairs.
     */
    void processMousePressedEvent(int x, int y);

    /**
     * Updates the crosshairs.
     */
    void processMouseReleasedEvent(int x, int y);

    /**
     * Updates the crosshairs. TODO: Allow BBoxes to be set by clicking and
     * dragging.
     */
    void processMouseDraggedEvent(int x, int y);

    /**
     * Updates the crosshairs.
     */
    void processMouseMovedEvent(int x, int y);

    /**
     * Increments the number of frames to jump.
     */
    void incrmentFrameJump();

    /**
     * Decrements the number of frames to jump.
     */
    void decrementFrameJump();

}
