import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import org.bytedeco.javacv.FFmpegFrameGrabber;

import components.map.Map;
import components.map.Map1L;

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
    private Map<Integer, BBox> bbox;
    private Map<Integer, YOLO> yolo;
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
        this.bbox = new Map1L<Integer, BBox>();
        this.totalFrames = 0;
        this.yolo = new Map1L<Integer, YOLO>();
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
    public Map<Integer, BBox> bbox() {
        return this.bbox;
    }

    @Override
    public Map<Integer, YOLO> yolo() {
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
