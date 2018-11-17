import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

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
    private int itemIndex, currentFrame, frameRate, frameJump, totalFrames;
    private Map<Integer, BBox> bbox;
    private Map<Integer, YOLO> yolo;
    private File file;
    private FFmpegFrameGrabber frameGrabber;
    private Image image;

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
        try {
            this.image = ImageIO.read(new File("data/Default.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    }

    @Override
    public Image image() {
        return this.image;
    }

    @Override
    public void setImage(Image image) {
        this.image = image;
    }

    /*
     * Scales the image to the given height and width.
     */
    @Override
    public void scaleFrame(int height, int width) {
        this.image = this.getScaledImage(this.image, width, height);
    }

    /*
     * Returns an image of the given height and width using the given image.
     */
    private Image getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }

}
