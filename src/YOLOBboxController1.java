
/**
 * Controller class.
 *
 * @author Derek Opdycke
 */
public final class YOLOBboxController1 implements YOLOBboxController {

    /**
     * Model object.
     */
    private final YOLOBboxModel model;

    /**
     * View object.
     */
    private final YOLOBboxView view;

    /**
     * Updates view to display model.
     *
     * @param model
     *            the model
     * @param view
     *            the view
     */
    private static void updateViewToMatchModel(YOLOBboxModel model,
            YOLOBboxView view) {
        /*
         * Get model info
         */
        //String input = model.input();
        //String output = model.output();
        String videoLocation = model.videoLocation();
        String exportLocation = model.exportLocation();
        int itemIndex = model.itemIndex();
        int currentFrame = model.currentFrame();
        int frameRate = model.frameRate();
        int frameJump = model.frameJump();
        /*
         * Update view to reflect changes in model
         */
        //view.updateInputDisplay(input);
        //view.updateOutputDisplay(output);
        view.updateVideoLocationTextDisplay(videoLocation);
        view.updateExportLocationTextDisplay(exportLocation);
        view.updateItemIndexTextDisplay(itemIndex);

    }

    /**
     * Constructor; connects {@code this} to the model and view it coordinates.
     *
     * @param model
     *            model to connect to
     * @param view
     *            view to connect to
     */
    public YOLOBboxController1(YOLOBboxModel model, YOLOBboxView view) {
        this.model = model;
        this.view = view;
        /*
         * Update view to reflect initial value of model
         */
        updateViewToMatchModel(this.model, this.view);
    }

    /**
     * Processes reset event.
     */
    @Override
    public void processResetEvent() {
        /*
         * Update model in response to this event
         */
        this.model.setVideoLocation("");
        this.model.setExportLocation("");
        /*
         * Update view to reflect changes in model
         */
        updateViewToMatchModel(this.model, this.view);
    }

    /**
     * Processes browseVideoLocation event.
     *
     * @param input
     *            value of input text (provided by view)
     */
    @Override
    public void processBrowseVideoLocationEvent() {
        /*
         * Update model in response to this event
         */
        //this.model.setInput(input);
        //this.model.setOutput(input);
        /*
         * Update view to reflect changes in model
         */
        updateViewToMatchModel(this.model, this.view);
    }

    /**
     * Processes BrowseExportLocation event.
     */
    @Override
    public void processBrowseExportLocationEvent() {
        /*
         * Update model in response to this event
         */
        this.model.setVideoLocation("");
        this.model.setExportLocation("");
        /*
         * Update view to reflect changes in model
         */
        updateViewToMatchModel(this.model, this.view);
    }

    /**
     * Processes export event.
     */
    @Override
    public void processExportEvent() {
        /*
         * Update model in response to this event
         */
        this.model.setVideoLocation("");
        this.model.setExportLocation("");
        /*
         * Update view to reflect changes in model
         */
        updateViewToMatchModel(this.model, this.view);
    }

    /**
     * Processes framesBack event.
     */
    @Override
    public void processFramesBackEvent() {
        /*
         * Update model in response to this event
         */
        this.model.setVideoLocation("");
        this.model.setExportLocation("");
        /*
         * Update view to reflect changes in model
         */
        updateViewToMatchModel(this.model, this.view);
    }

    /**
     * Processes frameForward event.
     */
    @Override
    public void processFramesForwardEvent() {
        /*
         * Update model in response to this event
         */
        this.model.setVideoLocation("");
        this.model.setExportLocation("");
        /*
         * Update view to reflect changes in model
         */
        updateViewToMatchModel(this.model, this.view);
    }

    /**
     * Processes the CV by taking the given bboxes and filling in the frames
     * between
     */
    private void processCV() {

    }

    /**
     * Uses the bboxes find the center x and y and the width and height for a
     * given frame.
     */
    private void findYOLOValues(int frame) {

    }

}
