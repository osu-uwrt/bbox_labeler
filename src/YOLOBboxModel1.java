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
        this.frameJump = 0;
        this.bbox = new Map1L<Integer, BBox>();
        this.totalFrames = 0;
        this.yolo = new Map1L<Integer, YOLO>();

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

}
