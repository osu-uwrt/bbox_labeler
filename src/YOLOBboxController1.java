import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

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
        //Create a file chooser
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(fc);

        //if they chose a file
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            //try to load the file and load the first frame
            try {
                FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(
                        String.valueOf(file));
                this.model.setFrameGrabber(frameGrabber);
                frameGrabber.start();
                //make sure the file is a video file and has frames
                if (frameGrabber.hasVideo()
                        && frameGrabber.getLengthInFrames() > 0) {
                    Java2DFrameConverter j = new Java2DFrameConverter();
                    BufferedImage bi = j.convert(frameGrabber.grab());
                    this.view.loadFrame(bi);
                    this.model.setFrameRate((int) frameGrabber.getFrameRate());
                    this.model.setTotalFrames(
                            frameGrabber.getLengthInVideoFrames());

                }
                frameGrabber.stop();
            } catch (IOException e) {
                System.out.println("Trouble Loading File");
            }

            /*
             * Update model in response to this event
             */
            this.model.setVideoLocation(String.valueOf(file));
            this.model.setFile(file);
            this.model.setCurrentFrame(0);
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

        //TODO process the video and create a picture file and text
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

        //TODO go back in the video a number of frames
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

        //TODO go forward in the video a number of frames
        //equal to the given number of frames

        FFmpegFrameGrabber frameGrabber = this.model.frameGrabber();
        int i = 0;
        int distToEnd = this.model.totalFrames() - this.model.currentFrame();
        Frame f = new Frame();
        try {
            frameGrabber.start();
            //load the the frameJump-th next frame
            while (i < distToEnd && i < this.model.frameJump()) {
                f = frameGrabber.grab();
                i++;
            }
            Java2DFrameConverter j = new Java2DFrameConverter();
            BufferedImage bi = j.convert(f);
            this.view.loadFrame(bi);
            this.model.setCurrentFrame(this.model.currentFrame() + i);
            frameGrabber.stop();
        } catch (Exception e) {
            System.out.println("Could not load next frame");
        }
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

    /*
     * Converts a buffered image to a Mat needed for opencv
     */
    private static Mat img3Mat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer())
                .getData();
        mat.put(0, 0, data);
        return mat;
    }

    public static Mat img2Mat(BufferedImage in) {
        Mat out;
        byte[] data;
        int r, g, b;

        if (in.getType() == BufferedImage.TYPE_INT_RGB) {
            out = new Mat(240, 320, CvType.CV_8UC3);
            data = new byte[320 * 240 * (int) out.elemSize()];
            int[] dataBuff = in.getRGB(0, 0, 320, 240, null, 0, 320);
            for (int i = 0; i < dataBuff.length; i++) {
                data[i * 3] = (byte) ((dataBuff[i] >> 16) & 0xFF);
                data[i * 3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
                data[i * 3 + 2] = (byte) ((dataBuff[i] >> 0) & 0xFF);
            }
        } else {
            out = new Mat(240, 320, CvType.CV_8UC1);
            data = new byte[320 * 240 * (int) out.elemSize()];
            int[] dataBuff = in.getRGB(0, 0, 320, 240, null, 0, 320);
            for (int i = 0; i < dataBuff.length; i++) {
                r = (byte) ((dataBuff[i] >> 16) & 0xFF);
                g = (byte) ((dataBuff[i] >> 8) & 0xFF);
                b = (byte) ((dataBuff[i] >> 0) & 0xFF);
                data[i] = (byte) ((0.21 * r) + (0.71 * g) + (0.07 * b)); //luminosity
            }
        }
        out.put(0, 0, data);
        return out;
    }

    /*
     * Converts a Mat to a buffered image
     */
    private static BufferedImage mat2Img(Mat in) {
        BufferedImage out;
        byte[] data = new byte[320 * 240 * (int) in.elemSize()];
        int type = BufferedImage.TYPE_3BYTE_BGR;
        ;
        in.get(0, 0, data);

        out = new BufferedImage(320, 240, type);

        out.getRaster().setDataElements(0, 0, 320, 240, data);
        return out;
    }

}
