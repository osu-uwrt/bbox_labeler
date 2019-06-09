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
public interface SessionTypeController {

    /**
     * Opens a new window to select the video to work on
     */
    void processNewEvent();

    /**
     * Changes the UI to choose a video to continue working on
     */
    void processLoadEvent();

    /**
     * Loads the video into the bbox labeller and imports data from the
     * corresponding save file.
     */
    void processStartEvent();

    /**
     * Changes the UI back to the new or load buttons
     */
    void processBackEvent();
}
