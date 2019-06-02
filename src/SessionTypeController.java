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
     * Opens a new window to select the video to continue working on
     */
    void processLoadEvent();

    /**
     * Loads the video into the bbox labeller and imports data from the
     * corresponding save file.
     */
    void processStartEvent();

}
