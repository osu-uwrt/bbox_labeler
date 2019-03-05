import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.Timer;

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
     * Timer for quickly cycling through frames Is not currently used
     */
    private Timer timer;

    //TODO: change defaultDirectory
    private final String defaultDirectory = "data\\Wildlife.wmv";

    /**
     * Updates view to display model.
     *
     * @param model
     *            the model
     * @param view
     *            the view
     */
    private void updateViewToMatchModel(YOLOBboxModel model,
            YOLOBboxView view) {
        /*
         * Get model info
         */
        String videoLocation = model.videoLocation();
        String username = model.username();
        String password = model.password();
        int itemIndex = model.itemIndex();
        int currentFrame = model.currentFrame();
        int frameRate = model.frameRate();
        int frameJump = model.frameJump();
        int totalFrames = model.totalFrames();
        BufferedImage image = model.lines();

        /*
         * Update view to reflect changes in model
         */
        view.updateVideoLocationTextDisplay(videoLocation);
        view.updateUsernameTextDisplay(username);
        view.updatePasswordTextDisplay(password);
        view.updateItemIndexTextDisplay(itemIndex);
        view.updateCurrentFrameTextDisplay(currentFrame);
        view.updateFrameRateTextDisplay(frameRate);
        view.updateFrameJumpTextDisplay(frameJump);
        view.updateTotalFramesTextDisplay(totalFrames);
        view.loadFrame(image);

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
        BufferedImage scaled = (BufferedImage) this
                .getScaledImage(this.model.master());
        this.model.setScaled(scaled);
        BufferedImage lines = deepCopy(scaled);
        this.model.setLines(lines);
        /*
         * Update view to reflect initial value of model
         */
        this.updateViewToMatchModel(this.model, this.view);
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
        this.model.setUsername("");
        this.model.setPassword("");
        this.model.setItemIndex(0);
        /*
         * Update view to reflect changes in model
         */
        this.updateViewToMatchModel(this.model, this.view);
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
        fc.setCurrentDirectory(new File(this.defaultDirectory));
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
        this.updateViewToMatchModel(this.model, this.view);
    }

    /**
     * Processes export event.
     */
    @Override
    public void processExportEvent() {
        /*
         * TODO: change naming convention folderName = {ITEM_NAME} fileName =
         * {ITEM_NAME} + "_" + {USERNAME} + "_" + {FRAME#} + ".jpg"
         */

        String preName = "data/horse";
        List<YOLO> yolo = this.model.yolo();
        PrintWriter writer;
        //counter for iterating through yolo
        int i = 0;
        //the highest number to iterate to
        int max = this.model.totalFrames() - 1;
        //bring i to the first yolo
        while (i < max) {
            //get the YOLO values
            this.findYOLOValues(i);
            //iterate through the yolos and create the files for the ones with data
            YOLO next = yolo.get(i);
            if (next.x() > 0.0 && next.y() > 0.0 && next.width() > 0.0
                    && next.height() > 0.0) {
                String format = String.format("%%0%dd",
                        String.valueOf(max).length());
                String name = "_" + String.format(format, i);
                /*
                 * output the text file
                 */
                try {
                    writer = new PrintWriter(preName + name + ".txt", "UTF-8");
                    //print [item index] [x] [y] [width] [height]
                    writer.println(this.model.itemIndex() + " " + next.x() + " "
                            + next.y() + " " + next.width() + " "
                            + next.height());
                    writer.close();
                } catch (FileNotFoundException
                        | UnsupportedEncodingException e) {
                    System.err.println("Problem exporting to text file");
                }
                /*
                 * output the image file
                 */
                try {
                    File outputfile = new File(preName + name + ".jpg");
                    FFmpegFrameGrabber frameGrabber = this.model.frameGrabber();
                    frameGrabber.start();
                    frameGrabber.setFrameNumber(i);
                    Java2DFrameConverter j = new Java2DFrameConverter();
                    BufferedImage bi = j.convert(frameGrabber.grabImage());
                    ImageIO.write(bi, "jpg", outputfile);
                    frameGrabber.close();
                } catch (Exception e) {
                    System.err.println("Problem getting frame");
                    e.printStackTrace();
                } catch (IOException e) {
                    System.err.println("Problem exporting to jpg");
                    e.printStackTrace();
                }
            }
            i++;
        }
        /*
         * compress the folder into a zip file
         */
        //TODO compress it into a .zip file

        /*
         * Update view to reflect changes in model
         */
        this.updateViewToMatchModel(this.model, this.view);
    }

    @Override
    public void processReviewEvent() {
        /*
         * TODO: Open new window to display the preview in full screen
         */
    }

    @Override
    public void processFillInFramesEvent() {
        this.processCV();
    }

    /**
     * Processes framesBack event.
     */
    @Override
    public void processFramesBackEvent() {
        /*
         * Update model in response to this event
         */

        //update frame jump in the model
        this.model.setFrameJump(this.view.getFrameJump());
        FFmpegFrameGrabber frameGrabber = this.model.frameGrabber();
        int currentFrame = this.model.currentFrame();
        Frame f = new Frame();
        try {
            frameGrabber.start();
            int jump = this.model.frameJump();
            //load the the frameJump-th previous frame
            if ((jump + 1) < currentFrame) {
                frameGrabber.setFrameNumber(currentFrame - (jump + 1));
            } else {
                frameGrabber.setFrameNumber(0);
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
            BufferedImage lines = deepCopy(scaled);
            this.redrawLines(lines,
                    this.model.bbox().get(this.model.currentFrame()));
            this.model.setLines(lines);
            frameGrabber.stop();
        } catch (Exception e) {
            System.out.println("Could not load next frame");
        }
        /*
         * Update view to reflect changes in model
         */
        this.updateViewToMatchModel(this.model, this.view);
    }

    /**
     * Processes frameForward event.
     */
    @Override
    public void processFramesForwardEvent() {
        /*
         * Update model in response to this event
         */

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
            BufferedImage lines = deepCopy(scaled);
            this.redrawLines(lines,
                    this.model.bbox().get(this.model.currentFrame()));
            this.model.setLines(lines);
            frameGrabber.stop();
        } catch (Exception e) {
            System.out.println("Could not load next frame");
        }
        /*
         * Update view to reflect changes in model
         */
        this.updateViewToMatchModel(this.model, this.view);
    }

    @Override
    public void processResizeEvent() {
        BufferedImage bi = (BufferedImage) this.model.master();
        BufferedImage scaled = (BufferedImage) this.getScaledImage(bi);
        this.model.setScaled(scaled);
        BufferedImage lines = deepCopy(scaled);
        this.redrawLines(lines,
                this.model.bbox().get(this.model.currentFrame()));
        this.model.setLines(lines);
        /*
         * Update view to reflect changes in model
         */
        this.updateViewToMatchModel(this.model, this.view);
    }

    @Override
    public void processMouseClickedEvent(int x, int y) {
        List<BBox> bbox = this.model.bbox();
        BBox temp = bbox.get(this.model.currentFrame());
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
        //set the data where it needs to be
        bbox.set(this.model.currentFrame(), temp);
        //Print out the list
        System.out.println("Frame: " + this.model.currentFrame());
        int i = 0;
        while (i < bbox.size()) {
            if (bbox.get(i).x1() + bbox.get(i).x2() + bbox.get(i).y1()
                    + bbox.get(i).y2() > 0.00001) {
                System.out.println("Frame " + i + ": " + bbox.get(i).x1() + ","
                        + bbox.get(i).y1() + " " + bbox.get(i).x2() + ","
                        + bbox.get(i).y2());
            }
            i++;
        }
        BufferedImage lines = deepCopy(this.model.scaled());
        this.redrawLines(lines,
                this.model.bbox().get(this.model.currentFrame()));
        this.model.setLines(lines);

        /*
         * Update view to reflect changes in model
         */
        this.updateViewToMatchModel(this.model, this.view);
    }

    @Override
    public void processMouseEnteredEvent(int x, int y) {
        this.model.setLastKnownX((double) x / this.model.scaled().getWidth());
        this.model.setLastKnownY((double) y / this.model.scaled().getHeight());
        BufferedImage lines = deepCopy(this.model.scaled());
        this.redrawLines(lines,
                this.model.bbox().get(this.model.currentFrame()));
        this.model.setLines(lines);

        /*
         * Update view to reflect changes in model
         */
        this.updateViewToMatchModel(this.model, this.view);
    }

    @Override
    public void processMouseExitedEvent(int x, int y) {
        this.model.setLastKnownX((double) x / this.model.scaled().getWidth());
        this.model.setLastKnownY((double) y / this.model.scaled().getHeight());
        BufferedImage lines = deepCopy(this.model.scaled());
        this.redrawLines(lines,
                this.model.bbox().get(this.model.currentFrame()));
        this.model.setLines(lines);

        /*
         * Update view to reflect changes in model
         */
        this.updateViewToMatchModel(this.model, this.view);
    }

    @Override
    public void processMousePressedEvent(int x, int y) {
        this.model.setLastKnownX((double) x / this.model.scaled().getWidth());
        this.model.setLastKnownY((double) y / this.model.scaled().getHeight());
        BufferedImage lines = deepCopy(this.model.scaled());
        this.redrawLines(lines,
                this.model.bbox().get(this.model.currentFrame()));
        this.model.setLines(lines);

        /*
         * Update view to reflect changes in model
         */
        this.updateViewToMatchModel(this.model, this.view);
    }

    @Override
    public void processMouseReleasedEvent(int x, int y) {
        this.model.setLastKnownX((double) x / this.model.scaled().getWidth());
        this.model.setLastKnownY((double) y / this.model.scaled().getHeight());
        BufferedImage lines = deepCopy(this.model.scaled());
        this.redrawLines(lines,
                this.model.bbox().get(this.model.currentFrame()));
        this.model.setLines(lines);

        /*
         * Update view to reflect changes in model
         */
        this.updateViewToMatchModel(this.model, this.view);
    }

    @Override
    public void processMouseDraggedEvent(int x, int y) {
        this.model.setLastKnownX((double) x / this.model.scaled().getWidth());
        this.model.setLastKnownY((double) y / this.model.scaled().getHeight());
        BufferedImage lines = deepCopy(this.model.scaled());
        this.redrawLines(lines,
                this.model.bbox().get(this.model.currentFrame()));
        this.model.setLines(lines);

        /*
         * Update view to reflect changes in model
         */
        this.updateViewToMatchModel(this.model, this.view);
    }

    @Override
    public void processMouseMovedEvent(int x, int y) {
        this.model.setLastKnownX((double) x / this.model.scaled().getWidth());
        this.model.setLastKnownY((double) y / this.model.scaled().getHeight());
        BufferedImage lines = deepCopy(this.model.scaled());
        this.redrawLines(lines,
                this.model.bbox().get(this.model.currentFrame()));
        this.model.setLines(lines);

        /*
         * Update view to reflect changes in model
         */
        this.updateViewToMatchModel(this.model, this.view);
    }

    /**
     * Processes the CV by taking the given bboxes and filling in the frames
     * between using linear interpolation
     */
    private void processCV() {
        int i = 0;
        List<BBox> bbox = this.model.bbox();

        //get the first frame with a bounding box
        while (i < bbox.size() && (bbox.get(i).x1() < 0 || bbox.get(i).y1() < 0
                || bbox.get(i).x2() < 0 || bbox.get(i).y2() < 0)) {
            i++;
        }
        //if one was found
        if (i != bbox.size()) {
            BBox first = bbox.get(i);
            int firstIndex = i;
            //get the last frame with a bounding box
            i = bbox.size() - 1;
            while (i > firstIndex
                    && (bbox.get(i).x1() < 0 || bbox.get(i).y1() < 0
                            || bbox.get(i).x2() < 0 || bbox.get(i).y2() < 0)) {
                i--;
            }
            //if one other than the first one was found
            if (i != firstIndex) {
                int lastIndex = i;
                int nextIndex;
                do {

                    //get the next frame after the first with a bounding box
                    i = firstIndex + 1;
                    while (i < bbox.size() && (bbox.get(i).x1() < 0
                            || bbox.get(i).y1() < 0 || bbox.get(i).x2() < 0
                            || bbox.get(i).y2() < 0)) {
                        i++;
                    }
                    BBox next = bbox.get(i);
                    nextIndex = i;
                    //get the difference in frame numbers between the frames
                    int indexDifference = nextIndex - firstIndex;
                    //get the differences in height, width, x, and y between the frames
                    //the data for the first and last frame
                    double firstWidth = Math.abs(first.x1() - first.x2());
                    double firstHeight = Math.abs(first.y1() - first.y2());
                    double firstXCenter = (first.x1() + first.x2()) / 2;
                    double firstYCenter = (first.y1() + first.y2()) / 2;
                    double lastWidth = Math.abs(next.x1() - next.x2());
                    double lastHeight = Math.abs(next.y1() - next.y2());
                    double lastXCenter = (next.x1() + next.x2()) / 2;
                    double lastYCenter = (next.y1() + next.y2()) / 2;
                    //the differences between the first and last frame
                    double heightDifference = lastHeight - firstHeight;
                    double widthDifference = lastWidth - firstWidth;
                    double xDifference = lastXCenter - firstXCenter;
                    double yDifference = lastYCenter - firstYCenter;
                    //the differences per frame between the first and last frame
                    double heightDifferenceScaled = heightDifference
                            / indexDifference;
                    double widthDifferenceScaled = widthDifference
                            / indexDifference;
                    double xDifferenceScaled = xDifference / indexDifference;
                    double yDifferenceScaled = yDifference / indexDifference;
                    int currentIndex = firstIndex + 1;
                    while (currentIndex < nextIndex) {
                        //set the scaled differences for the frames inbetween
                        //the values to set the next the next bbox to
                        double xCenter = firstXCenter + xDifferenceScaled
                                * (currentIndex - firstIndex);
                        double yCenter = firstYCenter + yDifferenceScaled
                                * (currentIndex - firstIndex);
                        double width = firstWidth + widthDifferenceScaled
                                * (currentIndex - firstIndex);
                        double height = firstHeight + heightDifferenceScaled
                                * (currentIndex - firstIndex);
                        double x1 = xCenter - (width / 2);
                        double y1 = yCenter - (height / 2);
                        double x2 = xCenter + (width / 2);
                        double y2 = yCenter + (height / 2);
                        bbox.set(currentIndex, new BBox(x1, y1, x2, y2));
                        currentIndex++;
                    }
                    firstIndex = nextIndex;
                    first = next;
                } while (firstIndex < lastIndex);
            } else {
                //no other bboxes were set
            }
        } else {
            //no bboxes were set
        }
        System.out.println("done cv");
    }

    /**
     * Uses the bboxes find the center x and y and the width and height for a
     * given frame.
     */
    private void findYOLOValues(int frame) {
        List<BBox> bbox = this.model.bbox();
        List<YOLO> yolo = this.model.yolo();
        BBox p = bbox.get(frame);
        //calculate the values for YOLO from the bbox
        int width = (int) (Math.abs(p.x1() - p.x2()));
        int height = (int) (Math.abs(p.y1() - p.y2()));
        int x = (int) (p.x1() + p.x2()) / 2;
        int y = (int) (p.y1() + p.y2()) / 2;
        //add the values to the yolo map
        YOLO ny = new YOLO(x, y, width, height);
        yolo.add(frame, ny);
    }

    /*
     * This is not my code. I found it online and just removed some unneeded
     * parts. Not currently used but might be needed for better object tracking.
     * Converts a buffered image to a Mat needed for opencv
     */
    private static Mat img3Mat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer())
                .getData();
        mat.put(0, 0, data);
        return mat;
    }

    /*
     * This is not my code. I found it online and just removed some unneeded
     * parts. Not currently used but might be needed for better object tracking.
     */
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
     * This is not my code. I found it online and just removed some unneeded
     * parts. Not currently used but might be needed for better object tracking.
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

    private void redrawLines(BufferedImage image, BBox bbox) {
        Graphics2D g = image.createGraphics();
        g.setColor(Color.BLACK);
        //draw lines for bounding boxes
        int x = (int) (bbox.x1() * image.getWidth());
        g.drawLine(x, 0, x, image.getHeight());
        int y = (int) (bbox.y1() * image.getHeight());
        g.drawLine(0, y, image.getWidth(), y);
        x = (int) (bbox.x2() * image.getWidth());
        g.drawLine(x, 0, x, image.getHeight());
        y = (int) (bbox.y2() * image.getHeight());
        g.drawLine(0, y, image.getWidth(), y);
        //Print out the list
        System.out.println("Frame: ?");
        System.out.println("Frame ?: " + bbox.x1() + "," + bbox.y1() + " "
                + bbox.x2() + "," + bbox.y2());
        //draw lines for cursor
        if (this.model.lastKnownX() >= 0.0 && this.model.lastKnownY() >= 0) {
            drawCrosshairs(image,
                    (int) (this.model.lastKnownX() * image.getWidth()),
                    (int) (this.model.lastKnownY() * image.getHeight()));
        }
    }

    private static void drawCrosshairs(BufferedImage image, int x, int y) {
        Graphics2D g = image.createGraphics();
        g.setColor(Color.BLACK);
        int dashStart = x;
        final int DASH_LENGTH = 20;
        //Drawing dashed line at: x = x);
        //print the crosshairs
        if (dashStart + (DASH_LENGTH / 2) < image.getWidth()) {
            g.drawLine(dashStart, y, dashStart + (DASH_LENGTH / 2), y);
        } else {
            g.drawLine(dashStart, y, image.getWidth(), y);
        }
        if (dashStart - (DASH_LENGTH / 2) > 0) {
            g.drawLine(dashStart, y, dashStart - (DASH_LENGTH / 2), y);
        } else {
            g.drawLine(dashStart, y, 0, y);
        }
        //print the rest of the dashed line
        dashStart = x + (DASH_LENGTH / 2) + DASH_LENGTH;
        while (dashStart + DASH_LENGTH < image.getWidth()) {
            g.drawLine(dashStart, y, dashStart + DASH_LENGTH, y);
            dashStart += DASH_LENGTH * 2;
        }
        g.drawLine(dashStart, y, image.getWidth(), y);
        dashStart = (x - (DASH_LENGTH / 2)) - DASH_LENGTH;
        while (dashStart - DASH_LENGTH > 0) {
            g.drawLine(dashStart, y, dashStart - DASH_LENGTH, y);
            dashStart -= DASH_LENGTH * 2;
        }
        g.drawLine(dashStart, y, 0, y);

        //Drawing dashed line at: x = x);
        dashStart = y;
        //print the crosshairs
        if (dashStart + (DASH_LENGTH / 2) < image.getHeight()) {
            g.drawLine(x, dashStart, x, dashStart + (DASH_LENGTH / 2));
        } else {
            g.drawLine(x, dashStart, x, image.getHeight());
        }
        if (dashStart - (DASH_LENGTH / 2) > 0) {
            g.drawLine(x, dashStart, x, dashStart - (DASH_LENGTH / 2));
        } else {
            g.drawLine(x, dashStart, x, 0);
        }
        //print the rest of the dashed line
        dashStart = y + (DASH_LENGTH / 2) + DASH_LENGTH;
        while (dashStart + DASH_LENGTH < image.getHeight()) {
            g.drawLine(x, dashStart, x, dashStart + DASH_LENGTH);
            dashStart += DASH_LENGTH * 2;
        }
        g.drawLine(x, dashStart, x, image.getHeight());
        dashStart = (y - (DASH_LENGTH / 2)) - DASH_LENGTH;
        while (dashStart - DASH_LENGTH > 0) {
            g.drawLine(x, dashStart, x, dashStart - DASH_LENGTH);
            dashStart -= DASH_LENGTH * 2;
        }
        g.drawLine(x, dashStart, x, 0);

    }

    /*
     * This is not my code. I found it online.
     */
    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

}
