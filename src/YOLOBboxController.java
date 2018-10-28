/**
 * Controller interface.
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
     *
     * @updates this.model, this.view
     * @ensures <pre>
     * this.model.input = ""  and
     * this.model.output = ""  and
     * [this.view has been updated to match this.model]
     * </pre>
     */
    void processResetEvent();

    /**
     * Processes event to browse for the location of a video file and insert the
     * location into the corresponding textbox.
     *
     * @param input
     *            string to be copied
     *
     * @updates this.model, this.view
     * @ensures <pre>
     * this.model.input = input  and
     * this.model.output = input  and
     * [this.view has been updated to match this.model]
     * </pre>
     */
    void processBrowseVideoLocationEvent();

    /**
     * Processes event to browse for the location of a folder to send output
     * files to.
     *
     * @param input
     *            string to be copied
     *
     * @updates this.model, this.view
     * @ensures <pre>
     * this.model.input = input  and
     * this.model.output = input  and
     * [this.view has been updated to match this.model]
     * </pre>
     */
    void processBrowseExportLocationEvent();

    /**
     * Processes event to export data to the given location and finalize it.
     *
     * @param input
     *            string to be copied
     *
     * @updates this.model, this.view
     * @ensures <pre>
     * this.model.input = input  and
     * this.model.output = input  and
     * [this.view has been updated to match this.model]
     * </pre>
     */
    void processExportEvent();

    /**
     * Processes event to move the video back the number of given frames.
     *
     * @param input
     *            string to be copied
     *
     * @updates this.model, this.view
     * @ensures <pre>
     * this.model.input = input  and
     * this.model.output = input  and
     * [this.view has been updated to match this.model]
     * </pre>
     */
    void processFramesBackEvent();

    /**
     * Processes event to move the video forward the number of given frames.
     *
     * @param input
     *            string to be copied
     *
     * @updates this.model, this.view
     * @ensures <pre>
     * this.model.input = input  and
     * this.model.output = input  and
     * [this.view has been updated to match this.model]
     * </pre>
     */
    void processFramesForwardEvent();

}
