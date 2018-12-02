import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.bytedeco.javacv.FFmpegFrameGrabber;

/**
 * Model class.
 *
 * @author Derek Opdycke
 */
public final class YOLOBboxModel1 implements YOLOBboxModel {

    /**
     * Model variables.
     */
    private String videoLocation, exportLocation;
    private int itemIndex, currentFrame, frameRate, frameJump, totalFrames,
            videoWidth, videoHeight;
    private List<BBox> bbox;
    private List<YOLO> yolo;
    private File file;
    private FFmpegFrameGrabber frameGrabber;
    private Image master;
    private BufferedImage scaled;

    /**
     * Default constructor.
     */
    public YOLOBboxModel1() {
        /*
         * Initialize model
         */
        this.videoLocation = "";
        this.exportLocation = "";
        this.itemIndex = 0;
        this.currentFrame = 0;
        this.frameRate = 0;
        this.frameJump = 2;
        this.bbox = new LinkedList<BBox>();
        this.totalFrames = 0;
        this.yolo = new LinkedList<YOLO>();
        this.file = new File("");
        this.frameGrabber = new FFmpegFrameGrabber(String.valueOf(this.file));
        this.master = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        this.scaled = (BufferedImage) this.master;
        this.videoHeight = this.frameGrabber.getImageHeight();
        this.videoWidth = this.frameGrabber.getImageWidth();
    }

    @Override
    public void setVideoLocation(String vl) {
        this.videoLocation = vl;
    }

    @Override
    public String videoLocation() {
        return this.videoLocation;
    }

    @Override
    public void setExportLocation(String el) {
        this.exportLocation = el;
    }

    @Override
    public String exportLocation() {
        return this.exportLocation;
    }

    @Override
    public void setItemIndex(int x) {
        this.itemIndex = x;
    }

    @Override
    public int itemIndex() {
        return this.itemIndex;
    }

    @Override
    public void setCurrentFrame(int x) {
        this.currentFrame = x;
        System.out.println("Current Frame: " + this.currentFrame);
    }

    @Override
    public int currentFrame() {
        return this.currentFrame;
    }

    @Override
    public void setFrameRate(int x) {
        this.frameRate = x;
    }

    @Override
    public int frameRate() {
        return this.frameRate;
    }

    @Override
    public void setFrameJump(int x) {
        this.frameJump = x;
    }

    @Override
    public int frameJump() {
        return this.frameJump;
    }

    @Override
    public void setTotalFrames(int x) {
        this.totalFrames = x;

    }

    @Override
    public int totalFrames() {
        return this.totalFrames;
    }

    @Override
    public List<BBox> bbox() {
        return this.bbox;
    }

    @Override
    public List<YOLO> yolo() {
        return this.yolo;
    }

    @Override
    public File file() {
        return this.file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public FFmpegFrameGrabber frameGrabber() {
        return this.frameGrabber;
    }

    @Override
    public void setFrameGrabber(FFmpegFrameGrabber frameGrabber) {
        this.frameGrabber = frameGrabber;
        this.videoHeight = this.frameGrabber.getImageHeight();
        this.videoWidth = this.frameGrabber.getImageWidth();
    }

    @Override
    public Image master() {
        return this.master;
    }

    @Override
    public void setMaster(Image image) {
        this.master = image;
    }

    @Override
    public BufferedImage scaled() {
        return this.scaled;
    }

    @Override
    public void setScaled(BufferedImage image) {
        this.scaled = image;
        this.videoHeight = image.getHeight();
        this.videoWidth = image.getWidth();
    }

    @Override
    public int videoHeight() {
        return this.videoHeight;
    }

    @Override
    public int videoWidth() {
        return this.videoWidth;
    }

}
