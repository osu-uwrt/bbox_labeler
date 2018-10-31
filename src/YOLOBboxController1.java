import java.io.File;

import javax.swing.JFileChooser;

import components.map.Map;
import components.map.Map1L;

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
        String videoLocation = model.videoLocation();
        String exportLocation = model.exportLocation();
        int itemIndex = model.itemIndex();
        int currentFrame = model.currentFrame();
        int frameRate = model.frameRate();
        int frameJump = model.frameJump();
        int totalFrames = model.totalFrames();
        /*
         * Update view to reflect changes in model
         */
        view.updateVideoLocationTextDisplay(videoLocation);
        view.updateExportLocationTextDisplay(exportLocation);
        view.updateItemIndexTextDisplay(itemIndex);
        view.updateCurrentFrameTextDisplay(currentFrame);
        view.updateFrameRateTextDisplay(frameRate);
        view.updateFrameJumpTextDisplay(frameJump);
        view.updateTotalFramesTextDisplay(totalFrames);

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
        //Create a file chooser
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(fc);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            this.model.setVideoLocation(String.valueOf(file));

            //open the file and show the first frame if the file is the right type

        }
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
        //Create a file chooser
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(fc);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            this.model.setExportLocation(String.valueOf(file));
        }
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

        //process the video and create a picture file and text
        //file for every frame of the video and send it to the
        //location and then compress it into a .zip file
        this.processCV();
        int largestFrameNumber = this.findLargestKeyValue(this.model.bbox());
        int i = 0;
        while (i <= largestFrameNumber) {
            this.findYOLOValues(i);
        }

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

        //go back in the video a number of frames
        //equal to the given number of frames

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

        //go forward in the video a number of frames
        //equal to the given number of frames

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
        Map<Integer, BBox> bbox = this.model.bbox();
        Map<Integer, YOLO> yolo = this.model.yolo();
        Map.Pair<Integer, BBox> p = bbox.remove(frame);
        //calculate the values for YOLO from the bbox
        int width = Math.abs(p.value().x1() - p.value().x2());
        int height = Math.abs(p.value().y1() - p.value().y2());
        int x = (p.value().x1() + p.value().x2()) / 2;
        int y = (p.value().y1() + p.value().y2()) / 2;
        //add the values to the yolo map
        YOLO ny = new YOLO(x, y, width, height);
        yolo.add(frame, ny);
        //re-add p to bbox
        bbox.add(p.key(), p.value());
    }

    /**
     * Finds the largest key value in the given map
     */
    private int findLargestKeyValue(Map<Integer, BBox> m) {
        int largestKey = -1;
        Map<Integer, BBox> tempMap = new Map1L<Integer, BBox>();
        Map.Pair<Integer, BBox> p = m.removeAny();
        while (m.size() > 0) {
            p = m.removeAny();
            if (largestKey < p.key()) {
                largestKey = p.key();
            }
            tempMap.add(p.key(), p.value());
        }
        m = tempMap;
        return largestKey;
    }

}
