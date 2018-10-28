/**
 * Model interface.
 *
 * @author Derek Opdycke
 */
public interface YOLOBboxModel {

    String videoLocation();

    void setVideoLocation(String vl);

    String exportLocation();

    void setExportLocation(String el);

    int itemIndex();

    void setItemIndex(int x);

    int currentFrame();

    void setCurrentFrame(int x);

    int frameRate();

    void setFrameRate(int x);

    int frameJump();

    void setFrameJump(int x);

}
