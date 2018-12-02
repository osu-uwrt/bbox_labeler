import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

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
        Image image = model.scaled();
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
        view.loadFrame((BufferedImage) image);

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
        //TODO remove the next line. It is only there for testing.
        fc.setCurrentDirectory(new File(
                "C:\\Users\\Public\\Videos\\Sample Videos\\Wildlife.wmv"));
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
                    frameGrabber.setAudioChannels(0);
                    Java2DFrameConverter j = new Java2DFrameConverter();
                    frameGrabber.setFrameNumber(0);
                    Image bi = j.convert(frameGrabber.grabImage());
                    this.model.setMaster(bi);
                    BufferedImage scaled = (BufferedImage) this
                            .getScaledImage(bi);
                    this.model.setScaled(scaled);
                    this.model.setFrameRate((int) frameGrabber.getFrameRate());
                    this.model.setTotalFrames(
                            frameGrabber.getLengthInVideoFrames());

                }
                frameGrabber.stop();

                /*
                 * Update model in response to this event
                 */
                this.model.setVideoLocation(String.valueOf(file));
                this.model.setFile(file);
                this.model.setCurrentFrame(frameGrabber.getFrameNumber());

            } catch (IOException e) {
                System.out.println("Trouble Loading File");
            }
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
        int largestFrameNumber = this.model.bbox().size();
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

        //update frame jump in the model
        this.model.setFrameJump(this.view.getFrameJump());
        FFmpegFrameGrabber frameGrabber = this.model.frameGrabber();
        int currentFrame = this.model.currentFrame();
        int distToEnd = this.model.totalFrames() - this.model.currentFrame();
        Frame f = new Frame();
        try {
            frameGrabber.start();
            int jump = this.model.frameJump();
            //load the the frameJump-th next frame
            if (distToEnd > jump) {
                frameGrabber.setFrameNumber(currentFrame + jump - 1);
            } else {
                frameGrabber.setFrameNumber(this.model.totalFrames() - 2);
            }
            f = frameGrabber.grabImage();
            Java2DFrameConverter j = new Java2DFrameConverter();
            System.out.println(f);
            BufferedImage bi = j.convert(f);
            System.out.println(bi);
            this.model.setMaster(bi);
            BufferedImage scaled = (BufferedImage) this.getScaledImage(bi);
            this.model.setScaled(scaled);
            this.model.setCurrentFrame(frameGrabber.getFrameNumber());
            frameGrabber.stop();
        } catch (Exception e) {
            System.out.println("Could not load next frame");
        }
        /*
         * Update view to reflect changes in model
         */
        updateViewToMatchModel(this.model, this.view);
    }

    @Override
    public void processResizeEvent() {
        BufferedImage bi = (BufferedImage) this.model.master();
        BufferedImage scaled = (BufferedImage) this.getScaledImage(bi);
        this.model.setScaled(scaled);
        /*
         * Update view to reflect changes in model
         */
        updateViewToMatchModel(this.model, this.view);
    }

    @Override
    public void processMouseClickedEvent(int x, int y) {
        List<BBox> bbox = this.model.bbox();
        BBox temp;
        //if data for this frame already exists, load it. Else, start from scratch.
        if (bbox.size() > this.model.currentFrame()
                && bbox.get(this.model.currentFrame()) != null) {
            temp = bbox.get(this.model.currentFrame());
        } else {
            temp = new BBox();
        }
        //get the proportion of the click relative to the frame
        double dx = ((double) x) / this.model.scaled().getWidth();
        double dy = ((double) y) / this.model.scaled().getHeight();
        //set the pair of values based on the last pair that was set
        if (temp.firstIsSetNext()) {
            temp.setx1(dx);
            temp.sety1(dy);
        } else {
            temp.setx2(dx);
            temp.sety2(dy);
        }
        //add space to the end of the list if needed
        while (bbox.size() - this.model.currentFrame() <= 0) {
            bbox.add(bbox.size(), new BBox());
        }
        //set the data where it needs to be
        bbox.set(this.model.currentFrame(), temp);
        //Print out the list
        System.out.println("Frame: " + this.model.currentFrame());
        int i = 0;
        while (i < bbox.size()) {
            System.out.println(bbox.get(i).x1() + "," + bbox.get(i).y1() + " "
                    + bbox.get(i).x2() + "," + bbox.get(i).y2());
            i++;
        }

    }

    @Override
    public void processMouseEnteredEvent(int x, int y) {
        // TODO Auto-generated method stub

    }

    @Override
    public void processMouseExitedEvent(int x, int y) {
        // TODO Auto-generated method stub

    }

    @Override
    public void processMousePressedEvent(int x, int y) {
        // TODO Auto-generated method stub

    }

    @Override
    public void processMouseReleasedEvent(int x, int y) {
        // TODO Auto-generated method stub

    }

    @Override
    public void processDraggedEvent(int x, int y) {
        // TODO Auto-generated method stub

    }

    @Override
    public void processMouseMovedEvent(int x, int y) {
        // TODO Auto-generated method stub

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
        List<BBox> bbox = this.model.bbox();
        List<YOLO> yolo = this.model.yolo();
        BBox p = bbox.remove(frame);
        //calculate the values for YOLO from the bbox
        int width = (int) ((Math.abs(p.x1() - p.x2())
                * this.model.videoWidth()));
        int height = (int) ((Math.abs(p.y1() - p.y2())
                * this.model.videoHeight()));
        int x = (int) ((p.x1() + p.x2()) * this.model.videoWidth()) / 2;
        int y = (int) ((p.y1() + p.y2()) * this.model.videoWidth()) / 2;
        //add the values to the yolo map
        YOLO ny = new YOLO(x, y, width, height);
        yolo.add(frame, ny);
        //re-add p to bbox
        bbox.add(this.model.currentFrame(), p);
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

    /*
     * Returns an image of the appropriately scaled height for the window
     */
    private Image getScaledImage(Image srcImg) {
        double heightRatio = (double) this.model.master().getHeight(null)
                / (double) this.view.getFrameAreaHeight();
        double widthRatio = (double) this.model.master().getWidth(null)
                / (double) this.view.getFrameAreaWidth();
        int newWidth;
        int newHeight;
        if (heightRatio > widthRatio) {
            newHeight = (int) (this.model.master().getHeight(null)
                    / heightRatio);
            newWidth = (int) (this.model.master().getWidth(null) / heightRatio);
        } else {
            newHeight = (int) (this.model.master().getHeight(null)
                    / widthRatio);
            newWidth = (int) (this.model.master().getWidth(null) / widthRatio);
        }
        BufferedImage resizedImg = new BufferedImage(newWidth, newHeight,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, newWidth, newHeight, null);
        g2.dispose();
        return resizedImg;
    }

}
